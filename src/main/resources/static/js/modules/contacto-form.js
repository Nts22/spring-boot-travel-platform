/**
 * Modulo para el formulario de contacto
 * @namespace NMViajes.ContactoForm
 * @requires NMViajes.Toast
 */
window.NMViajes = window.NMViajes || {};

NMViajes.ContactoForm = (function() {
    'use strict';

    // Elementos DOM
    let form = null;
    let submitBtn = null;
    let btnText = null;
    let btnSpinner = null;

    /**
     * Envia el formulario via AJAX
     */
    async function enviarFormulario(e) {
        e.preventDefault();

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        const formData = new FormData(form);
        const data = {
            nombre: formData.get('nombre'),
            email: formData.get('email'),
            telefono: formData.get('telefono') || null,
            mensaje: formData.get('mensaje')
        };

        // Mostrar estado de carga
        setLoading(true);

        try {
            const response = await fetch('/api/v1/contacto', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                // Exito
                if (window.NMViajes && window.NMViajes.Toast) {
                    NMViajes.Toast.success('Mensaje enviado correctamente. Nos pondremos en contacto contigo pronto.');
                } else {
                    alert('Mensaje enviado correctamente');
                }
                form.reset();
                cerrarModal();
            } else {
                // Error de validacion u otro
                const errorData = await response.json().catch(() => null);
                const mensaje = errorData?.message || 'Error al enviar el mensaje. Por favor, intenta nuevamente.';

                if (window.NMViajes && window.NMViajes.Toast) {
                    NMViajes.Toast.error(mensaje);
                } else {
                    alert(mensaje);
                }
            }
        } catch (error) {
            console.error('Error al enviar contacto:', error);
            if (window.NMViajes && window.NMViajes.Toast) {
                NMViajes.Toast.error('Error de conexion. Por favor, verifica tu conexion a internet.');
            } else {
                alert('Error de conexion');
            }
        } finally {
            setLoading(false);
        }
    }

    /**
     * Muestra/oculta estado de carga en el boton
     */
    function setLoading(loading) {
        if (submitBtn) {
            submitBtn.disabled = loading;
        }
        if (btnText) {
            btnText.textContent = loading ? 'Enviando...' : 'Enviar mensaje';
        }
        if (btnSpinner) {
            btnSpinner.classList.toggle('hidden', !loading);
        }
    }

    /**
     * Cierra el modal de contacto
     */
    function cerrarModal() {
        const modal = document.getElementById('contact-modal');
        if (modal && window.FlowbiteInstances) {
            const modalInstance = window.FlowbiteInstances.getInstance('Modal', 'contact-modal');
            if (modalInstance) {
                modalInstance.hide();
            }
        }
    }

    /**
     * Inicializa el modulo
     */
    function init() {
        form = document.getElementById('contact-form');
        if (!form) {
            return;
        }

        submitBtn = form.querySelector('button[type="submit"]');
        btnText = submitBtn?.querySelector('.btn-text');
        btnSpinner = submitBtn?.querySelector('.btn-spinner');

        form.addEventListener('submit', enviarFormulario);

        console.log('ContactoForm inicializado');
    }

    // API publica
    return {
        init
    };
})();

// Auto-inicializar cuando el DOM este listo
(function() {
    'use strict';
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            NMViajes.ContactoForm.init();
        });
    } else {
        NMViajes.ContactoForm.init();
    }
})();