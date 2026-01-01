package com.ptirado.nmviajes.controller.web;

import java.util.List;

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
import com.ptirado.nmviajes.service.PaqueteService;
import com.ptirado.nmviajes.service.ReservaService;
import com.ptirado.nmviajes.service.ServicioAdicionalService;
import com.ptirado.nmviajes.viewmodel.PaqueteView;
import com.ptirado.nmviajes.viewmodel.ReservaView;
import com.ptirado.nmviajes.viewmodel.ServicioAdicionalView;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaWebController {

    private final ReservaService reservaService;
    private final PaqueteService paqueteService;
    private final ServicioAdicionalService servicioAdicionalService;

    @GetMapping
    public String listar(Model model) {
        List<ReservaView> reservas = reservaService.listarParaWeb();
        model.addAttribute("reservas", reservas);
        model.addAttribute("title", "Mis Reservas");
        model.addAttribute("content", "reserva/list");
        return "layout/main";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        ReservaView reserva = reservaService.obtenerParaWeb(id);
        model.addAttribute("reserva", reserva);
        model.addAttribute("title", "Detalle de Reserva #" + id);
        model.addAttribute("content", "reserva/detail");
        return "layout/main";
    }

    @GetMapping("/nueva/{idPaquete}")
    public String mostrarFormulario(@PathVariable Integer idPaquete, Model model) {
        PaqueteView paquete = paqueteService.obtenerParaWeb(idPaquete);
        List<ServicioAdicionalView> servicios = servicioAdicionalService.listarActivosParaWeb();

        ReservaForm form = ReservaForm.builder()
                .idPaquete(idPaquete)
                .idUsuario(1) // Usuario por defecto (en produccion vendria de la sesion)
                .build();

        model.addAttribute("reservaForm", form);
        model.addAttribute("paquete", paquete);
        model.addAttribute("serviciosAdicionales", servicios);
        model.addAttribute("title", "Nueva Reserva - " + paquete.getNombre());
        model.addAttribute("content", "reserva/form");
        return "layout/main";
    }

    @PostMapping("/nueva")
    public String procesarFormulario(
            @Valid @ModelAttribute("reservaForm") ReservaForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            PaqueteView paquete = paqueteService.obtenerParaWeb(form.getIdPaquete());
            List<ServicioAdicionalView> servicios = servicioAdicionalService.listarActivosParaWeb();
            model.addAttribute("paquete", paquete);
            model.addAttribute("serviciosAdicionales", servicios);
            model.addAttribute("title", "Nueva Reserva - " + paquete.getNombre());
            model.addAttribute("content", "reserva/form");
            return "layout/main";
        }

        try {
            reservaService.crearDesdeForm(form);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva creada exitosamente");
            return "redirect:/reservas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reservas/nueva/" + form.getIdPaquete();
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public String listarPorUsuario(@PathVariable Integer idUsuario, Model model) {
        List<ReservaView> reservas = reservaService.listarPorUsuarioParaWeb(idUsuario);
        model.addAttribute("reservas", reservas);
        model.addAttribute("title", "Mis Reservas");
        model.addAttribute("content", "reserva/list");
        return "layout/main";
    }
}
