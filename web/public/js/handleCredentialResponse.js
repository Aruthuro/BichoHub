function handleCredentialResponse(response) {
    fetch('/googleauth', {
        method: 'POST',
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token: response.credential })
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            window.location.href = "/ocorrencias/abertas";
        } else {
            console.log("Falha na autenticação");
        }
    })
    .catch(err => console.error(err));
}