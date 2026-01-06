-- =============================================
-- MIGRACIÓN: Creación de tablas para Spring Security
-- Fecha: 2026-01-06
-- Descripción: Crea la tabla role y la tabla intermedia usuario_role
--              para el sistema de autenticación y autorización
-- =============================================

-- =============================================
-- 1. TABLA: ROLE
-- =============================================
CREATE TABLE role (
    id_role INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- =============================================
-- 2. TABLA INTERMEDIA: USUARIO_ROLE
-- =============================================
CREATE TABLE usuario_role (
    id_usuario INT NOT NULL,
    id_role INT NOT NULL,

    PRIMARY KEY (id_usuario, id_role),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_role) REFERENCES role(id_role) ON DELETE CASCADE
);

CREATE INDEX idx_usuario_role_usuario ON usuario_role (id_usuario);
CREATE INDEX idx_usuario_role_role ON usuario_role (id_role);

-- =============================================
-- 3. ELIMINAR COLUMNA ROL DE USUARIO (ya no se usa)
-- =============================================
ALTER TABLE usuario DROP COLUMN rol;

-- =============================================
-- 4. INSERTAR ROLES POR DEFECTO
-- =============================================
INSERT INTO role (nombre, descripcion) VALUES
('ROLE_USER', 'Usuario estándar con acceso básico'),
('ROLE_ADMIN', 'Administrador con acceso completo');

-- =============================================
-- 5. ASIGNAR ROL USER A USUARIOS EXISTENTES
-- =============================================
INSERT INTO usuario_role (id_usuario, id_role)
SELECT u.id_usuario, r.id_role
FROM usuario u, role r
WHERE r.nombre = 'ROLE_USER';

-- =============================================
-- 6. ACTUALIZAR PASSWORDS EXISTENTES CON BCRYPT
-- Password por defecto: "password123" codificado con BCrypt
-- =============================================
UPDATE usuario SET password = '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqLpL0Z1r3OC9yJkKG3K7Lm7xT0bRGu';
