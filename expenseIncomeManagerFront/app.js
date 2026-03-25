document.addEventListener('DOMContentLoaded', function() {
    
    const API_BASE_URL = 'http://localhost:9393/api';
    
    const userJson = localStorage.getItem('currentUser');
    if (!userJson) {
        window.location.href = 'login.html';
        return; 
    }

    const currentUser = JSON.parse(userJson);
    document.getElementById('loggedUserName').innerText = currentUser.username;
    document.getElementById('loggedUserRole').innerText = currentUser.role;

    document.getElementById('btnLogout').addEventListener('click', function() {
        localStorage.removeItem('currentUser'); 
        window.location.href = 'login.html';
    });

    const btnToggle = document.getElementById('btnToggleNotifications');
    const statusText = document.getElementById('notificationStatus');

    btnToggle.addEventListener('click', function() {
        fetch(`${API_BASE_URL}/users/${currentUser.id}/toggle-notifications`, { method: 'PUT' })
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
        .catch(() => showNotification('Error al cambiar notificaciones', 'error'));
    });

    const calendarElement = document.getElementById('calendar');
    const transactionModalElement = document.getElementById('transactionModal');
    const transactionModal = new bootstrap.Modal(transactionModalElement);
    const transactionForm = document.getElementById('transactionForm');
    const btnDelete = document.getElementById('btnDelete');
    const btnSave = document.getElementById('btnSave');
    const fileInputContainer = document.getElementById('fileInputContainer');
    const fileViewer = document.getElementById('fileViewer');
    const fileLink = document.getElementById('fileLink');
    
    const calendar = new FullCalendar.Calendar(calendarElement, {
        initialView: 'dayGridMonth',
        locale: 'es',
        height: '100%', 
        headerToolbar: false,           
        showNonCurrentDates: false,     
        fixedWeekCount: false,          
        firstDay: 1,                    
        events: [],
        
        eventDidMount: function(info) {
            const isIncome = info.event.extendedProps.type === 'INCOME';
            info.el.style.backgroundColor = isIncome ? '#2a9d8f' : '#e63946'; 
        },
        
        datesSet: function(info) {
            document.getElementById('customCalendarTitle').innerText = info.view.title;
        },

        dateClick: (info) => {
            prepareModalForCreation(info.dateStr);
            transactionModal.show();
        },
        eventClick: (info) => {
            if (['admin', 'superadmin', 'basic'].includes(currentUser.role)) {
                prepareModalForEdition(info.event);
                transactionModal.show();
            }
        }
    });
    calendar.render();

    document.getElementById('btnPrevMonth').addEventListener('click', () => calendar.prev());
    document.getElementById('btnNextMonth').addEventListener('click', () => calendar.next());
    document.getElementById('btnToday').addEventListener('click', () => calendar.today());

    function loadTransactionsFromBackend() {
        fetch(`${API_BASE_URL}/movements?userId=${currentUser.id}`)
            .then(res => res.json())
            .then(data => {
                calendar.removeAllEvents();
                calendar.addEventSource(data);
            })
            .catch(err => console.error('Error cargando movimientos:', err));
    }

    function loadBalanceFromBackend() {
        fetch(`${API_BASE_URL}/users/${currentUser.id}/balance`)
            .then(res => res.json())
            .then(data => {
                const balanceElement = document.getElementById('balanceValue');
                balanceElement.innerText = data.balance.toFixed(2) + " €";
                balanceElement.classList.remove('text-success', 'text-warning', 'text-danger');
                if (data.balance > 0) balanceElement.classList.add('text-success');
                else if (data.balance === 0) balanceElement.classList.add('text-warning');
                else balanceElement.classList.add('text-danger');
            })
            .catch(err => console.error('Error cargando saldo:', err));
    }

    function refreshDashboard() {
        loadTransactionsFromBackend();
        loadBalanceFromBackend();
    }

    refreshDashboard(); 

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
        const diffInMinutes = (new Date() - new Date(event.start)) / 1000 / 60;
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

    function handleApiResponse(response) {
        if (response.ok) return response.json().catch(() => ({})); 
        return response.json().then(err => { throw new Error(err.error || "Error en la petición") });
    }

    transactionForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const id = document.getElementById('transactionId').value;
        const formData = new FormData();
        formData.append('description', document.getElementById('description').value);
        formData.append('amount', document.getElementById('amount').value);
        formData.append('type', document.getElementById('category').value);
        
        const fileInput = document.getElementById('file').files[0];
        if (fileInput) formData.append('file', fileInput);

        if (id) {
            fetch(`${API_BASE_URL}/movements/${id}`, { method: 'PUT', body: formData })
            .then(handleApiResponse)
            .then(() => {
                showNotification('Movimiento actualizado correctamente', 'success');
                transactionModal.hide();
                refreshDashboard();
            })
            .catch(err => showNotification(err.message, 'error'));
        } else {
            formData.append('userId', currentUser.id);
            formData.append('date', transactionForm.dataset.date);

            fetch(`${API_BASE_URL}/movements`, { method: 'POST', body: formData })
            .then(handleApiResponse)
            .then(() => {
                showNotification('Transacción creada correctamente', 'success');
                transactionModal.hide();
                refreshDashboard();
            })
            .catch(err => showNotification(err.message, 'error'));
        }
    });

    btnDelete.addEventListener('click', function() {
        const id = document.getElementById('transactionId').value;
        if (id) {
            fetch(`${API_BASE_URL}/movements/${id}`, { method: 'DELETE' })
            .then(handleApiResponse)
            .then(() => {
                showNotification('Movimiento eliminado', 'success');
                transactionModal.hide();
                refreshDashboard();
            })
            .catch(err => showNotification(err.message, 'error'));
        }
    });

    function executeMoneyTransfer(endpoint, payload, modalId, formId, successMsg) {
        fetch(`${API_BASE_URL}/${endpoint}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (response.ok) return response; 
            return response.json().then(err => { throw new Error(err.error || "Error en el envío") });
        })
        .then(() => {
            showNotification(successMsg, 'success');
            bootstrap.Modal.getInstance(document.getElementById(modalId)).hide();
            document.getElementById(formId).reset();
            refreshDashboard();
        })
        .catch(err => showNotification(err.message, 'error'));
    }

    document.getElementById('transferForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const payload = {
            originUserId: currentUser.id,
            targetUsername: document.getElementById('targetUser').value,
            amount: parseFloat(document.getElementById('transferAmount').value),
            description: document.getElementById('transferConcept').value
        };
        executeMoneyTransfer('transfers', payload, 'transferModal', 'transferForm', 'Transferencia enviada con éxito');
    });

    document.getElementById('bizumForm').addEventListener('submit', function(e) {
        e.preventDefault();
        const payload = {
            originUserId: currentUser.id,
            targetPhoneNumber: document.getElementById('bizumPhone').value,
            amount: parseFloat(document.getElementById('bizumAmount').value),
            description: document.getElementById('bizumConcept').value
        };
        executeMoneyTransfer('bizum', payload, 'bizumModal', 'bizumForm', 'Bizum enviado al instante');
    });

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