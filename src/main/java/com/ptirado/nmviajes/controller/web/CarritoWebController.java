package com.ptirado.nmviajes.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ptirado.nmviajes.dto.api.request.CarritoItemRequest;
import com.ptirado.nmviajes.dto.api.request.ServicioAdicionalItemRequest;
import com.ptirado.nmviajes.service.CarritoService;
import com.ptirado.nmviajes.service.PaqueteService;
import com.ptirado.nmviajes.service.ServicioAdicionalService;
import com.ptirado.nmviajes.viewmodel.CarritoView;
import com.ptirado.nmviajes.viewmodel.PaqueteView;
import com.ptirado.nmviajes.viewmodel.ServicioAdicionalView;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/carrito")
@RequiredArgsConstructor
public class CarritoWebController {

    private final CarritoService carritoService;
    private final PaqueteService paqueteService;
    private final ServicioAdicionalService servicioAdicionalService;

    // Usuario por defecto (en produccion vendria de la sesion)
    private static final Integer ID_USUARIO_DEFAULT = 1;

    @GetMapping
    public String verCarrito(Model model) {
        CarritoView carrito = carritoService.obtenerCarritoParaWeb(ID_USUARIO_DEFAULT);
        model.addAttribute("carrito", carrito);
        model.addAttribute("title", "Mi Carrito");
        model.addAttribute("content", "carrito/list");
        return "layout/main";
    }

    @GetMapping("/agregar/{idPaquete}")
    public String mostrarFormularioAgregar(@PathVariable Integer idPaquete, Model model) {
        PaqueteView paquete = paqueteService.obtenerParaWeb(idPaquete);
        List<ServicioAdicionalView> servicios = servicioAdicionalService.listarActivosParaWeb();

        model.addAttribute("paquete", paquete);
        model.addAttribute("serviciosAdicionales", servicios);
        model.addAttribute("title", "Agregar al Carrito - " + paquete.getNombre());
        model.addAttribute("content", "carrito/agregar");
        return "layout/main";
    }

    @PostMapping("/agregar")
    public String agregarItem(
            @RequestParam Integer idPaquete,
            @RequestParam String fechaViajeInicio,
            @RequestParam(required = false) List<Integer> servicioIds,
            @RequestParam(required = false) List<Integer> servicioCantidades,
            RedirectAttributes redirectAttributes) {

        try {
            CarritoItemRequest request = new CarritoItemRequest();
            request.setIdPaquete(idPaquete);
            request.setFechaViajeInicio(java.time.LocalDate.parse(fechaViajeInicio));

            // Procesar servicios adicionales
            if (servicioIds != null && servicioCantidades != null) {
                List<ServicioAdicionalItemRequest> servicios = new java.util.ArrayList<>();
                for (int i = 0; i < servicioIds.size(); i++) {
                    Integer cantidad = i < servicioCantidades.size() ? servicioCantidades.get(i) : 0;
                    if (cantidad != null && cantidad > 0) {
                        servicios.add(ServicioAdicionalItemRequest.builder()
                                .idServicio(servicioIds.get(i))
                                .cantidad(cantidad)
                                .build());
                    }
                }
                request.setServiciosAdicionales(servicios);
            }

            carritoService.agregarItemParaWeb(ID_USUARIO_DEFAULT, request);
            redirectAttributes.addFlashAttribute("successMessage", "Paquete agregado al carrito");
            return "redirect:/carrito";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/paquetes/" + idPaquete;
        }
    }

    @PostMapping("/eliminar/{idItem}")
    public String eliminarItem(@PathVariable Integer idItem, RedirectAttributes redirectAttributes) {
        try {
            carritoService.eliminarItemParaWeb(ID_USUARIO_DEFAULT, idItem);
            redirectAttributes.addFlashAttribute("successMessage", "Item eliminado del carrito");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/vaciar")
    public String vaciarCarrito(RedirectAttributes redirectAttributes) {
        try {
            carritoService.vaciarCarritoParaWeb(ID_USUARIO_DEFAULT);
            redirectAttributes.addFlashAttribute("successMessage", "Carrito vaciado");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/carrito";
    }

    @PostMapping("/checkout")
    public String procesarCompra(RedirectAttributes redirectAttributes) {
        try {
            carritoService.procesarCompraParaWeb(ID_USUARIO_DEFAULT);
            redirectAttributes.addFlashAttribute("successMessage",
                "Compra procesada exitosamente. Tus reservas han sido creadas.");
            return "redirect:/reservas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/carrito";
        }
    }
}
