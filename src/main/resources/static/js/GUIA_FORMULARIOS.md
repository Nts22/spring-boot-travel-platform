# Guia para crear formularios AJAX

Esta guia explica como crear formularios que envian datos al backend y muestran errores de validacion automaticamente.

## Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│  FRONTEND (JavaScript)                                          │
│  - Solo envia datos al servidor                                 │
│  - Muestra errores que vienen del backend                       │
│  - NO hace validaciones                                         │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│  BACKEND (Java + Bean Validation)                               │
│  - Valida los datos con anotaciones (@NotBlank, @Size, etc)     │
│  - Retorna errores en formato JSON                              │
└─────────────────────────────────────────────────────────────────┘
```

## Estructura de archivos

```
src/main/resources/static/js/
├── core/
│   ├── toast.js           # Sistema de notificaciones
│   └── form-handler.js    # Modulo base para formularios
└── modules/
    ├── contacto-form.js   # Ejemplo: formulario de contacto
    └── {tu-form}.js       # Tu nuevo formulario
```

---

## Paso 1: Crear el DTO en Java

Crea una clase Request con las validaciones usando Bean Validation.

**Archivo:** `src/main/java/.../dto/api/request/ReservaRequest.java`

```java
package com.ptirado.nmviajes.dto.api.request;

import static com.ptirado.nmviajes.constants.ValidationConstants.*;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombreCliente;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    private String email;

    @NotNull(message = "El paquete es obligatorio")
    private Integer idPaquete;

    @NotNull(message = "La cantidad de pasajeros es obligatoria")
    @Min(value = 1, message = "Debe haber al menos 1 pasajero")
    private Integer cantidadPasajeros;
}
```

### Anotaciones de validacion disponibles

| Anotacion | Uso | Ejemplo |
|-----------|-----|---------|
| `@NotBlank` | Campo texto obligatorio | `@NotBlank(message = "...")` |
| `@NotNull` | Campo obligatorio (no texto) | `@NotNull(message = "...")` |
| `@Size` | Longitud min/max | `@Size(min = 2, max = 100)` |
| `@Email` | Formato email | `@Email(message = "...")` |
| `@Min` | Valor minimo | `@Min(value = 1)` |
| `@Max` | Valor maximo | `@Max(value = 100)` |
| `@Pattern` | Expresion regular | `@Pattern(regexp = "...")` |
| `@Future` | Fecha futura | `@Future` |
| `@FutureOrPresent` | Fecha presente o futura | `@FutureOrPresent` |

---

## Paso 2: Crear el Controller

**Archivo:** `src/main/java/.../controller/api/ReservaController.java`

```java
package com.ptirado.nmviajes.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ptirado.nmviajes.dto.api.request.ReservaRequest;
import com.ptirado.nmviajes.service.ReservaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<Void> crearReserva(@Valid @RequestBody ReservaRequest request) {
        reservaService.crearReserva(request);
        return ResponseEntity.ok().build();
    }
}
```

**IMPORTANTE:** El `@Valid` activa las validaciones del DTO.

---

## Paso 3: Crear el formulario HTML

**Archivo:** `src/main/resources/templates/reserva/form.html`

```html
<form id="reserva-form" class="space-y-4">

    <!-- Campo: nombreCliente -->
    <div>
        <label for="reserva-nombreCliente" class="block mb-2 text-sm font-medium">
            Nombre completo
        </label>
        <input type="text"
               name="nombreCliente"
               id="reserva-nombreCliente"
               class="border border-gray-300 rounded-lg w-full p-2.5">
        <p id="error-nombreCliente" class="mt-1 text-sm text-red-600 hidden"></p>
    </div>

    <!-- Campo: email -->
    <div>
        <label for="reserva-email" class="block mb-2 text-sm font-medium">
            Correo electronico
        </label>
        <input type="text"
               name="email"
               id="reserva-email"
               class="border border-gray-300 rounded-lg w-full p-2.5">
        <p id="error-email" class="mt-1 text-sm text-red-600 hidden"></p>
    </div>

    <!-- Campo: idPaquete -->
    <div>
        <label for="reserva-idPaquete" class="block mb-2 text-sm font-medium">
            Paquete
        </label>
        <select name="idPaquete"
                id="reserva-idPaquete"
                class="border border-gray-300 rounded-lg w-full p-2.5">
            <option value="">Selecciona un paquete</option>
            <option th:each="p : ${paquetes}" th:value="${p.id}" th:text="${p.nombre}"></option>
        </select>
        <p id="error-idPaquete" class="mt-1 text-sm text-red-600 hidden"></p>
    </div>

    <!-- Campo: cantidadPasajeros -->
    <div>
        <label for="reserva-cantidadPasajeros" class="block mb-2 text-sm font-medium">
            Cantidad de pasajeros
        </label>
        <input type="number"
               name="cantidadPasajeros"
               id="reserva-cantidadPasajeros"
               min="1"
               class="border border-gray-300 rounded-lg w-full p-2.5">
        <p id="error-cantidadPasajeros" class="mt-1 text-sm text-red-600 hidden"></p>
    </div>

    <!-- Boton submit -->
    <button type="submit" class="bg-red-600 text-white rounded-lg px-5 py-2.5 flex items-center gap-2">
        <svg class="btn-spinner hidden w-4 h-4 animate-spin" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <span class="btn-text">Reservar</span>
    </button>

</form>

<!-- Scripts necesarios -->
<script th:src="@{/js/core/toast.js}"></script>
<script th:src="@{/js/core/form-handler.js}"></script>
<script th:src="@{/js/modules/reserva-form.js}"></script>
```

### Convencion de IDs (MUY IMPORTANTE)

| Elemento | Patron | Ejemplo |
|----------|--------|---------|
| Formulario | `{nombre}-form` | `reserva-form` |
| Input | `{nombre}-{campo}` | `reserva-nombreCliente` |
| Mensaje error | `error-{campo}` | `error-nombreCliente` |

**Los nombres de campo deben coincidir EXACTAMENTE con el DTO Java.**

---

## Paso 4: Crear el archivo JavaScript

**Archivo:** `src/main/resources/static/js/modules/reserva-form.js`

```javascript
/**
 * ============================================================================
 * FORMULARIO: Reserva
 * ============================================================================
 *
 * ARCHIVOS RELACIONADOS:
 *   - Backend: ReservaRequest.java     (define los campos y validaciones)
 *   - Backend: ReservaController.java  (endpoint POST /api/v1/reservas)
 *   - HTML:    reserva/form.html       (formulario)
 *
 * SI AGREGAS UN CAMPO NUEVO:
 *   1. Agregalo en 'campos' abajo
 *   2. Agregalo en ReservaRequest.java con sus validaciones
 *   3. Agrega el input en el HTML con id="reserva-{campo}" y name="{campo}"
 *   4. Agrega el <p> de error con id="error-{campo}"
 *
 * ============================================================================
 */
NMViajes.FormHandler.crear({

    // Nombre del modulo - se registra como NMViajes.ReservaForm
    nombre: 'ReservaForm',

    // Endpoint del backend - debe coincidir con @RequestMapping
    apiUrl: '/api/v1/reservas',

    // ID del formulario en el HTML
    formId: 'reserva-form',

    // Campos del formulario - DEBEN coincidir con ReservaRequest.java
    campos: [
        'nombreCliente',      // ReservaRequest.nombreCliente
        'email',              // ReservaRequest.email
        'idPaquete',          // ReservaRequest.idPaquete
        'cantidadPasajeros'   // ReservaRequest.cantidadPasajeros
    ],

    // Mensajes personalizados (opcional)
    mensajes: {
        exito: 'Reserva creada correctamente!',
        btnNormal: 'Reservar',
        btnEnviando: 'Procesando...'
    }

});
```

---

## Opciones avanzadas

### Formulario en modal

Si el formulario esta dentro de un modal de Flowbite:

```javascript
NMViajes.FormHandler.crear({
    nombre: 'ContactoForm',
    apiUrl: '/api/v1/contacto',
    formId: 'contact-form',
    campos: ['nombre', 'email', 'mensaje'],

    // El modal se cierra automaticamente al enviar con exito
    modalId: 'contact-modal'
});
```

### Transformar datos antes de enviar

Si necesitas convertir tipos (ej: string a number):

```javascript
NMViajes.FormHandler.crear({
    nombre: 'ReservaForm',
    apiUrl: '/api/v1/reservas',
    formId: 'reserva-form',
    campos: ['nombreCliente', 'idPaquete', 'cantidadPasajeros'],

    // Funcion para transformar datos
    transformarDatos: function(datos, formData) {
        return {
            nombreCliente: datos.nombreCliente,
            idPaquete: parseInt(datos.idPaquete) || null,
            cantidadPasajeros: parseInt(datos.cantidadPasajeros) || null
        };
    }
});
```

### Ejecutar codigo despues del envio

```javascript
NMViajes.FormHandler.crear({
    nombre: 'ReservaForm',
    apiUrl: '/api/v1/reservas',
    formId: 'reserva-form',
    campos: ['nombreCliente', 'idPaquete'],

    // Se ejecuta despues del exito
    onExito: function() {
        // Redirigir a otra pagina
        window.location.href = '/reservas/confirmacion';
    },

    // Se ejecuta despues de un error
    onError: function(respuestaError) {
        console.log('Error:', respuestaError);
    }
});
```

### Todos los mensajes personalizables

```javascript
mensajes: {
    exito: 'Operacion realizada correctamente.',
    errorValidacion: 'Por favor, corrige los errores del formulario.',
    errorServidor: 'Error en el servidor. Por favor, intenta nuevamente.',
    errorConexion: 'Error de conexion. Por favor, verifica tu conexion.',
    btnEnviando: 'Procesando...',
    btnNormal: 'Enviar'
}
```

---

## Checklist rapido

Antes de probar tu formulario, verifica:

- [ ] DTO Java creado con validaciones (`@NotBlank`, `@Size`, etc)
- [ ] Controller con `@Valid @RequestBody`
- [ ] Formulario HTML con `id="{nombre}-form"`
- [ ] Cada input tiene `name="{campo}"` e `id="{nombre}-{campo}"`
- [ ] Cada input tiene su `<p id="error-{campo}">`
- [ ] Boton submit tiene `.btn-text` y `.btn-spinner`
- [ ] Scripts incluidos: `toast.js`, `form-handler.js`, `{tu-form}.js`
- [ ] Archivo JS con `NMViajes.FormHandler.crear({...})`
- [ ] Los nombres en `campos` coinciden con el DTO Java

---

## Como funciona internamente

1. Usuario hace clic en "Enviar"
2. JavaScript envia POST al endpoint con JSON
3. Spring valida el DTO con Bean Validation
4. Si hay errores, `ApiExceptionHandler` retorna:
   ```json
   {
     "status": 400,
     "error": "ValidationError",
     "errors": {
       "nombreCliente": "El nombre es obligatorio",
       "email": "El email debe tener un formato valido"
     }
   }
   ```
5. JavaScript muestra cada error en su campo correspondiente
6. Si no hay errores, muestra mensaje de exito

---

## Ejemplo completo: Formulario de contacto

Ver archivo: `src/main/resources/static/js/modules/contacto-form.js`
