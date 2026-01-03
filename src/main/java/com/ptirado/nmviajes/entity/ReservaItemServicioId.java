package com.ptirado.nmviajes.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class ReservaItemServicioId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idItem;
    private Integer idServicio;
}
