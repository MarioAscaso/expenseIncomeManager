let fileInputContainer, fileViewer, fileLink, transactionForm, btnDelete, btnSave;

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

document.addEventListener('DOMContentLoaded', function() {
    
    fileInputContainer = document.getElementById('fileInputContainer');
    fileViewer = document.getElementById('fileViewer');
    fileLink = document.getElementById('fileLink');
    transactionForm = document.getElementById('transactionForm');
    btnDelete = document.getElementById('btnDelete');
    btnSave = document.getElementById('btnSave');

    if(transactionForm) {
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
                    bootstrap.Modal.getInstance(document.getElementById('transactionModal')).hide();
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
                    bootstrap.Modal.getInstance(document.getElementById('transactionModal')).hide();
                    refreshDashboard();
                })
                .catch(err => showNotification(err.message, 'error'));
            }
        });
    }

    if(btnDelete) {
        btnDelete.addEventListener('click', function() {
            const id = document.getElementById('transactionId').value;
            if (id) {
                fetch(`${API_BASE_URL}/movements/${id}`, { method: 'DELETE' })
                .then(handleApiResponse)
                .then(() => {
                    showNotification('Movimiento eliminado', 'success');
                    bootstrap.Modal.getInstance(document.getElementById('transactionModal')).hide();
                    refreshDashboard();
                })
                .catch(err => showNotification(err.message, 'error'));
            }
        });
    }

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

    const transferForm = document.getElementById('transferForm');
    if(transferForm) {
        transferForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const payload = {
                originUserId: currentUser.id,
                targetUsername: document.getElementById('targetUser').value,
                amount: parseFloat(document.getElementById('transferAmount').value),
                description: document.getElementById('transferConcept').value
            };
            executeMoneyTransfer('transfers', payload, 'transferModal', 'transferForm', 'Transferencia enviada con éxito');
        });
    }

    const bizumForm = document.getElementById('bizumForm');
    if(bizumForm) {
        bizumForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const payload = {
                originUserId: currentUser.id,
                targetPhoneNumber: document.getElementById('bizumPhone').value,
                amount: parseFloat(document.getElementById('bizumAmount').value),
                description: document.getElementById('bizumConcept').value
            };
            executeMoneyTransfer('bizum', payload, 'bizumModal', 'bizumForm', 'Bizum enviado al instante');
        });
    }
});