import { Router } from "express";

import {
  criarTabelaMensagens,
  inserirMensagem,
  buscarMensagens,
  removerMensagem
} from "../bancoDeDados.js";

const router = Router();

/*
    rota do status do backend
*/
router.get("/status", (req, res) => {
    res.status(200).json({ 
        status: "UP", 
        message: "BichoHub rodando!",
        node_version: process.version,
        platform: process.platform,
        uptime: `${process.uptime().toFixed(2)} segundos`
    });
}); 
/*
  setup do banco
*/
router.get("/setup", async (req, res) => {

  await criarTabelaMensagens();

  res.json({
    mensagem: "Tabela criada"
  });
});

/*
  inserir mensagem
*/
router.post("/mensagens", async (req, res) => {

  const { texto } = req.body;

  const mensagem = await inserirMensagem(texto);

  res.status(201).json(mensagem);
});

/*
  buscar mensagens
*/
router.get("/mensagens", async (req, res) => {

  const mensagens = await buscarMensagens();

  res.json(mensagens);
});

/*
  remover mensagem
*/
router.delete("/mensagens/:id", async (req, res) => {

  const id = Number(req.params.id);

  const mensagemRemovida = await removerMensagem(id);

  /*
    verifica se a mensagem existia
  */
  if (!mensagemRemovida) {

    return res.status(404).json({
      erro: "Mensagem não encontrada"
    });
  }

  res.json({
    mensagem: "Mensagem remvida",
    dados: mensagemRemovida
  });
});

export default router;