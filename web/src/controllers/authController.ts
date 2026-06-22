import { type Request, type Response } from "express";
import * as authService from "../services/auth.js";
import * as adminService from "../services/admin.js";
import dotenv from "dotenv";

dotenv.config();

async function checkAdmin(token: string): Promise<boolean> {
    try {
        await adminService.getDashboard(token);
        return true;
    } catch {
        return false;
    }
}

const googleLogin = async (req: Request, res: Response) => {
    try {
        const { token: credential } = req.body;
        if (!credential) {
            return res.status(400).json({ success: false, error: "Credencial não fornecida" });
        }
        const token = await authService.logingoogle(credential);
        req.session.token = token;
        req.session.logado = true;
        req.session.eh_admin = await checkAdmin(token);
        res.json({ success: true });
    } catch (erro: any) {
        let message = ""
        if (erro?.response?.status === 401){
            message = "Aguarde a aprovação de um administrador. Obrigado!";
        } else{
            message = erro?.response?.data?.erro || "Erro ao fazer login. Verifique email e senha.";
        }
        res.render("login", { error: message });
    }
};

const login = async (req: Request, res: Response) => {
    if (req.method == "GET"){
        const redirect = typeof req.query.redirect === "string" ? req.query.redirect : "/ocorrencias/abertas";
        res.render("login", { client_id: process.env.CLIENT_ID, redirect, error: null });
    } else if (req.method == "POST"){
        try {
            const { email, password } = req.body;
            if (!email || !password) {
                return res.render("login", {
                    client_id: process.env.CLIENT_ID,
                    redirect: "/ocorrencias/abertas",
                    error: "Email e senha são obrigatórios"
                });
            }
            const token = await authService.login(email, password);
            req.session.token = token;
            req.session.logado = true;
            req.session.eh_admin = await checkAdmin(token);
            res.redirect("/ocorrencias/abertas");
        } catch (erro: any) {
            let message = ""
            if (erro?.response?.status === 401){
                message = "Aguarde a aprovação de um administrador. Obrigado!";
            } else{
                message = erro?.response?.data?.erro || "Erro ao fazer login. Verifique email e senha.";
            }
            res.render("login", {
                client_id: process.env.CLIENT_ID,
                redirect: "/ocorrencias/abertas",
                error: message
            });
        }
    }
};

const signup = async (req: Request, res: Response) => {
    if (req.method == "GET"){
        res.render("signup", { error: null });
    } else if (req.method == "POST"){
        try {
            const { nome, email, senha, contato } = req.body;
            if (!nome || !email || !senha || !contato) {
                return res.render("signup", { error: "Algum campo faltanto." });
            }
            await authService.signup(nome, email, senha, contato);
            res.redirect("/login");
        } catch (erro: any) {
            const message = erro?.response?.data?.erro || "Erro ao cadastrar. Tente novamente.";
            res.render("signup", { error: message });
        }
    }
};

const logout = (req: Request, res: Response) => {
    req.session.destroy(() => {
        res.redirect("/login");
    });
};

export default { login, signup, googleLogin, logout };
