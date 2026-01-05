/**
 * ============================================================================
 * MODULO: Buscador de Paquetes Turisticos
 * ============================================================================
 * @namespace NMViajes.PaqueteBuscador
 *
 * Modulo para buscar y filtrar paquetes turisticos con AJAX.
 * Renderiza dinamicamente las cards de paquetes sin recargar la pagina.
 *
 * ============================================================================
 * VISTA QUE USA ESTE MODULO:
 * ============================================================================
 *   templates/paquete/list.html
 *
 * ============================================================================
 * ELEMENTOS HTML REQUERIDOS (IDs):
 * ============================================================================
 *
 * FILTROS:
 *   #filtro-destino        - <select> con destinos disponibles
 *   #filtro-fecha-inicio   - <input> datepicker Flowbite (formato dd/mm/yyyy)
 *   #filtro-fecha-fin      - <input> datepicker Flowbite (formato dd/mm/yyyy)
 *
 * BOTONES:
 *   #btn-buscar            - Boton para ejecutar la busqueda
 *   #btn-limpiar           - Boton dentro del mensaje vacio para limpiar
 *   #btn-limpiar-filtros   - Boton X para limpiar filtros (en la barra)
 *
 * CONTENEDORES:
 *   #grid-paquetes         - <div> donde se renderizan las cards de paquetes
 *   #loading-spinner       - Spinner que se muestra durante la carga
 *   #mensaje-vacio         - Mensaje cuando no hay resultados
 *   #contador-resultados   - <span> que muestra "X resultados"
 *   #badge-filtros         - Badge que indica cuando hay filtros activos
 *
 * ============================================================================
 * API BACKEND:
 * ============================================================================
 *
 *   GET /api/v1/paquetes/buscar
 *
 *   Query params (todos opcionales):
 *     - idDestino: Integer
 *     - fechaInicio: String (yyyy-MM-dd)
 *     - fechaFin: String (yyyy-MM-dd)
 *
 *   Response: Array de PaqueteResponse
 *     {
 *       idPaquete: number,
 *       nombre: string,
 *       descripcion: string,
 *       precio: string,           // Ya formateado desde el backend
 *       fechaInicio: string,      // Ya formateado: "15/01/2026"
 *       fechaFin: string,
 *       nombreDestino: string,
 *       stockDisponible: number
 *     }
 *
 * ============================================================================
 * DEPENDENCIAS:
 * ============================================================================
 *   - NMViajes.Utils  -> escapeHtml(), sleep()
 *   - NMViajes.Toast  -> notificaciones
 *   - Flowbite        -> datepickers
 *
 * ============================================================================
 * USO:
 * ============================================================================
 *
 *   // Inicializar (generalmente en el template)
 *   NMViajes.PaqueteBuscador.init({
 *       imgDefault: '/img/carrusel/hero_3.jpg'
 *   });
 *
 *   // Ejecutar busqueda manualmente
 *   NMViajes.PaqueteBuscador.buscar();
 *
 *   // Limpiar filtros y buscar todos
 *   NMViajes.PaqueteBuscador.limpiarFiltros();
 *
 * ============================================================================
 */
window.NMViajes = window.NMViajes || {};

NMViajes.PaqueteBuscador = (function() {
    'use strict';

    // ========================================================================
    // DEPENDENCIAS
    // ========================================================================
    const Utils = NMViajes.Utils;
    const Toast = NMViajes.Toast;

    // ========================================================================
    // CONFIGURACION
    // ========================================================================

    /**
     * Configuracion del modulo.
     * API_URL: Endpoint del backend para buscar paquetes
     * MIN_LOADING_TIME: Tiempo minimo de loading para mejor UX (evita flashes)
     */
    const CONFIG = {
        API_URL: '/api/v1/paquetes/buscar',
        MIN_LOADING_TIME: 500
    };

    /**
     * Referencias a elementos del DOM.
     * Se inicializan en init() para evitar errores si el DOM no esta listo.
     */
    let DOM = {};

    /**
     * Imagen por defecto para las cards de paquetes.
     * Se puede sobrescribir en init({ imgDefault: '...' })
     */
    let imgDefault = '/img/carrusel/hero_3.jpg';

    // ========================================================================
    // RENDERIZADO DE CARDS
    // ========================================================================

    /**
     * Crea el HTML de una card de paquete.
     *
     * IMPORTANTE: Los campos precio, fechaInicio, fechaFin ya vienen
     * formateados desde el backend (FormatConfig.java).
     *
     * @param {Object} paquete - Datos del paquete desde la API
     * @returns {string} HTML de la card
     */
    function crearCardPaquete(paquete) {
        // Stock: mostrar cantidad o "Agotado"
        const stockHtml = paquete.stockDisponible && paquete.stockDisponible > 0
            ? `<span>${paquete.stockDisponible} disponibles</span>`
            : `<span class="text-red-500">Agotado</span>`;

        // Destino: solo mostrar si existe
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
                        ${paquete.fechaInicio} - ${paquete.fechaFin}
                        ${destinoHtml}
                    </p>

                    <p class="mt-3 text-gray-700 text-sm line-clamp-3">${Utils.escapeHtml(paquete.descripcion)}</p>

                    <div class="flex justify-between items-center mt-4">
                        <span class="text-xl font-bold text-red-600">
                            S/ ${paquete.precio}
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

    // ========================================================================
    // FUNCIONES DE UI
    // ========================================================================

    /**
     * Muestra u oculta el spinner de carga.
     * @param {boolean} show - true para mostrar, false para ocultar
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
     * Renderiza los paquetes en el grid.
     * @param {Array} paquetes - Array de paquetes desde la API
     */
    function renderizarPaquetes(paquetes) {
        mostrarLoading(false);

        // Sin resultados: mostrar mensaje vacio
        if (!paquetes || paquetes.length === 0) {
            DOM.gridPaquetes.classList.add('hidden');
            DOM.mensajeVacio.classList.remove('hidden');
            if (DOM.contadorResultados) {
                DOM.contadorResultados.textContent = '0 resultados';
            }
            return;
        }

        // Con resultados: renderizar cards
        DOM.mensajeVacio.classList.add('hidden');
        DOM.gridPaquetes.classList.remove('hidden');
        DOM.gridPaquetes.innerHTML = paquetes.map(p => crearCardPaquete(p)).join('');

        // Actualizar contador
        if (DOM.contadorResultados) {
            DOM.contadorResultados.textContent = `${paquetes.length} resultado${paquetes.length !== 1 ? 's' : ''}`;
        }
    }

    /**
     * Actualiza el badge que indica si hay filtros activos.
     */
    function actualizarBadgeFiltros() {
        if (!DOM.badgeFiltros) return;

        const hayFiltros = DOM.filtroDestino?.value ||
                           DOM.filtroFechaInicio?.value ||
                           DOM.filtroFechaFin?.value;

        DOM.badgeFiltros.classList.toggle('hidden', !hayFiltros);
    }

    // ========================================================================
    // CONVERSION DE FECHAS
    // ========================================================================

    /**
     * Convierte fecha de formato Flowbite (dd/mm/yyyy) a formato API (yyyy-mm-dd).
     *
     * @param {string} fechaStr - Fecha en formato dd/mm/yyyy
     * @returns {string|null} Fecha en formato yyyy-mm-dd o null si es invalida
     *
     * @example
     * parseFechaFlowbite('15/01/2026') // '2026-01-15'
     * parseFechaFlowbite('')           // null
     */
    function parseFechaFlowbite(fechaStr) {
        if (!fechaStr) return null;
        const parts = fechaStr.split('/');
        if (parts.length !== 3) return null;
        return `${parts[2]}-${parts[1]}-${parts[0]}`;
    }

    // ========================================================================
    // BUSQUEDA
    // ========================================================================

    /**
     * Ejecuta la busqueda de paquetes con los filtros actuales.
     * @returns {Promise<void>}
     */
    async function buscar() {
        // 1. Construir query params
        const params = new URLSearchParams();

        if (DOM.filtroDestino?.value) {
            params.append('idDestino', DOM.filtroDestino.value);
        }

        const fechaInicio = parseFechaFlowbite(DOM.filtroFechaInicio?.value);
        if (fechaInicio) {
            params.append('fechaInicio', fechaInicio);
        }

        const fechaFin = parseFechaFlowbite(DOM.filtroFechaFin?.value);
        if (fechaFin) {
            params.append('fechaFin', fechaFin);
        }

        // 2. Mostrar loading y actualizar badge
        mostrarLoading(true);
        actualizarBadgeFiltros();

        const startTime = Date.now();

        try {
            // 3. Hacer request a la API
            const response = await fetch(`${CONFIG.API_URL}?${params.toString()}`);

            if (!response.ok) {
                throw new Error('Error al buscar paquetes');
            }

            const paquetes = await response.json();

            // 4. Asegurar tiempo minimo de loading para mejor UX
            const elapsed = Date.now() - startTime;
            if (elapsed < CONFIG.MIN_LOADING_TIME) {
                await Utils.sleep(CONFIG.MIN_LOADING_TIME - elapsed);
            }

            // 5. Renderizar resultados
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
     * Limpia todos los filtros y ejecuta una busqueda sin filtros.
     */
    function limpiarFiltros() {
        if (DOM.filtroDestino) DOM.filtroDestino.value = '';
        if (DOM.filtroFechaInicio) DOM.filtroFechaInicio.value = '';
        if (DOM.filtroFechaFin) DOM.filtroFechaFin.value = '';

        actualizarBadgeFiltros();
        buscar();
        Toast.info('Filtros limpiados');
    }

    // ========================================================================
    // INICIALIZACION
    // ========================================================================

    /**
     * Inicializa el modulo.
     *
     * @param {Object} options - Opciones de configuracion
     * @param {string} options.imgDefault - URL de imagen por defecto para cards
     */
    function init(options = {}) {
        // Sobrescribir imagen por defecto si se proporciona
        if (options.imgDefault) {
            imgDefault = options.imgDefault;
        }

        // Inicializar referencias DOM
        DOM = {
            // Filtros
            filtroDestino: document.getElementById('filtro-destino'),
            filtroFechaInicio: document.getElementById('filtro-fecha-inicio'),
            filtroFechaFin: document.getElementById('filtro-fecha-fin'),

            // Botones
            btnBuscar: document.getElementById('btn-buscar'),
            btnLimpiar: document.getElementById('btn-limpiar'),
            btnLimpiarFiltros: document.getElementById('btn-limpiar-filtros'),

            // Contenedores
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

        // Contar resultados iniciales (los que vienen del servidor con Thymeleaf)
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

    // ========================================================================
    // API PUBLICA
    // ========================================================================

    return {
        init,
        buscar,
        limpiarFiltros
    };
})();
