package com.ptirado.nmviajes.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ptirado.nmviajes.constants.MessageKeys;
import com.ptirado.nmviajes.entity.Reserva.EstadoReserva;
import com.ptirado.nmviajes.dto.api.request.ReservaRequest;
import com.ptirado.nmviajes.dto.api.request.ServicioAdicionalItemRequest;
import com.ptirado.nmviajes.dto.api.response.ReservaResponse;
import com.ptirado.nmviajes.dto.form.ReservaForm;
import com.ptirado.nmviajes.entity.Paquete;
import com.ptirado.nmviajes.entity.Reserva;
import com.ptirado.nmviajes.entity.ReservaItem;
import com.ptirado.nmviajes.entity.ReservaItemServicio;
import com.ptirado.nmviajes.entity.ReservaItemServicioId;
import com.ptirado.nmviajes.entity.ServicioAdicional;
import com.ptirado.nmviajes.entity.Usuario;
import com.ptirado.nmviajes.exception.api.BadRequestException;
import com.ptirado.nmviajes.exception.api.NotFoundException;
import com.ptirado.nmviajes.mapper.ReservaMapper;
import com.ptirado.nmviajes.repository.PaqueteRepository;
import com.ptirado.nmviajes.repository.ReservaRepository;
import com.ptirado.nmviajes.repository.ServicioAdicionalRepository;
import com.ptirado.nmviajes.repository.UsuarioRepository;
import com.ptirado.nmviajes.service.ReservaService;
import com.ptirado.nmviajes.viewmodel.ReservaView;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository reservaRepository;
    private final PaqueteRepository paqueteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioAdicionalRepository servicioAdicionalRepository;
    private final ReservaMapper reservaMapper;

    // ===========================================================
    // UTILIDAD INTERNA
    // ===========================================================

    private Reserva getReservaOrThrow(Integer id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(MessageKeys.RESERVA_NOT_FOUND, id));
    }

    private Paquete getPaqueteOrThrow(Integer idPaquete) {
        return paqueteRepository.findById(idPaquete)
                .orElseThrow(() -> new NotFoundException(MessageKeys.PAQUETE_NOT_FOUND, idPaquete));
    }

    private Usuario getUsuarioOrThrow(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new NotFoundException(MessageKeys.USUARIO_NOT_FOUND, idUsuario));
    }

    private ServicioAdicional getServicioOrThrow(Integer idServicio) {
        return servicioAdicionalRepository.findById(idServicio)
                .orElseThrow(() -> new NotFoundException(MessageKeys.SERVICIO_NOT_FOUND, idServicio));
    }

    private void validarStockDisponible(Paquete paquete) {
        if (paquete.getStockDisponible() == null || paquete.getStockDisponible() <= 0) {
            throw new BadRequestException(MessageKeys.STOCK_INSUFICIENTE, paquete.getNombre());
        }
    }

    private void decrementarStock(Paquete paquete) {
        paquete.setStockDisponible(paquete.getStockDisponible() - 1);
        paqueteRepository.save(paquete);
    }

    private BigDecimal calcularSubtotalItem(Paquete paquete,
            List<ServicioAdicionalItemRequest> serviciosRequest) {

        BigDecimal total = paquete.getPrecio();

        if (serviciosRequest != null && !serviciosRequest.isEmpty()) {
            for (ServicioAdicionalItemRequest item : serviciosRequest) {
                ServicioAdicional servicio = getServicioOrThrow(item.getIdServicio());
                BigDecimal subtotal = servicio.getCosto()
                        .multiply(BigDecimal.valueOf(item.getCantidad()));
                total = total.add(subtotal);
            }
        }

        return total;
    }

    private List<ReservaItemServicio> crearServiciosItem(ReservaItem reservaItem,
            List<ServicioAdicionalItemRequest> serviciosRequest) {

        List<ReservaItemServicio> servicios = new ArrayList<>();

        if (serviciosRequest != null && !serviciosRequest.isEmpty()) {
            for (ServicioAdicionalItemRequest item : serviciosRequest) {
                ServicioAdicional servicio = getServicioOrThrow(item.getIdServicio());

                ReservaItemServicio ris = new ReservaItemServicio();
                ris.setId(new ReservaItemServicioId(null, servicio.getIdServicio()));
                ris.setReservaItem(reservaItem);
                ris.setServicioAdicional(servicio);
                ris.setCantidad(item.getCantidad());

                servicios.add(ris);
            }
        }

        return servicios;
    }

    // ===========================================================
    // API REST
    // ===========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> listarParaApi() {
        return reservaMapper.toResponseList(reservaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponse obtenerParaApi(Integer id) {
        return reservaMapper.toResponseFromEntity(getReservaOrThrow(id));
    }

    @Override
    public ReservaResponse crearDesdeApi(ReservaRequest request) {
        // 1. Validar existencia de entidades relacionadas
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());
        Paquete paquete = getPaqueteOrThrow(request.getIdPaquete());

        // 2. Validar stock disponible
        validarStockDisponible(paquete);

        // 3. Calcular subtotal del item
        BigDecimal subtotalItem = calcularSubtotalItem(paquete, request.getServiciosAdicionales());

        // 4. Crear la reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setTotalPagar(subtotalItem);
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);

        // 5. Crear el item de la reserva
        ReservaItem reservaItem = new ReservaItem();
        reservaItem.setReserva(reserva);
        reservaItem.setPaquete(paquete);
        reservaItem.setFechaViajeInicio(request.getFechaViajeInicio());
        reservaItem.setSubtotal(subtotalItem);

        // 6. Crear los servicios del item
        List<ReservaItemServicio> servicios = crearServiciosItem(reservaItem, request.getServiciosAdicionales());
        reservaItem.setServicios(servicios);

        // 7. Agregar item a la reserva
        List<ReservaItem> items = new ArrayList<>();
        items.add(reservaItem);
        reserva.setItems(items);

        // 8. Guardar la reserva
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // 9. Decrementar stock del paquete
        decrementarStock(paquete);

        return reservaMapper.toResponseFromEntity(reservaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorUsuarioParaApi(Integer idUsuario) {
        getUsuarioOrThrow(idUsuario);
        List<Reserva> reservas = reservaRepository.findByUsuario_IdUsuario(idUsuario);
        return reservaMapper.toResponseList(reservas);
    }

    // ===========================================================
    // WEB MVC
    // ===========================================================

    @Override
    @Transactional(readOnly = true)
    public List<ReservaView> listarParaWeb() {
        return reservaMapper.toViewList(reservaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaView obtenerParaWeb(Integer id) {
        return reservaMapper.toViewModelFromEntity(getReservaOrThrow(id));
    }

    @Override
    public void crearDesdeForm(ReservaForm form) {
        // 1. Validar existencia de entidades relacionadas
        Usuario usuario = getUsuarioOrThrow(form.getIdUsuario());
        Paquete paquete = getPaqueteOrThrow(form.getIdPaquete());

        // 2. Validar stock disponible
        validarStockDisponible(paquete);

        // 3. Convertir servicios del form a request format
        List<ServicioAdicionalItemRequest> serviciosRequest = new ArrayList<>();
        if (form.getServiciosSeleccionados() != null) {
            for (ReservaForm.ServicioSeleccionado ss : form.getServiciosSeleccionados()) {
                if (ss.getIdServicio() != null && ss.getCantidad() != null && ss.getCantidad() > 0) {
                    serviciosRequest.add(ServicioAdicionalItemRequest.builder()
                            .idServicio(ss.getIdServicio())
                            .cantidad(ss.getCantidad())
                            .build());
                }
            }
        }

        // 4. Calcular subtotal del item
        BigDecimal subtotalItem = calcularSubtotalItem(paquete, serviciosRequest);

        // 5. Crear la reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setTotalPagar(subtotalItem);
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);

        // 6. Crear el item de la reserva
        ReservaItem reservaItem = new ReservaItem();
        reservaItem.setReserva(reserva);
        reservaItem.setPaquete(paquete);
        reservaItem.setFechaViajeInicio(form.getFechaViajeInicio());
        reservaItem.setSubtotal(subtotalItem);

        // 7. Crear los servicios del item
        List<ReservaItemServicio> servicios = crearServiciosItem(reservaItem, serviciosRequest);
        reservaItem.setServicios(servicios);

        // 8. Agregar item a la reserva
        List<ReservaItem> items = new ArrayList<>();
        items.add(reservaItem);
        reserva.setItems(items);

        // 9. Guardar la reserva
        reservaRepository.save(reserva);

        // 10. Decrementar stock del paquete
        decrementarStock(paquete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaView> listarPorUsuarioParaWeb(Integer idUsuario) {
        getUsuarioOrThrow(idUsuario);
        List<Reserva> reservas = reservaRepository.findByUsuario_IdUsuario(idUsuario);
        return reservaMapper.toViewList(reservas);
    }

    // ===========================================================
    // PAGO / FINALIZACIÓN
    // ===========================================================

    @Override
    public ReservaResponse confirmarPago(Integer idReserva) {
        Reserva reserva = getReservaOrThrow(idReserva);

        if (reserva.estaFinalizada()) {
            throw new BadRequestException(MessageKeys.RESERVA_YA_PAGADA, idReserva);
        }

        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new BadRequestException(MessageKeys.RESERVA_CANCELADA, idReserva);
        }

        reserva.confirmarPago();
        Reserva reservaActualizada = reservaRepository.save(reserva);

        return reservaMapper.toResponseFromEntity(reservaActualizada);
    }

    @Override
    public ReservaResponse cancelarReserva(Integer idReserva) {
        Reserva reserva = getReservaOrThrow(idReserva);

        if (reserva.estaFinalizada()) {
            throw new BadRequestException(MessageKeys.RESERVA_YA_PAGADA, idReserva);
        }

        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        Reserva reservaActualizada = reservaRepository.save(reserva);

        return reservaMapper.toResponseFromEntity(reservaActualizada);
    }

    @Override
    public void pagarParaWeb(Integer idReserva) {
        confirmarPago(idReserva);
    }

    @Override
    public void cancelarParaWeb(Integer idReserva) {
        cancelarReserva(idReserva);
    }
}
