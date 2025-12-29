package com.ptirado.nmviajes.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ptirado.nmviajes.service.DestinoService;
import com.ptirado.nmviajes.service.PaqueteService;
import com.ptirado.nmviajes.viewmodel.DestinoView;
import com.ptirado.nmviajes.viewmodel.PaqueteView;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/paquetes")
@RequiredArgsConstructor
public class PaqueteWebController {

    private final PaqueteService paqueteService;
    private final DestinoService destinoService;

    // LISTADO DE PAQUETES
    @GetMapping
    public String listar(Model model) {
        List<PaqueteView> paquetes = paqueteService.listarParaWeb();
        List<DestinoView> destinos = destinoService.listarParaWeb();
        model.addAttribute("paquetes", paquetes);
        model.addAttribute("destinos", destinos);
        model.addAttribute("title", "Paquetes");
        model.addAttribute("content", "paquete/list");
        return "layout/main";
    }

    // DETALLE DE PAQUETE
    @GetMapping("/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        PaqueteView paquete = paqueteService.obtenerParaWeb(id);
        model.addAttribute("paquete", paquete);
        model.addAttribute("title", paquete.getNombre());
        model.addAttribute("content", "paquete/detail");
        return "layout/main";
    }
}