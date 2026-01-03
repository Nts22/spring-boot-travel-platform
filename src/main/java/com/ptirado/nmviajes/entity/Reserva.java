package com.ptirado.nmviajes.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
    private BigDecimal totalPagar;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estadoReserva;

    private String estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (estadoReserva == null) {
            estadoReserva = EstadoReserva.PENDIENTE;
        }
        if (estado == null) {
            estado = "ACT";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = LocalDateTime.now();
    }

    public void confirmarPago() {
        this.estadoReserva = EstadoReserva.PAGADA;
    }

    public boolean estaFinalizada() {
        return this.estadoReserva == EstadoReserva.PAGADA;
    }

    public enum EstadoReserva {
        PENDIENTE,
        PAGADA,
        CANCELADA
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "reserva", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaItem> items = new ArrayList<>();

    @ToString.Include(name = "usuarioId")
    public Integer getUsuarioId() {
        return usuario != null ? usuario.getIdUsuario() : null;
    }

    @ToString.Include(name = "cantidadItems")
    public int getCantidadItems() {
        return items != null ? items.size() : 0;
    }
}
