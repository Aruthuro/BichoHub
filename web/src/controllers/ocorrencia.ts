import { type Request, type Response } from "express";
import * as ocorrenciaService from "../services/ocorrencia.js";

const listarAbertas = async (req: Request, res: Response) => {
    try {
        const ocorrencias = await ocorrenciaService.getOcorrenciasAbertas(req.token!);
        res.render("ocorrencias/abertas", { ocorrencias });
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || erro?.message || "Erro desconhecido";
        res.render("ocorrencias/abertas", { ocorrencias: [], erro: msg });
    }
};

const detalhes = async (req: Request, res: Response) => {
    try {
        const id = Number(req.params.id);
        if (!id) return res.status(400).render("error", { status: 400, message: "ID inválido." });
        const ocorrencia = await ocorrenciaService.getOcorrenciaById(req.token!, id);
        if (!ocorrencia) return res.status(404).send("Ocorrência não encontrada");
        res.render("ocorrencias/detalhes", { ocorrencia });
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || erro?.message || "Erro ao carregar ocorrência";
        res.status(500).render("error", { status: 500, message: msg });
    }
};

const listarMinhas = async (req: Request, res: Response) => {
    try {
        const ocorrencias = await ocorrenciaService.getOcorrenciasByColetor(req.token!);
        res.render("ocorrencias/historico", { ocorrencias });
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || erro?.message || "Erro ao listar ocorrências";
        res.render("ocorrencias/historico", { ocorrencias: [], erro: msg });
    }
};

const responder = async (req: Request, res: Response) => {
    try {
        const id = Number(req.params.id);
        const { resposta } = req.body;

        if (resposta === "rejeitar") {
            return res.redirect("/ocorrencias/abertas");
        }

        await ocorrenciaService.responderOcorrencia(req.token!, id, resposta);
        res.redirect(`/ocorrencias/${id}`);
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || erro?.message || "Erro ao responder chamada";
        res.redirect("/ocorrencias/abertas?erro=" + encodeURIComponent(msg));
    }
};

const editar = async (req: Request, res: Response) => {
    const id = Number(req.params.id);
    try {
        const { estado, status_saude, observacoes, risco, equipamento_captura, descricao_destino, destino_gps } = req.body;

        await ocorrenciaService.atualizarOcorrencia(req.token!, id, {
            estado: estado ? Number(estado) : undefined,
            status_saude, observacoes, risco,
            equipamento_captura, descricao_destino, destino_gps
        });

        res.redirect(`/ocorrencias/${id}`);
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || erro?.message || "Erro ao editar";
        res.redirect(`/ocorrencias/${id}?erro=` + encodeURIComponent(msg));
    }
};

const mapa = async (req: Request, res: Response) => {
    try {
        const ocorrencias = await ocorrenciaService.getOcorrencias(req.token!);
        res.render("ocorrencias/mapa", { ocorrencias });
    } catch (erro: any) {
        res.status(500).render("error", { status: 500, message: "Erro ao carregar mapa" });
    }
};

const encerrar = async (req: Request, res: Response) => {
    try {
        const id = Number(req.params.id);
        const { desfecho, descricao_soltura } = req.body;

        await ocorrenciaService.finalizarOcorrencia(req.token!, id, desfecho, descricao_soltura);
        res.redirect("/ocorrencias/historico");
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || erro?.message || "Erro ao encerrar";
        res.redirect("/ocorrencias/historico?erro=" + encodeURIComponent(msg));
    }
};

export default { listarAbertas, detalhes, listarMinhas, mapa, responder, editar, encerrar }
