import dotenv from 'dotenv';
import { api } from "../constants.js"
import { type Ocorrencia } from '../views/types/ocorrencia.js';

dotenv.config();

const headers = (token: string) => ({
    Authorization: `Bearer ${token}`
});

export async function getOcorrenciasAbertas(token: string): Promise<Ocorrencia[]> {
    const resp = await api.get('/v1/coletores/ocorrencias/abertas', { headers: headers(token) });
    return resp.data;
}

export async function getOcorrenciaById(token: string, id: number): Promise<Ocorrencia> {
    const resp = await api.get(`/v1/coletores/ocorrencias/${id}`, { headers: headers(token) });
    return resp.data;
}

export async function getOcorrenciasByColetor(token: string): Promise<Ocorrencia[]> {
    const resp = await api.get('/v1/coletores/ocorrencias/historico', { headers: headers(token) });
    return resp.data;
}

export async function getOcorrencias(token: string): Promise<Ocorrencia[]> {
    const resp = await api.get('/v1/coletores/ocorrencias/historico', { headers: headers(token) });
    return resp.data;
}

export async function responderOcorrencia(token: string, id: number, resposta: string) {
    const resp = await api.patch(`/v1/coletores/responder/${id}`, { resposta }, { headers: headers(token) });
    return resp.data;
}

export async function atualizarOcorrencia(token: string, id: number, dados: any) {
    const resp = await api.patch(`/v1/coletores/ocorrencias/editar/${id}`, dados, { headers: headers(token) });
    return resp.data;
}

export async function finalizarOcorrencia(token: string, id: number, desfecho: string, descricao_soltura?: string) {
    const resp = await api.patch(`/v1/coletores/ocorrencias/encerrar/${id}`, { desfecho, descricao_soltura }, { headers: headers(token) });
    return resp.data;
}
