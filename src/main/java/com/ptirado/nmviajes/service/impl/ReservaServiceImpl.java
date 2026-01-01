package com.ptirado.nmviajes.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ptirado.nmviajes.constants.AppConstants;
import com.ptirado.nmviajes.constants.MessageKeys;
import com.ptirado.nmviajes.dto.api.request.ReservaRequest;
import com.ptirado.nmviajes.dto.api.request.ServicioAdicionalItemRequest;
import com.ptirado.nmviajes.dto.api.response.ReservaResponse;
import com.ptirado.nmviajes.dto.form.ReservaForm;
import com.ptirado.nmviajes.entity.Paquete;
import com.ptirado.nmviajes.entity.Reserva;
import com.ptirado.nmviajes.entity.ReservaServicio;
import com.ptirado.nmviajes.entity.ReservaServicioId;
import com.ptirado.nmviajes.entity.ServicioAdicional;
import com.ptirado.nmviajes.entity.Usuario;
import com.ptirado.nmviajes.exception.api.BadRequestException;
import com.ptirado.nmviajes.exception.api.NotFoundException;
import com.ptirado.nmviajes.mapper.ReservaMapper;
import com.ptirado.nmviajes.repository.PaqueteRepository;
import com.ptirado.nmviajes.repository.ReservaRepository;
import com.ptirado.nmviajes.repository.ReservaServicioRepository;
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
    private final ReservaServicioRepository reservaServicioRepository;
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

    private BigDecimal calcularTotalPagar(Paquete paquete,
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

    private List<ReservaServicio> crearReservasServicios(Reserva reserva,
            List<ServicioAdicionalItemRequest> serviciosRequest) {

        List<ReservaServicio> reservasServicios = new ArrayList<>();

        if (serviciosRequest != null && !serviciosRequest.isEmpty()) {
            for (ServicioAdicionalItemRequest item : serviciosRequest) {
                ServicioAdicional servicio = getServicioOrThrow(item.getIdServicio());

                ReservaServicio rs = new ReservaServicio();
                rs.setId(new ReservaServicioId(reserva.getIdReserva(), servicio.getIdServicio()));
                rs.setReserva(reserva);
                rs.setServicioAdicional(servicio);
                rs.setCantidad(item.getCantidad());

                reservasServicios.add(reservaServicioRepository.save(rs));
            }
        }

        return reservasServicios;
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

        // 3. Calcular total a pagar
        BigDecimal totalPagar = calcularTotalPagar(paquete, request.getServiciosAdicionales());

        // 4. Crear la reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setPaquete(paquete);
        reserva.setFechaViajeInicio(request.getFechaViajeInicio());
        reserva.setTotalPagar(totalPagar);
        reserva.setEstadoReserva("PENDIENTE");
        reserva.setEstado(AppConstants.STATUS_ACTIVO);
        reserva.setFechaCreacion(LocalDateTime.now());

        Reserva reservaGuardada = reservaRepository.save(reserva);

        // 5. Crear los servicios adicionales de la reserva
        List<ReservaServicio> servicios = crearReservasServicios(
            reservaGuardada, request.getServiciosAdicionales());
        reservaGuardada.setReservasServicios(servicios);

        // 6. Decrementar stock del paquete
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

        // 4. Calcular total a pagar
        BigDecimal totalPagar = calcularTotalPagar(paquete, serviciosRequest);

        // 5. Crear la reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setPaquete(paquete);
        reserva.setFechaViajeInicio(form.getFechaViajeInicio());
        reserva.setTotalPagar(totalPagar);
        reserva.setEstadoReserva("PENDIENTE");
        reserva.setEstado(AppConstants.STATUS_ACTIVO);
        reserva.setFechaCreacion(LocalDateTime.now());

        Reserva reservaGuardada = reservaRepository.save(reserva);

        // 6. Crear los servicios adicionales de la reserva
        crearReservasServicios(reservaGuardada, serviciosRequest);

        // 7. Decrementar stock del paquete
        decrementarStock(paquete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaView> listarPorUsuarioParaWeb(Integer idUsuario) {
        getUsuarioOrThrow(idUsuario);
        List<Reserva> reservas = reservaRepository.findByUsuario_IdUsuario(idUsuario);
        return reservaMapper.toViewList(reservas);
    }
}
