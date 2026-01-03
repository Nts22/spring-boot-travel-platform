package com.ptirado.nmviajes.controller.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.ptirado.nmviajes.service.CarritoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(ApiPaths.CARRITO)
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public ResponseEntity<CarritoResponse> obtenerCarrito(@RequestParam Integer idUsuario) {
        return ResponseEntity.ok(carritoService.obtenerCarritoParaApi(idUsuario));
    }

    @PostMapping(ApiPaths.CARRITO_ITEMS)
    public ResponseEntity<CarritoResponse> agregarItem(
            @RequestParam Integer idUsuario,
            @Valid @RequestBody CarritoItemRequest request) {
        CarritoResponse response = carritoService.agregarItemParaApi(idUsuario, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping(ApiPaths.CARRITO_ITEM_ID)
    public ResponseEntity<CarritoResponse> eliminarItem(
            @RequestParam Integer idUsuario,
            @PathVariable Integer idItem) {
        return ResponseEntity.ok(carritoService.eliminarItemParaApi(idUsuario, idItem));
    }

    @DeleteMapping(ApiPaths.CARRITO_VACIAR)
    public ResponseEntity<Void> vaciarCarrito(@RequestParam Integer idUsuario) {
        carritoService.vaciarCarritoParaApi(idUsuario);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(ApiPaths.CARRITO_CHECKOUT)
    public ResponseEntity<Void> procesarCompra(@RequestParam Integer idUsuario) {
        carritoService.procesarCompraParaApi(idUsuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiPaths.CARRITO_CONTAR)
    public ResponseEntity<Integer> contarItems(@RequestParam Integer idUsuario) {
        return ResponseEntity.ok(carritoService.contarItemsParaApi(idUsuario));
    }
}
