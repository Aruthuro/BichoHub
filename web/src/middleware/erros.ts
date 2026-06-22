import { type Request, type Response, type NextFunction } from "express";

export function erroFaltando(_req: Request, res: Response) {
    res.status(404).render("error", { status: 404, message: "Página não encontrada." });
}

export function erroServidor(err: any, _req: Request, res: Response, _next: NextFunction) {
    console.error(err);
    res.status(500).render("error", { status: 500, message: "Erro interno do servidor." });
}