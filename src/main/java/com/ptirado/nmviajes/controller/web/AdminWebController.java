package com.ptirado.nmviajes.controller.web;

import com.ptirado.nmviajes.entity.*;
import com.ptirado.nmviajes.entity.Reserva.EstadoReserva;
import com.ptirado.nmviajes.repository.*;
import com.ptirado.nmviajes.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminWebController {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final DestinoRepository destinoRepository;
    private final PaqueteRepository paqueteRepository;
    private final ServicioAdicionalRepository servicioRepository;
    private final ReservaRepository reservaRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int PAGE_SIZE = 10;

    // ==================== DASHBOARD ====================

    @GetMapping
    public String dashboard(Model model) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsuarios", usuarioRepository.count());
        stats.put("totalReservas", reservaRepository.count());
        stats.put("totalPaquetes", paqueteRepository.count());

        BigDecimal ingresos = reservaRepository.findAll().stream()
                .filter(r -> r.getEstadoReserva() == EstadoReserva.PAGADA)
                .map(Reserva::getTotalPagar)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("ingresosTotales", ingresos);

        stats.put("reservasPendientes", reservaRepository.countByEstadoReserva(EstadoReserva.PENDIENTE));
        stats.put("reservasPagadas", reservaRepository.countByEstadoReserva(EstadoReserva.PAGADA));
        stats.put("reservasCanceladas", reservaRepository.countByEstadoReserva(EstadoReserva.CANCELADA));

        List<Reserva> ultimasReservas = reservaRepository.findTop5ByOrderByFechaCreacionDesc();

        model.addAttribute("title", "Dashboard");
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("stats", stats);
        model.addAttribute("ultimasReservas", ultimasReservas);
        model.addAttribute("content", "admin/dashboard");
        return "admin/layout";
    }

    // ==================== USUARIOS ====================

    @GetMapping("/usuarios")
    public String listarUsuarios(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Usuario> usuarios = usuarioRepository.findAll(
                PageRequest.of(page, PAGE_SIZE, Sort.by("idUsuario").descending()));

        model.addAttribute("title", "Usuarios");
        model.addAttribute("activeMenu", "usuarios");
        model.addAttribute("usuarios", usuarios.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", usuarios.getTotalPages());
        model.addAttribute("content", "admin/usuario/list");
        return "admin/layout";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuarioForm(Model model) {
        model.addAttribute("title", "Nuevo Usuario");
        model.addAttribute("activeMenu", "usuarios");
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("content", "admin/usuario/form");
        return "admin/layout";
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuarioForm(@PathVariable Integer id, Model model) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("title", "Editar Usuario");
        model.addAttribute("activeMenu", "usuarios");
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("content", "admin/usuario/form");
        return "admin/layout";
    }

    @PostMapping("/usuarios")
    public String crearUsuario(@RequestParam String nombre,
                               @RequestParam String apellido,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam(required = false) String telefono,
                               @RequestParam(required = false) List<Integer> roleIds,
                               @RequestParam(defaultValue = "ACT") String estado,
                               RedirectAttributes redirectAttributes) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setTelefono(telefono);
        usuario.setEstado(estado);

        if (roleIds != null && !roleIds.isEmpty()) {
            usuario.setRoles(new HashSet<>(roleRepository.findAllById(roleIds)));
        }

        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("success", "Usuario creado exitosamente");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}")
    public String actualizarUsuario(@PathVariable Integer id,
                                    @RequestParam String nombre,
                                    @RequestParam String apellido,
                                    @RequestParam String email,
                                    @RequestParam(required = false) String password,
                                    @RequestParam(required = false) String telefono,
                                    @RequestParam(required = false) List<Integer> roleIds,
                                    @RequestParam(defaultValue = "ACT") String estado,
                                    RedirectAttributes redirectAttributes) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setTelefono(telefono);
        usuario.setEstado(estado);

        if (password != null && !password.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(password));
        }

        if (roleIds != null) {
            usuario.setRoles(new HashSet<>(roleRepository.findAllById(roleIds)));
        } else {
            usuario.getRoles().clear();
        }

        usuarioRepository.save(usuario);
        redirectAttributes.addFlashAttribute("success", "Usuario actualizado exitosamente");
        return "redirect:/admin/usuarios";
    }

    // ==================== DESTINOS ====================

    @GetMapping("/destinos")
    public String listarDestinos(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Destino> destinos = destinoRepository.findAllWithPaquetes(
                PageRequest.of(page, PAGE_SIZE, Sort.by("idDestino").descending()));

        model.addAttribute("title", "Destinos");
        model.addAttribute("activeMenu", "destinos");
        model.addAttribute("destinos", destinos.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", destinos.getTotalPages());
        model.addAttribute("content", "admin/destino/list");
        return "admin/layout";
    }

    @GetMapping("/destinos/nuevo")
    public String nuevoDestinoForm(Model model) {
        model.addAttribute("title", "Nuevo Destino");
        model.addAttribute("activeMenu", "destinos");
        model.addAttribute("destino", new Destino());
        model.addAttribute("content", "admin/destino/form");
        return "admin/layout";
    }

    @GetMapping("/destinos/{id}/editar")
    public String editarDestinoForm(@PathVariable Integer id, Model model) {
        Destino destino = destinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        model.addAttribute("title", "Editar Destino");
        model.addAttribute("activeMenu", "destinos");
        model.addAttribute("destino", destino);
        model.addAttribute("content", "admin/destino/form");
        return "admin/layout";
    }

    @PostMapping("/destinos")
    public String crearDestino(@RequestParam String nombre,
                               @RequestParam String pais,
                               @RequestParam(required = false) String descripcion,
                               @RequestParam(defaultValue = "ACT") String estado,
                               RedirectAttributes redirectAttributes) {
        Destino destino = new Destino();
        destino.setNombre(nombre);
        destino.setPais(pais);
        destino.setDescripcion(descripcion);
        destino.setEstado(estado);

        destinoRepository.save(destino);
        redirectAttributes.addFlashAttribute("success", "Destino creado exitosamente");
        return "redirect:/admin/destinos";
    }

    @PostMapping("/destinos/{id}")
    public String actualizarDestino(@PathVariable Integer id,
                                    @RequestParam String nombre,
                                    @RequestParam String pais,
                                    @RequestParam(required = false) String descripcion,
                                    @RequestParam(defaultValue = "ACT") String estado,
                                    RedirectAttributes redirectAttributes) {
        Destino destino = destinoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        destino.setNombre(nombre);
        destino.setPais(pais);
        destino.setDescripcion(descripcion);
        destino.setEstado(estado);

        destinoRepository.save(destino);
        redirectAttributes.addFlashAttribute("success", "Destino actualizado exitosamente");
        return "redirect:/admin/destinos";
    }

    // ==================== PAQUETES ====================

    @GetMapping("/paquetes")
    public String listarPaquetes(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Paquete> paquetes = paqueteRepository.findAllWithDestino(
                PageRequest.of(page, PAGE_SIZE, Sort.by("idPaquete").descending()));

        model.addAttribute("title", "Paquetes");
        model.addAttribute("activeMenu", "paquetes");
        model.addAttribute("paquetes", paquetes.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paquetes.getTotalPages());
        model.addAttribute("content", "admin/paquete/list");
        return "admin/layout";
    }

    @GetMapping("/paquetes/nuevo")
    public String nuevoPaqueteForm(Model model) {
        model.addAttribute("title", "Nuevo Paquete");
        model.addAttribute("activeMenu", "paquetes");
        model.addAttribute("paquete", new Paquete());
        model.addAttribute("destinos", destinoRepository.findByEstado("ACT"));
        model.addAttribute("content", "admin/paquete/form");
        return "admin/layout";
    }

    @GetMapping("/paquetes/{id}/editar")
    public String editarPaqueteForm(@PathVariable Integer id, Model model) {
        Paquete paquete = paqueteRepository.findByIdWithDestino(id)
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado"));

        model.addAttribute("title", "Editar Paquete");
        model.addAttribute("activeMenu", "paquetes");
        model.addAttribute("paquete", paquete);
        model.addAttribute("destinos", destinoRepository.findByEstado("ACT"));
        model.addAttribute("content", "admin/paquete/form");
        return "admin/layout";
    }

    @PostMapping("/paquetes")
    public String crearPaquete(@RequestParam String nombre,
                               @RequestParam Integer idDestino,
                               @RequestParam(required = false) String descripcion,
                               @RequestParam String fechaInicio,
                               @RequestParam String fechaFin,
                               @RequestParam BigDecimal precio,
                               @RequestParam Integer stockDisponible,
                               @RequestParam(defaultValue = "ACT") String estado,
                               RedirectAttributes redirectAttributes) {
        Destino destino = destinoRepository.findById(idDestino)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        Paquete paquete = new Paquete();
        paquete.setNombre(nombre);
        paquete.setDestino(destino);
        paquete.setDescripcion(descripcion);
        paquete.setFechaInicio(java.time.LocalDate.parse(fechaInicio));
        paquete.setFechaFin(java.time.LocalDate.parse(fechaFin));
        paquete.setPrecio(precio);
        paquete.setStockDisponible(stockDisponible);
        paquete.setEstado(estado);

        paqueteRepository.save(paquete);
        redirectAttributes.addFlashAttribute("success", "Paquete creado exitosamente");
        return "redirect:/admin/paquetes";
    }

    @PostMapping("/paquetes/{id}")
    public String actualizarPaquete(@PathVariable Integer id,
                                    @RequestParam String nombre,
                                    @RequestParam Integer idDestino,
                                    @RequestParam(required = false) String descripcion,
                                    @RequestParam String fechaInicio,
                                    @RequestParam String fechaFin,
                                    @RequestParam BigDecimal precio,
                                    @RequestParam Integer stockDisponible,
                                    @RequestParam(defaultValue = "ACT") String estado,
                                    RedirectAttributes redirectAttributes) {
        Paquete paquete = paqueteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado"));

        Destino destino = destinoRepository.findById(idDestino)
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        paquete.setNombre(nombre);
        paquete.setDestino(destino);
        paquete.setDescripcion(descripcion);
        paquete.setFechaInicio(java.time.LocalDate.parse(fechaInicio));
        paquete.setFechaFin(java.time.LocalDate.parse(fechaFin));
        paquete.setPrecio(precio);
        paquete.setStockDisponible(stockDisponible);
        paquete.setEstado(estado);

        paqueteRepository.save(paquete);
        redirectAttributes.addFlashAttribute("success", "Paquete actualizado exitosamente");
        return "redirect:/admin/paquetes";
    }

    // ==================== SERVICIOS ====================

    @GetMapping("/servicios")
    public String listarServicios(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<ServicioAdicional> servicios = servicioRepository.findAll(
                PageRequest.of(page, PAGE_SIZE, Sort.by("idServicio").descending()));

        model.addAttribute("title", "Servicios Adicionales");
        model.addAttribute("activeMenu", "servicios");
        model.addAttribute("servicios", servicios.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", servicios.getTotalPages());
        model.addAttribute("content", "admin/servicio/list");
        return "admin/layout";
    }

    @GetMapping("/servicios/nuevo")
    public String nuevoServicioForm(Model model) {
        model.addAttribute("title", "Nuevo Servicio");
        model.addAttribute("activeMenu", "servicios");
        model.addAttribute("servicio", new ServicioAdicional());
        model.addAttribute("content", "admin/servicio/form");
        return "admin/layout";
    }

    @GetMapping("/servicios/{id}/editar")
    public String editarServicioForm(@PathVariable Integer id, Model model) {
        ServicioAdicional servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        model.addAttribute("title", "Editar Servicio");
        model.addAttribute("activeMenu", "servicios");
        model.addAttribute("servicio", servicio);
        model.addAttribute("content", "admin/servicio/form");
        return "admin/layout";
    }

    @PostMapping("/servicios")
    public String crearServicio(@RequestParam String nombre,
                                @RequestParam BigDecimal costo,
                                @RequestParam(defaultValue = "ACT") String estado,
                                RedirectAttributes redirectAttributes) {
        ServicioAdicional servicio = new ServicioAdicional();
        servicio.setNombre(nombre);
        servicio.setCosto(costo);
        servicio.setEstado(estado);

        servicioRepository.save(servicio);
        redirectAttributes.addFlashAttribute("success", "Servicio creado exitosamente");
        return "redirect:/admin/servicios";
    }

    @PostMapping("/servicios/{id}")
    public String actualizarServicio(@PathVariable Integer id,
                                     @RequestParam String nombre,
                                     @RequestParam BigDecimal costo,
                                     @RequestParam(defaultValue = "ACT") String estado,
                                     RedirectAttributes redirectAttributes) {
        ServicioAdicional servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        servicio.setNombre(nombre);
        servicio.setCosto(costo);
        servicio.setEstado(estado);

        servicioRepository.save(servicio);
        redirectAttributes.addFlashAttribute("success", "Servicio actualizado exitosamente");
        return "redirect:/admin/servicios";
    }

    // ==================== RESERVAS ====================

    @GetMapping("/reservas")
    public String listarReservas(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(required = false) String estado,
                                 Model model) {
        Page<Reserva> reservas;
        if (estado != null && !estado.isEmpty()) {
            EstadoReserva estadoReserva = EstadoReserva.valueOf(estado);
            reservas = reservaRepository.findByEstadoReservaWithUsuarioAndItems(estadoReserva,
                    PageRequest.of(page, PAGE_SIZE, Sort.by("fechaCreacion").descending()));
        } else {
            reservas = reservaRepository.findAllWithUsuarioAndItems(
                    PageRequest.of(page, PAGE_SIZE, Sort.by("fechaCreacion").descending()));
        }

        model.addAttribute("title", "Reservas");
        model.addAttribute("activeMenu", "reservas");
        model.addAttribute("reservas", reservas.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", reservas.getTotalPages());
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("content", "admin/reserva/list");
        return "admin/layout";
    }

    @GetMapping("/reservas/{id}")
    public String verReserva(@PathVariable Integer id, Model model) {
        Reserva reserva = reservaRepository.findByIdWithUsuarioAndItems(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        model.addAttribute("title", "Detalle de Reserva");
        model.addAttribute("activeMenu", "reservas");
        model.addAttribute("reserva", reserva);
        model.addAttribute("content", "admin/reserva/detail");
        return "admin/layout";
    }
}
