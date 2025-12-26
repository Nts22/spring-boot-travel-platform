# API Conventions – Plataforma de Viajes

Este documento define las **convenciones oficiales** para el diseño, implementación y consumo de la API REST de la plataforma.

El objetivo es:
- Mantener consistencia entre endpoints
- Facilitar mantenimiento y escalabilidad
- Garantizar una experiencia clara para frontend y clientes externos
- Servir como plantilla reutilizable en futuros proyectos

---

## 1. Convenciones Generales

### 1.1 Base URL y Versionado

Todas las APIs deben estar versionadas desde la URL base:
/api/v1

Ejemplo:
/api/v1/destinos
/api/v1/paquetes


El versionado permite:
- Evolucionar la API sin romper clientes
- Mantener compatibilidad hacia atrás

---

### 1.2 Convención de Rutas

- Sustantivos en plural
- Minúsculas
- Sin verbos en la URL

Correcto:
GET /api/v1/destinos
POST /api/v1/destinos
GET /api/v1/destinos/{id}
PUT /api/v1/destinos/{id}
DELETE /api/v1/destinos/{id}

Incorrecto:
/getDestinos
/crearDestino
/update-destino


---

## 2. Métodos HTTP

| Método | Uso |
|------|----|
| GET | Obtener recursos |
| POST | Crear recursos |
| PUT | Actualizar completamente un recurso |
| PATCH | Actualizar parcialmente (opcional) |
| DELETE | Eliminar recursos |

---

## 3. Convenciones de Status Codes

### 3.1 Respuestas Exitosas

| Código | Uso |
|-----|----|
| 200 OK | Operación exitosa |
| 201 Created | Recurso creado |
| 204 No Content | Eliminación exitosa |

---

### 3.2 Errores del Cliente

| Código | Uso |
|-----|----|
| 400 Bad Request | Error de validación |
| 404 Not Found | Recurso no encontrado |
| 409 Conflict | Conflicto de negocio |
| 422 Unprocessable Entity | Datos válidos pero no procesables |

---

### 3.3 Errores del Servidor

| Código | Uso |
|-----|----|
| 500 Internal Server Error | Error inesperado |
| 503 Service Unavailable | Servicio temporalmente no disponible |

---

## 4. Formato Estándar de Respuesta

### 4.1 Respuesta Exitosa (Ejemplo)

```json
{
  "idDestino": 1,
  "nombre": "Cusco",
  "pais": "Perú",
  "estado": "ACT",
  "fechaCreacion": "2025-11-16T14:56:20"
}

### 4.2 Respuesta de Error (Formato Único)

Todas las respuestas de error deben seguir esta estructura:
{
  "timestamp": "2025-12-10T00:29:09",
  "status": 404,
  "error": "NotFoundException",
  "message": "Destino no encontrado",
  "path": "/api/v1/destinos/10"
}

### 4.3 Errores de Validación
{
  "timestamp": "2025-12-10T00:31:12",
  "status": 400,
  "error": "ValidationError",
  "message": "Error de validación",
  "errors": {
    "nombre": "El nombre es obligatorio",
    "pais": "El país es obligatorio"
  },
  "path": "/api/v1/destinos"
}
```
### 5. Validaciones
Todas las entradas deben validarse con @Valid

Las reglas se definen en los DTOs

No se valida en entidades ni controladores
Ejemplo:
@NotBlank
@Size(min = 3, max = 100)
private String nombre;

## 6. Manejo de Excepciones
### 6.1 Excepciones de Negocio

Todas las excepciones de negocio deben:

	Extender de ApiException

		Definir:

			HttpStatus

			messageKey
Ejemplo:
throw new NotFoundException(MessageKeys.DESTINO_NOT_FOUND);

### 6.2 Exception Handler Centralizado

Todas las excepciones se capturan en ApiExceptionHandler

No se usan try/catch en controladores

Los mensajes se obtienen desde archivos messages_*.properties

## 7. Convención de Mensajes

Los mensajes no deben estar hardcodeados

Se usan claves desde messages_es.properties
Ejemplo:
destino.notfound=Destino no encontrado
destino.duplicate=Ya existe un destino con ese nombre

## 8. Naming Conventions
### 8.1 DTOs
| Tipo      | Convención        |
| --------- | ----------------- |
| Request   | `DestinoRequest`  |
| Response  | `DestinoResponse` |
| Form      | `DestinoForm`     |
| ViewModel | `DestinoView`     |

### 8.2 Controladores

Sufijo Controller

Separados por tipo:
	controller.api
	controller.web

## 9. Buenas Prácticas

Nunca exponer entidades directamente
No devolver Optional en la API
Evitar lógica en controladores
Mantener endpoints simples
Centralizar validaciones y errores

## 10. Principios Clave

Consistencia
Claridad
Predictibilidad
Escalabilidad
Mantenibilidad