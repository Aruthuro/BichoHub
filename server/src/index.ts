import express from "express";
import fs from "fs";
import path from "path";
import { env } from "./env.js";
import { client } from "./bancoDeDados.js";
import { logger, logError } from "./logger.js";
import router from "./router/routes.js";
import modeloRouter from "./router/modeloRoutes.js";
import whatsappRouter from "./router/whatsappRoutes.js";
import { iniciarWhatsAppBot } from "./services/whatsappBot.js";
import { type Request, type Response, type NextFunction } from "express";

const app = express();
const publicPath = `${process.cwd()}/public`;


/*
  middleware para converter JSON do body
  em objeto JavaScript acessível por req.body
*/ 
app.use(express.json({ limit: "10mb" }));

// para logs
app.use(logger);

app.use("/css", express.static(`${publicPath}/css`));
app.use("/js", express.static(`${publicPath}/js`));
app.use("/img", express.static(`${publicPath}/img`));

app.use("/api", router);
app.use("/api/modelo", modeloRouter);
app.use("/api/whatsapp", whatsappRouter);

/*
  como o router deixa tem as rotas, caso não vá para nenhuma delas não vai para o 404
*/
app.use((req, res) => {
  res.status(404).json({ erro: "Rota não encontrada" });
});

app.use((erro: any, req: Request, res: Response, next: NextFunction) => {
  logError(erro, req);
  const status = erro.status || 500;
  res.status(status).json({ erro: erro.message || "Erro interno" });
});



/*
  funcao para esperar um tempo (ms)
*/
function sleep(ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

/*
  funcao assincrona para iniciar o servidor
  Pool (pg) conecta sob demanda na primeira query
*/
async function iniciarServidor() {
  // tenta schema.sql com retry (banco pode estar iniciando)
  const schemaPath = path.join(process.cwd(), "src", "database", "schema.sql");
  const schema = fs.readFileSync(schemaPath, "utf8");

  for (let i = 1; i <= 10; i++) {
    try {
      await client.query(schema);
      console.log("Schema atualizado");
      break;
    } catch (erro) {
      if (i === 10) {
        console.log("Aviso: nao foi possivel executar schema.sql");
      } else {
        console.log(`Aguardando banco (${i}/10)...`);
        await sleep(3000);
      }
    }
  }

  app.listen(env.PORT, () => {
    console.log(`Servidor rodando na porta ${env.PORT}`);
  });

  if (env.NODE_ENV !== "test") {
    try {
      if (!process.env.RENDER) {
        iniciarWhatsAppBot();
      } else {
        console.log("WhatsApp Bot desabilitado — sem Chromium no Render");
      }
    } catch (e) {
      console.log("WhatsApp bot nao disponivel neste ambiente");
    }
  }
}

iniciarServidor();