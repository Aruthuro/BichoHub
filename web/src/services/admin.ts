import { api } from "../constants.js"
import { type Ocorrencia } from '../views/types/ocorrencia.js';

const headers = (token: string) => ({
    Authorization: `Bearer ${token}`
});

export async function getDashboard(token: string): Promise<any> {
    const resp = await api.get('/v1/admin/dashboard', { headers: headers(token) });
    return resp.data;
}

export async function getUsuarios(token: string): Promise<any[]> {
    const resp = await api.get('/v1/admin/usuarios', { headers: headers(token) });
    return resp.data;
}

export async function getOcorrencias(token: string, filtro?: string): Promise<Ocorrencia[]> {
    const resp = await api.get('/v1/admin/ocorrencias', { headers: headers(token), params: { filtro } });
    return resp.data;
}

export async function getOcorrenciaById(token: string, id: number): Promise<Ocorrencia> {
    const resp = await api.get(`/v1/admin/ocorrencias/${id}`, { headers: headers(token) });
    return resp.data;
}

export async function tornarAdmin(token: string, id: number): Promise<any> {
    const resp = await api.post(`/v1/admin/usuarios/${id}/tornar-admin`, { headers: headers(token) });
    return resp.data;
}

export async function tornarColetor(token: string, id: number, contato: string): Promise<any> {
    const resp = await api.post(`/v1/admin/usuarios/${id}/tornar-coletor`, { contato }, { headers: headers(token) });
    return resp.data;
}

export async function removerUsuario(token: string, id: number): Promise<any> {
    const resp = await api.delete(`/v1/admin/usuarios/${id}`, { headers: headers(token) });
    return resp.data;
}
