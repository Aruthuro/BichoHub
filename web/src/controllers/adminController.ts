import { type Request, type Response } from "express";
import * as adminService from "../services/admin.js";

const dashboard = async (req: Request, res: Response) => {
    try {
        const stats = await adminService.getDashboard(req.token!);
        res.render("admin/dashboard", { stats });
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || erro?.message || "Erro ao carregar dashboard";
        res.render("admin/dashboard", { stats: null, erro: msg });
    }
};

const listarUsuarios = async (req: Request, res: Response) => {
    try {
        const usuarios = await adminService.getUsuarios(req.token!);
        const erro = req.query.erro as string | undefined;
        res.render("admin/usuarios", { usuarios, erro });
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || erro?.message || "Erro ao listar usuários";
        res.render("admin/usuarios", { usuarios: [], erro: msg });
    }
};

const listarOcorrencias = async (req: Request, res: Response) => {
    try {
        const filtro = req.query.filtro as string | undefined;
        const ocorrencias = await adminService.getOcorrencias(req.token!, filtro);
        res.render("admin/ocorrencias", { ocorrencias, filtro });
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || erro?.message || "Erro ao listar ocorrências";
        res.render("admin/ocorrencias", { ocorrencias: [], erro: msg });
    }
};

const detalhes = async (req: Request, res: Response) => {
    try {
        const id = Number(req.params.id);
        const ocorrencia = await adminService.getOcorrenciaById(req.token!, id);
        if (!ocorrencia) return res.status(404).send("Ocorrência não encontrada");
        res.render("ocorrencias/detalhes", { ocorrencia });
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || erro?.message || "Erro ao carregar ocorrência";
        res.status(500).send(msg);
    }
};

const promoverAdmin = async (req: Request, res: Response) => {
    try {
        const id = Number(req.params.id);
        await adminService.tornarAdmin(req.token!, id);
        res.redirect("/admin/usuarios");
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || "Erro ao promover";
        res.render("admin/usuarios", { erro: msg });
    }
};

const promoverColetor = async (req: Request, res: Response) => {
    try {
        const id = Number(req.params.id);
        const { contato } = req.body;
        await adminService.tornarColetor(req.token!, id, contato);
        res.redirect("/admin/usuarios");
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || "Erro ao promover";
        res.render("admin/usuarios", { erro: msg });
    }
};

const remover = async (req: Request, res: Response) => {
    try {
        const id = Number(req.params.id);
        await adminService.removerUsuario(req.token!, id);
        res.redirect("/admin/usuarios");
    } catch (erro: any) {
        const msg = erro?.response?.data?.erro || "Erro ao remover";
        res.render("admin/usuarios", { erro: msg });
    }
};

export default { dashboard, listarUsuarios, listarOcorrencias, detalhes, promoverAdmin, promoverColetor, remover };