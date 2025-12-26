package com.ptirado.nmviajes.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "reserva")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Integer idReserva;

    @ToString.Include
    private LocalDate fechaViajeInicio;

    @ToString.Include
    private BigDecimal totalPagar;

    private String estadoReserva;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paquete")
    private Paquete paquete;

    @OneToMany(mappedBy = "reserva", fetch = FetchType.LAZY)
    private List<ReservaServicio> reservasServicios = new ArrayList<>();

    @ToString.Include(name = "usuarioId")
    public Integer getUsuarioId() {
        return usuario != null ? usuario.getIdUsuario() : null;
    }

    @ToString.Include(name = "paqueteId")
    public Integer getPaqueteId() {
        return paquete != null ? paquete.getIdPaquete() : null;
    }
}
