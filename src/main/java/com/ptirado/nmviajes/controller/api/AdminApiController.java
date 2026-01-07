package com.ptirado.nmviajes.controller.api;

import com.ptirado.nmviajes.entity.*;
import com.ptirado.nmviajes.entity.Reserva.EstadoReserva;
import com.ptirado.nmviajes.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminApiController {

    private final UsuarioRepository usuarioRepository;
    private final DestinoRepository destinoRepository;
    private final PaqueteRepository paqueteRepository;
    private final ServicioAdicionalRepository servicioRepository;
    private final ReservaRepository reservaRepository;

    // ==================== USUARIOS ====================

    @PatchMapping("/usuarios/{id}/estado")
    public ResponseEntity<?> cambiarEstadoUsuario(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String nuevoEstado = body.get("estado");
        usuario.setEstado(nuevoEstado);
        usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of("message", "Estado actualizado correctamente"));
    }

    // ==================== DESTINOS ====================

    @PatchMapping("/destinos/{id}/estado")
    public ResponseEntity<?> cambiarEstadoDestino(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Destino destino = destinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        String nuevoEstado = body.get("estado");
        destino.setEstado(nuevoEstado);
        destinoRepository.save(destino);

        return ResponseEntity.ok(Map.of("message", "Estado actualizado correctamente"));
    }

    // ==================== PAQUETES ====================

    @PatchMapping("/paquetes/{id}/estado")
    public ResponseEntity<?> cambiarEstadoPaquete(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Paquete paquete = paqueteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado"));

        String nuevoEstado = body.get("estado");
        paquete.setEstado(nuevoEstado);
        paqueteRepository.save(paquete);

        return ResponseEntity.ok(Map.of("message", "Estado actualizado correctamente"));
    }

    // ==================== SERVICIOS ====================

    @PatchMapping("/servicios/{id}/estado")
    public ResponseEntity<?> cambiarEstadoServicio(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        ServicioAdicional servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        String nuevoEstado = body.get("estado");
        servicio.setEstado(nuevoEstado);
        servicioRepository.save(servicio);

        return ResponseEntity.ok(Map.of("message", "Estado actualizado correctamente"));
    }

    // ==================== RESERVAS ====================

    @PatchMapping("/reservas/{id}/estado")
    public ResponseEntity<?> cambiarEstadoReserva(@PathVariable Integer id, @RequestBody Map<String, String> body) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        String nuevoEstado = body.get("estado");
        EstadoReserva estadoReserva = EstadoReserva.valueOf(nuevoEstado);

        if (estadoReserva == EstadoReserva.PAGADA) {
            reserva.confirmarPago();
        } else if (estadoReserva == EstadoReserva.CANCELADA) {
            reserva.setEstadoReserva(EstadoReserva.CANCELADA);
            // Restaurar stock de los paquetes
            reserva.getItems().forEach(item -> {
                Paquete paquete = item.getPaquete();
                paquete.setStockDisponible(paquete.getStockDisponible() + 1);
                paqueteRepository.save(paquete);
            });
        }

        reservaRepository.save(reserva);

        return ResponseEntity.ok(Map.of("message", "Estado de reserva actualizado correctamente"));
    }
}
