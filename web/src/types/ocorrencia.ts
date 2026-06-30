import type { GeoJSON } from "geojson";

export interface OcorrenciaMapa {
    id: number;
    data_captura: string;
    risco?: string;
    status_saude?: string;
    classificacao?: string;
    origem_gps: GeoJSON;
}

export interface Ocorrencia {
    id: number;
    coletor_id?: number;
    origem_solicitacao_id: number;
    origem_gps: JSON;
    destino_gps?: JSON;
    data_captura: string;
    descricao_origem?: string;
    descricao_destino?: string;
    observacoes?: string;
    risco?: string;
    equipamento_captura?: string;
    referencia_imagem?: string;
    classificacao?: string;
    confianca_classificacao?: number;
    estado: number;
    tipo: number;
    status_saude?: string;
    desfecho?: string;
    descricao_soltura?: string;
    ultimo_caso: boolean;
    solicitante_nome?: string;
    solicitante_contato?: string;
    coletor_nome?: string;
}
