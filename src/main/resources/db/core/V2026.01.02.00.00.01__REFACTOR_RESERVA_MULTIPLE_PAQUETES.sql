-- =============================================
-- MIGRACIÓN: Refactorizar reserva para soportar múltiples paquetes
-- Una reserva puede tener N items (paquetes)
-- =============================================

-- =============================================
-- 1. TABLA: RESERVA_ITEM
-- Items individuales dentro de la reserva
-- =============================================
CREATE TABLE reserva_item (
    id_item INT PRIMARY KEY AUTO_INCREMENT,
    id_reserva INT NOT NULL,
    id_paquete INT NOT NULL,
    fecha_viaje_inicio DATE NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva) ON DELETE CASCADE,
    FOREIGN KEY (id_paquete) REFERENCES paquete(id_paquete)
);

CREATE INDEX idx_reserva_item_reserva ON reserva_item (id_reserva);
CREATE INDEX idx_reserva_item_paquete ON reserva_item (id_paquete);

-- =============================================
-- 2. TABLA: RESERVA_ITEM_SERVICIO
-- Servicios adicionales por cada item de la reserva
-- =============================================
CREATE TABLE reserva_item_servicio (
    id_item INT NOT NULL,
    id_servicio INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,

    PRIMARY KEY (id_item, id_servicio),
    FOREIGN KEY (id_item) REFERENCES reserva_item(id_item) ON DELETE CASCADE,
    FOREIGN KEY (id_servicio) REFERENCES servicio_adicional(id_servicio)
);

CREATE INDEX idx_reserva_item_servicio ON reserva_item_servicio (id_servicio);

-- =============================================
-- 3. MIGRAR DATOS EXISTENTES
-- Mover datos de reserva actual a reserva_item
-- =============================================
INSERT INTO reserva_item (id_reserva, id_paquete, fecha_viaje_inicio, subtotal)
SELECT id_reserva, id_paquete, fecha_viaje_inicio, total_pagar
FROM reserva
WHERE id_paquete IS NOT NULL;

-- Migrar servicios de reserva_servicio a reserva_item_servicio
INSERT INTO reserva_item_servicio (id_item, id_servicio, cantidad)
SELECT ri.id_item, rs.id_servicio, rs.cantidad
FROM reserva_servicio rs
INNER JOIN reserva r ON rs.id_reserva = r.id_reserva
INNER JOIN reserva_item ri ON ri.id_reserva = r.id_reserva AND ri.id_paquete = r.id_paquete;

-- =============================================
-- 4. ELIMINAR COLUMNAS OBSOLETAS DE RESERVA
-- =============================================
ALTER TABLE reserva DROP FOREIGN KEY reserva_ibfk_2;
ALTER TABLE reserva DROP COLUMN id_paquete;
ALTER TABLE reserva DROP COLUMN fecha_viaje_inicio;

-- =============================================
-- 5. ELIMINAR TABLA RESERVA_SERVICIO (reemplazada por RESERVA_ITEM_SERVICIO)
-- =============================================
DROP TABLE reserva_servicio;
