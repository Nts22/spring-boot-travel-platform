/**
 * Utilidades globales para la aplicacion
 * @namespace NMViajes.Utils
 */
window.NMViajes = window.NMViajes || {};

NMViajes.Utils = (function() {
    'use strict';

    /**
     * Formatea un numero como precio en soles peruanos
     * @param {number} precio - El precio a formatear
     * @returns {string} Precio formateado (ej: "1,500.00")
     */
    function formatearPrecio(precio) {
        return new Intl.NumberFormat('es-PE', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(precio);
    }

    /**
     * Formatea una fecha ISO a formato peruano dd/mm/yyyy
     * @param {string} fechaStr - Fecha en formato ISO (yyyy-mm-dd)
     * @returns {string} Fecha formateada
     */
    function formatearFecha(fechaStr) {
        if (!fechaStr) return '';
        const fecha = new Date(fechaStr + 'T00:00:00');
        return fecha.toLocaleDateString('es-PE', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    }

    /**
     * Convierte fecha dd/mm/yyyy (Flowbite) a yyyy-mm-dd (API)
     * @param {string} fechaStr - Fecha en formato dd/mm/yyyy
     * @returns {string|null} Fecha en formato ISO o null
     */
    function parseFechaFlowbite(fechaStr) {
        if (!fechaStr) return null;
        const parts = fechaStr.split('/');
        if (parts.length !== 3) return null;
        return `${parts[2]}-${parts[1]}-${parts[0]}`;
    }

    /**
     * Crea una funcion con debounce
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
     * Escapa caracteres HTML para prevenir XSS
     * @param {string} text - Texto a escapar
     * @returns {string} Texto escapado
     */
    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text || '';
        return div.innerHTML;
    }

    /**
     * Espera un tiempo determinado (para async/await)
     * @param {number} ms - Milisegundos a esperar
     * @returns {Promise} Promesa que se resuelve despues del tiempo
     */
    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    // API publica
    return {
        formatearPrecio,
        formatearFecha,
        parseFechaFlowbite,
        debounce,
        escapeHtml,
        sleep
    };
})();
