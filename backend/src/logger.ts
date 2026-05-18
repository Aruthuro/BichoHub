import fs from "fs";
import path from "path";

import type {
  Request,
  Response,
  NextFunction
} from "express";

import { env } from "./env.js";

/*
  cria o diretório de logs caso ele não exista
*/
if (!fs.existsSync(env.LOG_DIR)) {

  fs.mkdirSync(env.LOG_DIR, {
    recursive: true
  });
}

/*
  caminho do arquivo de log
*/
const logPath =
  path.join(env.LOG_DIR, "geral.log");

/*
  middleware responsável por registrar
  os acessos da aplicação
*/
export function logger(
  req: Request,
  res: Response,
  next: NextFunction
) {

  /*
    data/hora atual no formato ISO
  */
  const data =
    new Date().toISOString();

  let mensagem = "";

  /*
    log simples:
    data, método HTTP e URL
  */
  if (env.LOG_FORMAT === "simple") {

    mensagem =
      `[${data}] ` +
      `${req.method} ${req.url}\n`;

  } else {

    /*
      log completo:
      data, método, URL,
      versão HTTP e user-agent
    */
    mensagem =
      `[${data}] ` +
      `${req.method} ${req.url} ` +
      `HTTP/${req.httpVersion} ` +
      `${req.headers["user-agent"]}\n`;
  }

  /*
    adiciona mensagem ao final
    do arquivo de log
  */
  fs.appendFileSync(
    logPath,
    mensagem
  );

  /*
    continua para o próximo
    middleware/rota
  */
  next();
}