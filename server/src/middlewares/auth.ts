/*
    esse arquivo por enquanto eh basicamente place holder, tem que fazer a verificaca certa para autenticar o usuario
    tem essa biblioteca do jsonwebtoken (sugestao do deepseek)
*/ 


import { type Request, type Response, type NextFunction } from "express";
import { env } from "../env.js"
import jwt from "jsonwebtoken";

interface CustomRequest extends Request {
  usuario?: any;
}

export function verificarToken(req: CustomRequest, res: Response, next: NextFunction) {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return res.status(401).json({ erro: "Token não fornecido" });
  }

  const token = authHeader.split(" ")[1];
  if (!token) {
    return res.status(401).json({ erro: "Token mal formatado" });
  }

  try {
    const secret = process.env.JWT_SECRET; // env ainda nao criado a configurado
    if (!secret) {
      console.error("JWT_SECRET não definido");
      return res.status(500).json({ erro: "Configuração do servidor" });
    }

    const payload = jwt.verify(token, secret);
    req.usuario = payload;  
    next();
  } catch (erro) {
    return res.status(401).json({ erro: "Token inválido ou expirado" });
  }
}