import { type Request, type Response, type NextFunction } from "express";
import { api } from "../constants.js";

export function requireToken(req: Request, res: Response, next: NextFunction) {
    if (!req.session.logado || !req.session.token) {
        return res.redirect("/login");
    }
    req.token = req.session.token;
    next();
}

export async function requireColetorOuAdmin(req: Request, res: Response, next: NextFunction) {
    try {
        await api.get("/coletores/ocorrencias/abertas", {
            headers: { Authorization: `Bearer ${req.session.token}` }
        });
        next();
    } catch (erro: any) {
        if (erro?.response?.status === 401) {
            return res.redirect("/login");
        }
        res.status(500).render("error", { status: 500, message: "Erro ao verificar permissões." });
    }
}

export async function requireAdmin(req: Request, res: Response, next: NextFunction) {
    try {
        await api.get("/admin/dashboard", {
            headers: { Authorization: `Bearer ${req.session.token}` }
        });
        next();
    } catch (erro: any) {
        if (erro?.response?.status === 401) {
            return res.redirect("/ocorrencias/abertas");
        }
        res.status(500).render("error", { status: 500, message: "Erro ao verificar permissões." });
    }
}