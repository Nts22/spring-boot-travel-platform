/**
 * Utilidades de UI para la aplicacion
 * @namespace NMViajes.Utils
 *
 * NOTA: El formateo de fechas, precios y tipos de datos se maneja desde el controlador.
 * Este modulo solo contiene utilidades para diseño, renderizado y formularios.
 */
window.NMViajes = window.NMViajes || {};

NMViajes.Utils = (function() {
    'use strict';

    /**
     * Crea una funcion con debounce para evitar llamadas excesivas
     * @param {Function} func - Funcion a ejecutar
     * @param {number} wait - Tiempo de espera en ms
     * @returns {Function} Funcion con debounce
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

    /**
     * Escapa caracteres HTML para prevenir XSS en renderizado
     * @param {string} text - Texto a escapar
     * @returns {string} Texto escapado
     */
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text || '';
        return div.innerHTML;
    }

    /**
     * Espera un tiempo determinado (util para animaciones y transiciones)
     * @param {number} ms - Milisegundos a esperar
     * @returns {Promise} Promesa que se resuelve despues del tiempo
     */
    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    // API publica
    return {
        debounce,
        escapeHtml,
        sleep
    };
})();
