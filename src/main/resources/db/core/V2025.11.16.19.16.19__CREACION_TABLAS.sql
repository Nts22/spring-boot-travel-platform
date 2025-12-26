-- =============================================
-- 1. TABLA: DESTINO
-- =============================================
CREATE TABLE destino (
    id_destino INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    pais VARCHAR(50) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(3) NOT NULL ,
    fecha_creacion TIMESTAMP ,
    fecha_modificacion TIMESTAMP
);

-- =============================================
-- 2. TABLA: PAQUETEº
-- =============================================
CREATE TABLE paquete (
    id_paquete INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    stock_disponible INT NOT NULL,
    id_destino INT NOT NULL,
    estado VARCHAR(3) NOT NULL ,
    fecha_creacion TIMESTAMP ,
    fecha_modificacion TIMESTAMP ,
    
    FOREIGN KEY (id_destino) REFERENCES destino(id_destino)
);

CREATE INDEX idx_paquete_destino ON paquete (id_destino);
CREATE INDEX idx_paquete_fechas ON paquete (fecha_inicio, fecha_fin);

-- =============================================
-- 3. TABLA: USUARIO
-- =============================================
CREATE TABLE usuario (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(15),
    rol VARCHAR(15) ,
    estado VARCHAR(3) NOT NULL ,
    fecha_creacion TIMESTAMP ,
    fecha_modificacion TIMESTAMP 
);

CREATE INDEX idx_usuario_email ON usuario (email);

-- =============================================
-- 4. TABLA: RESERVA
-- =============================================
CREATE TABLE reserva (
    id_reserva INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_paquete INT NOT NULL,
    fecha_viaje_inicio DATE NOT NULL,
    total_pagar DECIMAL(10, 2) NOT NULL,
    estado_reserva VARCHAR(20) ,
    estado VARCHAR(3) NOT NULL , 
    fecha_creacion TIMESTAMP ,
    fecha_modificacion TIMESTAMP ,

    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_paquete) REFERENCES paquete(id_paquete)
);

CREATE INDEX idx_reserva_usuario ON reserva (id_usuario);
CREATE INDEX idx_reserva_paquete ON reserva (id_paquete);
CREATE INDEX idx_reserva_estado ON reserva (estado_reserva);

-- =============================================
-- 5. TABLA: SERVICIO_ADICIONAL
-- =============================================
CREATE TABLE servicio_adicional (
    id_servicio INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    costo DECIMAL(8, 2) NOT NULL,
    estado VARCHAR(3) NOT NULL
);

-- =============================================
-- 6. TABLA: RESERVA_SERVICIO
-- =============================================
CREATE TABLE reserva_servicio (
    id_reserva INT NOT NULL,
    id_servicio INT NOT NULL,
    cantidad INT ,

    PRIMARY KEY (id_reserva, id_servicio),
    FOREIGN KEY (id_reserva) REFERENCES reserva(id_reserva),
    FOREIGN KEY (id_servicio) REFERENCES servicio_adicional(id_servicio)
);

CREATE INDEX idx_reserva_servicio ON reserva_servicio (id_servicio);
