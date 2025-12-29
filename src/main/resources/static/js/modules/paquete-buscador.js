/**
 * Modulo de busqueda de paquetes turisticos
 * @namespace NMViajes.PaqueteBuscador
 * @requires NMViajes.Utils
 * @requires NMViajes.Toast
 */
window.NMViajes = window.NMViajes || {};

NMViajes.PaqueteBuscador = (function() {
    'use strict';

    // Dependencias
    const Utils = NMViajes.Utils;
    const Toast = NMViajes.Toast;

    // Configuracion
    const CONFIG = {
        API_URL: '/api/v1/paquetes/buscar',
        MIN_LOADING_TIME: 500
    };

    // Elementos DOM (se inicializan en init)
    let DOM = {};

    // Imagen por defecto (se puede sobrescribir en init)
    let imgDefault = '/img/carrusel/hero_3.jpg';

    /**
     * Crea el HTML de una card de paquete
     */
    function crearCardPaquete(paquete) {
        const stockHtml = paquete.stockDisponible && paquete.stockDisponible > 0
            ? `<span>${paquete.stockDisponible} disponibles</span>`
            : `<span class="text-red-500">Agotado</span>`;

        const destinoHtml = paquete.nombreDestino
            ? `<span class="ml-2">| ${Utils.escapeHtml(paquete.nombreDestino)}</span>`
            : '';

        return `
            <div class="bg-white rounded-lg shadow hover:shadow-lg transition">
                <img src="${imgDefault}"
                     alt="${Utils.escapeHtml(paquete.nombre)}"
                     class="rounded-t-lg h-48 w-full object-cover">

                <div class="p-5">
                    <h3 class="text-lg font-semibold">${Utils.escapeHtml(paquete.nombre)}</h3>
                    <p class="text-sm text-gray-500 mt-1">
                        ${Utils.formatearFecha(paquete.fechaInicio)} - ${Utils.formatearFecha(paquete.fechaFin)}
                        ${destinoHtml}
                    </p>

                    <p class="mt-3 text-gray-700 text-sm line-clamp-3">${Utils.escapeHtml(paquete.descripcion)}</p>

                    <div class="flex justify-between items-center mt-4">
                        <span class="text-xl font-bold text-red-600">
                            S/ ${Utils.formatearPrecio(paquete.precio)}
                        </span>

                        <a href="/paquetes/${paquete.idPaquete}"
                           class="text-sm bg-red-600 text-white px-4 py-2 rounded-lg hover:bg-red-700 transition">
                            Ver detalle
                        </a>
                    </div>

                    <div class="mt-3 text-xs text-gray-400">
                        ${stockHtml}
                    </div>
                </div>
            </div>
        `;
    }

    /**
     * Muestra u oculta el spinner de carga
     */
    function mostrarLoading(show) {
        if (DOM.loadingSpinner) {
            DOM.loadingSpinner.classList.toggle('hidden', !show);
        }
        if (DOM.gridPaquetes) {
            DOM.gridPaquetes.classList.toggle('hidden', show);
        }
        if (DOM.mensajeVacio) {
            DOM.mensajeVacio.classList.add('hidden');
        }
    }

    /**
     * Renderiza los paquetes en el grid
     */
    function renderizarPaquetes(paquetes) {
        mostrarLoading(false);

        if (!paquetes || paquetes.length === 0) {
            DOM.gridPaquetes.classList.add('hidden');
            DOM.mensajeVacio.classList.remove('hidden');
            if (DOM.contadorResultados) {
                DOM.contadorResultados.textContent = '0 resultados';
            }
            return;
        }

        DOM.mensajeVacio.classList.add('hidden');
        DOM.gridPaquetes.classList.remove('hidden');
        DOM.gridPaquetes.innerHTML = paquetes.map(p => crearCardPaquete(p)).join('');

        if (DOM.contadorResultados) {
            DOM.contadorResultados.textContent = `${paquetes.length} resultado${paquetes.length !== 1 ? 's' : ''}`;
        }
    }

    /**
     * Actualiza el badge de filtros activos
     */
    function actualizarBadgeFiltros() {
        if (!DOM.badgeFiltros) return;

        const hayFiltros = DOM.filtroDestino?.value ||
                           DOM.filtroFechaInicio?.value ||
                           DOM.filtroFechaFin?.value;

        DOM.badgeFiltros.classList.toggle('hidden', !hayFiltros);
    }

    /**
     * Ejecuta la busqueda de paquetes
     */
    async function buscar() {
        const params = new URLSearchParams();

        if (DOM.filtroDestino?.value) {
            params.append('idDestino', DOM.filtroDestino.value);
        }

        const fechaInicio = Utils.parseFechaFlowbite(DOM.filtroFechaInicio?.value);
        if (fechaInicio) {
            params.append('fechaInicio', fechaInicio);
        }

        const fechaFin = Utils.parseFechaFlowbite(DOM.filtroFechaFin?.value);
        if (fechaFin) {
            params.append('fechaFin', fechaFin);
        }

        mostrarLoading(true);
        actualizarBadgeFiltros();

        const startTime = Date.now();

        try {
            const response = await fetch(`${CONFIG.API_URL}?${params.toString()}`);

            if (!response.ok) {
                throw new Error('Error al buscar paquetes');
            }

            const paquetes = await response.json();

            // Asegurar tiempo minimo de loading para mejor UX
            const elapsed = Date.now() - startTime;
            if (elapsed < CONFIG.MIN_LOADING_TIME) {
                await Utils.sleep(CONFIG.MIN_LOADING_TIME - elapsed);
            }

            renderizarPaquetes(paquetes);

            if (paquetes.length > 0) {
                Toast.success(`Se encontraron ${paquetes.length} paquete${paquetes.length !== 1 ? 's' : ''}`);
            }

        } catch (error) {
            console.error('Error:', error);
            mostrarLoading(false);
            Toast.error('Ocurrio un error al buscar. Intenta nuevamente.');
            DOM.gridPaquetes.innerHTML = `
                <div class="col-span-3 text-center py-10">
                    <p class="text-red-500">Error al cargar los paquetes.</p>
                </div>
            `;
        }
    }

    /**
     * Limpia todos los filtros y ejecuta busqueda
     */
    function limpiarFiltros() {
        if (DOM.filtroDestino) DOM.filtroDestino.value = '';
        if (DOM.filtroFechaInicio) DOM.filtroFechaInicio.value = '';
        if (DOM.filtroFechaFin) DOM.filtroFechaFin.value = '';

        actualizarBadgeFiltros();
        buscar();
        Toast.info('Filtros limpiados');
    }

    /**
     * Inicializa el modulo
     * @param {object} options - Opciones de configuracion
     */
    function init(options = {}) {
        // Sobrescribir imagen por defecto si se proporciona
        if (options.imgDefault) {
            imgDefault = options.imgDefault;
        }

        // Inicializar referencias DOM
        DOM = {
            filtroDestino: document.getElementById('filtro-destino'),
            filtroFechaInicio: document.getElementById('filtro-fecha-inicio'),
            filtroFechaFin: document.getElementById('filtro-fecha-fin'),
            btnBuscar: document.getElementById('btn-buscar'),
            btnLimpiar: document.getElementById('btn-limpiar'),
            btnLimpiarFiltros: document.getElementById('btn-limpiar-filtros'),
            gridPaquetes: document.getElementById('grid-paquetes'),
            loadingSpinner: document.getElementById('loading-spinner'),
            mensajeVacio: document.getElementById('mensaje-vacio'),
            contadorResultados: document.getElementById('contador-resultados'),
            badgeFiltros: document.getElementById('badge-filtros')
        };

        // Event listeners
        DOM.btnBuscar?.addEventListener('click', buscar);
        DOM.btnLimpiar?.addEventListener('click', limpiarFiltros);
        DOM.btnLimpiarFiltros?.addEventListener('click', limpiarFiltros);

        // Contar resultados iniciales
        if (DOM.gridPaquetes && DOM.contadorResultados) {
            const cardsIniciales = DOM.gridPaquetes.querySelectorAll(':scope > div').length;
            if (cardsIniciales > 0) {
                DOM.contadorResultados.textContent = `${cardsIniciales} resultado${cardsIniciales !== 1 ? 's' : ''}`;
            }
        }

        // Inicializar tooltips de Flowbite si existe
        if (typeof window.initFlowbite === 'function') {
            window.initFlowbite();
        }

        console.log('PaqueteBuscador inicializado');
    }

    // API publica
    return {
        init,
        buscar,
        limpiarFiltros
    };
})();
