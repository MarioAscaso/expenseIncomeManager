// --- login.js ---

const API_BASE_URL = 'http://localhost:9393/api';

document.addEventListener('DOMContentLoaded', function() {
    
    const loginForm = document.getElementById('loginForm');

    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();

            // CAPTURAMOS LOS ELEMENTOS (Nos aseguramos de que existan)
            const userInput = document.getElementById('loginUsername');
            const passInput = document.getElementById('loginPassword');

            // Si por algún motivo no los encuentra, paramos aquí para no dar el error de 'null'
            if (!userInput || !passInput) {
                console.error("No se han encontrado los inputs loginUsername o loginPassword en el HTML");
                return;
            }

            const payload = {
                username: userInput.value,
                password: passInput.value
            };

            // PETICIÓN AL BACKEND
            fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    return response.json().then(err => {
                        throw new Error(err.error || "Usuario o contraseña incorrectos");
                    });
                }
            })
            .then(userData => {
                // GUARDAMOS EL USUARIO EN LOCALSTORAGE
                // Importante: El backend debe devolver un objeto con id, username y role
                localStorage.setItem('currentUser', JSON.stringify(userData));

                Swal.fire({
                    title: '¡Bienvenido!',
                    text: `Hola de nuevo, ${userData.username}`,
                    icon: 'success',
                    timer: 1500,
                    showConfirmButton: false
                }).then(() => {
                    // REDIRIGIMOS AL INDEX (Panel principal)
                    window.location.href = 'index.html';
                });
            })
            .catch(error => {
                Swal.fire({
                    title: 'Error de acceso',
                    text: error.message,
                    icon: 'error',
                    confirmButtonColor: '#ef4444'
                });
            });
        });
    } else {
        console.error("No se ha encontrado el formulario con ID 'loginForm'");
    }
});