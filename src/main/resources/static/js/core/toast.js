/**
 * ============================================================================
 * MODULO: Sistema de Notificaciones Toast
 * ============================================================================
 * @namespace NMViajes.Toast
 *
 * Sistema de notificaciones estilo "toast" (mensajes emergentes temporales).
 * Usa estilos compatibles con Tailwind CSS y Flowbite.
 *
 * ============================================================================
 * USO RAPIDO:
 * ============================================================================
 *
 *   NMViajes.Toast.success('Operacion exitosa!');
 *   NMViajes.Toast.error('Ocurrio un error');
 *   NMViajes.Toast.warning('Atencion: stock bajo');
 *   NMViajes.Toast.info('Tienes 3 items en el carrito');
 *
 * ============================================================================
 * METODOS DISPONIBLES:
 * ============================================================================
 *
 *   success(mensaje, opciones?)  - Toast verde con icono de check
 *   error(mensaje, opciones?)    - Toast rojo con icono de X
 *   warning(mensaje, opciones?)  - Toast naranja con icono de alerta
 *   info(mensaje, opciones?)     - Toast azul con icono de info
 *   show(mensaje, tipo, opciones?) - Metodo generico
 *
 * Opciones disponibles:
 *   { duration: 5000 }  - Duracion en ms (default: 4000)
 *   { duration: 0 }     - Toast permanente (solo cierra con click)
 *
 * ============================================================================
 * DEPENDENCIAS:
 * ============================================================================
 *   - Tailwind CSS (clases de estilos)
 *   - NMViajes.Utils (opcional, para escapeHtml)
 *
 * ============================================================================
 * CARACTERISTICAS:
 * ============================================================================
 *   - Auto-cierre configurable (default 4 segundos)
 *   - Boton de cierre manual (X)
 *   - Animaciones de entrada/salida
 *   - Multiples toasts apilados
 *   - Prevencion XSS (escapa HTML del mensaje)
 *   - Crea el contenedor automaticamente si no existe
 *
 * ============================================================================
 * PERSONALIZACION:
 * ============================================================================
 *
 * El contenedor de toasts se posiciona en top-right por defecto.
 * Puedes crear un contenedor manualmente con id="toast-container" y
 * posicionarlo donde prefieras:
 *
 *   <div id="toast-container" class="fixed bottom-5 left-5 z-50"></div>
 *
 * ============================================================================
 * ARCHIVOS QUE USAN ESTE MODULO:
 * ============================================================================
 *   - form-handler.js     -> notificar exito/error de formularios
 *   - paquete-buscador.js -> notificar resultados de busqueda
 *   - (cualquier modulo que necesite mostrar mensajes al usuario)
 *
 * ============================================================================
 */
window.NMViajes = window.NMViajes || {};

NMViajes.Toast = (function() {
    'use strict';

    // ========================================================================
    // CONFIGURACION
    // ========================================================================

    /**
     * Contenedor de toasts. Se crea automaticamente si no existe.
     * @type {HTMLElement|null}
     */
    let container = null;

    /**
     * Iconos SVG para cada tipo de toast.
     * Estos iconos son de Heroicons (compatibles con Tailwind).
     */
    const ICONS = {
        success: `<svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path></svg>`,
        error: `<svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path></svg>`,
        warning: `<svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"></path></svg>`,
        info: `<svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"></path></svg>`
    };

    /**
     * Clases CSS para el icono segun el tipo de toast.
     * Usa clases de Tailwind para colores de fondo e icono.
     */
    const COLORS = {
        success: 'text-green-500 bg-green-100',
        error: 'text-red-500 bg-red-100',
        warning: 'text-orange-500 bg-orange-100',
        info: 'text-blue-500 bg-blue-100'
    };

    /**
     * Configuracion por defecto.
     */
    const CONFIG = {
        duration: 4000,      // Duracion en ms (0 = permanente)
        position: 'top-right' // Posicion (solo si se crea el contenedor)
    };

    // ========================================================================
    // FUNCIONES PRIVADAS
    // ========================================================================

    /**
     * Obtiene o crea el contenedor de toasts.
     *
     * Si no existe un elemento con id="toast-container", lo crea
     * automaticamente en la esquina superior derecha.
     *
     * @returns {HTMLElement} El contenedor de toasts
     */
    function getContainer() {
        if (!container) {
            container = document.getElementById('toast-container');
            if (!container) {
                container = document.createElement('div');
                container.id = 'toast-container';
                container.className = 'fixed top-5 right-5 z-50 flex flex-col gap-2';
                document.body.appendChild(container);
            }
        }
        return container;
    }

    /**
     * Remueve un toast del DOM con animacion de salida.
     *
     * @param {HTMLElement} toast - El elemento toast a remover
     */
    function removeToast(toast) {
        // Animacion de salida
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        toast.style.transition = 'all 0.3s ease-out';

        // Remover del DOM despues de la animacion
        setTimeout(() => toast.remove(), 300);
    }

    // ========================================================================
    // FUNCION PRINCIPAL
    // ========================================================================

    /**
     * Muestra una notificacion toast.
     *
     * @param {string} message - Mensaje a mostrar (se escapa para prevenir XSS)
     * @param {string} type - Tipo: 'success', 'error', 'warning', 'info'
     * @param {object} options - Opciones adicionales
     * @param {number} options.duration - Duracion en ms (0 = permanente)
     * @returns {HTMLElement} El elemento toast creado
     *
     * @example
     * Toast.show('Guardado correctamente', 'success');
     * Toast.show('Error de conexion', 'error', { duration: 6000 });
     */
    function show(message, type = 'info', options = {}) {
        const duration = options.duration !== undefined ? options.duration : CONFIG.duration;
        const toastContainer = getContainer();

        // Crear elemento toast
        const toast = document.createElement('div');
        toast.className = 'flex items-center w-full max-w-xs p-4 text-gray-500 bg-white rounded-lg shadow border border-gray-100';
        toast.style.animation = 'toast-fade-in 0.3s ease-out';

        // Escapar mensaje para prevenir XSS
        const safeMessage = NMViajes.Utils ? NMViajes.Utils.escapeHtml(message) : message;

        // Construir HTML del toast
        toast.innerHTML = `
            <div class="inline-flex items-center justify-center flex-shrink-0 w-8 h-8 ${COLORS[type] || COLORS.info} rounded-lg">
                ${ICONS[type] || ICONS.info}
            </div>
            <div class="ms-3 text-sm font-normal">${safeMessage}</div>
            <button type="button" class="ms-auto -mx-1.5 -my-1.5 bg-white text-gray-400 hover:text-gray-900 rounded-lg focus:ring-2 focus:ring-gray-300 p-1.5 hover:bg-gray-100 inline-flex items-center justify-center h-8 w-8" aria-label="Cerrar">
                <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
            </button>
        `;

        // Agregar al contenedor
        toastContainer.appendChild(toast);

        // Configurar boton de cierre
        toast.querySelector('button').addEventListener('click', () => removeToast(toast));

        // Auto-cerrar despues de la duracion (si no es permanente)
        if (duration > 0) {
            setTimeout(() => removeToast(toast), duration);
        }

        return toast;
    }

    // ========================================================================
    // METODOS DE CONVENIENCIA
    // ========================================================================

    /**
     * Muestra un toast de exito (verde).
     * @param {string} message - Mensaje a mostrar
     * @param {object} options - Opciones adicionales
     */
    function success(message, options) {
        return show(message, 'success', options);
    }

    /**
     * Muestra un toast de error (rojo).
     * @param {string} message - Mensaje a mostrar
     * @param {object} options - Opciones adicionales
     */
    function error(message, options) {
        return show(message, 'error', options);
    }

    /**
     * Muestra un toast de advertencia (naranja).
     * @param {string} message - Mensaje a mostrar
     * @param {object} options - Opciones adicionales
     */
    function warning(message, options) {
        return show(message, 'warning', options);
    }

    /**
     * Muestra un toast informativo (azul).
     * @param {string} message - Mensaje a mostrar
     * @param {object} options - Opciones adicionales
     */
    function info(message, options) {
        return show(message, 'info', options);
    }

    // ========================================================================
    // ESTILOS DE ANIMACION
    // ========================================================================

    /**
     * Inyecta los estilos CSS necesarios para las animaciones.
     * Solo se ejecuta una vez (verifica si ya existen).
     */
    if (!document.getElementById('toast-styles')) {
        const style = document.createElement('style');
        style.id = 'toast-styles';
        style.textContent = `
            @keyframes toast-fade-in {
                from { opacity: 0; transform: translateX(100%); }
                to { opacity: 1; transform: translateX(0); }
            }
        `;
        document.head.appendChild(style);
    }

    // ========================================================================
    // API PUBLICA
    // ========================================================================

    return {
        show,
        success,
        error,
        warning,
        info
    };
})();
