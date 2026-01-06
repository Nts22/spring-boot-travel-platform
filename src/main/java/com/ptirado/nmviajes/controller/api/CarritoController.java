package com.ptirado.nmviajes.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ptirado.nmviajes.constants.ApiPaths;
import com.ptirado.nmviajes.dto.api.request.CarritoItemRequest;
import com.ptirado.nmviajes.dto.api.response.CarritoResponse;
import com.ptirado.nmviajes.security.CustomUserDetails;
import com.ptirado.nmviajes.service.CarritoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.CARRITO)
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public ResponseEntity<CarritoResponse> obtenerCarrito(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(carritoService.obtenerCarritoParaApi(userDetails.getIdUsuario()));
    }

    @PostMapping(ApiPaths.CARRITO_ITEMS)
    public ResponseEntity<CarritoResponse> agregarItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CarritoItemRequest request) {
        CarritoResponse response = carritoService.agregarItemParaApi(userDetails.getIdUsuario(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping(ApiPaths.CARRITO_ITEM_ID)
    public ResponseEntity<CarritoResponse> eliminarItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Integer idItem) {
        return ResponseEntity.ok(carritoService.eliminarItemParaApi(userDetails.getIdUsuario(), idItem));
    }

    @DeleteMapping(ApiPaths.CARRITO_VACIAR)
    public ResponseEntity<Void> vaciarCarrito(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        carritoService.vaciarCarritoParaApi(userDetails.getIdUsuario());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(ApiPaths.CARRITO_CHECKOUT)
    public ResponseEntity<Void> procesarCompra(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        carritoService.procesarCompraParaApi(userDetails.getIdUsuario());
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiPaths.CARRITO_CONTAR)
    public ResponseEntity<Integer> contarItems(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) Integer idUsuario) {
        // Si no hay usuario autenticado, usar el parámetro (para compatibilidad)
        Integer id = userDetails != null ? userDetails.getIdUsuario() : idUsuario;
        if (id == null) {
            return ResponseEntity.ok(0);
        }
        return ResponseEntity.ok(carritoService.contarItemsParaApi(id));
    }
}
