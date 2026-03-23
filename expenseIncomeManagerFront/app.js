document.addEventListener('DOMContentLoaded', function() {
    // 1. DOM Elements
    const calendarElement = document.getElementById('calendar');
    const transactionModalElement = document.getElementById('transactionModal');
    const transactionModal = new bootstrap.Modal(transactionModalElement);
    const transactionForm = document.getElementById('transactionForm');
    const btnDelete = document.getElementById('btnDelete');
    const btnSave = document.getElementById('btnSave');
    const roleSelector = document.getElementById('roleSelector');
    
    // 2. State Management
    // Forzamos el ID 3 (que según tu DataInitializer es el basicUser)
    let currentUser = { role: 'basic', id: 3 }; 
    let currentBalance = 0;

    // Change role for testing dynamically
    roleSelector.addEventListener('change', (e) => {
        currentUser.role = e.target.value;
        // Dependiendo del rol, simulamos usar un usuario u otro de los que creaste
        if(currentUser.role === 'basic') currentUser.id = 3;
        if(currentUser.role === 'admin') currentUser.id = 2;
        if(currentUser.role === 'superadmin') currentUser.id = 1;
        
        showNotification(`Role changed to ${currentUser.role.toUpperCase()}`, 'info');
        // Recargar el calendario y el saldo al cambiar de rol
        loadTransactionsFromBackend(); 
        loadBalanceFromBackend();
    });

    // 3. FullCalendar Initialization
    const calendar = new FullCalendar.Calendar(calendarElement, {
        initialView: 'dayGridMonth',
        locale: 'es', // Calendario en español
        events: [], // Inicialmente vacío, los cargaremos de la BD
        
        // When clicking a day: Create transaction
        dateClick: function(info) {
            prepareModalForCreation(info.dateStr);
            transactionModal.show();
        },

        // When clicking an event: Edit/Delete depending on role
        eventClick: function(info) {
            if (currentUser.role === 'admin' || currentUser.role === 'superadmin') {
                prepareModalForEdition(info.event);
                transactionModal.show();
            } else {
                showNotification('Basic users do not have permissions to edit or delete transactions.', 'warning');
            }
        }
    });
    
    calendar.render();

    // Función para cargar los eventos del backend
    function loadTransactionsFromBackend() {
        fetch(`http://localhost:9393/api/movements?userId=${currentUser.id}`)
            .then(response => response.json())
            .then(data => {
                calendar.removeAllEvents();
                calendar.addEventSource(data);
            })
            .catch(error => console.error('Error cargando movimientos:', error));
    }

    // Función para pedir el saldo actual del usuario
    function loadBalanceFromBackend() {
        fetch(`http://localhost:9393/api/users/${currentUser.id}/balance`)
            .then(response => response.json())
            .then(data => {
                updateBalanceColor(data.balance);
            })
            .catch(error => console.error('Error cargando saldo:', error));
    }

    // Cargamos los datos (movimientos y saldo) por primera vez al abrir la página
    loadTransactionsFromBackend();
    loadBalanceFromBackend();

    // 4. Interface Functions
    function updateBalanceColor(amount) {
        const balanceElement = document.getElementById('balanceValue');
        balanceElement.innerText = amount.toFixed(2) + " €";
        
        balanceElement.classList.remove('text-success', 'text-warning', 'text-danger');
        
        if (amount > 0) balanceElement.classList.add('text-success');
        else if (amount === 0) balanceElement.classList.add('text-warning');
        else balanceElement.classList.add('text-danger');
    }

    // 5. Modal Management
    function prepareModalForCreation(date) {
        document.getElementById('modalTitle').innerText = 'New Transaction';
        document.getElementById('transactionId').value = '';
        document.getElementById('description').value = '';
        document.getElementById('amount').value = '';
        document.getElementById('category').value = 'EXPENSE';
        
        // Store date in form dataset
        transactionForm.dataset.date = date;
        
        btnDelete.classList.add('d-none'); // Hide delete button
        btnSave.classList.remove('d-none'); // Show save button
        
        // Enable fields (in case they were disabled by an admin view)
        toggleFormFields(false);
    }

    function prepareModalForEdition(event) {
        document.getElementById('modalTitle').innerText = 'Transaction Details';
        document.getElementById('transactionId').value = event.id;
        document.getElementById('description').value = event.extendedProps.description || event.title;
        document.getElementById('amount').value = event.extendedProps.amount;
        document.getElementById('category').value = event.extendedProps.type; // En el backend lo llamas "type"
        
        transactionForm.dataset.date = event.startStr;
        
        // Role Logic for Edit/Delete
        btnDelete.classList.remove('d-none'); // Admin and Superadmin can delete
        
        if (currentUser.role === 'admin') {
            // Admin can view and delete, but NOT modify
            btnSave.classList.add('d-none');
            toggleFormFields(true);
        } else if (currentUser.role === 'superadmin') {
            // Superadmin can view, modify and delete
            btnSave.classList.remove('d-none');
            toggleFormFields(false);
        }
    }

    function toggleFormFields(disabled) {
        document.getElementById('description').disabled = disabled;
        document.getElementById('amount').disabled = disabled;
        document.getElementById('category').disabled = disabled;
    }

    // 6. Form Submission (Create/Update)
    transactionForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const id = document.getElementById('transactionId').value;
        const description = document.getElementById('description').value;
        const amount = document.getElementById('amount').value;
        const category = document.getElementById('category').value;
        const date = transactionForm.dataset.date;

        if (id) {
            // Lógica de UPDATE (Superadmin) -> Lo haremos en el Paso 4
            showNotification('Update no implementado aún en backend', 'warning');
            transactionModal.hide();
        } else {
            // Lógica de CREATE -> Enviamos al Backend
            const requestBody = {
                description: description,
                amount: parseFloat(amount),
                type: category,
                userId: currentUser.id
            };

            fetch('http://localhost:9393/api/movements', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestBody)
            })
            .then(response => {
                if(response.ok) return response.json();
                throw new Error("Error al guardar");
            })
            .then(savedMovement => {
                showNotification('Transaction created successfully', 'success');
                transactionModal.hide();
                // Recargamos los eventos del calendario y el saldo
                loadTransactionsFromBackend();
                loadBalanceFromBackend();
            })
            .catch(error => {
                console.error(error);
                showNotification('Error guardando en el servidor', 'danger');
            });
        }
    });

    // 7. Delete Logic (Admin & Superadmin)
    btnDelete.addEventListener('click', function() {
        const id = document.getElementById('transactionId').value;
        if (id) {
            // Hacemos la petición DELETE al backend
            fetch(`http://localhost:9393/api/movements/${id}`, {
                method: 'DELETE'
            })
            .then(response => {
                if(response.ok) {
                    showNotification('Movimiento eliminado correctamente', 'success');
                    transactionModal.hide();
                    // Como el backend ha actualizado el saldo, recargamos las dos cosas
                    loadTransactionsFromBackend();
                    loadBalanceFromBackend();
                } else {
                    throw new Error("Error al eliminar");
                }
            })
            .catch(error => {
                console.error(error);
                showNotification('Error eliminando en el servidor', 'danger');
            });
        }
    });

    // 8. Notifications
    function showNotification(message, type) {
        const notificationArea = document.getElementById('notificationArea');
        const notificationMessage = document.getElementById('notificationMessage');
        
        notificationArea.className = `alert alert-${type} mt-3`;
        notificationMessage.innerText = message;
        notificationArea.classList.remove('d-none');
        
        // Auto-hide after 5 seconds
        setTimeout(() => notificationArea.classList.add('d-none'), 5000);
    }
});