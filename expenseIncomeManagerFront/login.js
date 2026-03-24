document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('errorMessage');

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
            const currentUser = {
                id: userData.id,
                username: userData.username,
                role: userData.role.toLowerCase()
            };
            
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            window.location.href = 'index.html';
        })
        .catch(error => {
            errorMessage.innerText = error.message;
            errorMessage.classList.remove('d-none');
        });
    });
});