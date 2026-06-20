import { Router, type Response, type Request, type NextFunction } from "express";

import {
  criarTabelaMensagens,
  inserirMensagem,
  buscarMensagens,
  removerMensagem,
  listarSolicitacoes,
  listarColetoresDisponiveisNoHorario
} from "../bancoDeDados.js";
import { verificarToken, realizarLogin, cadastrarUsuario} from "../middlewares/authService.js";
import { type CustomRequest } from "../middlewares/authService.js"
import { identificarAnimal, type RequestComAnimal } from "../middlewares/deteccaoAnimal.js";

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
router.get("/setup", async (req, res, next) => {

  try {await criarTabelaMensagens();

  res.json({
    mensagem: "Tabela criada"
  });
  } catch(erro){
    console.error(erro);
    next(erro)
  }
});

/*
  inserir mensagem
*/
router.post("/mensagens", async (req, res, next) => {

  try{
    const { texto } = req.body;
    const mensagem = await inserirMensagem(texto);
    res.status(201).json(mensagem);
  }catch (erro){
    console.error(erro);
    next(erro)
  }
  
});

/*
  buscar mensagens
*/
router.get("/mensagens", async (req, res, next) => {
  try{
    const mensagens = await buscarMensagens();

    res.json(mensagens);
  }catch(erro){
    console.error(erro);
    next(erro)
  }
  
});

/*
  remover mensagem
*/
router.delete("/mensagens/:id", async (req, res, next) => {

  try {
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
  }catch(erro){
    console.error(erro);
    next(erro)
  }

  
});

/* 
  mostra os platoes atuais
*/
router.get(
  "/v1/usuarios/coletores-disponiveis",
  verificarToken,
  async (req, res, next) => {

    // extracao segura do horario 
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
router.get("/v1/usuarios/listar", verificarToken, async (req, res, next) => {
  try {
    // O middleware injeta o payload do token em req.usuario
    const usuarioId = (req as CustomRequest).usuario.id;
    const solicitacoes = await listarSolicitacoes(usuarioId);

    res.status(200).json(solicitacoes);
  } catch (erro) {
    console.error(erro);
    next(erro)
  }
});

/*
  login do usuario
*/
router.post("/v1/usuarios/login", async (req, res, next) => {
  try {
    const { email, senha } = req.body;
    if (!email || !senha) {
      return res.status(400).json({ erro: "Email e senha são obrigatórios" });
    }
    const resultado = await realizarLogin(email, senha);
    return res.json(resultado);
  } catch (erro: any) {
    console.log(erro)
    erro.status = 403;
    next(erro);
  }
});


/*
  cadastro do usuario
*/
router.post("/v1/usuarios/cadastrar", async (req, res, next) => {
  const { nome, email, senha, contato } = req.body;

  // Validação básica
  if (!nome || !email || !senha) {
    return res.status(400).json({ erro: "Nome, email e senha são obrigatórios" });
  }

  try {
    const usuario = await cadastrarUsuario(nome, email, senha, contato);
    res.status(201).json({
      mensagem: "Usuário cadastrado com sucesso",
      usuario: { id: usuario.id, nome: usuario.nome, email: usuario.email },
    });
  } catch (erro: any) {
    if (erro.message === "Email já cadastrado") {
      return res.status(409).json({ erro: erro.message });
    }
    console.log(erro)
    next(erro)
  }
});

/*
  identificação do animal
*/
router.post("/v1/animais/identificar", 
  identificarAnimal, 
  async (req: RequestComAnimal, res: Response, next: NextFunction) => {
    try {
      // Verificar se o middleware realmente identificou algo
      if (!req.animalIdentificado) {
        return res.status(404).json({
          success: false,
          mensagem: "Nenhum animal foi identificado na imagem"
        });
      }

      // Retornar o resultado da identificação
      res.json({
        success: true,
        animal: req.animalIdentificado.especie,
        confidence: req.animalIdentificado.confidence,
        confianca_percentual: `${(req.animalIdentificado.confidence * 100).toFixed(2)}%`,
        pipeline: req.animalIdentificado.pipeline,
        timestamp: new Date().toISOString()
      });
    } catch (error) {/
      console.log(`Error: ${error}`)
      next(error);
    }
  }
);

export default router;