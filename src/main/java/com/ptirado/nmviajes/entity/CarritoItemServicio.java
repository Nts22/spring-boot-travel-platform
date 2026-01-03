package com.ptirado.nmviajes.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "carrito_item_servicio")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CarritoItemServicio {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private CarritoItemServicioId id;

    @ToString.Include
    private Integer cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idItem")
    @JoinColumn(name = "id_item")
    private CarritoItem carritoItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idServicio")
    @JoinColumn(name = "id_servicio")
    private ServicioAdicional servicioAdicional;

    @ToString.Include(name = "itemId")
    public Integer getItemId() {
        return id != null ? id.getIdItem() : null;
    }

    @ToString.Include(name = "servicioId")
    public Integer getServicioId() {
        return id != null ? id.getIdServicio() : null;
    }
}
