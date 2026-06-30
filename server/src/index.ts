import express from "express";
import fs from "fs";
import path from "path";
import { env } from "./env.js";
import { client } from "./bancoDeDados.js";
import { logger, logError } from "./logger.js";
import router from "./router/routes.js";
import modeloRouter from "./router/modeloRoutes.js";
import { type Request, type Response, type NextFunction } from "express";

const app = express();
const publicPath = `${process.cwd()}/public`;

app.use(express.json({ limit: "100mb" }));

app.use(logger);

app.use("/css", express.static(`${publicPath}/css`));
app.use("/js", express.static(`${publicPath}/js`));
app.use("/img", express.static(`${publicPath}/img`));

app.use("/api", router);
app.use("/api/modelo", modeloRouter);

app.use((req, res) => {
  res.status(404).json({ erro: "Rota não encontrada" });
});

app.use((erro: any, req: Request, res: Response, next: NextFunction) => {
  logError(erro, req);
  const status = erro.status || 500;
  res.status(status).json({ erro: erro.message || "Erro interno" });
});

function sleep(ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function iniciarServidor() {
  const imgDir = path.join(process.cwd(), "public", "img");
  if (!fs.existsSync(imgDir)) {
    fs.mkdirSync(imgDir, { recursive: true });
  }

  const schemaPath = path.join(process.cwd(), "src", "database", "schema.sql");
  const schema = fs.readFileSync(schemaPath, "utf8");

  for (let i = 1; i <= 10; i++) {
    try {
      await client.query(schema);

      await client.query(`
        ALTER TABLE ocorrencias
        ADD COLUMN IF NOT EXISTS classificacao_coletor VARCHAR(100),
        ADD COLUMN IF NOT EXISTS classificacao_confirmada BOOLEAN NOT NULL DEFAULT FALSE
      `);

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
    console.log("WhatsApp Bot desabilitado");
  }
}

iniciarServidor();
