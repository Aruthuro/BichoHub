import { type Request, type Response } from "express";
import * as authService from "../services/auth.js";

const login = async (req: Request, res: Response) => {
    if (req.method == "GET"){
        const redirect = typeof req.query.redirect === 'string' ? req.query.redirect : '/ocorrencias/abertas';
        res.render('auth/login', { redirect, error: null });
    } else if (req.method == "POST"){
        try {
            const { email, password, redirect } = req.body;
            if (!email || !password) {
                return res.render('auth/login', {
                    redirect: redirect || '/ocorrencias/abertas',
                    error: 'Email e senha são obrigatórios'
                });
            }
            const token = await authService.login(email, password);
            res.cookie('token', token, {
                httpOnly: true,
                sameSite: 'lax',
                maxAge: 3600
            });
            res.redirect(typeof redirect === 'string' ? redirect : '/ocorrencias/abertas');
        } catch (erro: any) {
            const message = erro?.response?.data?.erro || 'Erro ao fazer login. Verifique email e senha.';
            res.render('auth/login', {
                redirect: req.body.redirect || '/ocorrencias/abertas',
                error: message
            });
        }
    }
};

const signin = async (req: Request, res: Response) => {
    if (req.method == "GET"){
        res.render('auth/signin', { error: null });
    } else if (req.method == "POST"){
        try {
            const { nome, email, senha, contato } = req.body;
            if (!nome || !email || !senha) {
                return res.render('auth/signin', { error: 'Nome, email e senha são obrigatórios' });
            }
            await authService.signin(nome, email, senha, contato || undefined);
            res.redirect('/login');
        } catch (erro: any) {
            const message = erro?.response?.data?.erro || 'Erro ao cadastrar. Tente novamente.';
            res.render('auth/signin', { error: message });
        }
    }
};

const logout = (_req: Request, res: Response) => {
    res.clearCookie('token');
    res.redirect('/login');
};

export default { login, signin, logout };
