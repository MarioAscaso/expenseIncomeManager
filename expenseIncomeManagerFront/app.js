document.addEventListener('DOMContentLoaded', function() {
    
    // --- 1. SEGURIDAD: COMPROBAR SESIÓN ---
    const userJson = localStorage.getItem('currentUser');
    if (!userJson) {
        window.location.href = 'login.html';
        return; 
    }

    const currentUser = JSON.parse(userJson);
    document.getElementById('loggedUserName').innerText = currentUser.username;
    document.getElementById('loggedUserRole').innerText = currentUser.role;

    // --- 2. LÓGICA DE LOGOUT ---
    document.getElementById('btnLogout').addEventListener('click', function() {
        localStorage.removeItem('currentUser'); 
        window.location.href = 'login.html';
    });

    // --- 3. LÓGICA CAMPANITA DE NOTIFICACIONES ---
    const btnToggle = document.getElementById('btnToggleNotifications');
    const statusText = document.getElementById('notificationStatus');

    btnToggle.addEventListener('click', function() {
        fetch(`http://localhost:9393/api/users/${currentUser.id}/toggle-notifications`, {
            method: 'PUT'
        })
        .then(res => res.json())
        .then(isEnabled => {
            if(isEnabled) {
                statusText.innerText = "Activas";
                btnToggle.classList.replace('btn-outline-danger', 'btn-outline-secondary');
                showNotification('Notificaciones de correo ACTIVADAS', 'success');
            } else {
                statusText.innerText = "Apagadas";
                btnToggle.classList.replace('btn-outline-secondary', 'btn-outline-danger');
                showNotification('Notificaciones de correo DESACTIVADAS', 'info');
            }
        })
        .catch(err => showNotification('Error al cambiar notificaciones', 'error'));
    });

    // --- 4. ELEMENTOS DEL DOM ---
    const calendarElement = document.getElementById('calendar');
    const transactionModalElement = document.getElementById('transactionModal');
    const transactionModal = new bootstrap.Modal(transactionModalElement);
    const transactionForm = document.getElementById('transactionForm');
    const btnDelete = document.getElementById('btnDelete');
    const btnSave = document.getElementById('btnSave');
    const fileInputContainer = document.getElementById('fileInputContainer');
    const fileViewer = document.getElementById('fileViewer');
    const fileLink = document.getElementById('fileLink');
    
    // --- 5. INICIALIZAR FULLCALENDAR ---
    const calendar = new FullCalendar.Calendar(calendarElement, {
        initialView: 'dayGridMonth',
        locale: 'es',
        events: [],
        
        dateClick: function(info) {
            prepareModalForCreation(info.dateStr);
            transactionModal.show();
        },

        eventClick: function(info) {
            if (currentUser.role === 'admin' || currentUser.role === 'superadmin' || currentUser.role === 'basic') {
                prepareModalForEdition(info.event);
                transactionModal.show();
            }
        }
    });
    calendar.render();

    // --- 6. CARGAR DATOS DEL BACKEND ---
    function loadTransactionsFromBackend() {
        fetch(`http://localhost:9393/api/movements?userId=${currentUser.id}`)
            .then(response => response.json())
            .then(data => {
                calendar.removeAllEvents();
                calendar.addEventSource(data);
            })
            .catch(error => console.error('Error cargando movimientos:', error));
    }

    function loadBalanceFromBackend() {
        fetch(`http://localhost:9393/api/users/${currentUser.id}/balance`)
            .then(response => response.json())
            .then(data => {
                const balanceElement = document.getElementById('balanceValue');
                balanceElement.innerText = data.balance.toFixed(2) + " €";
                balanceElement.classList.remove('text-success', 'text-warning', 'text-danger');
                if (data.balance > 0) balanceElement.classList.add('text-success');
                else if (data.balance === 0) balanceElement.classList.add('text-warning');
                else balanceElement.classList.add('text-danger');
            })
            .catch(error => console.error('Error cargando saldo:', error));
    }

    loadTransactionsFromBackend();
    loadBalanceFromBackend();

    // --- 7. FUNCIONES DE INTERFAZ Y MODAL ---
    function prepareModalForCreation(date) {
        document.getElementById('modalTitle').innerText = 'Nueva Transacción';
        document.getElementById('transactionId').value = '';
        document.getElementById('description').value = '';
        document.getElementById('amount').value = '';
        document.getElementById('category').value = 'EXPENSE';
        document.getElementById('file').value = '';
        
        fileInputContainer.classList.remove('d-none');
        fileViewer.classList.add('d-none');
        
        transactionForm.dataset.date = date;
        btnDelete.classList.add('d-none');
        btnSave.classList.remove('d-none');
        toggleFormFields(false);
    }

    function prepareModalForEdition(event) {
        document.getElementById('transactionId').value = event.id;
        document.getElementById('description').value = event.extendedProps.description || event.title;
        document.getElementById('amount').value = event.extendedProps.amount;
        document.getElementById('category').value = event.extendedProps.type;
        
        fileInputContainer.classList.remove('d-none');
        if (event.extendedProps.attachedFileUrl) {
            fileViewer.classList.remove('d-none');
            fileLink.href = event.extendedProps.attachedFileUrl;
        } else {
            fileViewer.classList.add('d-none');
            fileLink.href = "#";
        }

        transactionForm.dataset.date = event.startStr;

        const now = new Date();
        const createdDate = new Date(event.start);
        const diffInMinutes = (now - createdDate) / 1000 / 60;
        const remaining = Math.max(0, (5 - diffInMinutes)).toFixed(1); 
        const isExpired = diffInMinutes > 5;

        if (currentUser.role === 'basic') {
            if (isExpired) {
                btnDelete.classList.add('d-none');
                btnSave.classList.add('d-none');
                fileInputContainer.classList.add('d-none');
                toggleFormFields(true);
                document.getElementById('modalTitle').innerText = 'Consulta (Tiempo agotado)';
            } else {
                btnDelete.classList.remove('d-none');
                btnSave.classList.remove('d-none');
                toggleFormFields(false);
                document.getElementById('modalTitle').innerText = `Editar (Quedan ${remaining} min)`;
            }
        } else if (currentUser.role === 'admin') {
            btnDelete.classList.remove('d-none');
            btnSave.classList.add('d-none');
            fileInputContainer.classList.add('d-none');
            toggleFormFields(true);
            document.getElementById('modalTitle').innerText = 'Consulta de Transacción';
        } else if (currentUser.role === 'superadmin') {
            btnDelete.classList.remove('d-none');
            btnSave.classList.remove('d-none');
            toggleFormFields(false);
            document.getElementById('modalTitle').innerText = 'Editar Transacción';
        }
    }

    function toggleFormFields(disabled) {
        document.getElementById('description').disabled = disabled;
        document.getElementById('amount').disabled = disabled;
        document.getElementById('category').disabled = disabled;
    }

    // --- 8. GUARDAR O ACTUALIZAR MOVIMIENTO ---
    transactionForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const id = document.getElementById('transactionId').value;
        const description = document.getElementById('description').value;
        const amount = document.getElementById('amount').value;
        const category = document.getElementById('category').value;
        const fileInput = document.getElementById('file').files[0];
        
        const formData = new FormData();
        formData.append('description', description);
        formData.append('amount', amount);
        formData.append('type', category);
        if (fileInput) formData.append('file', fileInput);

        if (id) {
            // ACTUALIZAR (PUT)
            fetch(`http://localhost:9393/api/movements/${id}`, {
                method: 'PUT',
                body: formData
            })
            .then(async response => {
                if(response.ok) return response.json();
                const err = await response.json();
                throw new Error(err.error || "Error al actualizar");
            })
            .then(() => {
                showNotification('Movimiento actualizado correctamente', 'success');
                transactionModal.hide();
                loadTransactionsFromBackend();
                loadBalanceFromBackend();
            })
            .catch(error => showNotification(error.message, 'error'));
        } else {
            // CREAR (POST)
            formData.append('userId', currentUser.id);
            fetch('http://localhost:9393/api/movements', {
                method: 'POST',
                body: formData
            })
            .then(async response => {
                if(response.ok) return response.json();
                const err = await response.json();
                throw new Error(err.error || "Error al guardar");
            })
            .then(() => {
                showNotification('Transacción creada correctamente', 'success');
                transactionModal.hide();
                loadTransactionsFromBackend();
                loadBalanceFromBackend();
            })
            .catch(error => showNotification(error.message, 'error'));
        }
    });

    // --- 9. LÓGICA DE TRANSFERENCIAS CLÁSICAS ---
    document.getElementById('transferForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const payload = {
            originUserId: currentUser.id,
            targetUsername: document.getElementById('targetUser').value,
            amount: parseFloat(document.getElementById('transferAmount').value),
            description: document.getElementById('transferConcept').value
        };

        fetch('http://localhost:9393/api/transfers', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(async response => {
            if(response.ok) {
                showNotification('Transferencia enviada con éxito', 'success');
                bootstrap.Modal.getInstance(document.getElementById('transferModal')).hide();
                document.getElementById('transferForm').reset();
                loadTransactionsFromBackend();
                loadBalanceFromBackend();
            } else {
                const err = await response.text(); // <-- OJO, esta API devuelve texto (String)
                throw new Error(err);
            }
        })
        .catch(error => showNotification(error.message, 'error'));
    });

    // --- 10. LÓGICA DEL BIZUM (NUEVO) ---
    document.getElementById('bizumForm').addEventListener('submit', function(e) {
        e.preventDefault();
        
        const payload = {
            originUserId: currentUser.id,
            targetPhoneNumber: document.getElementById('bizumPhone').value,
            amount: parseFloat(document.getElementById('bizumAmount').value),
            description: document.getElementById('bizumConcept').value
        };

        fetch('http://localhost:9393/api/bizum', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(async response => {
            if(response.ok) {
                showNotification('Bizum enviado al instante', 'success');
                bootstrap.Modal.getInstance(document.getElementById('bizumModal')).hide();
                document.getElementById('bizumForm').reset();
                loadTransactionsFromBackend();
                loadBalanceFromBackend();
            } else {
                const err = await response.json(); // <-- Gracias al manejador global
                throw new Error(err.error || "Error al enviar el Bizum");
            }
        })
        .catch(error => showNotification(error.message, 'error'));
    });

    // --- 11. BORRAR ---
    btnDelete.addEventListener('click', function() {
        const id = document.getElementById('transactionId').value;
        if (id) {
            fetch(`http://localhost:9393/api/movements/${id}`, { method: 'DELETE' })
            .then(async response => {
                if(response.ok) {
                    showNotification('Movimiento eliminado', 'success');
                    transactionModal.hide();
                    loadTransactionsFromBackend();
                    loadBalanceFromBackend();
                } else { 
                    const err = await response.json();
                    throw new Error(err.error || "Error al eliminar"); 
                }
            })
            .catch(error => showNotification(error.message, 'error'));
        }
    });

    // --- 12. SWEETALERT2 ---
    function showNotification(message, type) {
        Swal.fire({
            title: type === 'success' ? '¡Éxito!' : (type === 'error' ? 'Error' : 'Aviso'),
            text: message,
            icon: type,
            confirmButtonColor: type === 'success' ? '#28a745' : (type === 'info' ? '#17a2b8' : '#dc3545'),
            timer: 4000,
            timerProgressBar: true
        });
    }
});