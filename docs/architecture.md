# Architecture Overview – Plataforma de Viajes

Este documento describe la **arquitectura técnica** de la plataforma de viajes, explicando la organización por capas, responsabilidades de cada componente y el flujo general de una solicitud.

La arquitectura está diseñada para ser:
- Escalable
- Mantenible
- Clara para equipos nuevos
- Reutilizable como plantilla en futuros proyectos

---

## 1. Enfoque Arquitectónico

El sistema sigue una arquitectura **monolítica modular**, basada en capas bien definidas, alineada con buenas prácticas de Spring Boot y principios de diseño empresarial.

Características principales:
- Separación clara de responsabilidades
- Dominio centralizado en entidades JPA
- Servicios reutilizables para API REST y MVC Web
- Controladores independientes para API y Web
- Manejo centralizado de excepciones
- Mapeo explícito entre entidades y DTOs

---

## 2. Capas de la Aplicación

La aplicación se organiza en las siguientes capas:

Controller (API / Web)
↓
Service (Negocio)
↓
Repository (Persistencia)
↓
Database


Cada capa cumple una responsabilidad específica y no invade funciones de otras capas.

---

## 3. Capa Controller

### 3.1 API REST Controllers

Paquete:
com.ptirado.nmviajes.controller.api

Responsabilidades:
- Exponer endpoints REST
- Validar entrada con `@Valid`
- Retornar códigos HTTP adecuados
- No contener lógica de negocio

Ejemplo:
- `DestinoController`
- `PaqueteController`

Los endpoints están versionados mediante constantes (`ApiPaths`).

---

### 3.2 Web MVC Controllers

Paquete:
com.ptirado.nmviajes.controller.web

Responsabilidades:
- Renderizar vistas Thymeleaf
- Interactuar con DTOs de formulario
- Preparar modelos para la vista
- Delegar toda lógica al servicio

Esta capa es independiente de la API REST.

---

## 4. Capa Service (Negocio)

Paquete:
com.ptirado.nmviajes.service
com.ptirado.nmviajes.service.impl


Responsabilidades:
- Contener reglas de negocio
- Orquestar repositorios
- Validar consistencia del dominio
- Lanzar excepciones de negocio
- Trabajar con entidades JPA

El servicio actúa como el **núcleo del sistema**.

Un mismo servicio puede ser usado por:
- Controladores API
- Controladores Web

---

## 5. Capa Repository (Persistencia)

Paquete:
com.ptirado.nmviajes.repository


Responsabilidades:
- Acceso a datos mediante Spring Data JPA
- Definir consultas derivadas o personalizadas
- No contener lógica de negocio

Ejemplo:
- `DestinoRepository`
- `ReservaRepository`

---

## 6. Capa Entity (Dominio Persistente)

Paquete:
com.ptirado.nmviajes.entity


Responsabilidades:
- Representar el modelo del dominio
- Mapear tablas de base de datos
- Definir relaciones JPA
- Contener anotaciones de persistencia

Las entidades:
- No contienen lógica de presentación
- No exponen detalles de la API

---

## 7. DTOs y ViewModels

### 7.1 DTOs para API REST

Paquetes:
dto/api/request
dto/api/response


Uso:
- Aislar la API del modelo interno
- Controlar datos de entrada y salida
- Evitar exponer entidades directamente

---

### 7.2 DTOs para Web (Form / ViewModel)

Paquetes:
dto/form
viewmodel


Uso:
- Manejo de formularios Thymeleaf
- Preparación de datos para vistas
- Independientes de la API REST

---

## 8. Capa Mapper

Paquete:
com.ptirado.nmviajes.mapper


Responsabilidades:
- Convertir entre entidades y DTOs
- Evitar lógica de conversión en servicios
- Mantener el código limpio y desacoplado

Ejemplos:
- `DestinoMapper`
- `PaqueteMapper`

---

## 9. Manejo de Excepciones

### 9.1 Excepciones de Negocio

Paquete:
com.ptirado.nmviajes.exception.api


Responsabilidades:
- Interceptar excepciones
- Traducirlas a respuestas HTTP
- Estandarizar el formato de error
- Evitar try/catch en controladores

---

## 10. Utilidades y Configuración

### 10.1 Utilidades

Paquete:
com.ptirado.nmviajes.util


Incluye:
- `MessageUtils`
- `DateUtils`

---

### 10.2 Configuración

Paquete:
com.ptirado.nmviajes.config


Incluye:
- Configuración de mensajes
- Configuración regional
- Otras configuraciones transversales

---

## 11. Flujo de una Solicitud API

Ejemplo: `GET /api/v1/destinos/1`

1. Controller recibe la solicitud
2. Service ejecuta la lógica
3. Repository accede a la base de datos
4. Mapper convierte entidad a DTO
5. Controller retorna `ResponseEntity`
6. En caso de error, el ExceptionHandler responde

---

## 12. Principios Aplicados

- Single Responsibility Principle
- Separation of Concerns
- Clean Architecture (adaptada a monolito)
- Defensive Programming
- Internationalización de mensajes
- Diseño orientado a mantenimiento

---

Documentos relacionados:
- `domain-overview.md`
- `database-schema.md`
- `api-conventions.md`
