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
    let currentUser = { role: 'basic', id: 1 }; // Default role
    let transactions = [];
    let currentBalance = 0;

    // Change role for testing dynamically
    roleSelector.addEventListener('change', (e) => {
        currentUser.role = e.target.value;
        showNotification(`Role changed to ${currentUser.role.toUpperCase()}`, 'info');
    });

    // 3. FullCalendar Initialization
    const calendar = new FullCalendar.Calendar(calendarElement, {
        initialView: 'dayGridMonth',
        locale: 'en',
        events: transactions,
        
        // When clicking a day: Create transaction (All roles can create)
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

    // 4. Interface Functions
    function recalculateBalance() {
        currentBalance = transactions.reduce((total, transaction) => {
            const amount = parseFloat(transaction.extendedProps.amount);
            return transaction.extendedProps.category === 'INCOME' ? total + amount : total - amount;
        }, 0);
        
        updateBalanceColor(currentBalance);
    }

    function updateBalanceColor(amount) {
        const balanceElement = document.getElementById('balanceValue');
        balanceElement.innerText = amount.toFixed(2);
        
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
        document.getElementById('description').value = event.title;
        document.getElementById('amount').value = event.extendedProps.amount;
        document.getElementById('category').value = event.extendedProps.category;
        
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

        const newTransaction = {
            id: id ? id : Date.now().toString(), // Simulated ID, backend will provide this later
            title: description,
            start: date,
            color: category === 'INCOME' ? '#28a745' : '#dc3545',
            extendedProps: { amount: amount, category: category }
        };

        if (id) {
            // Update logic (Superadmin only)
            const existingEvent = calendar.getEventById(id);
            if (existingEvent) existingEvent.remove();
            transactions = transactions.filter(t => t.id !== id);
            showNotification('Transaction updated successfully', 'success');
        } else {
            showNotification('Transaction created successfully', 'success');
        }

        // Add to calendar and state
        calendar.addEvent(newTransaction);
        transactions.push(newTransaction);
        
        recalculateBalance();
        transactionModal.hide();
    });

    // 7. Delete Logic (Admin & Superadmin)
    btnDelete.addEventListener('click', function() {
        const id = document.getElementById('transactionId').value;
        if (id) {
            const existingEvent = calendar.getEventById(id);
            if (existingEvent) existingEvent.remove();
            transactions = transactions.filter(t => t.id !== id);
            
            recalculateBalance();
            transactionModal.hide();
            showNotification('Transaction deleted successfully by Admin', 'info');
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