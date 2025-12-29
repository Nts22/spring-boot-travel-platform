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
@Table(name = "paquete")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Paquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    @EqualsAndHashCode.Include
    private Integer idPaquete;

    @ToString.Include
    private String nombre;

    private String descripcion;

    @ToString.Include
    private BigDecimal precio;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private Integer stockDisponible;
    private String estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_destino")
    private Destino destino;

    @OneToMany(mappedBy = "paquete", fetch = FetchType.LAZY)
    private List<Reserva> reservas = new ArrayList<>();

    @ToString.Include(name = "destinoId")
    public Integer getDestinoId() {
        return destino != null ? destino.getIdDestino() : null;
    }

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = this.estado == null ? "ACT" : this.estado;
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }
}
