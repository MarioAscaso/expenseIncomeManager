document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('errorMessage');

    // Si el usuario ya había iniciado sesión antes, lo mandamos directo al index
    if (localStorage.getItem('currentUser')) {
        window.location.href = 'index.html';
    }

    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const userValue = document.getElementById('username').value;
        const passValue = document.getElementById('password').value;

        fetch('http://localhost:9393/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: userValue, password: passValue })
        })
        .then(response => {
            if (response.ok) return response.json();
            throw new Error("Credenciales incorrectas");
        })
        .then(userData => {
            // Creamos un objeto con los datos del usuario
            const currentUser = {
                id: userData.id,
                username: userData.username,
                role: userData.role.toLowerCase()
            };
            
            // LO GUARDAMOS EN LA MEMORIA DEL NAVEGADOR (localStorage)
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            // Redirigimos a la página principal
            window.location.href = 'index.html';
        })
        .catch(error => {
            errorMessage.innerText = error.message;
            errorMessage.classList.remove('d-none');
        });
    });
});