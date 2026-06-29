import { api } from "../constants.js"

const headers = (token: string) => ({ Authorization: `Bearer ${token}` });

export async function checkRole(token: string): Promise<{ eh_admin: boolean; eh_coletor: boolean }> {
    const [admin, coletor] = await Promise.allSettled([
        api.get('/v1/admin/dashboard', { headers: headers(token) }),
        api.get('/v1/coletores/ocorrencias/abertas', { headers: headers(token) })
    ]);
    return {
        eh_admin: admin.status === "fulfilled",
        eh_coletor: coletor.status === "fulfilled"
    };
}

export async function logingoogle(credential: string): Promise<any> {
    const resp = await api.post('/v1/usuarios/google', { token: credential });
    return resp.data;
}

export async function login(email: string, senha: string): Promise<string> {
    const resp = await api.post('/v1/usuarios/login', { email, senha });
    return resp.data.token as string;
}

export async function signup(nome: string, email: string, senha: string, contato: string): Promise<void> {
    await api.post('/v1/usuarios/cadastrar', { nome, email, senha, contato });
}
