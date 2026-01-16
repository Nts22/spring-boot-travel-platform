# Fragmentos Reutilizables de Thymeleaf

Esta carpeta contiene fragmentos reutilizables de Thymeleaf para mantener las vistas del admin de forma consistente y mantenible.

## Archivos

### 0. `form-layout.html` ⭐ NUEVO
Fragmentos reutilizables para formularios del admin con diseño centrado y consistente.

#### Fragmento: `formContainer`
Contenedor principal para formularios centrados con botón de retorno.

**Uso:**
```html
<th:block th:replace="~{fragments/form-layout :: formContainer(
    backUrl='/admin/usuarios',
    backText='Volver a usuarios',
    titulo='Nuevo Usuario'
)}">
    <th:block th:fragment="formContent">
        <form>
            <!-- Campos del formulario aquí -->
        </form>
    </th:block>
</th:block>
```

**Características:**
- Layout centrado con `max-width-3xl` y `mx-auto`
- Padding de 8 (más espacioso)
- Botón de retorno con ícono
- Título grande y prominente
- Fondo blanco con sombra y bordes redondeados

#### Fragmento: `textInput`
Campo de texto reutilizable.

**Uso:**
```html
<th:block th:replace="~{fragments/form-layout :: textInput(
    id='nombre',
    label='Nombre',
    name='nombre',
    value=${entity.nombre},
    required=true,
    placeholder='Ingrese el nombre'
)}" />
```

#### Fragmento: `emailInput`
Campo de email.

**Uso:**
```html
<th:block th:replace="~{fragments/form-layout :: emailInput(
    id='email',
    label='Email',
    name='email',
    value=${entity.email},
    required=true
)}" />
```

#### Fragmento: `passwordInput`
Campo de contraseña con texto de ayuda.

**Uso:**
```html
<th:block th:replace="~{fragments/form-layout :: passwordInput(
    id='password',
    label='Contraseña',
    name='password',
    required=true,
    helpText='Mínimo 6 caracteres'
)}" />
```

#### Fragmento: `numberInput`
Campo numérico con opciones de min, max y step.

**Uso:**
```html
<th:block th:replace="~{fragments/form-layout :: numberInput(
    id='precio',
    label='Precio',
    name='precio',
    value=${entity.precio},
    required=true,
    min=0,
    step=0.01
)}" />
```

#### Fragmento: `dateInput`
Campo de fecha.

**Uso:**
```html
<th:block th:replace="~{fragments/form-layout :: dateInput(
    id='fechaInicio',
    label='Fecha Inicio',
    name='fechaInicio',
    value=${entity.fechaInicio},
    required=true
)}" />
```

#### Fragmento: `textareaInput`
Campo de texto multilínea.

**Uso:**
```html
<th:block th:replace="~{fragments/form-layout :: textareaInput(
    id='descripcion',
    label='Descripción',
    name='descripcion',
    value=${entity.descripcion},
    rows=5
)}" />
```

#### Fragmento: `estadoSelect`
Select predefinido para Activo/Inactivo.

**Uso:**
```html
<th:block th:replace="~{fragments/form-layout :: estadoSelect(value=${entity.estado})}" />
```

#### Fragmento: `formActions`
Botones de acción del formulario (Cancelar/Guardar).

**Uso:**
```html
<th:block th:replace="~{fragments/form-layout :: formActions(
    cancelUrl='/admin/usuarios',
    submitText='Guardar Usuario'
)}" />
```

---

### 1. `pagination.html`
Fragmento de paginación reutilizable con navegación por páginas.

**Uso:**
```html
<th:block th:replace="~{fragments/pagination :: pagination(
    baseUrl='/admin/usuarios',
    currentPage=${currentPage},
    totalPages=${totalPages},
    filtroEstado=${filtroEstado},
    filtroBusqueda=${filtroBusqueda}
)}" />
```

**Parámetros:**
- `baseUrl` (requerido): URL base para la paginación (ej: '/admin/usuarios')
- `currentPage` (requerido): Página actual (0-indexed)
- `totalPages` (requerido): Total de páginas
- `filtroEstado` (opcional): Estado del filtro activo
- `filtroBusqueda` (opcional): Texto de búsqueda activo

---

### 2. `filters.html`
Fragmentos para filtros de búsqueda y badges de estado.

#### Fragmento: `simpleFilter`
Formulario de filtros con búsqueda y selector de estado.

**Uso:**
```html
<th:block th:replace="~{fragments/filters :: simpleFilter(
    actionUrl='/admin/usuarios',
    filtroBusqueda=${filtroBusqueda},
    filtroEstado=${filtroEstado},
    placeholder='Buscar por nombre, apellido o email...'
)}" />
```

**Parámetros:**
- `actionUrl` (requerido): URL del formulario
- `filtroBusqueda` (opcional): Valor actual del filtro de búsqueda
- `filtroEstado` (opcional): Valor actual del filtro de estado
- `placeholder` (requerido): Texto placeholder del input de búsqueda

#### Fragmento: `estadoBadge`
Badge visual para mostrar el estado (Activo/Inactivo).

**Uso:**
```html
<th:block th:replace="~{fragments/filters :: estadoBadge(estado=${item.estado})}" />
```

#### Fragmento: `toggleButton`
Botón para alternar el estado de un registro.

**Uso:**
```html
<th:block th:replace="~{fragments/filters :: toggleButton(
    id=${item.id},
    estado=${item.estado},
    entityType='usuario'
)}" />
```

**entityType** puede ser: `usuario`, `destino`, `paquete`, `servicio`

---

### 3. `admin-common.html`
Fragmentos comunes para las vistas de administración.

#### Fragmento: `pageHeader`
Encabezado de página con título, subtítulo y botón de acción.

**Uso:**
```html
<th:block th:replace="~{fragments/admin-common :: pageHeader(
    titulo='Gestión de Usuarios',
    subtitulo='Administra los usuarios del sistema',
    btnUrl='/admin/usuarios/nuevo',
    btnTexto='Nuevo Usuario'
)}" />
```

#### Fragmento: `paginationContainer`
Contenedor completo de paginación con navegación (equivalente a `pagination.html`).

**Uso:**
```html
<th:block th:replace="~{fragments/admin-common :: paginationContainer(
    baseUrl='/admin/usuarios',
    currentPage=${currentPage},
    totalPages=${totalPages},
    filtroEstado=${filtroEstado},
    filtroBusqueda=${filtroBusqueda}
)}" />
```

#### Fragmento: `emptyRow`
Fila de tabla vacía para cuando no hay registros.

**Uso:**
```html
<th:block th:replace="~{fragments/admin-common :: emptyRow(
    colspan=7,
    mensaje='No hay registros'
)}" />
```

#### Fragmento: `toggleScript`
Script JavaScript genérico para manejar el toggle de estado.

**Uso:**
```html
<th:block th:replace="~{fragments/admin-common :: toggleScript}" />
```

**Nota:** Los botones deben tener los atributos:
- `data-id`: ID del registro
- `data-estado`: Estado actual ('ACT' o 'INA')
- `data-entity`: Tipo de entidad ('usuario', 'destino', 'paquete', 'servicio')

#### Fragmento: `precio`
Muestra un precio formateado.

**Uso:**
```html
<th:block th:replace="~{fragments/admin-common :: precio(valor=${item.precio})}" />
```

#### Fragmento: `stockBadge`
Badge visual para mostrar el stock disponible con colores según cantidad.

**Uso:**
```html
<th:block th:replace="~{fragments/admin-common :: stockBadge(stock=${item.stockDisponible})}" />
```

#### Fragmento: `editButton`
Botón de editar estandarizado.

**Uso:**
```html
<th:block th:replace="~{fragments/admin-common :: editButton(
    url='/admin/usuarios/' + ${item.id} + '/editar'
)}" />
```

---

## Ejemplo Completo

Aquí hay un ejemplo de cómo usar los fragmentos en una vista de lista:

```html
<!-- Vista de lista de usuarios -->
<main class="flex-1 p-6" xmlns:th="http://www.thymeleaf.org">
    <!-- Header -->
    <th:block th:replace="~{fragments/admin-common :: pageHeader(
        titulo='Gestión de Usuarios',
        subtitulo='Administra los usuarios del sistema',
        btnUrl='/admin/usuarios/nuevo',
        btnTexto='Nuevo Usuario'
    )}" />

    <!-- Filtros -->
    <th:block th:replace="~{fragments/filters :: simpleFilter(
        actionUrl='/admin/usuarios',
        filtroBusqueda=${filtroBusqueda},
        filtroEstado=${filtroEstado},
        placeholder='Buscar por nombre, apellido o email...'
    )}" />

    <!-- Tabla de usuarios -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 overflow-hidden">
        <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
                <tr th:if="${#lists.isEmpty(usuarios)}">
                    <th:block th:replace="~{fragments/admin-common :: emptyRow(
                        colspan=4,
                        mensaje='No hay usuarios registrados'
                    )}" />
                </tr>
                <tr th:each="usuario : ${usuarios}" class="hover:bg-gray-50">
                    <td th:text="${usuario.idUsuario}">1</td>
                    <td th:text="${usuario.nombre}">Nombre</td>
                    <td>
                        <th:block th:replace="~{fragments/filters :: estadoBadge(
                            estado=${usuario.estado}
                        )}" />
                    </td>
                    <td>
                        <th:block th:replace="~{fragments/admin-common :: editButton(
                            url='/admin/usuarios/' + ${usuario.idUsuario} + '/editar'
                        )}" />
                        <button type="button"
                                th:data-id="${usuario.idUsuario}"
                                th:data-estado="${usuario.estado}"
                                data-entity="usuario"
                                onclick="toggleEstado(this)"
                                th:class="(${usuario.estado == 'ACT'} ? 'text-red-600' : 'text-green-600')"
                                th:text="${usuario.estado == 'ACT'} ? 'Desactivar' : 'Activar'">
                        </button>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>

    <!-- Paginación -->
    <th:block th:replace="~{fragments/admin-common :: paginationContainer(
        baseUrl='/admin/usuarios',
        currentPage=${currentPage},
        totalPages=${totalPages},
        filtroEstado=${filtroEstado},
        filtroBusqueda=${filtroBusqueda}
    )}" />
</main>

<!-- Script de toggle -->
<th:block th:replace="~{fragments/admin-common :: toggleScript}" />
```

---

## Beneficios

1. **Consistencia**: Todos los componentes se ven y funcionan igual en todas las vistas
2. **Mantenibilidad**: Cambios en un fragmento se reflejan en todas las vistas que lo usan
3. **Reusabilidad**: No necesitas copiar y pegar código entre vistas
4. **Legibilidad**: Las vistas son más limpias y fáciles de entender
5. **Facilidad de testing**: Los componentes están centralizados

---

## Convenciones

1. Usa `th:block` con `th:replace` para incluir fragmentos
2. Los parámetros requeridos deben estar presentes siempre
3. Los parámetros opcionales pueden ser `null` o estar vacíos
4. Usa nombres descriptivos para los parámetros
5. Documenta cualquier nuevo fragmento que agregues

---

## Actualizado: Enero 2026
