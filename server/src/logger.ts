import fs from "fs";
import path from "path";
import type { Request, Response, NextFunction } from "express";
import { env } from "./env.js";

// Garante que o diretório de logs existe
if (!fs.existsSync(env.DIR_LOG)) {
  fs.mkdirSync(env.DIR_LOG, { recursive: true });
}

// Caminho do arquivo de log de acesso
const logPath = path.join(env.DIR_LOG, "geral.log");

// Caminho do arquivo de log de erro (separado)
const errorLogPath = path.join(env.DIR_LOG, "erro.log");

/**
 * Middleware para registrar requisições HTTP.
 */
export function logger(req: Request, res: Response, next: NextFunction) {
  const data = new Date().toISOString();
  let mensagem: string;

  if (env.FORMAT_LOG === "simple") {
    mensagem = `[${data}] ${req.method} ${req.url}\n`;
  } else {
    mensagem = `[${data}] ${req.method} ${req.url} HTTP/${req.httpVersion} ${req.headers["user-agent"]}\n`;
  }

  fs.appendFileSync(logPath, mensagem);
  next();
}

/**
 * Registra um erro no arquivo de log e opcionalmente no console.
 * Pode ser chamada pelo middleware de erro global.
 */
export function logError(erro: Error, req?: Request) {
  const data = new Date().toISOString();
  let mensagem = `[${data}] ${erro.message}\n${erro.stack}\n`;

  if (req) {
    mensagem = `[${data}] ${req.method} ${req.url} - ${erro.message}\n${erro.stack}\n`;
  }

  fs.appendFileSync(errorLogPath, mensagem);
  console.error(mensagem); // também exibe no console (útil em desenvolvimento)
}