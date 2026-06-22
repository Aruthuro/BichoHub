export interface Ocorrencia {
    id: number;
    coletor_id?: number;
    origem_solicitacao_id: number;
    origem_gps: string;
    destino_gps?: string;
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
