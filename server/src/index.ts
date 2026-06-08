import express from "express";
import { env } from "./env.js";
import { client } from "./bancoDeDados.js";
import { logger, logError } from "./logger.js";
import router from "./router/routes.js";
import { type Request, type Response, type NextFunction } from "express";

const app = express();
const publicPath = `${process.cwd()}/public`;


/*
  middleware para converter JSON do body
  em objeto JavaScript acessível por req.body
*/ 
app.use(express.json());

// para logs
app.use(logger);

app.use("/css", express.static(`${publicPath}/css`));
app.use("/js", express.static(`${publicPath}/js`));
app.use("/img", express.static(`${publicPath}/img`));

app.use("/api", router);

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
  funcao assincrona para iniciar o servidor (nao sei se tem q colocar promisse honestamente)
*/
async function iniciarServidor() {
  try {
    await client.connect();
    console.log("Banco conectado");
    app.listen(env.PORT, () => {
      console.log(`Servidor rodando na porta ${env.PORT}`);
    });
  } catch (erro) {
    logError(erro as Error);
  }
}

iniciarServidor();