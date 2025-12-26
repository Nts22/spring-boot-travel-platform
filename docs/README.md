# 🌍 Plataforma de Viajes – Spring Boot

Aplicación **Spring Boot de estilo empresarial** para la gestión de **destinos turísticos, paquetes de viaje, reservas y usuarios**, diseñada con arquitectura limpia, buenas prácticas y preparada para **API REST + Web (Thymeleaf)**.

---

## 📌 Visión General

Este proyecto representa una **plataforma de viajes** donde se gestionan:

- Destinos turísticos
- Paquetes asociados a destinos
- Usuarios del sistema
- Reservas de viajes
- Servicios adicionales por reserva

El enfoque principal es **arquitectónico y técnico**, simulando un proyecto real de empresa, **no un ejemplo académico**.

---

## 🎯 Objetivos del Proyecto

- Aplicar **arquitectura limpia**
- Diseñar APIs REST consistentes
- Centralizar manejo de excepciones
- Separar entidades de DTOs
- Implementar validaciones robustas
- Usar migraciones de base de datos
- Preparar base para UI Web moderna

---

## 🧩 Alcance Funcional

### Incluido
- CRUD completo de Destinos
- Gestión de Paquetes turísticos
- Gestión de Usuarios
- Creación de Reservas
- Asociación de Servicios adicionales
- Manejo de estados (ACT, INA, etc.)
- Manejo centralizado de errores

### No incluido (por ahora)
- Autenticación y autorización
- Pagos en línea
- Integraciones externas

---

## 🏗️ Arquitectura del Sistema

Arquitectura **por capas**, estándar en proyectos empresariales:

Controller
↓
Service
↓
Repository
↓
Database


Capas adicionales:
- DTOs (Request / Response)
- Mappers
- Exception Handling
- Utilidades y constantes

---

## 🧠 Modelo de Dominio (alto nivel)

Entidades principales:

- **Destino**
- **Paquete**
- **Usuario**
- **Reserva**
- **ServicioAdicional**
- **ReservaServicio** (tabla intermedia)

La relación principal es:

- Un **Destino** tiene muchos **Paquetes**
- Un **Usuario** realiza **Reservas**
- Una **Reserva** puede incluir múltiples **Servicios adicionales**

> El detalle completo se encuentra en `docs/domain-overview.md`

---

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **Spring Boot**
- Spring Web (REST)
- Spring Data JPA
- Hibernate ORM
- MySQL 8
- Flyway (migraciones)
- Thymeleaf (Web MVC)
- Flowbite + Tailwind CSS
- Lombok
- Maven

---

## 📂 Estructura del Proyecto

```text
src/main/java/com/ptirado/nmviajes
├── controller
│   ├── api
│   └── web
├── service
│   └── impl
├── repository
├── entity
├── dto
│   ├── api
│   └── form
├── mapper
├── exception
│   └── api
├── util
├── constants
├── config
└── scheduler
