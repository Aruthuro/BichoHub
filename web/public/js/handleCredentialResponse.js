function handleCredentialResponse(response) {
    fetch('/auth/google', {
        method: 'POST',
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token: response.credential })
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            window.location.href = "/ocorrencias/abertas";
        } else if (data.redirect) {
            window.location.href = data.redirect;
        } else {
            console.log(data.error || "Falha na autenticação");
        }
    })
    .catch(err => console.error(err));
}