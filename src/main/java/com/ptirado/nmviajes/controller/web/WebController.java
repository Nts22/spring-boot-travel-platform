package com.ptirado.nmviajes.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class WebController {

    // HOME
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Inicio");
        model.addAttribute("content", "home/index");
        return "layout/main";
    }

    // LISTADO DE PAQUETES
    @GetMapping("/paquetes")
    public String paquetes(Model model) {
        model.addAttribute("title", "Paquetes");
        model.addAttribute("content", "paquete/list");
        return "layout/main";
    }

    // DETALLE DE PAQUETE
    @GetMapping("/paquetes/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("title", "Detalle Paquete");
        model.addAttribute("content", "paquete/detail");
        return "layout/main";
    }
}