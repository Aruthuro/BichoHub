import { Router } from "express";

import {
  criarTabelaMensagens,
  inserirMensagem,
  buscarMensagens,
  removerMensagem,
  listarSolicitacoes,
  listarColetoresDisponiveisNoHorario
} from "../bancoDeDados.js";
import { verificarToken } from "../middlewares/auth.js";

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

/* 
  mostra os platoes atuais
*/
router.get(
  "/v1/usuarios/coletores-disponiveis",
  verificarToken,
  async (req, res) => {
    // Extração segura do horário (código que você já tem)
    const raw = req.query.horario;
    let horario: string;
    if (Array.isArray(raw)) {
      horario = raw[0]?.toString() ?? "";
    } else if (typeof raw === "string") {
      horario = raw;
    } else {
      horario = "";
    }

    if (!horario || isNaN(Date.parse(horario))) {
      return res.status(400).json({ erro: "Parâmetro 'horario' inválido ou ausente..." });
    }

    try {
      const coletores = await listarColetoresDisponiveisNoHorario(horario);
      return res.status(200).json(coletores);
    } catch (erro) {
      console.error(erro);
      return res.status(500).json({ erro: "Erro ao buscar coletores disponíveis" });
    }
  }
);

/*
  lista todas as ocorrencias abertas por um usuario
*/
router.get("/v1/usuarios/listar/:id", async (req, res) => {

  const id = Number(req.params.id);

  try {

    const resgates = await listarSolicitacoes(id);

    res.status(200).json(resgates);

  } catch (erro) {

    console.error(erro);

    res.status(500).json({
      erro: "Erro ao buscar a lista de resgates do usuário"
    });

  }

});


export default router;