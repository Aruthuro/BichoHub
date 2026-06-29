const nome = document.getElementById('nome');
const email = document.getElementById('email');
const senha = document.getElementById('senha');
const contato = document.getElementById('contato');
const botao = document.getElementById('botao');

function validaCampos() {
	const emailOK = email.validity.valid;
	const senhaOK = senha.validity.valid;
	const contatoOK = contato.validity.valid;

	if (nome.value.trim().length > 0 && emailOK && senhaOK && contatoOK) {
		botao.disabled = false;
	} else {
		botao.disabled = true;
	}
}

nome.addEventListener('input', validaCampos);
email.addEventListener('input', validaCampos);
senha.addEventListener('input', validaCampos);
contato.addEventListener('input', validaCampos);
