-- =============================================
-- TABLA: CARRITO
-- Almacena el carrito de compras de cada usuario
-- =============================================
CREATE TABLE carrito (
    id_carrito INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL UNIQUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE INDEX idx_carrito_usuario ON carrito (id_usuario);

-- =============================================
-- TABLA: CARRITO_ITEM
-- Items individuales dentro del carrito
-- =============================================
CREATE TABLE carrito_item (
    id_item INT PRIMARY KEY AUTO_INCREMENT,
    id_carrito INT NOT NULL,
    id_paquete INT NOT NULL,
    fecha_viaje_inicio DATE NOT NULL,
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_carrito) REFERENCES carrito(id_carrito) ON DELETE CASCADE,
    FOREIGN KEY (id_paquete) REFERENCES paquete(id_paquete),

    UNIQUE KEY uk_carrito_paquete (id_carrito, id_paquete)
);

CREATE INDEX idx_carrito_item_carrito ON carrito_item (id_carrito);
CREATE INDEX idx_carrito_item_paquete ON carrito_item (id_paquete);

-- =============================================
-- TABLA: CARRITO_ITEM_SERVICIO
-- Servicios adicionales seleccionados para cada item del carrito
-- =============================================
CREATE TABLE carrito_item_servicio (
    id_item INT NOT NULL,
    id_servicio INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,

    PRIMARY KEY (id_item, id_servicio),
    FOREIGN KEY (id_item) REFERENCES carrito_item(id_item) ON DELETE CASCADE,
    FOREIGN KEY (id_servicio) REFERENCES servicio_adicional(id_servicio)
);

CREATE INDEX idx_carrito_item_servicio ON carrito_item_servicio (id_servicio);
