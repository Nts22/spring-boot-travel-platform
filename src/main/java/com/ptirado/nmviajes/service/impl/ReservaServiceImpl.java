package com.ptirado.nmviajes.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ptirado.nmviajes.constants.MessageKeys;
import com.ptirado.nmviajes.dto.api.request.ReservaRequest;
import com.ptirado.nmviajes.dto.api.request.ServicioAdicionalItemRequest;
import com.ptirado.nmviajes.dto.api.response.ReservaResponse;
import com.ptirado.nmviajes.dto.form.ReservaForm;
import com.ptirado.nmviajes.entity.Paquete;
import com.ptirado.nmviajes.entity.Reserva;
import com.ptirado.nmviajes.entity.Reserva.EstadoReserva;
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

/**
 * Implementacion del servicio de reservas.
 *
 * <p>Maneja toda la logica de negocio relacionada con las reservas de paquetes turisticos,
 * incluyendo creacion, consulta, pago y cancelacion.</p>
 *
 * <h3>Flujo de estados de una reserva:</h3>
 * <pre>
 *   PENDIENTE ──► PAGADA (finalizada)
 *       │
 *       └──────► CANCELADA
 * </pre>
 *
 * <h3>Responsabilidades:</h3>
 * <ul>
 *   <li>Crear reservas desde API REST o formularios web</li>
 *   <li>Validar disponibilidad de stock</li>
 *   <li>Calcular totales incluyendo servicios adicionales</li>
 *   <li>Gestionar el ciclo de vida de la reserva (pago/cancelacion)</li>
 * </ul>
 *
 * @author Sistema NMViajes
 * @see ReservaService
 * @see Reserva
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaServiceImpl.class);

    private final ReservaRepository reservaRepository;
    private final PaqueteRepository paqueteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioAdicionalRepository servicioAdicionalRepository;
    private final ReservaMapper reservaMapper;

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                         BUSQUEDA DE ENTIDADES                              ║
    // ║  Metodos auxiliares para obtener entidades o lanzar excepciones           ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

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

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                         VALIDACIONES                                       ║
    // ║  Metodos de validacion de reglas de negocio                               ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Valida que el paquete tenga stock disponible.
     *
     * @param paquete El paquete a validar
     * @throws BadRequestException si no hay stock disponible
     */
    private void validarStockDisponible(Paquete paquete) {
        if (paquete.getStockDisponible() == null || paquete.getStockDisponible() <= 0) {
            log.warn("Stock insuficiente para paquete: {} (id={})", paquete.getNombre(), paquete.getIdPaquete());
            throw new BadRequestException(MessageKeys.STOCK_INSUFICIENTE, paquete.getNombre());
        }
    }

    /**
     * Valida que la reserva pueda ser modificada (no este pagada ni cancelada).
     *
     * @param reserva La reserva a validar
     * @throws BadRequestException si la reserva ya esta finalizada o cancelada
     */
    private void validarReservaModificable(Reserva reserva) {
        if (reserva.estaFinalizada()) {
            throw new BadRequestException(MessageKeys.RESERVA_YA_PAGADA, reserva.getIdReserva());
        }
        if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
            throw new BadRequestException(MessageKeys.RESERVA_CANCELADA, reserva.getIdReserva());
        }
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                    LOGICA DE CREACION DE RESERVAS                          ║
    // ║  Metodos internos para construir y persistir reservas                     ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Crea una reserva completa con todos sus componentes.
     *
     * <p>Este metodo centraliza la logica de creacion de reservas, evitando
     * duplicacion de codigo entre la API REST y el formulario web.</p>
     *
     * @param usuario Usuario que realiza la reserva
     * @param paquete Paquete turistico a reservar
     * @param fechaViajeInicio Fecha de inicio del viaje
     * @param serviciosAdicionales Lista de servicios adicionales (puede ser null)
     * @return La reserva creada y persistida
     */
    private Reserva crearReservaCompleta(Usuario usuario, Paquete paquete,
            LocalDate fechaViajeInicio, List<ServicioAdicionalItemRequest> serviciosAdicionales) {

        // 1. Calcular el subtotal (paquete + servicios adicionales)
        BigDecimal subtotalItem = calcularSubtotalItem(paquete, serviciosAdicionales);

        // 2. Construir la reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setTotalPagar(subtotalItem);
        reserva.setEstadoReserva(EstadoReserva.PENDIENTE);

        // 3. Construir el item de la reserva
        ReservaItem reservaItem = construirReservaItem(reserva, paquete, fechaViajeInicio,
                subtotalItem, serviciosAdicionales);

        // 4. Asociar item a la reserva
        List<ReservaItem> items = new ArrayList<>();
        items.add(reservaItem);
        reserva.setItems(items);

        // 5. Persistir la reserva
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // 6. Actualizar stock del paquete
        decrementarStock(paquete);

        log.info("Reserva creada exitosamente: id={}, usuario={}, paquete={}, total={}",
                reservaGuardada.getIdReserva(), usuario.getIdUsuario(),
                paquete.getIdPaquete(), subtotalItem);

        return reservaGuardada;
    }

    /**
     * Construye un item de reserva con sus servicios adicionales.
     */
    private ReservaItem construirReservaItem(Reserva reserva, Paquete paquete,
            LocalDate fechaViajeInicio, BigDecimal subtotal,
            List<ServicioAdicionalItemRequest> serviciosAdicionales) {

        ReservaItem reservaItem = new ReservaItem();
        reservaItem.setReserva(reserva);
        reservaItem.setPaquete(paquete);
        reservaItem.setFechaViajeInicio(fechaViajeInicio);
        reservaItem.setSubtotal(subtotal);

        // Agregar servicios adicionales si existen
        List<ReservaItemServicio> servicios = construirServiciosItem(reservaItem, serviciosAdicionales);
        reservaItem.setServicios(servicios);

        return reservaItem;
    }

    /**
     * Construye la lista de servicios adicionales para un item de reserva.
     */
    private List<ReservaItemServicio> construirServiciosItem(ReservaItem reservaItem,
            List<ServicioAdicionalItemRequest> serviciosRequest) {

        List<ReservaItemServicio> servicios = new ArrayList<>();

        if (serviciosRequest == null || serviciosRequest.isEmpty()) {
            return servicios;
        }

        for (ServicioAdicionalItemRequest item : serviciosRequest) {
            ServicioAdicional servicio = getServicioOrThrow(item.getIdServicio());

            ReservaItemServicio itemServicio = new ReservaItemServicio();
            itemServicio.setId(new ReservaItemServicioId(null, servicio.getIdServicio()));
            itemServicio.setReservaItem(reservaItem);
            itemServicio.setServicioAdicional(servicio);
            itemServicio.setCantidad(item.getCantidad());

            servicios.add(itemServicio);
        }

        return servicios;
    }

    /**
     * Calcula el subtotal de un item (precio del paquete + servicios adicionales).
     *
     * @param paquete El paquete base
     * @param serviciosRequest Los servicios adicionales seleccionados
     * @return El subtotal calculado
     */
    private BigDecimal calcularSubtotalItem(Paquete paquete,
            List<ServicioAdicionalItemRequest> serviciosRequest) {

        BigDecimal total = paquete.getPrecio();

        if (serviciosRequest == null || serviciosRequest.isEmpty()) {
            return total;
        }

        for (ServicioAdicionalItemRequest item : serviciosRequest) {
            ServicioAdicional servicio = getServicioOrThrow(item.getIdServicio());
            BigDecimal costoServicio = servicio.getCosto()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(costoServicio);
        }

        return total;
    }

    /**
     * Convierte los servicios seleccionados del formulario web al formato de request.
     */
    private List<ServicioAdicionalItemRequest> convertirServiciosDelForm(
            List<ReservaForm.ServicioSeleccionado> serviciosForm) {

        List<ServicioAdicionalItemRequest> serviciosRequest = new ArrayList<>();

        if (serviciosForm == null) {
            return serviciosRequest;
        }

        for (ReservaForm.ServicioSeleccionado ss : serviciosForm) {
            boolean esValido = ss.getIdServicio() != null
                    && ss.getCantidad() != null
                    && ss.getCantidad() > 0;

            if (esValido) {
                serviciosRequest.add(ServicioAdicionalItemRequest.builder()
                        .idServicio(ss.getIdServicio())
                        .cantidad(ss.getCantidad())
                        .build());
            }
        }

        return serviciosRequest;
    }

    /**
     * Decrementa el stock disponible de un paquete.
     */
    private void decrementarStock(Paquete paquete) {
        paquete.setStockDisponible(paquete.getStockDisponible() - 1);
        paqueteRepository.save(paquete);
        log.debug("Stock decrementado para paquete {}: nuevo stock={}",
                paquete.getIdPaquete(), paquete.getStockDisponible());
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                              API REST                                      ║
    // ║  Metodos expuestos para consumo via API REST (retornan Response/DTO)      ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

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
        log.info("Creando reserva desde API: usuario={}, paquete={}",
                request.getIdUsuario(), request.getIdPaquete());

        // Validar y obtener entidades
        Usuario usuario = getUsuarioOrThrow(request.getIdUsuario());
        Paquete paquete = getPaqueteOrThrow(request.getIdPaquete());
        validarStockDisponible(paquete);

        // Crear la reserva usando el metodo centralizado
        Reserva reserva = crearReservaCompleta(usuario, paquete,
                request.getFechaViajeInicio(), request.getServiciosAdicionales());

        return reservaMapper.toResponseFromEntity(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> listarPorUsuarioParaApi(Integer idUsuario) {
        getUsuarioOrThrow(idUsuario); // Valida que el usuario exista
        List<Reserva> reservas = reservaRepository.findByUsuario_IdUsuario(idUsuario);
        return reservaMapper.toResponseList(reservas);
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                              WEB MVC                                        ║
    // ║  Metodos para el controlador web (retornan ViewModels para Thymeleaf)     ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    @Override
    @Transactional(readOnly = true)
    public List<ReservaView> listarParaWeb() {
        return reservaMapper.toViewList(reservaRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaView> listarParaWebPaginado(Pageable pageable) {
        return reservaRepository.findAll(pageable).map(reservaMapper::toViewModelFromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaView obtenerParaWeb(Integer id) {
        return reservaMapper.toViewModelFromEntity(getReservaOrThrow(id));
    }

    @Override
    public void crearDesdeForm(ReservaForm form) {
        log.info("Creando reserva desde formulario web: usuario={}, paquete={}",
                form.getIdUsuario(), form.getIdPaquete());

        // Validar y obtener entidades
        Usuario usuario = getUsuarioOrThrow(form.getIdUsuario());
        Paquete paquete = getPaqueteOrThrow(form.getIdPaquete());
        validarStockDisponible(paquete);

        // Convertir servicios del formulario al formato estandar
        List<ServicioAdicionalItemRequest> servicios = convertirServiciosDelForm(
                form.getServiciosSeleccionados());

        // Crear la reserva usando el metodo centralizado
        crearReservaCompleta(usuario, paquete, form.getFechaViajeInicio(), servicios);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaView> listarPorUsuarioParaWeb(Integer idUsuario) {
        getUsuarioOrThrow(idUsuario); // Valida que el usuario exista
        List<Reserva> reservas = reservaRepository.findByUsuario_IdUsuario(idUsuario);
        return reservaMapper.toViewList(reservas);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaView> listarPorUsuarioParaWebPaginado(Integer idUsuario, Pageable pageable) {
        getUsuarioOrThrow(idUsuario); // Valida que el usuario exista
        return reservaRepository.findByUsuario_IdUsuario(idUsuario, pageable)
                .map(reservaMapper::toViewModelFromEntity);
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                        PAGO Y CANCELACION                                  ║
    // ║  Metodos para gestionar el ciclo de vida de la reserva                    ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Confirma el pago de una reserva, cambiando su estado a PAGADA.
     *
     * <p>Una vez pagada, la reserva se considera finalizada y no puede
     * ser modificada ni cancelada.</p>
     *
     * @param idReserva ID de la reserva a pagar
     * @return La reserva actualizada
     * @throws NotFoundException si la reserva no existe
     * @throws BadRequestException si la reserva ya esta pagada o cancelada
     */
    @Override
    public ReservaResponse confirmarPago(Integer idReserva) {
        log.info("Confirmando pago de reserva: id={}", idReserva);

        Reserva reserva = getReservaOrThrow(idReserva);
        validarReservaModificable(reserva);

        reserva.confirmarPago();
        Reserva reservaActualizada = reservaRepository.save(reserva);

        log.info("Pago confirmado exitosamente para reserva: id={}", idReserva);
        return reservaMapper.toResponseFromEntity(reservaActualizada);
    }

    /**
     * Cancela una reserva pendiente.
     *
     * <p>Solo se pueden cancelar reservas en estado PENDIENTE.
     * Las reservas pagadas no pueden cancelarse.</p>
     *
     * @param idReserva ID de la reserva a cancelar
     * @return La reserva actualizada
     * @throws NotFoundException si la reserva no existe
     * @throws BadRequestException si la reserva ya esta pagada
     */
    @Override
    public ReservaResponse cancelarReserva(Integer idReserva) {
        log.info("Cancelando reserva: id={}", idReserva);

        Reserva reserva = getReservaOrThrow(idReserva);

        if (reserva.estaFinalizada()) {
            throw new BadRequestException(MessageKeys.RESERVA_YA_PAGADA, idReserva);
        }

        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        Reserva reservaActualizada = reservaRepository.save(reserva);

        log.info("Reserva cancelada exitosamente: id={}", idReserva);
        return reservaMapper.toResponseFromEntity(reservaActualizada);
    }
}
