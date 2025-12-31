/**
 * ============================================================================
 * FORMULARIO: Contacto
 * ============================================================================
 *
 * Usa el modulo base FormHandler para manejar el formulario.
 * Solo necesitas configurar los parametros abajo.
 *
 * ARCHIVOS RELACIONADOS:
 *   - Backend: ContactoRequest.java     (define los campos y validaciones)
 *   - Backend: ContactoController.java  (endpoint POST /api/v1/contacto)
 *   - HTML:    layout/main.html         (formulario en modal)
 *
 * ============================================================================
 * SI AGREGAS UN CAMPO NUEVO:
 * ============================================================================
 *   1. Agregalo en 'campos' abajo (debe coincidir con ContactoRequest.java)
 *   2. Agregalo en ContactoRequest.java con sus validaciones (@NotBlank, @Size, etc)
 *   3. Agrega el input en main.html con:
 *      - id="contact-{nombreCampo}"
 *      - name="{nombreCampo}"
 *   4. Agrega el <p> de error con id="error-{nombreCampo}"
 *
 * EJEMPLO para agregar campo "asunto":
 *   - Aqui:     campos: [..., 'asunto']
 *   - Java:     private String asunto; con @NotBlank
 *   - HTML:     <input id="contact-asunto" name="asunto">
 *               <p id="error-asunto" class="text-red-600 hidden"></p>
 *
 * ============================================================================
 */
NMViajes.FormHandler.crear({

    // ========================================================================
    // CONFIGURACION BASICA (requerida)
    // ========================================================================

    // Nombre del modulo - se registra como NMViajes.ContactoForm
    nombre: 'ContactoForm',

    // Endpoint del backend - debe coincidir con @RequestMapping en ContactoController
    apiUrl: '/api/v1/contacto',

    // ID del formulario en el HTML - debe existir un <form id="contact-form">
    formId: 'contact-form',

    // ========================================================================
    // CAMPOS DEL FORMULARIO
    // IMPORTANTE: Estos nombres DEBEN coincidir con ContactoRequest.java
    // ========================================================================
    campos: [
        'nombre',    // ContactoRequest.java -> private String nombre
        'email',     // ContactoRequest.java -> private String email
        'telefono',  // ContactoRequest.java -> private String telefono
        'mensaje'    // ContactoRequest.java -> private String mensaje
    ],

    // ========================================================================
    // CONFIGURACION OPCIONAL
    // ========================================================================

    // ID del modal Flowbite - se cierra automaticamente al enviar con exito
    modalId: 'contact-modal',

    // Mensajes personalizados
    mensajes: {
        exito: 'Mensaje enviado correctamente. Nos pondremos en contacto contigo pronto.',
        btnNormal: 'Enviar mensaje',
        btnEnviando: 'Enviando...'
    }

});
