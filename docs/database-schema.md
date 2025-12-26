# Database Schema – Plataforma de Viajes

Este documento describe la **estructura de la base de datos** de la plataforma de viajes, incluyendo tablas, relaciones, claves y restricciones.

El esquema está alineado con el modelo de dominio descrito en `domain-overview.md`.

---

## Visión General

La base de datos está diseñada bajo un modelo **relacional normalizado**, donde:

- Cada tabla representa una entidad del dominio
- Las relaciones se implementan mediante claves foráneas
- Se garantiza integridad referencial
- El esquema es gestionado mediante **Flyway**

---

## Diagrama Entidad–Relación (ER)

> El diagrama ER oficial del proyecto se encuentra documentado y sirve como referencia visual para entender las relaciones entre tablas.

Relaciones principales:

DESTINO 1 ──── * PAQUETE
PAQUETE 1 ──── * RESERVA
USUARIO 1 ──── * RESERVA
RESERVA * ──── * SERVICIO_ADICIONAL


La relación muchos-a-muchos entre **RESERVA** y **SERVICIO_ADICIONAL** se implementa mediante la tabla intermedia **RESERVA_SERVICIO**.

---

## Descripción de Tablas

### DESTINO

Representa un destino turístico.

**Campos principales:**
- `id_destino` (PK)
- `nombre` (único)
- `pais`
- `descripcion`
- `estado`
- `fecha_creacion`
- `fecha_modificacion`

**Restricciones:**
- `nombre` es único
- `estado` controla disponibilidad lógica

---

### PAQUETE

Representa un paquete turístico asociado a un destino.

**Campos principales:**
- `id_paquete` (PK)
- `id_destino` (FK → DESTINO)
- `nombre`
- `descripcion`
- `precio`
- `fecha_inicio`
- `fecha_fin`
- `stock_disponible`
- `estado`

**Relaciones:**
- Muchos paquetes pertenecen a un destino

---

### USUARIO

Representa un usuario del sistema.

**Campos principales:**
- `id_usuario` (PK)
- `email` (único)
- `password`
- `nombre`
- `estado`
- `fecha_creacion`

---

### RESERVA

Representa la reserva de un paquete por un usuario.

**Campos principales:**
- `id_reserva` (PK)
- `id_usuario` (FK → USUARIO)
- `id_paquete` (FK → PAQUETE)
- `fecha_reserva`
- `estado`
- `total`

**Relaciones:**
- Un usuario puede tener múltiples reservas
- Un paquete puede estar en múltiples reservas

---

### SERVICIO_ADICIONAL

Representa servicios opcionales.

**Campos principales:**
- `id_servicio` (PK)
- `nombre`
- `precio`
- `estado`

---

### RESERVA_SERVICIO

Tabla intermedia entre RESERVA y SERVICIO_ADICIONAL.

**Campos principales:**
- `id_reserva` (FK → RESERVA)
- `id_servicio` (FK → SERVICIO_ADICIONAL)
- `cantidad`

**Clave primaria compuesta:**
- (`id_reserva`, `id_servicio`)

---

## Claves y Relaciones

| Tabla | FK | Referencia |
|-----|----|------------|
| PAQUETE | id_destino | DESTINO |
| RESERVA | id_usuario | USUARIO |
| RESERVA | id_paquete | PAQUETE |
| RESERVA_SERVICIO | id_reserva | RESERVA |
| RESERVA_SERVICIO | id_servicio | SERVICIO_ADICIONAL |

---

## Restricciones Importantes

- No se permite eliminar registros con dependencias activas
- Se utiliza **borrado lógico** mediante el campo `estado`
- La integridad referencial se mantiene con claves foráneas
- Los nombres clave (`email`, `nombre`) son únicos según contexto

---

## Consideraciones Técnicas

- Base de datos: **MySQL 8**
- Migraciones gestionadas con **Flyway**
- Fechas manejadas en formato `TIMESTAMP`
- Índices definidos en claves primarias y foráneas

---

 **Documentos relacionados:**
- `domain-overview.md`
- `architecture.md`
- `api-conventions.md`
