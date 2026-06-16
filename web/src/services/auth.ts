import { api } from "../constants.js"

export async function login(email: string, password: string): Promise<string> {
    const resp = await api.post('/v1/usuarios/login', { email, password });
    return resp.data.token as string;
}

export async function signin(nome: string, email: string, senha: string, contato?: string): Promise<void> {
    await api.post('/v1/usuarios/cadastrar', { nome, email, senha, contato });
}
