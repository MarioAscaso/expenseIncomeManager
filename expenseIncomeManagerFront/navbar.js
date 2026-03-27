document.addEventListener('DOMContentLoaded', function() {
    
    const nameEl = document.getElementById('loggedUserName');
    const roleEl = document.getElementById('loggedUserRole');
    if(nameEl) nameEl.innerText = currentUser.username;
    if(roleEl) roleEl.innerText = currentUser.role;

    const btnLogout = document.getElementById('btnLogout');
    if(btnLogout) {
        btnLogout.addEventListener('click', function() {
            localStorage.removeItem('currentUser'); 
            window.location.href = 'login.html';
        });
    }

    const btnToggle = document.getElementById('btnToggleNotifications');
    const statusText = document.getElementById('notificationStatus');

    if(btnToggle) {
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
    }
});