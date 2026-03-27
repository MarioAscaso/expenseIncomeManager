const API_BASE_URL = 'http://localhost:9393/api';

document.addEventListener('DOMContentLoaded', function() {
    
    const registerForm = document.getElementById('registerForm');

    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault(); 

            const usernameValue = document.getElementById('registerUsername').value;
            const emailValue = document.getElementById('registerEmail').value;
            const phoneValue = document.getElementById('registerPhone').value;
            const passwordValue = document.getElementById('registerPassword').value;

            const payload = {
                username: usernameValue,
                email: emailValue,
                phoneNumber: phoneValue, 
                password: passwordValue
            };

            fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            })
            .then(response => {
                if (response.ok) {
                    Swal.fire({
                        title: '¡Registro Exitoso!',
                        text: 'Tu cuenta ha sido creada. Ya puedes iniciar sesión.',
                        icon: 'success',
                        confirmButtonColor: '#10b981', 
                        timer: 3000,
                        timerProgressBar: true
                    }).then(() => {
                        window.location.href = 'login.html';
                    });
                } else {
                    return response.json().then(err => {
                        throw new Error(err.error || err.message || "Error al registrar la cuenta");
                    });
                }
            })
            .catch(error => {
                Swal.fire({
                    title: 'Error de registro',
                    text: error.message,
                    icon: 'error',
                    confirmButtonColor: '#ef4444' 
                });
            });
        });
    }
});