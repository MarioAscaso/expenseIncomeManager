document.addEventListener('DOMContentLoaded', function() {
    // 1. Configuración inicial y estado simulado
    const calendarEl = document.getElementById('calendar');
    const modalMovimiento = new bootstrap.Modal(document.getElementById('modalMovimiento'));
    const formMovimiento = document.getElementById('formMovimiento');
    const btnEliminar = document.getElementById('btnEliminar');
    
    // Simulación del usuario actual (Cambia a 'basico' o 'admin' para probar)
    const currentUser = { rol: 'admin', id: 1 }; 
    
    // Estado local para simular la base de datos temporalmente
    let movimientos = [];
    let saldoActual = 0;

    // 2. Inicialización de FullCalendar
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'dayGridMonth',
        locale: 'es',
        events: movimientos,
        
        // Al clicar en un día: Crear movimiento (Ambos roles)
        dateClick: function(info) {
            prepararModalCreacion(info.dateStr);
            modalMovimiento.show();
        },

        // Al clicar en un evento: Editar/Eliminar (Solo Admin)
        eventClick: function(info) {
            if (currentUser.rol === 'admin' || currentUser.rol === 'superadmin') {
                prepararModalEdicion(info.event);
                modalMovimiento.show();
            } else {
                mostrarNotificacion('No tienes permisos para editar o eliminar movimientos.', 'warning');
            }
        }
    });
    calendar.render();

    // 3. Funciones de la Interfaz
    function recalcularSaldo() {
        saldoActual = movimientos.reduce((total, mov) => {
            const importe = parseFloat(mov.extendedProps.importe);
            return mov.extendedProps.categoria === 'INGRESO' ? total + importe : total - importe;
        }, 0);
        
        actualizarColorSaldo(saldoActual);
    }

    function actualizarColorSaldo(monto) {
        const saldoElement = document.getElementById('saldo-valor');
        saldoElement.innerText = monto.toFixed(2);
        
        // Limpiar clases previas
        saldoElement.classList.remove('text-success', 'text-warning', 'text-danger');
        
        if (monto > 0) saldoElement.classList.add('text-success');
        else if (monto === 0) saldoElement.classList.add('text-warning');
        else saldoElement.classList.add('text-danger');
    }

    // 4. Gestión del Modal
    function prepararModalCreacion(fecha) {
        document.getElementById('modalTitle').innerText = 'Nuevo Movimiento';
        document.getElementById('movimientoId').value = '';
        document.getElementById('desc').value = '';
        document.getElementById('importe').value = '';
        document.getElementById('categoria').value = 'GASTO';
        
        // Campo fecha (oculto en el HTML o en una variable, pero lo guardamos para el evento)
        document.getElementById('formMovimiento').dataset.fecha = fecha;
        
        btnEliminar.classList.add('d-none'); // Ocultar botón eliminar
    }

    function prepararModalEdicion(evento) {
        document.getElementById('modalTitle').innerText = 'Modificar Movimiento';
        document.getElementById('movimientoId').value = evento.id;
        document.getElementById('desc').value = evento.title;
        document.getElementById('importe').value = evento.extendedProps.importe;
        document.getElementById('categoria').value = evento.extendedProps.categoria;
        
        // La fecha se mantiene intacta y no se muestra en el form para modificar
        document.getElementById('formMovimiento').dataset.fecha = evento.startStr;
        
        btnEliminar.classList.remove('d-none'); // Mostrar botón eliminar para Admin
    }

    // 5. Simulación de Envío de Formulario (Crear/Modificar)
    formMovimiento.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const id = document.getElementById('movimientoId').value;
        const desc = document.getElementById('desc').value;
        const importe = document.getElementById('importe').value;
        const categoria = document.getElementById('categoria').value;
        const fecha = document.getElementById('formMovimiento').dataset.fecha;

        const nuevoMovimiento = {
            id: id ? id : Date.now().toString(), // ID simulado
            title: desc,
            start: fecha,
            color: categoria === 'INGRESO' ? '#28a745' : '#dc3545',
            extendedProps: { importe: importe, categoria: categoria }
        };

        if (id) {
            // Lógica de Modificar (Admin)
            const eventoExistente = calendar.getEventById(id);
            eventoExistente.remove();
            movimientos = movimientos.filter(m => m.id !== id);
        }

        // Lógica de Añadir
        calendar.addEvent(nuevoMovimiento);
        movimientos.push(nuevoMovimiento);
        
        recalcularSaldo();
        modalMovimiento.hide();
    });

    // 6. Lógica de Eliminar (Admin)
    btnEliminar.addEventListener('click', function() {
        const id = document.getElementById('movimientoId').value;
        if (id) {
            const eventoExistente = calendar.getEventById(id);
            eventoExistente.remove();
            movimientos = movimientos.filter(m => m.id !== id);
            
            recalcularSaldo();
            modalMovimiento.hide();
            mostrarNotificacion('Movimiento eliminado correctamente', 'info');
        }
    });

    function mostrarNotificacion(mensaje, tipo) {
        const notiEl = document.getElementById('notificaciones');
        const msgEl = document.getElementById('msg-notificacion');
        
        notiEl.className = `alert alert-${tipo}`;
        msgEl.innerText = mensaje;
        notiEl.classList.remove('d-none');
        
        setTimeout(() => notiEl.classList.add('d-none'), 5000);
    }
});