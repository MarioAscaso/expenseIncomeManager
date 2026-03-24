document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    const alertMessage = document.getElementById('alertMessage');

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
        const emailValue = document.getElementById('registerEmail').value;
        const phoneValue = document.getElementById('registerPhone').value;

        fetch('http://localhost:9393/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                username: userValue, 
                password: passValue,
                email: emailValue,
                phoneNumber: phoneValue
            })
        })
        .then(async response => {
            if (response.ok) {
                sessionStorage.setItem('registerSuccess', '¡Cuenta creada con éxito! Ya puedes iniciar sesión.');
                window.location.href = 'login.html';
            } else {
                const errorText = await response.text();
                throw new Error(errorText);
            }
        })
        .catch(error => showAlert(error.message, 'danger'));
    });
}); 