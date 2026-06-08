import { Router } from "express";

import {
  criarTabelaMensagens,
  inserirMensagem,
  buscarMensagens,
  removerMensagem,
  listarSolicitacoes,
  listarColetoresDisponiveisNoHorario,
  inserirOcorrencia
} from "../bancoDeDados.js";
import { verificarToken, realizarLogin, cadastrarUsuario} from "../middlewares/authService.js";
import { type CustomRequest } from "../middlewares/authService.js"

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
router.get("/v1/usuarios/coletores-disponiveis", verificarToken, async (req, res) => {
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
router.get("/v1/usuarios/listar", verificarToken, async (req, res) => {
  try {
    // O middleware injeta o payload do token em req.usuario
    const usuarioId = (req as CustomRequest).usuario.id;
    const solicitacoes = await listarSolicitacoes(usuarioId);
    res.status(200).json(solicitacoes);
  } catch (erro) {
    console.error(erro);
    res.status(500).json({ erro: "Erro ao buscar as solicitações do usuário" });
  }
});

/*
  login do usuario
*/
router.post("/v1/usuarios/login", async (req, res) => {
  const { email, senha } = req.body;

  if (!email || !senha) {
    return res.status(400).json({ erro: "Email e senha são obrigatórios" });
  }

  try {
    const resultado = await realizarLogin(email, senha);
    return res.json(resultado); // { token, id }
  } catch (erro: any) {
    if (erro.message === "Credenciais inválidas") {
      return res.status(403).json({ erro: "Credenciais inválidas" });
    }
    console.error(erro);
    return res.status(500).json({ erro: "Erro interno ao realizar login" });
  }
});

/*
  cadastro do usuario
*/
router.post("/v1/usuarios/cadastrar", async (req, res) => {
  const { nome, email, senha, contato } = req.body;

  // Validação básica
  if (!nome || !email || !senha) {
    return res.status(400).json({ erro: "Nome, email e senha são obrigatórios" });
  }

  try {
    const usuario = await cadastrarUsuario(nome, email, senha, contato);
    res.status(201).json({
      mensagem: "Usuário cadastrado com sucesso",
      usuario: { token: usuario.token, id: usuario.id },
    });
  } catch (erro: any) {
    if (erro.message === "Email já cadastrado") {
      return res.status(409).json({ erro: erro.message });
    }
    console.error(erro);
    res.status(500).json({ erro: "Erro ao cadastrar usuário" });
  }
});

/*
  Registrar ocorrência
*/
router.post("/v1/usuarios/registrar-ocorrencia", async (req, res) => {
  const { usuario_id, gps_origem, descricao, imagem, tipo } = req.body;

  // Validação básica
  if (!usuario_id || !tipo || !gps_origem) {
    return res.status(400).json({ erro: "O id do usuário, tipo de ocorrência e localização são obrigatórios" });
  }

  try {
    const ocorrenciaID = await inserirOcorrencia(usuario_id, gps_origem, descricao, imagem, tipo);
    res.status(200).json({ mensagem: "Registro enviado com sucesso", id: ocorrenciaID });
  } catch (erro: any) {
    console.error(erro);
    res.status(500).json({ erro: "Erro ao registrar ocorrência" });
  }
});

export default router;
