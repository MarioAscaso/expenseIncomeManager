// --- register.js ---

// Definimos la URL base de tu API (igual que en config.js)
const API_BASE_URL = 'http://localhost:9393/api';

document.addEventListener('DOMContentLoaded', function() {
    
    const registerForm = document.getElementById('registerForm');

    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault(); // Evitamos que la página se recargue al enviar

            // 1. Recogemos los valores de los inputs
            const usernameValue = document.getElementById('registerUsername').value;
            const emailValue = document.getElementById('registerEmail').value;
            const phoneValue = document.getElementById('registerPhone').value;
            const passwordValue = document.getElementById('registerPassword').value;

            // 2. Preparamos el JSON para el Backend
            // ⚠️ OJO: Asegúrate de que los nombres de estas propiedades ('username', 'email', 'phoneNumber', 'password') 
            // coinciden exactamente con lo que espera tu DTO en Java. Si en Java se llama 'phone', cámbialo aquí.
            const payload = {
                username: usernameValue,
                email: emailValue,
                phoneNumber: phoneValue, 
                password: passwordValue
            };

            // 3. Enviamos la petición POST
            // ⚠️ OJO 2: Revisa que la ruta '/auth/register' sea la correcta. 
            // A veces en Spring Boot la gente lo llama '/users/register' o simplemente POST '/users'.
            fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            })
            .then(response => {
                if (response.ok) {
                    // Si el backend devuelve un 200 OK o 201 Created
                    Swal.fire({
                        title: '¡Registro Exitoso!',
                        text: 'Tu cuenta ha sido creada. Ya puedes iniciar sesión.',
                        icon: 'success',
                        confirmButtonColor: '#10b981', // Verde de nuestro diseño
                        timer: 3000,
                        timerProgressBar: true
                    }).then(() => {
                        // Redirigimos al login cuando cierren el aviso
                        window.location.href = 'login.html';
                    });
                } else {
                    // Si el backend devuelve un error (ej. Usuario ya existe)
                    return response.json().then(err => {
                        throw new Error(err.error || err.message || "Error al registrar la cuenta");
                    });
                }
            })
            .catch(error => {
                // Mostramos el error en pantalla
                Swal.fire({
                    title: 'Error de registro',
                    text: error.message,
                    icon: 'error',
                    confirmButtonColor: '#ef4444' // Rojo de nuestro diseño
                });
            });
        });
    }
});