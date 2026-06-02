// logger.ts (versão final sugerida)
import fs from "fs";
import path from "path";
import type { Request, Response, NextFunction } from "express";
import { env } from "./env.js";

if (!fs.existsSync(env.DIR_LOG)) {
  fs.mkdirSync(env.DIR_LOG, { recursive: true });
}

const logPath = path.join(env.DIR_LOG, "geral.log");
const errorLogPath = path.join(env.DIR_LOG, "erro.log");

export function logger(req: Request, res: Response, next: NextFunction) {
  const inicio = Date.now();

  res.on("finish", () => {
    const data = new Date().toISOString();
    const duracao = Date.now() - inicio;
    const status = res.statusCode;

    let mensagem:string;
    
   if (env.FORMAT_LOG === "simple") {
      mensagem = `[${data}] ${req.method} ${req.originalUrl} ${status} - ${duracao}ms\n`;
    } else {
      mensagem = `[${data}] ${req.method} ${req.originalUrl} HTTP/${req.httpVersion} ${status} - ${duracao}ms ${req.headers["user-agent"] || ""}\n`;
    }
    fs.appendFileSync(logPath, mensagem);
  });

  next();
}

export function logError(erro: Error, req?: Request) {
  const data = new Date().toISOString();
  let mensagem = `[${data}] ${erro.message}\n${erro.stack}\n`;

  if (req) {
    mensagem = `[${data}] ${req.method} ${req.originalUrl} - ${erro.message}\n${erro.stack}\n`;
  }

  fs.appendFileSync(errorLogPath, mensagem);
  console.error(mensagem);
}