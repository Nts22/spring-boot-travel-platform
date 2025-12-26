# NM VIAJES – Domain Overview

Este documento resume **las entidades, relaciones, flujos y consultas base** del dominio funcional del sistema *NM Viajes*.  
Sirve como guía rápida para desarrolladores, analistas y nuevos integrantes del equipo.

---

# 1. Entidades Principales

## 1.1 DESTINO
Representa un lugar turístico disponible para reservar.

**Atributos principales**
- id_destino
- nombre
- país
- descripción
- estado
- fecha_creacion / fecha_modificacion

**Relaciones**
- 1 DESTINO -> N PAQUETES

---

## 1.2 PAQUETE
Es una oferta turística asociada a un destino.

**Atributos**
- id_paquete
- nombre
- descripción
- precio
- fecha_inicio / fecha_fin
- stock_disponible
- estado
- id_destino (FK)

**Relaciones**
- N PAQUETES -> 1 DESTINO  
- 1 PAQUETE -> N RESERVAS

---

## 1.3 USUARIO
Persona que realiza una reserva.

**Atributos**
- id_usuario
- nombre / apellido
- email
- password
- teléfono
- rol
- estado

**Relaciones**
- 1 USUARIO -> N RESERVAS

---

## 1.4 RESERVA
Representa la compra final realizada por un usuario.

**Atributos**
- id_reserva
- id_usuario (FK)
- id_paquete (FK)
- fecha_viaje_inicio
- total_pagar
- estado_reserva
- fecha_creacion / fecha_modificacion

**Relaciones**
- 1 RESERVA -> 1 USUARIO  
- 1 RESERVA -> 1 PAQUETE  
- 1 RESERVA -> N SERVICIOS (vía tabla puente)  

---

## 1.5 SERVICIO_ADICIONAL
Servicios opcionales adicionales al paquete.

**Atributos**
- id_servicio
- nombre
- costo
- estado

**Relaciones**
- N SERVICIOS -> N RESERVAS (con reserva_servicio)

---

## 1.6 RESERVA_SERVICIO (tabla puente N:M)
Asocia servicios adicionales a una reserva.

**Atributos**
- id_reserva (FK)
- id_servicio (FK)
- cantidad

**Relaciones**
- N:M entre RESERVA y SERVICIO_ADICIONAL

---

# 2.  Diagrama Relacional Simplificado

                           ┌──────────────────────┐
                           │       DESTINO        │
                           │----------------------│
                           │ id_destino (PK)      │
                           │ nombre               │
                           │ país                 │
                           │ descripción          │
                           │ estado               │
                           │ fecha_creacion       │
                           │ fecha_modificacion   │
                           └───────────┬──────────┘
                                       │ 1:N
                                       │
                                       ▼
                           ┌──────────────────────┐
                           │       PAQUETE        │
                           │----------------------│
                           │ id_paquete (PK)      │
                           │ id_destino (FK)      │───┐
                           │ nombre               │   │
                           │ descripción          │   │
                           │ precio               │   │
                           │ fecha_inicio         │   │
                           │ fecha_fin            │   │
                           │ stock_disponible     │   │
                           │ estado               │   │
                           │ fecha_creacion       │   │
                           │ fecha_modificacion   │   │
                           └───────────┬──────────┘   │
                                       │ 1:N          │
                                       │              │
                                       ▼              │
                           ┌──────────────────────┐   │
                           │       RESERVA        │   │
                           │----------------------│   │
                           │ id_reserva (PK)      │   │
                           │ id_usuario (FK)      │◄──┘  N:1
                           │ id_paquete (FK)      │
                           │ fecha_viaje_inicio   │
                           │ total_pagar          │
                           │ estado_reserva       │
                           │ fecha_creacion       │
                           │ fecha_modificacion   │
                           └───────┬──────────────┘
                                   │ 1:N (por tabla puente)
                                   │
                                   ▼
                    ┌────────────────────────────┐
                    │     RESERVA_SERVICIO       │
                    │----------------------------│
                    │ id_reserva  (FK, PK)       │
                    │ id_servicio (FK, PK)       │
                    │ cantidad                   │
                    └───────────────┬────────────┘
                                    │  N:1
                                    │
                                    ▼
                      ┌────────────────────────┐
                      │   SERVICIO_ADICIONAL   │
                      │------------------------│
                      │ id_servicio (PK)       │
                      │ nombre                 │
                      │ costo                  │
                      │ estado                 │
                      └────────────────────────┘


                     ┌──────────────────────┐
                     │       USUARIO        │
                     │----------------------│
                     │ id_usuario (PK)      │
                     │ nombre               │
                     │ apellido             │
                     │ email                │
                     │ password             │
                     │ telefono             │
                     │ rol                  │
                     │ estado               │
                     │ fecha_creacion       │
                     │ fecha_modificacion   │
                     └──────────┬───────────┘
                                │ 1:N
                                ▼
                             RESERVA


Relaciones clave:
-DESTINO → PAQUETE (1:N)
Un destino puede tener muchos paquetes turísticos.

-PAQUETE → RESERVA (1:N)
Un paquete puede ser reservado por muchos usuarios.

-USUARIO → RESERVA (1:N)
Un usuario puede realizar muchas reservas.

-RESERVA → SERVICIO_ADICIONAL (N:M)
Una reserva puede agregar varios servicios adicionales,
y un servicio puede estar presente en muchas reservas.

---

# 3.  Flujo Funcional de una Reserva

1. El usuario ve un DESTINO.
2. Elige un PAQUETE asociado al destino.
3. Ingresa fecha de viaje.
4. (Opcional) Selecciona servicios adicionales.
5. El sistema calcula:
   - precio paquete  
   + costo de servicios × cantidad  
6. El sistema valida:
   - stock del paquete  
   - estado del usuario  
   - disponibilidad del destino  
7. Se crea la RESERVA.
8. Se registran sus servicios en RESERVA_SERVICIO.

---

# 4.  Consultas SQL Útiles

## 4.1 Reservas con información completa
```sql
SELECT r.*, u.*, p.*, d.*
FROM reserva r
JOIN usuario u ON r.id_usuario = u.id_usuario
JOIN paquete p ON r.id_paquete = p.id_paquete
JOIN destino d ON p.id_destino = d.id_destino;

## 4.2 Servicios adicionales por reserva
SELECT sa.nombre, sa.costo, rs.cantidad
FROM reserva_servicio rs
JOIN servicio_adicional sa ON sa.id_servicio = rs.id_servicio
WHERE rs.id_reserva = ?;


## 5. Estructura de Carpetas Relevante (DTO, Entity, Mapper, Service)
src/main/java/com/ptirado/nmviajes/
├── entity/
├── dto/
├── viewmodel/
├── mapper/
├── repository/
├── service/
└── controller/

## 6. Convenciones de Negocio
Todos los destinos, paquetes y servicios deben estar en estado ACTIVO para poder reservar.
El stock del paquete se descuenta al confirmar la reserva.
Un usuario puede tener muchas reservas.
Una reserva puede tener cero o muchos servicios adicionales.

## 7. Notas para Nuevos Desarrolladores
Revisa /docs/domain-overview.md antes de tocar entidades o relaciones.
Los cambios estructurales de BD deben hacerse siempre con Flyway.
Mantén sincronizados:	
	entidades JPA
	migraciones SQL
	diagramas
	documentación