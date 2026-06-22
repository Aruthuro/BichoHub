import { api } from "../constants.js"

export async function logingoogle(credential: string): Promise<string> {
    const resp = await api.post('/usuarios/google', { token: credential });
    return resp.data.token as string;
}

export async function login(email: string, senha: string): Promise<string> {
    const resp = await api.post('/usuarios/login', { email, senha });
    return resp.data.token as string;
}

export async function signup(nome: string, email: string, senha: string, contato: string): Promise<void> {
    await api.post('/usuarios/cadastrar', { nome, email, senha, contato });
}
