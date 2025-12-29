/**
 * Sistema de notificaciones Toast (estilo Flowbite)
 * @namespace NMViajes.Toast
 */
window.NMViajes = window.NMViajes || {};

NMViajes.Toast = (function() {
    'use strict';

    // Contenedor de toasts (se crea automaticamente si no existe)
    let container = null;

    const ICONS = {
        success: `<svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd"></path></svg>`,
        error: `<svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path></svg>`,
        warning: `<svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clip-rule="evenodd"></path></svg>`,
        info: `<svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"></path></svg>`
    };

    const COLORS = {
        success: 'text-green-500 bg-green-100',
        error: 'text-red-500 bg-red-100',
        warning: 'text-orange-500 bg-orange-100',
        info: 'text-blue-500 bg-blue-100'
    };

    const CONFIG = {
        duration: 4000,
        position: 'top-right'
    };

    /**
     * Obtiene o crea el contenedor de toasts
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
     * Muestra una notificacion toast
     * @param {string} message - Mensaje a mostrar
     * @param {string} type - Tipo: 'success', 'error', 'warning', 'info'
     * @param {object} options - Opciones adicionales
     */
    function show(message, type = 'info', options = {}) {
        const duration = options.duration || CONFIG.duration;
        const toastContainer = getContainer();

        const toast = document.createElement('div');
        toast.className = 'flex items-center w-full max-w-xs p-4 text-gray-500 bg-white rounded-lg shadow border border-gray-100';
        toast.style.animation = 'toast-fade-in 0.3s ease-out';

        toast.innerHTML = `
            <div class="inline-flex items-center justify-center flex-shrink-0 w-8 h-8 ${COLORS[type] || COLORS.info} rounded-lg">
                ${ICONS[type] || ICONS.info}
            </div>
            <div class="ms-3 text-sm font-normal">${NMViajes.Utils ? NMViajes.Utils.escapeHtml(message) : message}</div>
            <button type="button" class="ms-auto -mx-1.5 -my-1.5 bg-white text-gray-400 hover:text-gray-900 rounded-lg focus:ring-2 focus:ring-gray-300 p-1.5 hover:bg-gray-100 inline-flex items-center justify-center h-8 w-8" aria-label="Cerrar">
                <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
            </button>
        `;

        toastContainer.appendChild(toast);

        // Cerrar al click
        toast.querySelector('button').addEventListener('click', () => removeToast(toast));

        // Auto-cerrar
        if (duration > 0) {
            setTimeout(() => removeToast(toast), duration);
        }

        return toast;
    }

    /**
     * Remueve un toast con animacion
     */
    function removeToast(toast) {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        toast.style.transition = 'all 0.3s ease-out';
        setTimeout(() => toast.remove(), 300);
    }

    // Metodos de conveniencia
    function success(message, options) {
        return show(message, 'success', options);
    }

    function error(message, options) {
        return show(message, 'error', options);
    }

    function warning(message, options) {
        return show(message, 'warning', options);
    }

    function info(message, options) {
        return show(message, 'info', options);
    }

    // Agregar estilos de animacion si no existen
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

    // API publica
    return {
        show,
        success,
        error,
        warning,
        info
    };
})();
