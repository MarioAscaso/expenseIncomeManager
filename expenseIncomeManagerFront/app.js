document.addEventListener('DOMContentLoaded', function() {
    
    // --- 1. SEGURIDAD: COMPROBAR SESIÓN ---
    const userJson = localStorage.getItem('currentUser');
    if (!userJson) {
        // Si no hay usuario guardado en la memoria, redirigimos al login
        window.location.href = 'login.html';
        return; 
    }

    const currentUser = JSON.parse(userJson);
    document.getElementById('loggedUserName').innerText = currentUser.username;
    document.getElementById('loggedUserRole').innerText = currentUser.role;

    // --- 2. LÓGICA DE LOGOUT ---
    document.getElementById('btnLogout').addEventListener('click', function() {
        localStorage.removeItem('currentUser'); // Borramos la sesión
        window.location.href = 'login.html';
    });

    // --- 3. ELEMENTOS DEL DOM ---
    const calendarElement = document.getElementById('calendar');
    const transactionModalElement = document.getElementById('transactionModal');
    const transactionModal = new bootstrap.Modal(transactionModalElement);
    const transactionForm = document.getElementById('transactionForm');
    const btnDelete = document.getElementById('btnDelete');
    const btnSave = document.getElementById('btnSave');
    const fileInputContainer = document.getElementById('fileInputContainer');
    const fileViewer = document.getElementById('fileViewer');
    const fileLink = document.getElementById('fileLink');
    
    // --- 4. INICIALIZAR FULLCALENDAR ---
    const calendar = new FullCalendar.Calendar(calendarElement, {
        initialView: 'dayGridMonth',
        locale: 'es',
        events: [],
        
        dateClick: function(info) {
            prepareModalForCreation(info.dateStr);
            transactionModal.show();
        },

        eventClick: function(info) {
            // Verificamos permisos básicos antes de abrir el modal
            if (currentUser.role === 'admin' || currentUser.role === 'superadmin' || currentUser.role === 'basic') {
                prepareModalForEdition(info.event);
                transactionModal.show();
            }
        }
    });
    calendar.render();

    // --- 5. CARGAR DATOS DEL BACKEND ---
    function loadTransactionsFromBackend() {
        // Obtenemos los movimientos específicos del usuario logueado
        fetch(`http://localhost:9393/api/movements?userId=${currentUser.id}`)
            .then(response => response.json())
            .then(data => {
                calendar.removeAllEvents();
                calendar.addEventSource(data);
            })
            .catch(error => console.error('Error cargando movimientos:', error));
    }

    function loadBalanceFromBackend() {
        // Obtenemos el saldo actualizado de la cuenta del usuario
        fetch(`http://localhost:9393/api/users/${currentUser.id}/balance`)
            .then(response => response.json())
            .then(data => {
                updateBalanceColor(data.balance);
            })
            .catch(error => console.error('Error cargando saldo:', error));
    }

    // Carga inicial de datos al entrar
    loadTransactionsFromBackend();
    loadBalanceFromBackend();

    // --- 6. FUNCIONES DE INTERFAZ Y MODAL ---
    function updateBalanceColor(amount) {
        const balanceElement = document.getElementById('balanceValue');
        balanceElement.innerText = amount.toFixed(2) + " €";
        
        balanceElement.classList.remove('text-success', 'text-warning', 'text-danger');
        
        if (amount > 0) balanceElement.classList.add('text-success');
        else if (amount === 0) balanceElement.classList.add('text-warning');
        else balanceElement.classList.add('text-danger');
    }

    function prepareModalForCreation(date) {
        document.getElementById('modalTitle').innerText = 'Nueva Transacción';
        document.getElementById('transactionId').value = '';
        document.getElementById('description').value = '';
        document.getElementById('amount').value = '';
        document.getElementById('category').value = 'EXPENSE';
        document.getElementById('file').value = '';
        
        fileInputContainer.classList.remove('d-none'); // Permitir subida en nuevos
        fileViewer.classList.add('d-none');
        
        transactionForm.dataset.date = date;
        btnDelete.classList.add('d-none');
        btnSave.classList.remove('d-none');
        toggleFormFields(false);
    }

    function prepareModalForEdition(event) {
        document.getElementById('modalTitle').innerText = 'Detalles de Transacción';
        document.getElementById('transactionId').value = event.id;
        document.getElementById('description').value = event.extendedProps.description || event.title;
        document.getElementById('amount').value = event.extendedProps.amount;
        document.getElementById('category').value = event.extendedProps.type;
        
        // Gestión de visualización de archivos adjuntos (Imagen o PDF)
        fileInputContainer.classList.add('d-none');
        if (event.extendedProps.attachedFileUrl) {
            fileViewer.classList.remove('d-none');
            fileLink.href = event.extendedProps.attachedFileUrl;
        } else {
            fileViewer.classList.add('d-none');
            fileLink.href = "#";
        }

        transactionForm.dataset.date = event.startStr;

        // --- LÓGICA DE RESTRICCIÓN DE 5 MINUTOS ---
        const now = new Date();
        const createdDate = new Date(event.start);
        const diffInMinutes = (now - createdDate) / 1000 / 60;
        const isExpired = diffInMinutes > 5;

        // Permisos basados en Rol y Tiempo de creación
        if (currentUser.role === 'basic') {
            if (isExpired) {
                // Pasados 5 minutos, el usuario BASIC no puede editar ni borrar
                btnDelete.classList.add('d-none');
                btnSave.classList.add('d-none');
                toggleFormFields(true);
                document.getElementById('modalTitle').innerText += ' (Solo lectura)';
            } else {
                btnDelete.classList.remove('d-none');
                btnSave.classList.remove('d-none');
                toggleFormFields(false);
            }
        } else if (currentUser.role === 'admin') {
            // El Admin puede borrar siempre, pero no editar
            btnDelete.classList.remove('d-none');
            btnSave.classList.add('d-none');
            toggleFormFields(true);
        } else if (currentUser.role === 'superadmin') {
            // El Superadmin tiene control total sin restricciones de tiempo
            btnDelete.classList.remove('d-none');
            btnSave.classList.remove('d-none');
            toggleFormFields(false);
        }
    }

    function toggleFormFields(disabled) {
        document.getElementById('description').disabled = disabled;
        document.getElementById('amount').disabled = disabled;
        document.getElementById('category').disabled = disabled;
    }

    // --- 7. ENVÍO DEL FORMULARIO (CREAR / ACTUALIZAR) ---
    transactionForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const id = document.getElementById('transactionId').value;
        const description = document.getElementById('description').value;
        const amount = document.getElementById('amount').value;
        const category = document.getElementById('category').value;
        
        if (id) {
            // LÓGICA DE ACTUALIZACIÓN (Solo para perfiles con permiso)
            const requestBody = { description, amount: parseFloat(amount), type: category };

            fetch(`http://localhost:9393/api/movements/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestBody)
            })
            .then(response => {
                if(response.ok) return response.json();
                throw new Error("Error al actualizar");
            })
            .then(() => {
                showNotification('Movimiento actualizado', 'success');
                transactionModal.hide();
                loadTransactionsFromBackend();
                loadBalanceFromBackend();
            })
            .catch(error => showNotification('Error actualizando', 'danger'));

        } else {
            // LÓGICA DE CREACIÓN (Uso de FormData para soportar archivos)
            const fileInput = document.getElementById('file').files[0];
            const formData = new FormData();
            formData.append('description', description);
            formData.append('amount', amount);
            formData.append('type', category);
            formData.append('userId', currentUser.id);
            if (fileInput) {
                formData.append('file', fileInput);
            }

            fetch('http://localhost:9393/api/movements', {
                method: 'POST',
                // El navegador gestiona el Content-Type automáticamente al usar FormData
                body: formData
            })
            .then(response => {
                if(response.ok) return response.json();
                throw new Error("Error al guardar");
            })
            .then(() => {
                showNotification('Transacción creada correctamente', 'success');
                transactionModal.hide();
                loadTransactionsFromBackend();
                loadBalanceFromBackend();
            })
            .catch(error => showNotification('Error guardando el movimiento', 'danger'));
        }
    });

    // --- 8. LÓGICA DE ELIMINACIÓN ---
    btnDelete.addEventListener('click', function() {
        const id = document.getElementById('transactionId').value;
        if (id) {
            fetch(`http://localhost:9393/api/movements/${id}`, { method: 'DELETE' })
            .then(response => {
                if(response.ok) {
                    showNotification('Movimiento eliminado', 'success');
                    transactionModal.hide();
                    loadTransactionsFromBackend();
                    loadBalanceFromBackend();
                } else { throw new Error("Error al eliminar"); }
            })
            .catch(error => showNotification('Error al eliminar el movimiento', 'danger'));
        }
    });

    // --- 9. NOTIFICACIONES ---
    function showNotification(message, type) {
        const notificationArea = document.getElementById('notificationArea');
        const notificationMessage = document.getElementById('notificationMessage');
        notificationArea.className = `alert alert-${type} mt-3 mb-3`;
        notificationMessage.innerText = message;
        notificationArea.classList.remove('d-none');
        setTimeout(() => notificationArea.classList.add('d-none'), 5000);
    }
});