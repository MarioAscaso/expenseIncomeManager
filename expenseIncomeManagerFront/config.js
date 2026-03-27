const API_BASE_URL = 'http://localhost:9393/api';

const userJson = localStorage.getItem('currentUser');
if (!userJson) {
    window.location.href = 'login.html';
}
const currentUser = JSON.parse(userJson);

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

function handleApiResponse(response) {
    if (response.ok) return response.json().catch(() => ({})); 
    return response.json().then(err => { throw new Error(err.error || "Error en la petición") });
}

function loadBalanceFromBackend() {
    fetch(`${API_BASE_URL}/users/${currentUser.id}/balance`)
        .then(res => res.json())
        .then(data => {
            const balanceElement = document.getElementById('balanceValue');
            if(balanceElement) {
                balanceElement.innerText = data.balance.toFixed(2) + " €";
                balanceElement.classList.remove('text-success', 'text-warning', 'text-danger');
                if (data.balance > 0) balanceElement.classList.add('text-success');
                else if (data.balance === 0) balanceElement.classList.add('text-warning');
                else balanceElement.classList.add('text-danger');
            }
        })
        .catch(err => console.error('Error cargando saldo:', err));
}

function refreshDashboard() {
    loadBalanceFromBackend();
    if (typeof loadTransactionsFromBackend === 'function') {
        loadTransactionsFromBackend();
    }
}