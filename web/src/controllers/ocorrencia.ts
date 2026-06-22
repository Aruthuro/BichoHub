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
    const id = Number(req.params.id);
    if (!id) return res.status(400).render("error", { status: 400, message: "ID inválido." });
    const ocorrencia = await ocorrenciaService.getOcorrenciaById(req.token!, id);
    if (!ocorrencia) return res.status(404).send("Ocorrência não encontrada");
    res.render("ocorrencias/detalhes", { ocorrencia });
};

const listarMinhas = async (req: Request, res: Response) => {
    const ocorrencias = await ocorrenciaService.getOcorrenciasByColetor(req.token!);
    res.render("ocorrencias/listar", { ocorrencias });
};

const responder = async (req: Request, res: Response) => {
    const id = Number(req.params.id);
    const { resposta } = req.body;

    if (resposta === "rejeitar") {
        return res.redirect("/ocorrencias/abertas");
    }

    await ocorrenciaService.responderOcorrencia(req.token!, id, resposta);
    res.redirect(`/ocorrencias/${id}`);
};

const editar = async (req: Request, res: Response) => {
    const id = Number(req.params.id);
    const { estado, status_saude, observacoes, risco, equipamento_captura, descricao_destino, destino_gps } = req.body;

    await ocorrenciaService.atualizarOcorrencia(req.token!, id, {
        estado: estado ? Number(estado) : undefined,
        status_saude, observacoes, risco,
        equipamento_captura, descricao_destino, destino_gps
    });

    res.redirect(`/ocorrencias/${id}`);
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
    const id = Number(req.params.id);
    const { desfecho, descricao_soltura } = req.body;

    await ocorrenciaService.finalizarOcorrencia(req.token!, id, desfecho, descricao_soltura);
    res.redirect("/ocorrencias/listar");
};

export default { listarAbertas, detalhes, listarMinhas, mapa, responder, editar, encerrar }