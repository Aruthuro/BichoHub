import { Router, type Response, type Request, type NextFunction } from "express";
import multer from "multer";
import path from "path";
import crypto from "crypto";
import fs from "fs";
import os from "os";

import {
  criarTabelaMensagens,
  inserirMensagem,
  buscarMensagens,
  removerMensagem,
  listarSolicitacoes,
  listarColetoresDisponiveisNoHorario,
  listarOcorrenciasAbertas,
  listarOcorrenciasGPS,
  buscarOcorrenciaPorId,
  aceitarOcorrencia,
  listarOcorrenciasDoColetor,
  listarOcorrenciasAtivas,
  editarOcorrencia,
  encerrarOcorrencia,
  criarOcorrencia,
  tornarColetor,
  listarTodosUsuarios,
  listarTodasOcorrencias,
  obterDashboard,
  tornarAdministrador,
  removerUsuario,
  atualizarClassificacao,
  atualizarReferenciaImagem
} from "../bancoDeDados.js";
import { verificarToken, verificarTokenGoogle, verificarColetor, realizarLogin, realizarLoginGoogle, cadastrarUsuario, verificarAdminMiddleware, type AutenticateRequest } from "../middlewares/authService.js";
import { type CustomRequest } from "../middlewares/authService.js"
import { identificarAnimal, type RequestComAnimal } from "../middlewares/deteccaoAnimal.js";
import { classificarImagem } from "../services/classificadorService.js";

const storage = multer.diskStorage({
  destination: path.join(process.cwd(), "public", "img"),
  filename: (_req, file, cb) => {
    const ext = path.extname(file.originalname) || ".jpg";
    cb(null, `ocorrencia_${crypto.randomUUID()}${ext}`);
  }
});
const upload = multer({ storage });

const router = Router();

router.get("/status", (req, res) => {
    res.status(200).json({ 
        status: "UP", 
        message: "BichoHub rodando!",
        node_version: process.version,
        platform: process.platform,
        uptime: `${process.uptime().toFixed(2)} segundos`
    });
}); 

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

router.get("/mensagens", async (req, res, next) => {
  try{
    const mensagens = await buscarMensagens();
    res.json(mensagens);
  }catch(erro){
    console.error(erro);
    next(erro)
  }
});

router.delete("/mensagens/:id", async (req, res, next) => {
  try {
    const id = Number(req.params.id);
    const mensagemRemovida = await removerMensagem(id);

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

router.get("/v1/usuarios/coletores-disponiveis", verificarToken, async (req, res, next) => {
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

router.get("/v1/usuarios/listar", verificarToken, async (req, res, next) => {
  try {
    const usuarioId = (req as CustomRequest).usuario.id;
    const solicitacoes = await listarSolicitacoes(usuarioId);
    res.status(200).json(solicitacoes);
  } catch (erro) {
    console.error(erro);
    next(erro)
  }
});

router.post("/v1/usuarios/google", verificarTokenGoogle, async (req, res, next) => {
  try {
    const authReq = req as AutenticateRequest;
    const resultado = await realizarLoginGoogle(authReq.googleUser);
    return res.json(resultado);
  } catch (erro: any) {
    console.log(erro)
    erro.status = 400;
    next(erro);
  }
});

router.post("/v1/usuarios/login", async (req, res, next) => {
  try {
    const { email, senha } = req.body;
    if (!email || !senha) {
      return res.status(403).json({ erro: "Email e senha são obrigatórios" });
    }
    const resultado = await realizarLogin(email, senha);
    return res.json(resultado);
  } catch (erro: any) {
    console.log(erro)
    erro.status = 400;
    next(erro);
  }
});

router.post("/v1/usuarios/cadastrar", async (req, res, next) => {
  const { nome, email, senha, contato } = req.body;

  if (!nome || !email || !senha) {
    return res.status(400).json({ erro: "Nome, email e senha são obrigatórios" });
  }

  try {
    const usuario = await cadastrarUsuario(nome, email, senha, contato);
    res.status(201).json({
      mensagem: "Usuário cadastrado com sucesso",
      usuario: { id: usuario.id, nome, email },
    });
  } catch (erro: any) {
    if (erro.message === "Email já cadastrado") {
      return res.status(409).json({ erro: erro.message });
    }
    console.log(erro)
    next(erro)
  }
});

router.post("/v1/usuarios/registrar-ocorrencia", verificarToken, async (req, res, next) => {
  try {
    const usuarioId = (req as CustomRequest).usuario.id;
    const { tipo, gps_origem, data_captura, descricao_origem, observacoes, risco, referencia_imagem } = req.body;

    if (!tipo || !gps_origem || !data_captura) {
      return res.status(400).json({ erro: "tipo, gps_origem e data_captura são obrigatórios" });
    }

    const ocorrencia = await criarOcorrencia(
      usuarioId, tipo, gps_origem, data_captura,
      descricao_origem, observacoes, risco, referencia_imagem
    );

    if (referencia_imagem) {
      if (referencia_imagem) {
        void processarClassificacao(ocorrencia.id, referencia_imagem);
      }
    }

    async function processarClassificacao(
      ocorrenciaId: number,
      base64: string
    ) {
      let tempPath = "";
      try {
        const base64Data = base64.replace(/^data:image\/\w+;base64,/, "");
        const buffer = Buffer.from(base64Data, "base64");
        tempPath = path.join(os.tmpdir(), `${crypto.randomUUID()}.jpg`);
        fs.writeFileSync(tempPath, buffer);

        const resultado = classificarImagem(tempPath);

        if (resultado.erro) {
          console.error("Erro na classificação:", resultado.erro);
          return;
        }

        const classificacao = resultado.especie ?? resultado.classe_geral;
        const confianca = Math.round(
          ((resultado.confianca_especie ?? resultado.confianca ?? 0)) * 100
        );

        if (classificacao) {
          await atualizarClassificacao(ocorrenciaId, classificacao, confianca);
        }
      } catch (err) {
        console.error(
          "Erro ao classificar ocorrência",
          ocorrenciaId,
          err
        );
      } finally {
        if (tempPath) {
          try { fs.unlinkSync(tempPath); } catch { /* ignore */ }
        }
      }
    }

    res.status(201).json({
      mensagem: "Ocorrência registrada",
      id: ocorrencia.id,
      ultimo_caso: ocorrencia.ultimo_caso
    });
  } catch (erro) {
    console.error(erro);
    next(erro);
  }
});

router.post(
  "/v1/ocorrencias/:id/imagem",
  verificarToken,
  upload.single("imagem"),
  async (req, res, next) => {
    try {
      const id = Number(req.params.id);
      if (!req.file) {
        return res.status(400).json({ erro: "Arquivo de imagem é obrigatório" });
      }
      const imagemUrl = `${req.protocol}://${req.get("host")}/img/${req.file.filename}`;
      await atualizarReferenciaImagem(id, imagemUrl);

      void (async () => {
        try {
          const resultado = classificarImagem(req.file!.path);
          if (resultado.erro) {
            console.error("Erro na classificação do upload:", resultado.erro);
            return;
          }
          const classificacao = resultado.especie ?? resultado.classe_geral;
          const confianca = Math.round(
            ((resultado.confianca_especie ?? resultado.confianca ?? 0)) * 100
          );
          if (classificacao) {
            await atualizarClassificacao(id, classificacao, confianca);
          }
        } catch (err) {
          console.error("Erro ao classificar imagem da ocorrência", id, err);
        }
      })();

      res.status(200).json({ mensagem: "Imagem salva", referencia_imagem: imagemUrl });
    } catch (erro) {
      console.error(erro);
      next(erro);
    }
  }
);

router.get(
  "/v1/usuarios/ocorrencias/:id",
  verificarToken,
  async (req, res, next) => {
    try {
      const id = Number(req.params.id);
      const usuarioId = (req as CustomRequest).usuario.id;
      const ocorrencia = await buscarOcorrenciaPorId(id);

      if (!ocorrencia || ocorrencia.origem_solicitacao_id !== usuarioId) {
        return res.status(404).json({ erro: "Ocorrência não encontrada" });
      }

      res.status(200).json(ocorrencia);
    } catch (erro) {
      console.error(erro);
      next(erro);
    }
  }
);

router.get(
  "/v1/coletores/ocorrencias/abertas",
  verificarToken,
  verificarColetor,
  async (req, res, next) => {
    try {
      const ocorrencias = await listarOcorrenciasAbertas();
      res.status(200).json(ocorrencias);
    } catch (erro) {
      console.error(erro);
      next(erro);
    }
  }
);

router.get(
  "/v1/coletores/ocorrencias/ativas",
  verificarToken,
  verificarColetor,
  async (req, res, next) => {
    try {
      const coletorId = (req as CustomRequest).usuario!.id;
      const ocorrencias = await listarOcorrenciasAtivas(coletorId);
      res.status(200).json(ocorrencias);
    } catch (erro) {
      console.error(erro);
      next(erro);
    }
  }
);

router.get(
  "/v1/coletores/ocorrencias/historico",
  verificarToken,
  verificarColetor,
  async (req, res, next) => {
    try {
      const coletorId = (req as CustomRequest).usuario.id;
      const ocorrencias = await listarOcorrenciasDoColetor(coletorId);
      res.status(200).json(ocorrencias);
    } catch (erro) {
      console.error(erro);
      next(erro);
    }
  }
);

router.get(
  "/v1/coletores/ocorrencias/listar",
  verificarToken,
  verificarColetor,
  async (req, res, next) => {
    try {
      const ocorrencias = await listarOcorrenciasGPS();
      res.status(200).json(ocorrencias);
    } catch (erro) {
      console.error(erro);
      next(erro);
    }
  }
);

router.get(
  "/v1/coletores/ocorrencias/:id",
  verificarToken,
  verificarColetor,
  async (req, res, next) => {
    try {
      const id = Number(req.params.id);
      const ocorrencia = await buscarOcorrenciaPorId(id);

      if (!ocorrencia) {
        return res.status(404).json({ erro: "Ocorrência não encontrada" });
      }

      res.status(200).json(ocorrencia);
    } catch (erro) {
      console.error(erro);
      next(erro);
    }
  }
);

router.patch(
  "/v1/coletores/responder/:id",
  verificarToken,
  verificarColetor,
  async (req, res, next) => {
    try {
      const id = Number(req.params.id);
      const coletorId = (req as CustomRequest).usuario.id;
      const { resposta } = req.body;

      if (!resposta || !["aceitar", "rejeitar"].includes(resposta)) {
        return res.status(400).json({ erro: "resposta deve ser 'aceitar' ou 'rejeitar'" });
      }

      if (resposta === "rejeitar") {
        return res.status(200).json({ mensagem: "Chamada rejeitada" });
      }

      const ocorrencia = await aceitarOcorrencia(id, coletorId);
      if (!ocorrencia) {
        return res.status(409).json({ erro: "Ocorrência não está mais disponível" });
      }

      res.status(200).json({ mensagem: "Chamada aceita", id: ocorrencia.id });
    } catch (erro) {
      console.error(erro);
      next(erro);
    }
  }
);

router.patch(
  "/v1/coletores/ocorrencias/editar/:id",
  verificarToken,
  verificarColetor,
  async (req, res, next) => {
    try {
      const id = Number(req.params.id);
      const coletorId = (req as CustomRequest).usuario.id;
      const { estado, status_saude, observacoes, risco, equipamento_captura, descricao_destino, destino_gps } = req.body;

      if (estado !== undefined && ![2, 5].includes(estado)) {
        return res.status(400).json({ erro: "estado deve ser 2 (em andamento) ou 5 (animal sob cuidados)" });
      }

      const resultado = await editarOcorrencia(id, coletorId, {
        estado, status_saude, observacoes, risco,
        equipamento_captura, descricao_destino, destino_gps
      });

      if (!resultado) {
        return res.status(404).json({ erro: "Ocorrência não encontrada ou não pertence a este coletor" });
      }

      res.status(200).json({ mensagem: "Ocorrência atualizada", id: resultado.id });
    } catch (erro) {
      console.error(erro);
      next(erro);
    }
  }
);

router.patch(
  "/v1/coletores/ocorrencias/encerrar/:id",
  verificarToken,
  verificarColetor,
  async (req, res, next) => {
    try {
      const id = Number(req.params.id);
      const coletorId = (req as CustomRequest).usuario.id;
      const { desfecho, descricao_soltura } = req.body;

      if (!desfecho || !["solto", "sob_cuidados", "morto"].includes(desfecho)) {
        return res.status(400).json({
          erro: "desfecho deve ser 'solto', 'sob_cuidados' ou 'morto'"
        });
      }

      if (desfecho === "solto" && !descricao_soltura) {
        return res.status(400).json({
          erro: "descricao_soltura é obrigatória quando desfecho é 'solto'"
        });
      }

      const resultado = await encerrarOcorrencia(id, coletorId, desfecho, descricao_soltura);

      if (!resultado) {
        return res.status(404).json({
          erro: "Ocorrência não encontrada, não pertence a este coletor ou não está em andamento"
        });
      }

      res.status(200).json({ mensagem: "Chamada encerrada", id: resultado.id });
    } catch (erro) {
      console.error(erro);
      next(erro);
    }
  }
);

/*
  =====================================================
  ROTAS DE ADMINISTRADOR
  =====================================================
*/

router.get("/v1/admin/dashboard", verificarToken, verificarAdminMiddleware, async (req, res, next) => {
  try {
    const stats = await obterDashboard();
    res.status(200).json(stats);
  } catch (erro) {
    console.error(erro);
    next(erro);
  }
});

router.get("/v1/admin/usuarios", verificarToken, verificarAdminMiddleware, async (req, res, next) => {
  try {
    const usuarios = await listarTodosUsuarios();
    res.status(200).json(usuarios);
  } catch (erro) {
    console.error(erro);
    next(erro);
  }
});

router.get("/v1/admin/ocorrencias", verificarToken, verificarAdminMiddleware, async (req, res, next) => {
  try {
    const filtro = req.query.filtro as string | undefined;
    const ocorrencias = await listarTodasOcorrencias(filtro);
    res.status(200).json(ocorrencias);
  } catch (erro) {
    console.error(erro);
    next(erro);
  }
});

router.get("/v1/admin/ocorrencias/:id", verificarToken, verificarAdminMiddleware, async (req, res, next) => {
  try {
    const id = Number(req.params.id);
    const ocorrencia = await buscarOcorrenciaPorId(id);
    if (!ocorrencia) {
      return res.status(404).json({ erro: "Ocorrência não encontrada" });
    }
    res.status(200).json(ocorrencia);
  } catch (erro) {
    console.error(erro);
    next(erro);
  }
});

router.post("/v1/admin/usuarios/:id/tornar-admin", verificarToken, verificarAdminMiddleware, async (req, res, next) => {
  try {
    const usuarioId = Number(req.params.id);
    const resultado = await tornarAdministrador(usuarioId);
    if (!resultado) {
      return res.status(409).json({ erro: "Usuário já é administrador" });
    }
    res.status(201).json({ mensagem: "Administrador promovido", usuario_id: resultado.usuario_id });
  } catch (erro: any) {
    if (erro.code === "23503") {
      return res.status(404).json({ erro: "Usuário não encontrado" });
    }
    next(erro);
  }
});

router.post("/v1/admin/usuarios/:id/tornar-coletor", verificarToken, verificarAdminMiddleware, async (req, res, next) => {
  try {
    const usuarioId = Number(req.params.id);
    const resultado = await tornarColetor(usuarioId);
    if (!resultado) {
      return res.status(409).json({ erro: "Usuário já é coletor" });
    }
    res.status(201).json({ mensagem: "Coletor promovido", usuario_id: resultado.usuario_id });
  } catch (erro: any) {
    if (erro.code === "23503") {
      return res.status(404).json({ erro: "Usuário não encontrado" });
    }
    next(erro);
  }
});

router.delete("/v1/admin/usuarios/:id", verificarToken, verificarAdminMiddleware, async (req, res, next) => {
  try {
    const usuarioId = Number(req.params.id);
    const adminRequisitante = (req as CustomRequest).usuario.id;
    if (usuarioId === adminRequisitante) {
      return res.status(400).json({ erro: "Não é possível remover a si mesmo" });
    }
    const resultado = await removerUsuario(usuarioId);
    if (!resultado) {
      return res.status(404).json({ erro: "Usuário não encontrado" });
    }
    res.status(200).json({ mensagem: "Usuário removido", id: resultado.id });
  } catch (erro) {
    console.error(erro);
    next(erro);
  }
});

router.post("/v1/teste/tornar-coletor/:id", async (req, res, next) => {
  try {
    const usuarioId = Number(req.params.id);
    if (!usuarioId || isNaN(usuarioId)) {
      return res.status(400).json({ erro: "ID inválido" });
    }
    const resultado = await tornarColetor(usuarioId);
    if (!resultado) {
      return res.status(409).json({ erro: "Usuário já é um coletor" });
    }
    res.status(201).json({ mensagem: "Usuário transformado em coletor", usuario_id: resultado.usuario_id });
  } catch (erro: any) {
    if (erro.code === "23503") {
      return res.status(404).json({ erro: "Usuário não encontrado" });
    }
    next(erro);
  }
});

router.post("/v1/animais/identificar", 
  identificarAnimal, 
  async (req: RequestComAnimal, res: Response, next: NextFunction) => {
    try {
      if (!req.animalIdentificado) {
        return res.status(404).json({
          success: false,
          mensagem: "Nenhum animal foi identificado na imagem"
        });
      }

      res.json({
        success: true,
        animal: req.animalIdentificado.especie,
        confidence: req.animalIdentificado.confidence,
        confianca_percentual: `${(req.animalIdentificado.confidence * 100).toFixed(2)}%`,
        pipeline: req.animalIdentificado.pipeline,
        timestamp: new Date().toISOString()
      });
    } catch (error) {
      console.log(`Error: ${error}`)
      next(error);
    }
  }
);

export default router;
