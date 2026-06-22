import { api } from "../constants.js"

const headers = (token: string) => ({
    Authorization: `Bearer ${token}`
});

export async function getDashboard(token: string): Promise<any> {
    const resp = await api.get('/admin/dashboard', { headers: headers(token) });
    return resp.data;
}

export async function getUsuarios(token: string): Promise<any[]> {
    const resp = await api.get('/admin/usuarios', { headers: headers(token) });
    return resp.data;
}

export async function tornarAdmin(token: string, id: number): Promise<any> {
    const resp = await api.post(`/admin/usuarios/${id}/tornar-admin`, { headers: headers(token) });
    return resp.data;
}

export async function tornarColetor(token: string, id: number, contato: string): Promise<any> {
    const resp = await api.post(`/admin/usuarios/${id}/tornar-coletor`, { contato }, { headers: headers(token) });
    return resp.data;
}

export async function removerUsuario(token: string, id: number): Promise<any> {
    const resp = await api.delete(`/admin/usuarios/${id}`, { headers: headers(token) });
    return resp.data;
}
