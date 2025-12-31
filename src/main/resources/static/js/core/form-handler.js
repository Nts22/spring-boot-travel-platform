/**
 * ============================================================================
 * MODULO BASE: Manejador de Formularios AJAX
 * ============================================================================
 *
 * Este modulo permite crear formularios AJAX facilmente.
 * Solo necesitas configurar unos pocos parametros.
 *
 * ============================================================================
 * EJEMPLO DE USO RAPIDO:
 * ============================================================================
 *
 *   // 1. En tu archivo JS (ej: reserva-form.js):
 *
 *   NMViajes.FormHandler.crear({
 *       nombre: 'ReservaForm',
 *       apiUrl: '/api/v1/reservas',
 *       formId: 'reserva-form',
 *       campos: ['nombreCliente', 'idPaquete', 'cantidadPasajeros'],
 *       mensajes: {
 *           exito: 'Reserva creada correctamente!'
 *       }
 *   });
 *
 *   // 2. En tu HTML:
 *
 *   <form id="reserva-form">
 *       <input name="nombreCliente" id="reserva-nombreCliente">
 *       <p id="error-nombreCliente" class="text-red-600 hidden"></p>
 *       ...
 *       <button type="submit">
 *           <span class="btn-spinner hidden">...</span>
 *           <span class="btn-text">Reservar</span>
 *       </button>
 *   </form>
 *
 *   // 3. En tu DTO Java (ReservaRequest.java):
 *
 *   private String nombreCliente;  // <- mismo nombre que en 'campos'
 *   private Integer idPaquete;
 *   private Integer cantidadPasajeros;
 *
 * ============================================================================
 * CONVENCION DE IDS EN HTML:
 * ============================================================================
 *
 *   - Input:  id="{formId sin '-form'}-{campo}"  ->  reserva-nombreCliente
 *   - Error:  id="error-{campo}"                 ->  error-nombreCliente
 *   - Boton:  debe tener .btn-text y .btn-spinner (opcional)
 *
 * ============================================================================
 * PARAMETROS DE CONFIGURACION:
 * ============================================================================
 *
 *   nombre     (requerido)  Nombre del modulo, ej: 'ReservaForm'
 *   apiUrl     (requerido)  Endpoint del backend, ej: '/api/v1/reservas'
 *   formId     (requerido)  ID del formulario HTML, ej: 'reserva-form'
 *   campos     (requerido)  Array con nombres de campos (deben coincidir con el DTO Java)
 *
 *   modalId    (opcional)   ID del modal Flowbite si el form esta en un modal
 *   mensajes   (opcional)   Objeto con mensajes personalizados:
 *                           - exito: mensaje de exito
 *                           - errorValidacion: mensaje cuando hay errores
 *                           - errorServidor: mensaje de error del servidor
 *                           - errorConexion: mensaje de error de red
 *                           - btnEnviando: texto del boton mientras envia
 *                           - btnNormal: texto normal del boton
 *
 *   transformarDatos  (opcional)  Funcion para transformar datos antes de enviar
 *   onExito           (opcional)  Funcion que se ejecuta despues del exito
 *   onError           (opcional)  Funcion que se ejecuta despues de un error
 *
 * ============================================================================
 */
window.NMViajes = window.NMViajes || {};

NMViajes.FormHandler = (function() {
    'use strict';

    // ========================================================================
    // CONFIGURACION POR DEFECTO
    // ========================================================================
    var DEFAULTS = {
        inputPrefix: null,  // Se calcula automaticamente del formId
        errorPrefix: 'error-',
        cssError: 'border-red-500',
        cssNormal: 'border-gray-300',
        mensajes: {
            exito: 'Operacion realizada correctamente.',
            errorValidacion: 'Por favor, corrige los errores del formulario.',
            errorServidor: 'Error en el servidor. Por favor, intenta nuevamente.',
            errorConexion: 'Error de conexion. Por favor, verifica tu conexion a internet.',
            btnEnviando: 'Procesando...',
            btnNormal: 'Enviar'
        }
    };

    // ========================================================================
    // FUNCIONES AUXILIARES
    // ========================================================================

    /**
     * Combina configuracion del usuario con los valores por defecto
     */
    function mezclarConfiguracion(config) {
        // Calcular prefijo de input automaticamente: 'reserva-form' -> 'reserva-'
        var inputPrefix = config.formId.replace('-form', '') + '-';

        return {
            nombre: config.nombre,
            apiUrl: config.apiUrl,
            formId: config.formId,
            modalId: config.modalId || null,
            campos: config.campos || [],
            inputPrefix: config.inputPrefix || inputPrefix,
            errorPrefix: config.errorPrefix || DEFAULTS.errorPrefix,
            cssError: config.cssError || DEFAULTS.cssError,
            cssNormal: config.cssNormal || DEFAULTS.cssNormal,
            mensajes: Object.assign({}, DEFAULTS.mensajes, config.mensajes || {}),
            transformarDatos: config.transformarDatos || null,
            onExito: config.onExito || null,
            onError: config.onError || null
        };
    }

    /**
     * Muestra notificacion usando Toast o alert
     */
    function notificar(mensaje, tipo) {
        var Toast = window.NMViajes && window.NMViajes.Toast;
        if (Toast) {
            if (tipo === 'success') Toast.success(mensaje);
            else if (tipo === 'error') Toast.error(mensaje);
            else Toast.info(mensaje);
        } else {
            alert(mensaje);
        }
    }

    // ========================================================================
    // CLASE FORMULARIO
    // ========================================================================

    /**
     * Constructor del manejador de formulario
     */
    function Formulario(config) {
        this.config = mezclarConfiguracion(config);
        this.elementos = {
            form: null,
            submitBtn: null,
            btnText: null,
            btnSpinner: null
        };
    }

    /**
     * Obtiene un elemento input por nombre de campo
     */
    Formulario.prototype.obtenerInput = function(nombreCampo) {
        return document.getElementById(this.config.inputPrefix + nombreCampo);
    };

    /**
     * Obtiene el elemento de error por nombre de campo
     */
    Formulario.prototype.obtenerElementoError = function(nombreCampo) {
        return document.getElementById(this.config.errorPrefix + nombreCampo);
    };

    /**
     * Limpia el error de un campo
     */
    Formulario.prototype.limpiarErrorDeCampo = function(nombreCampo) {
        var input = this.obtenerInput(nombreCampo);
        var errorEl = this.obtenerElementoError(nombreCampo);

        if (input) {
            input.classList.remove(this.config.cssError);
            input.classList.add(this.config.cssNormal);
        }
        if (errorEl) {
            errorEl.textContent = '';
            errorEl.classList.add('hidden');
        }
    };

    /**
     * Limpia todos los errores del formulario
     */
    Formulario.prototype.limpiarTodosLosErrores = function() {
        for (var i = 0; i < this.config.campos.length; i++) {
            this.limpiarErrorDeCampo(this.config.campos[i]);
        }
    };

    /**
     * Muestra error en un campo especifico
     */
    Formulario.prototype.mostrarErrorEnCampo = function(nombreCampo, mensajeError) {
        var input = this.obtenerInput(nombreCampo);
        var errorEl = this.obtenerElementoError(nombreCampo);

        if (input) {
            input.classList.remove(this.config.cssNormal);
            input.classList.add(this.config.cssError);
        }
        if (errorEl) {
            errorEl.textContent = mensajeError;
            errorEl.classList.remove('hidden');
        }
    };

    /**
     * Muestra todos los errores del backend
     */
    Formulario.prototype.mostrarErroresDelBackend = function(errores) {
        if (!errores) return;

        var primerCampoConError = null;
        var campos = Object.keys(errores);

        for (var i = 0; i < campos.length; i++) {
            var nombreCampo = campos[i];
            this.mostrarErrorEnCampo(nombreCampo, errores[nombreCampo]);

            if (!primerCampoConError) {
                primerCampoConError = this.obtenerInput(nombreCampo);
            }
        }

        if (primerCampoConError) {
            primerCampoConError.focus();
        }
    };

    /**
     * Muestra/oculta estado de carga
     */
    Formulario.prototype.mostrarEstadoCarga = function(estaCargando) {
        if (this.elementos.submitBtn) {
            this.elementos.submitBtn.disabled = estaCargando;
        }
        if (this.elementos.btnText) {
            this.elementos.btnText.textContent = estaCargando
                ? this.config.mensajes.btnEnviando
                : this.config.mensajes.btnNormal;
        }
        if (this.elementos.btnSpinner) {
            if (estaCargando) {
                this.elementos.btnSpinner.classList.remove('hidden');
            } else {
                this.elementos.btnSpinner.classList.add('hidden');
            }
        }
    };

    /**
     * Cierra el modal si existe
     */
    Formulario.prototype.cerrarModal = function() {
        if (this.config.modalId && window.FlowbiteInstances) {
            var modalInstance = window.FlowbiteInstances.getInstance('Modal', this.config.modalId);
            if (modalInstance) {
                modalInstance.hide();
            }
        }
    };

    /**
     * Recolecta los datos del formulario
     */
    Formulario.prototype.recolectarDatos = function() {
        var formData = new FormData(this.elementos.form);
        var datos = {};

        for (var i = 0; i < this.config.campos.length; i++) {
            var campo = this.config.campos[i];
            var valor = formData.get(campo);
            datos[campo] = valor === '' ? null : valor;
        }

        // Permitir transformacion personalizada
        if (this.config.transformarDatos) {
            datos = this.config.transformarDatos(datos, formData);
        }

        return datos;
    };

    /**
     * Maneja el exito del envio
     */
    Formulario.prototype.manejarExito = function() {
        notificar(this.config.mensajes.exito, 'success');
        this.elementos.form.reset();
        this.cerrarModal();

        if (this.config.onExito) {
            this.config.onExito();
        }
    };

    /**
     * Maneja los errores
     */
    Formulario.prototype.manejarError = function(respuestaError) {
        if (respuestaError && respuestaError.errors) {
            this.mostrarErroresDelBackend(respuestaError.errors);
            notificar(this.config.mensajes.errorValidacion, 'error');
        } else {
            var mensaje = (respuestaError && respuestaError.message)
                ? respuestaError.message
                : this.config.mensajes.errorServidor;
            notificar(mensaje, 'error');
        }

        if (this.config.onError) {
            this.config.onError(respuestaError);
        }
    };

    /**
     * Envia el formulario
     */
    Formulario.prototype.enviar = function(evento) {
        var self = this;
        evento.preventDefault();

        self.limpiarTodosLosErrores();
        var datos = self.recolectarDatos();
        self.mostrarEstadoCarga(true);

        fetch(self.config.apiUrl, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(datos)
        })
        .then(function(response) {
            if (response.ok) {
                self.manejarExito();
                return null;
            }
            return response.json();
        })
        .then(function(respuestaError) {
            if (respuestaError) {
                self.manejarError(respuestaError);
            }
        })
        .catch(function(error) {
            console.error('Error de conexion:', error);
            notificar(self.config.mensajes.errorConexion, 'error');
        })
        .finally(function() {
            self.mostrarEstadoCarga(false);
        });
    };

    /**
     * Configura limpieza de errores al escribir
     */
    Formulario.prototype.configurarLimpiezaDeErrores = function() {
        var self = this;

        for (var i = 0; i < this.config.campos.length; i++) {
            var nombreCampo = this.config.campos[i];
            var input = this.obtenerInput(nombreCampo);

            if (input) {
                input.addEventListener('input', (function(campo) {
                    return function() {
                        self.limpiarErrorDeCampo(campo);
                    };
                })(nombreCampo));
            }
        }
    };

    /**
     * Inicializa el formulario
     */
    Formulario.prototype.init = function() {
        var self = this;

        this.elementos.form = document.getElementById(this.config.formId);
        if (!this.elementos.form) {
            return;
        }

        this.elementos.submitBtn = this.elementos.form.querySelector('button[type="submit"]');
        if (this.elementos.submitBtn) {
            this.elementos.btnText = this.elementos.submitBtn.querySelector('.btn-text');
            this.elementos.btnSpinner = this.elementos.submitBtn.querySelector('.btn-spinner');

            // Guardar texto original del boton
            if (this.elementos.btnText && this.config.mensajes.btnNormal === DEFAULTS.mensajes.btnNormal) {
                this.config.mensajes.btnNormal = this.elementos.btnText.textContent;
            }
        }

        this.elementos.form.addEventListener('submit', function(e) {
            self.enviar(e);
        });

        this.configurarLimpiezaDeErrores();
    };

    // ========================================================================
    // API PUBLICA
    // ========================================================================

    return {
        /**
         * Crea un nuevo manejador de formulario
         *
         * @param {Object} config - Configuracion del formulario
         * @returns {Formulario} Instancia del formulario
         *
         * @example
         * NMViajes.FormHandler.crear({
         *     nombre: 'ReservaForm',
         *     apiUrl: '/api/v1/reservas',
         *     formId: 'reserva-form',
         *     campos: ['nombreCliente', 'idPaquete', 'cantidadPasajeros']
         * });
         */
        crear: function(config) {
            // Validar configuracion minima
            if (!config.nombre) {
                console.error('FormHandler: falta el parametro "nombre"');
                return null;
            }
            if (!config.apiUrl) {
                console.error('FormHandler: falta el parametro "apiUrl"');
                return null;
            }
            if (!config.formId) {
                console.error('FormHandler: falta el parametro "formId"');
                return null;
            }
            if (!config.campos || config.campos.length === 0) {
                console.error('FormHandler: falta el parametro "campos"');
                return null;
            }

            var formulario = new Formulario(config);

            // Registrar en el namespace global
            NMViajes[config.nombre] = formulario;

            // Auto-inicializar cuando el DOM este listo
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', function() {
                    formulario.init();
                });
            } else {
                formulario.init();
            }

            return formulario;
        }
    };

})();
