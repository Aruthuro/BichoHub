import { type Request, type Response, type NextFunction } from "express";

export function requireToken(req: Request, res: Response, next: NextFunction) {
    const token = req.cookies?.token;
    if (!token) {
        return res.redirect("/login");
    }
    req.token = token;
    next();
}
