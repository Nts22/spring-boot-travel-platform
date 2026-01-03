package com.ptirado.nmviajes.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ptirado.nmviajes.constants.AppConstants;
import com.ptirado.nmviajes.constants.MessageKeys;
import com.ptirado.nmviajes.dto.api.request.CarritoItemRequest;
import com.ptirado.nmviajes.dto.api.request.ServicioAdicionalItemRequest;
import com.ptirado.nmviajes.dto.api.response.CarritoResponse;
import com.ptirado.nmviajes.entity.Carrito;
import com.ptirado.nmviajes.entity.CarritoItem;
import com.ptirado.nmviajes.entity.CarritoItemServicio;
import com.ptirado.nmviajes.entity.CarritoItemServicioId;
import com.ptirado.nmviajes.entity.Paquete;
import com.ptirado.nmviajes.entity.Reserva;
import com.ptirado.nmviajes.entity.ReservaItem;
import com.ptirado.nmviajes.entity.ReservaItemServicio;
import com.ptirado.nmviajes.entity.ReservaItemServicioId;
import com.ptirado.nmviajes.entity.ServicioAdicional;
import com.ptirado.nmviajes.entity.Usuario;
import com.ptirado.nmviajes.exception.api.BadRequestException;
import com.ptirado.nmviajes.exception.api.NotFoundException;
import com.ptirado.nmviajes.mapper.CarritoMapper;
import com.ptirado.nmviajes.repository.CarritoItemRepository;
import com.ptirado.nmviajes.repository.CarritoRepository;
import com.ptirado.nmviajes.repository.PaqueteRepository;
import com.ptirado.nmviajes.repository.ReservaRepository;
import com.ptirado.nmviajes.repository.ServicioAdicionalRepository;
import com.ptirado.nmviajes.repository.UsuarioRepository;
import com.ptirado.nmviajes.service.CarritoService;
import com.ptirado.nmviajes.viewmodel.CarritoView;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final PaqueteRepository paqueteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioAdicionalRepository servicioAdicionalRepository;
    private final ReservaRepository reservaRepository;
    private final CarritoMapper carritoMapper;

    // ===========================================================
    // UTILIDAD INTERNA
    // ===========================================================

    private Usuario getUsuarioOrThrow(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USUARIO_NOT_FOUND, idUsuario));
    }

    private Paquete getPaqueteOrThrow(Integer idPaquete) {
        return paqueteRepository.findById(idPaquete)
                .orElseThrow(() -> new NotFoundException(MessageKeys.PAQUETE_NOT_FOUND, idPaquete));
    }

    private ServicioAdicional getServicioOrThrow(Integer idServicio) {
        return servicioAdicionalRepository.findById(idServicio)
                .orElseThrow(() -> new NotFoundException(MessageKeys.SERVICIO_NOT_FOUND, idServicio));
    }

    private Carrito getOrCreateCarrito(Usuario usuario) {
        return carritoRepository.findByUsuario_IdUsuario(usuario.getIdUsuario())
                .orElseGet(() -> {
                    Carrito nuevoCarrito = new Carrito();
                    nuevoCarrito.setUsuario(usuario);
                    nuevoCarrito.setFechaCreacion(LocalDateTime.now());
                    nuevoCarrito.setFechaModificacion(LocalDateTime.now());
                    return carritoRepository.save(nuevoCarrito);
                });
    }

    private void validarStockDisponible(Paquete paquete) {
        if (paquete.getStockDisponible() == null || paquete.getStockDisponible() <= 0) {
            throw new BadRequestException(MessageKeys.STOCK_INSUFICIENTE, paquete.getNombre());
        }
    }

    private CarritoItem crearCarritoItem(Carrito carrito, CarritoItemRequest request) {
        Paquete paquete = getPaqueteOrThrow(request.getIdPaquete());
        validarStockDisponible(paquete);

        // Verificar que no exista el mismo paquete en el carrito
        carritoItemRepository.findByCarrito_IdCarritoAndPaquete_IdPaquete(
                carrito.getIdCarrito(), paquete.getIdPaquete())
                .ifPresent(item -> {
                    throw new BadRequestException(MessageKeys.CARRITO_ITEM_DUPLICADO, paquete.getNombre());
                });

        CarritoItem item = new CarritoItem();
        item.setCarrito(carrito);
        item.setPaquete(paquete);
        item.setFechaViajeInicio(request.getFechaViajeInicio());
        item.setFechaAgregado(LocalDateTime.now());

        CarritoItem itemGuardado = carritoItemRepository.save(item);

        // Agregar servicios adicionales
        if (request.getServiciosAdicionales() != null) {
            List<CarritoItemServicio> servicios = new ArrayList<>();
            for (ServicioAdicionalItemRequest servicioReq : request.getServiciosAdicionales()) {
                if (servicioReq.getIdServicio() != null && servicioReq.getCantidad() != null
                        && servicioReq.getCantidad() > 0) {
                    ServicioAdicional servicio = getServicioOrThrow(servicioReq.getIdServicio());

                    CarritoItemServicio cis = new CarritoItemServicio();
                    cis.setId(new CarritoItemServicioId(itemGuardado.getIdItem(), servicio.getIdServicio()));
                    cis.setCarritoItem(itemGuardado);
                    cis.setServicioAdicional(servicio);
                    cis.setCantidad(servicioReq.getCantidad());

                    servicios.add(cis);
                }
            }
            itemGuardado.setServicios(servicios);
        }

        // Actualizar fecha de modificación del carrito
        carrito.setFechaModificacion(LocalDateTime.now());
        carritoRepository.save(carrito);

        return itemGuardado;
    }

    private BigDecimal calcularTotalItem(CarritoItem item) {
        BigDecimal total = item.getPaquete().getPrecio();

        if (item.getServicios() != null) {
            for (CarritoItemServicio servicio : item.getServicios()) {
                BigDecimal costo = servicio.getServicioAdicional().getCosto();
                Integer cantidad = servicio.getCantidad();
                total = total.add(costo.multiply(BigDecimal.valueOf(cantidad)));
            }
        }

        return total;
    }

    // ===========================================================
    // API REST
    // ===========================================================

    @Override
    public CarritoResponse obtenerCarritoParaApi(Integer idUsuario) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        Carrito carrito = getOrCreateCarrito(usuario);
        return carritoMapper.toResponse(carrito);
    }

    @Override
    public CarritoResponse agregarItemParaApi(Integer idUsuario, CarritoItemRequest request) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        Carrito carrito = getOrCreateCarrito(usuario);
        crearCarritoItem(carrito, request);

        // Recargar el carrito con los items actualizados
        Carrito carritoActualizado = carritoRepository.findById(carrito.getIdCarrito()).orElse(carrito);
        return carritoMapper.toResponse(carritoActualizado);
    }

    @Override
    public CarritoResponse eliminarItemParaApi(Integer idUsuario, Integer idItem) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        Carrito carrito = getOrCreateCarrito(usuario);

        CarritoItem item = carritoItemRepository.findById(idItem)
                .orElseThrow(() -> new NotFoundException(MessageKeys.CARRITO_ITEM_NOT_FOUND, idItem));

        // Verificar que el item pertenece al carrito del usuario
        if (!item.getCarrito().getIdCarrito().equals(carrito.getIdCarrito())) {
            throw new NotFoundException(MessageKeys.CARRITO_ITEM_NOT_FOUND, idItem);
        }

        carritoItemRepository.delete(item);

        carrito.setFechaModificacion(LocalDateTime.now());
        carritoRepository.save(carrito);

        Carrito carritoActualizado = carritoRepository.findById(carrito.getIdCarrito()).orElse(carrito);
        return carritoMapper.toResponse(carritoActualizado);
    }

    @Override
    public void vaciarCarritoParaApi(Integer idUsuario) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        Carrito carrito = getOrCreateCarrito(usuario);

        carritoItemRepository.deleteByCarrito_IdCarrito(carrito.getIdCarrito());

        carrito.setFechaModificacion(LocalDateTime.now());
        carritoRepository.save(carrito);
    }

    @Override
    public void procesarCompraParaApi(Integer idUsuario) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        Carrito carrito = getOrCreateCarrito(usuario);

        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new BadRequestException(MessageKeys.CARRITO_VACIO);
        }

        // Copiar items para evitar problemas con la colección al eliminar
        List<CarritoItem> itemsAProcesar = new ArrayList<>(carrito.getItems());

        // Validar stock de todos los paquetes antes de procesar
        for (CarritoItem item : itemsAProcesar) {
            validarStockDisponible(item.getPaquete());
        }

        // Calcular total de la reserva
        BigDecimal totalReserva = BigDecimal.ZERO;
        for (CarritoItem item : itemsAProcesar) {
            totalReserva = totalReserva.add(calcularTotalItem(item));
        }

        // Crear una única reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setTotalPagar(totalReserva);
        reserva.setEstadoReserva("PENDIENTE");
        reserva.setEstado(AppConstants.STATUS_ACTIVO);
        reserva.setFechaCreacion(LocalDateTime.now());

        List<ReservaItem> reservaItems = new ArrayList<>();

        // Crear un ReservaItem por cada item del carrito
        for (CarritoItem carritoItem : itemsAProcesar) {
            Paquete paquete = carritoItem.getPaquete();
            BigDecimal subtotal = calcularTotalItem(carritoItem);

            ReservaItem reservaItem = new ReservaItem();
            reservaItem.setReserva(reserva);
            reservaItem.setPaquete(paquete);
            reservaItem.setFechaViajeInicio(carritoItem.getFechaViajeInicio());
            reservaItem.setSubtotal(subtotal);
            reservaItem.setFechaCreacion(LocalDateTime.now());

            // Crear servicios del item
            if (carritoItem.getServicios() != null) {
                List<ReservaItemServicio> serviciosReserva = new ArrayList<>();
                for (CarritoItemServicio cis : carritoItem.getServicios()) {
                    ReservaItemServicio ris = new ReservaItemServicio();
                    ris.setId(new ReservaItemServicioId(null, cis.getServicioAdicional().getIdServicio()));
                    ris.setReservaItem(reservaItem);
                    ris.setServicioAdicional(cis.getServicioAdicional());
                    ris.setCantidad(cis.getCantidad());
                    serviciosReserva.add(ris);
                }
                reservaItem.setServicios(serviciosReserva);
            }

            reservaItems.add(reservaItem);

            // Decrementar stock
            paquete.setStockDisponible(paquete.getStockDisponible() - 1);
            paqueteRepository.save(paquete);
        }

        reserva.setItems(reservaItems);
        reservaRepository.save(reserva);

        // Vaciar el carrito después de procesar
        carrito.getItems().clear();
        carrito.setFechaModificacion(LocalDateTime.now());
        carritoRepository.save(carrito);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer contarItemsParaApi(Integer idUsuario) {
        Integer count = carritoRepository.contarItemsPorUsuario(idUsuario);
        return count != null ? count : 0;
    }

    // ===========================================================
    // WEB MVC
    // ===========================================================

    @Override
    public CarritoView obtenerCarritoParaWeb(Integer idUsuario) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        Carrito carrito = getOrCreateCarrito(usuario);
        return carritoMapper.toView(carrito);
    }

    @Override
    public void agregarItemParaWeb(Integer idUsuario, CarritoItemRequest request) {
        Usuario usuario = getUsuarioOrThrow(idUsuario);
        Carrito carrito = getOrCreateCarrito(usuario);
        crearCarritoItem(carrito, request);
    }

    @Override
    public void eliminarItemParaWeb(Integer idUsuario, Integer idItem) {
        eliminarItemParaApi(idUsuario, idItem);
    }

    @Override
    public void vaciarCarritoParaWeb(Integer idUsuario) {
        vaciarCarritoParaApi(idUsuario);
    }

    @Override
    public void procesarCompraParaWeb(Integer idUsuario) {
        procesarCompraParaApi(idUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer contarItemsParaWeb(Integer idUsuario) {
        return contarItemsParaApi(idUsuario);
    }
}
