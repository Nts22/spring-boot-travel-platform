package com.ptirado.nmviajes.controller.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ptirado.nmviajes.dto.form.ReservaForm;
import com.ptirado.nmviajes.exception.api.BadRequestException;
import com.ptirado.nmviajes.exception.api.NotFoundException;
import com.ptirado.nmviajes.service.PaqueteService;
import com.ptirado.nmviajes.service.ReservaService;
import com.ptirado.nmviajes.service.ServicioAdicionalService;
import com.ptirado.nmviajes.viewmodel.PaqueteView;
import com.ptirado.nmviajes.viewmodel.ReservaView;
import com.ptirado.nmviajes.viewmodel.ServicioAdicionalView;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador web para la gestion de reservas.
 *
 * <p>Maneja todas las vistas relacionadas con reservas usando Thymeleaf.</p>
 *
 * <h3>Rutas disponibles:</h3>
 * <table border="1">
 *   <tr><th>Metodo</th><th>Ruta</th><th>Descripcion</th></tr>
 *   <tr><td>GET</td><td>/reservas</td><td>Lista todas las reservas</td></tr>
 *   <tr><td>GET</td><td>/reservas/{id}</td><td>Detalle de una reserva</td></tr>
 *   <tr><td>GET</td><td>/reservas/nueva/{idPaquete}</td><td>Formulario de nueva reserva</td></tr>
 *   <tr><td>POST</td><td>/reservas/nueva</td><td>Procesar formulario de reserva</td></tr>
 *   <tr><td>POST</td><td>/reservas/{id}/pagar</td><td>Confirmar pago de reserva</td></tr>
 *   <tr><td>POST</td><td>/reservas/{id}/cancelar</td><td>Cancelar reserva</td></tr>
 * </table>
 *
 * @author Sistema NMViajes
 */
@Controller
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaWebController {

    private static final Logger log = LoggerFactory.getLogger(ReservaWebController.class);

    // TODO: En produccion, obtener el ID del usuario autenticado desde Spring Security
    // Ejemplo: @AuthenticationPrincipal UserDetails userDetails
    private static final Integer USUARIO_POR_DEFECTO = 1;

    private final ReservaService reservaService;
    private final PaqueteService paqueteService;
    private final ServicioAdicionalService servicioAdicionalService;

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                           VISTAS DE CONSULTA                               ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Muestra la lista de todas las reservas.
     *
     * @param model Modelo para la vista
     * @return Vista de lista de reservas
     */
    @GetMapping
    public String listar(Model model) {
        List<ReservaView> reservas = reservaService.listarParaWeb();
        model.addAttribute("reservas", reservas);
        model.addAttribute("title", "Mis Reservas");
        model.addAttribute("content", "reserva/list");
        return "layout/main";
    }

    /**
     * Muestra el detalle de una reserva.
     *
     * <p>Desde esta vista el usuario puede pagar o cancelar la reserva
     * si esta en estado PENDIENTE.</p>
     *
     * @param id ID de la reserva
     * @param model Modelo para la vista
     * @return Vista de detalle de reserva
     */
    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        ReservaView reserva = reservaService.obtenerParaWeb(id);
        model.addAttribute("reserva", reserva);
        model.addAttribute("title", "Detalle de Reserva #" + id);
        model.addAttribute("content", "reserva/detail");
        return "layout/main";
    }

    /**
     * Lista las reservas de un usuario especifico.
     *
     * @param idUsuario ID del usuario
     * @param model Modelo para la vista
     * @return Vista de lista de reservas del usuario
     */
    @GetMapping("/usuario/{idUsuario}")
    public String listarPorUsuario(@PathVariable Integer idUsuario, Model model) {
        List<ReservaView> reservas = reservaService.listarPorUsuarioParaWeb(idUsuario);
        model.addAttribute("reservas", reservas);
        model.addAttribute("title", "Mis Reservas");
        model.addAttribute("content", "reserva/list");
        return "layout/main";
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                        CREACION DE RESERVAS                                ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Muestra el formulario para crear una nueva reserva.
     *
     * @param idPaquete ID del paquete a reservar
     * @param model Modelo para la vista
     * @return Vista del formulario de reserva
     */
    @GetMapping("/nueva/{idPaquete}")
    public String mostrarFormulario(@PathVariable Integer idPaquete, Model model) {
        PaqueteView paquete = paqueteService.obtenerParaWeb(idPaquete);
        List<ServicioAdicionalView> servicios = servicioAdicionalService.listarActivosParaWeb();

        ReservaForm form = ReservaForm.builder()
                .idPaquete(idPaquete)
                .idUsuario(USUARIO_POR_DEFECTO)
                .build();

        model.addAttribute("reservaForm", form);
        model.addAttribute("paquete", paquete);
        model.addAttribute("serviciosAdicionales", servicios);
        model.addAttribute("title", "Nueva Reserva - " + paquete.getNombre());
        model.addAttribute("content", "reserva/form");
        return "layout/main";
    }

    /**
     * Procesa el formulario de nueva reserva.
     *
     * @param form Datos del formulario
     * @param bindingResult Resultado de validaciones
     * @param redirectAttributes Atributos para redirecciones
     * @param model Modelo para la vista
     * @return Redireccion a lista de reservas o formulario con errores
     */
    @PostMapping("/nueva")
    public String procesarFormulario(
            @Valid @ModelAttribute("reservaForm") ReservaForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Si hay errores de validacion, volver a mostrar el formulario
        if (bindingResult.hasErrors()) {
            return prepararVistaFormularioConErrores(form, model);
        }

        try {
            reservaService.crearDesdeForm(form);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva creada exitosamente");
            return "redirect:/reservas";

        } catch (BadRequestException | NotFoundException e) {
            // Errores de negocio conocidos (stock insuficiente, entidad no encontrada)
            log.warn("Error de negocio al crear reserva: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reservas/nueva/" + form.getIdPaquete();

        } catch (Exception e) {
            // Errores inesperados
            log.error("Error inesperado al crear reserva", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ocurrio un error al procesar su reserva. Por favor intente nuevamente.");
            return "redirect:/reservas/nueva/" + form.getIdPaquete();
        }
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                         PAGO Y CANCELACION                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Procesa el pago de una reserva.
     *
     * <p>Cambia el estado de la reserva de PENDIENTE a PAGADA.</p>
     *
     * @param id ID de la reserva a pagar
     * @param redirectAttributes Atributos para redirecciones
     * @return Redireccion al detalle de la reserva
     */
    @PostMapping("/{id}/pagar")
    public String pagar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.confirmarPago(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Pago realizado exitosamente. Su reserva ha sido confirmada.");
            log.info("Pago procesado exitosamente para reserva: id={}", id);

        } catch (BadRequestException e) {
            // Reserva ya pagada o cancelada
            log.warn("No se pudo procesar pago para reserva {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        } catch (NotFoundException e) {
            // Reserva no existe
            log.warn("Reserva no encontrada al intentar pagar: id={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado al procesar pago de reserva: id={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ocurrio un error al procesar el pago. Por favor intente nuevamente.");
        }

        return "redirect:/reservas/" + id;
    }

    /**
     * Cancela una reserva pendiente.
     *
     * <p>Cambia el estado de la reserva de PENDIENTE a CANCELADA.</p>
     *
     * @param id ID de la reserva a cancelar
     * @param redirectAttributes Atributos para redirecciones
     * @return Redireccion al detalle de la reserva
     */
    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.cancelarReserva(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva cancelada exitosamente.");
            log.info("Reserva cancelada exitosamente: id={}", id);

        } catch (BadRequestException e) {
            // Reserva ya pagada (no se puede cancelar)
            log.warn("No se pudo cancelar reserva {}: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        } catch (NotFoundException e) {
            // Reserva no existe
            log.warn("Reserva no encontrada al intentar cancelar: id={}", id);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        } catch (Exception e) {
            log.error("Error inesperado al cancelar reserva: id={}", id, e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ocurrio un error al cancelar la reserva. Por favor intente nuevamente.");
        }

        return "redirect:/reservas/" + id;
    }

    // ╔═══════════════════════════════════════════════════════════════════════════╗
    // ║                         METODOS AUXILIARES                                 ║
    // ╚═══════════════════════════════════════════════════════════════════════════╝

    /**
     * Prepara la vista del formulario cuando hay errores de validacion.
     */
    private String prepararVistaFormularioConErrores(ReservaForm form, Model model) {
        PaqueteView paquete = paqueteService.obtenerParaWeb(form.getIdPaquete());
        List<ServicioAdicionalView> servicios = servicioAdicionalService.listarActivosParaWeb();

        model.addAttribute("paquete", paquete);
        model.addAttribute("serviciosAdicionales", servicios);
        model.addAttribute("title", "Nueva Reserva - " + paquete.getNombre());
        model.addAttribute("content", "reserva/form");

        return "layout/main";
    }
}
