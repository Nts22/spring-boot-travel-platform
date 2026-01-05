package com.ptirado.nmviajes.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ptirado.nmviajes.dto.api.request.ReservaRequest;
import com.ptirado.nmviajes.dto.api.response.ReservaResponse;
import com.ptirado.nmviajes.dto.form.ReservaForm;
import com.ptirado.nmviajes.viewmodel.ReservaView;

/**
 * Servicio para la gestion de reservas de paquetes turisticos.
 *
 * <p>Este servicio maneja todas las operaciones relacionadas con las reservas,
 * incluyendo creacion, consulta, pago y cancelacion.</p>
 *
 * <h3>Arquitectura:</h3>
 * <ul>
 *   <li><b>Metodos *ParaApi:</b> Retornan DTOs (Response) para consumo via REST</li>
 *   <li><b>Metodos *ParaWeb:</b> Retornan ViewModels para renderizado con Thymeleaf</li>
 * </ul>
 *
 * <h3>Flujo de estados:</h3>
 * <pre>
 *   PENDIENTE ──► PAGADA (via confirmarPago)
 *       │
 *       └──────► CANCELADA (via cancelarReserva)
 * </pre>
 *
 * @author Sistema NMViajes
 * @see ReservaResponse
 * @see ReservaView
 */
public interface ReservaService {

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                              API REST                                      ║
    // ║  Metodos para consumo via API REST - Retornan DTOs (Response)             ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Lista todas las reservas del sistema.
     *
     * @return Lista de reservas en formato Response
     */
    List<ReservaResponse> listarParaApi();

    /**
     * Obtiene una reserva por su ID.
     *
     * @param id ID de la reserva
     * @return La reserva encontrada
     * @throws com.ptirado.nmviajes.exception.api.NotFoundException si no existe
     */
    ReservaResponse obtenerParaApi(Integer id);

    /**
     * Crea una nueva reserva desde una peticion API.
     *
     * @param request Datos de la reserva a crear
     * @return La reserva creada
     * @throws com.ptirado.nmviajes.exception.api.BadRequestException si no hay stock
     */
    ReservaResponse crearDesdeApi(ReservaRequest request);

    /**
     * Lista las reservas de un usuario especifico.
     *
     * @param idUsuario ID del usuario
     * @return Lista de reservas del usuario
     */
    List<ReservaResponse> listarPorUsuarioParaApi(Integer idUsuario);

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                              WEB MVC                                        ║
    // ║  Metodos para controladores web - Retornan ViewModels para Thymeleaf      ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Lista todas las reservas para mostrar en la vista web.
     *
     * @return Lista de reservas en formato ViewModel (con datos formateados)
     */
    List<ReservaView> listarParaWeb();

    /**
     * Lista todas las reservas de forma paginada para la vista web.
     *
     * @param pageable Configuracion de paginacion
     * @return Pagina de reservas en formato ViewModel
     */
    Page<ReservaView> listarParaWebPaginado(Pageable pageable);

    /**
     * Obtiene una reserva por su ID para mostrar en la vista web.
     *
     * @param id ID de la reserva
     * @return La reserva en formato ViewModel
     */
    ReservaView obtenerParaWeb(Integer id);

    /**
     * Crea una nueva reserva desde un formulario web.
     *
     * @param form Datos del formulario de reserva
     */
    void crearDesdeForm(ReservaForm form);

    /**
     * Lista las reservas de un usuario para la vista web.
     *
     * @param idUsuario ID del usuario
     * @return Lista de reservas del usuario en formato ViewModel
     */
    List<ReservaView> listarPorUsuarioParaWeb(Integer idUsuario);

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                        PAGO Y CANCELACION                                  ║
    // ║  Operaciones de cambio de estado de la reserva                            ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Confirma el pago de una reserva, cambiando su estado a PAGADA.
     *
     * <p>Una vez pagada, la reserva se considera finalizada y no puede
     * ser modificada ni cancelada.</p>
     *
     * @param idReserva ID de la reserva a pagar
     * @return La reserva actualizada
     * @throws com.ptirado.nmviajes.exception.api.NotFoundException si no existe
     * @throws com.ptirado.nmviajes.exception.api.BadRequestException si ya esta pagada/cancelada
     */
    ReservaResponse confirmarPago(Integer idReserva);

    /**
     * Cancela una reserva pendiente.
     *
     * <p>Solo se pueden cancelar reservas en estado PENDIENTE.</p>
     *
     * @param idReserva ID de la reserva a cancelar
     * @return La reserva actualizada
     * @throws com.ptirado.nmviajes.exception.api.NotFoundException si no existe
     * @throws com.ptirado.nmviajes.exception.api.BadRequestException si ya esta pagada
     */
    ReservaResponse cancelarReserva(Integer idReserva);
}
