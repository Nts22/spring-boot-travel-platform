package com.ptirado.nmviajes.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ptirado.nmviajes.dto.api.request.RegistroRequest;
import com.ptirado.nmviajes.service.AuthService;

import jakarta.validation.Valid;

@Controller
public class AuthWebController {

    private final AuthService authService;

    public AuthWebController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("title", "Iniciar Sesion");
        model.addAttribute("content", "auth/login");
        return "layout/main";
    }

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("title", "Registro");
        model.addAttribute("content", "auth/registro");
        model.addAttribute("registroRequest", new RegistroRequest());
        return "layout/main";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registroRequest") RegistroRequest request,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("title", "Registro");
            model.addAttribute("content", "auth/registro");
            return "layout/main";
        }

        if (authService.existeEmail(request.getEmail())) {
            result.rejectValue("email", "error.email", "El email ya esta registrado");
            model.addAttribute("title", "Registro");
            model.addAttribute("content", "auth/registro");
            return "layout/main";
        }

        authService.registrar(request);
        redirectAttributes.addFlashAttribute("mensaje", "Registro exitoso. Por favor inicia sesion.");
        return "redirect:/login";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado(Model model) {
        model.addAttribute("title", "Acceso Denegado");
        model.addAttribute("content", "auth/acceso-denegado");
        return "layout/main";
    }
}
