import { type Request, type Response, type NextFunction } from "express";

export function requireToken(req: Request, res: Response, next: NextFunction) {
    if (!req.session.logado || !req.session.token) {
        return res.redirect("/auth/login");
    }
    req.token = req.session.token;
    next();
}

export function requireColetorOuAdmin(req: Request, res: Response, next: NextFunction) {
    if (req.session.eh_coletor || req.session.eh_admin) {
        return next();
    }
    res.status(401).redirect("/auth/login");
}

export function requireAdmin(req: Request, res: Response, next: NextFunction) {
    if (req.session.eh_admin) {
        return next();
    }
    res.status(401).redirect("/auth/login");
}