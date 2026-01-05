/**
 * ============================================================================
 * MODULO: Utilidades de UI
 * ============================================================================
 * @namespace NMViajes.Utils
 *
 * Funciones utilitarias de uso general para la interfaz de usuario.
 * Este modulo NO contiene formateo de datos (fechas, precios) ya que eso
 * se maneja desde el backend (FormatConfig.java).
 *
 * ============================================================================
 * FUNCIONES DISPONIBLES:
 * ============================================================================
 *
 *   debounce(func, wait)
 *     - Crea una funcion que solo se ejecuta despues de 'wait' ms sin llamadas
 *     - Util para: busquedas en tiempo real, resize, scroll
 *     - Ejemplo: window.addEventListener('resize', Utils.debounce(miFuncion, 300))
 *
 *   escapeHtml(text)
 *     - Escapa caracteres HTML peligrosos (<, >, &, ", ')
 *     - OBLIGATORIO al insertar datos de usuario en el DOM
 *     - Ejemplo: element.innerHTML = Utils.escapeHtml(userInput)
 *
 *   sleep(ms)
 *     - Pausa asincrona por X milisegundos
 *     - Util para: animaciones, UX de loading minimo
 *     - Ejemplo: await Utils.sleep(500)
 *
 * ============================================================================
 * USO EN OTROS MODULOS:
 * ============================================================================
 *
 *   // Acceso directo
 *   const Utils = NMViajes.Utils;
 *   Utils.debounce(miFuncion, 300);
 *
 *   // O usar directamente
 *   NMViajes.Utils.escapeHtml(texto);
 *
 * ============================================================================
 * DEPENDENCIAS:
 * ============================================================================
 *   - Ninguna (este es el modulo base)
 *
 * ============================================================================
 * ARCHIVOS QUE USAN ESTE MODULO:
 * ============================================================================
 *   - toast.js           -> usa escapeHtml() para mensajes
 *   - paquete-buscador.js -> usa debounce(), escapeHtml(), sleep()
 *   - (cualquier modulo futuro)
 *
 * ============================================================================
 */
window.NMViajes = window.NMViajes || {};

NMViajes.Utils = (function() {
    'use strict';

    // ========================================================================
    // DEBOUNCE
    // ========================================================================

    /**
     * Crea una funcion con debounce para evitar llamadas excesivas.
     *
     * El debounce retrasa la ejecucion hasta que pasen X milisegundos
     * sin que se vuelva a llamar la funcion. Ideal para eventos frecuentes
     * como typing, scroll o resize.
     *
     * @param {Function} func - Funcion a ejecutar
     * @param {number} wait - Tiempo de espera en milisegundos
     * @returns {Function} Funcion con debounce aplicado
     *
     * @example
     * // Buscar solo cuando el usuario deja de escribir por 300ms
     * const buscarConDebounce = Utils.debounce(buscar, 300);
     * inputBusqueda.addEventListener('input', buscarConDebounce);
     *
     * @example
     * // Recalcular layout solo cuando termina de hacer resize
     * window.addEventListener('resize', Utils.debounce(recalcular, 200));
     */
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    // ========================================================================
    // ESCAPE HTML (Prevencion XSS)
    // ========================================================================

    /**
     * Escapa caracteres HTML para prevenir ataques XSS.
     *
     * IMPORTANTE: Siempre usar cuando insertes datos de usuario o del servidor
     * en el DOM usando innerHTML, insertAdjacentHTML, etc.
     *
     * Caracteres escapados: < > & " '
     *
     * @param {string} text - Texto a escapar (puede ser null/undefined)
     * @returns {string} Texto con caracteres HTML escapados
     *
     * @example
     * // CORRECTO - Previene XSS
     * div.innerHTML = Utils.escapeHtml(nombreUsuario);
     *
     * // INCORRECTO - Vulnerable a XSS
     * div.innerHTML = nombreUsuario;  // Si nombreUsuario = "<script>alert('xss')</script>"
     *
     * @example
     * // Usar en templates generados con JS
     * const html = `<p>Nombre: ${Utils.escapeHtml(nombre)}</p>`;
     */
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text || '';
        return div.innerHTML;
    }

    // ========================================================================
    // SLEEP (Pausa Asincrona)
    // ========================================================================

    /**
     * Pausa la ejecucion por un tiempo determinado.
     *
     * Util para:
     * - Asegurar un tiempo minimo de loading (mejor UX)
     * - Secuenciar animaciones
     * - Dar tiempo a que el usuario lea un mensaje
     *
     * @param {number} ms - Milisegundos a esperar
     * @returns {Promise} Promesa que se resuelve despues del tiempo
     *
     * @example
     * // Asegurar minimo 500ms de loading
     * mostrarLoading();
     * await fetch('/api/datos');
     * await Utils.sleep(500);  // Aunque la llamada sea rapida, mostrar loading 500ms minimo
     * ocultarLoading();
     *
     * @example
     * // Secuenciar animaciones
     * elemento1.classList.add('fade-in');
     * await Utils.sleep(200);
     * elemento2.classList.add('fade-in');
     */
    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    // ========================================================================
    // API PUBLICA
    // ========================================================================

    return {
        debounce,
        escapeHtml,
        sleep
    };
})();
