document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    const alertMessage = document.getElementById('alertMessage');

    // Si ya estamos logueados, no pintamos nada aquí
    if (localStorage.getItem('currentUser')) {
        window.location.href = 'index.html';
    }

    function showAlert(message, type) {
        alertMessage.className = `alert alert-${type} text-center`;
        alertMessage.innerText = message;
        alertMessage.classList.remove('d-none');
    }

    registerForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const userValue = document.getElementById('registerUsername').value;
        const passValue = document.getElementById('registerPassword').value;

        fetch('http://localhost:9393/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: userValue, password: passValue })
        })
        .then(async response => {
            if (response.ok) {
                // Guardamos un mensaje temporal para leerlo en el login.js
                sessionStorage.setItem('registerSuccess', '¡Cuenta creada con éxito! Ya puedes iniciar sesión.');
                // Redirigimos al login
                window.location.href = 'login.html';
            } else {
                const errorText = await response.text();
                throw new Error(errorText);
            }
        })
        .catch(error => showAlert(error.message, 'danger'));
    });
});