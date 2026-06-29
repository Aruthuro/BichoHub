import { type Request, type Response } from "express";
import * as authService from "../services/auth.js";
import dotenv from "dotenv";

dotenv.config();

const login = async (req: Request, res: Response) => {
    if (req.method == "GET"){
        const redirect = typeof req.query.redirect === "string" ? req.query.redirect : "/ocorrencias/abertas";
        res.render("auth/login", { client_id: process.env.CLIENT_ID, redirect, error: null });
    } else if (req.method == "POST"){
        try {
            const { email, password } = req.body;
            if (!email || !password) {
                return res.render("auth/login", {
                    client_id: process.env.CLIENT_ID,
                    redirect: "/ocorrencias/abertas",
                    error: "Email e senha são obrigatórios"
                });
            }
            const token = await authService.login(email, password);
            const { eh_admin, eh_coletor } = await authService.checkRole(token);
            if (!eh_admin && !eh_coletor) {
                return res.status(401).render("auth/login", {
                    client_id: process.env.CLIENT_ID,
                    redirect: "/ocorrencias/abertas",
                    error: "Aguarde a aprovação de um administrador. Obrigado!"
                });
            }
            req.session.token = token;
            req.session.logado = true;
            req.session.eh_admin = eh_admin;
            req.session.eh_coletor = eh_coletor;
            res.redirect("/ocorrencias/abertas");
        } catch (erro: any) {
            const status = erro?.response?.status;
            let message = "Tente novamente.";
            if (status === 400 || status === 403) {
                message = erro?.response?.data?.erro;
            }
            res.status(status || 500).render("auth/login", {
                client_id: process.env.CLIENT_ID,
                redirect: "/ocorrencias/abertas",
                error: message
            });
        }
    }
};

const signup = async (req: Request, res: Response) => {
    if (req.method == "GET"){
        res.render("auth/signup", { error: null });
    } else if (req.method == "POST"){
        try {
            const { nome, email, senha, contato } = req.body;
            if (!nome || !email || !senha || !contato) {
                return res.render("auth/signup", { error: "Algum campo faltanto." });
            }
            await authService.signup(nome, email, senha, contato);
            res.redirect("/auth/login");
        } catch(erro){
			res.status(500).render("auth/signup", { error: "Tente novamente." });
        }
    }
};

const googleLogin = async (req: Request, res: Response) => {
    try {
        const { token: credential } = req.body;
        if (!credential) {
            res.status(400).json({ erro: "Credencial não fornecida" });
        } else{
            const resp = await authService.logingoogle(credential);
            if (resp.token){
                const { eh_admin, eh_coletor } = await authService.checkRole(resp.token);
                if (!eh_admin && !eh_coletor) {
                    return res.status(401).json({
                        success: false,
                        erro: "Aguarde a aprovação de um administrador. Obrigado!"
                    });
                }
                req.session.token = resp.token;
                req.session.logado = true;
                req.session.eh_admin = eh_admin;
                req.session.eh_coletor = eh_coletor;
                return res.json({ success: true });
            } else {
                req.session.pending = { nome: resp.nome, email: resp.email, senha: resp.senha };
                return res.json({ redirect: "/auth/google/quasela" });
            }
        }
    } catch (erro) {
        console.error(erro);
        return res.status(500).json({
            success: false,
            error: "Tente novamente."
        })
    }
};

const askContato = async (req: Request, res: Response) => {
    if (req.method == "GET"){
        res.render("auth/contato", { error: null });
    } else if (req.method == "POST"){
        try {
            const { contato } = req.body;
            if (!contato) {
                return res.render("auth/contato", { error: "Contato não informado." });
            }
            const { nome, email, senha } = req.session.pending;
            await authService.signup(nome, email, senha, contato);
            req.session.pending = null;
            res.redirect("/ocorrencias/abertas");
        } catch(erro){
			res.status(500).render("auth/signup", { error: "Tente novamente." });
        }
    }
}

const logout = (req: Request, res: Response) => {
    req.session.destroy(() => {
        res.redirect("/auth/login");
    });
};

export default { login, signup, googleLogin, askContato, logout };
