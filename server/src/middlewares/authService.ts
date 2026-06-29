import bcrypt from "bcrypt";
import { type Request, type Response, type NextFunction } from "express";
import { env } from "../env.js"
import jwt from "jsonwebtoken";
import { buscarCredencialPorEmail, type Credencial, client, checarColetor, checarAjudante, verificarAdmin } from "../bancoDeDados.js";
import { OAuth2Client } from "google-auth-library";

const googleClient = new OAuth2Client(env.WEB_CLIENT_ID);

export interface CustomRequest extends Request {
  usuario?: any;
}

export interface LoginResultado {
  token: string;
  nome: string;
  eh_admin: boolean;
  eh_coletor: boolean;
}

export interface AutenticateRequest extends Request {
  googleUser:{
    googleId:string;
    email:string;
    name:string;
  }
}

export async function verificarTokenGoogle(req: Request, res: Response, next: NextFunction) {
  const { token } = req.body;
  if (!token) {
    return res.status(403).json({ erro: "Token não fornecido" });
  }
  try {
    const ticket = await googleClient.verifyIdToken({
      idToken: token,
      audience: env.WEB_CLIENT_ID,
    });
    const payload = ticket.getPayload();
    if (!payload || !payload.email || !payload.name) {
      return res.status(401).json({ erro: "Token mal formatado" });
    }

    (req as AutenticateRequest).googleUser = { 
      googleId: payload.sub, 
      email: payload.email, 
      name: payload.name 
    };
    next();
  } catch {
    return res.status(401).json({ erro: "Token inválido ou expirado" });
  }
}

export function verificarToken(req: CustomRequest, res: Response, next: NextFunction) {
  const authHeader = req.headers.authorization;
  if (!authHeader || !authHeader.startsWith("Bearer ")) {
    return res.status(403).json({ erro: "Token não fornecido" });
  }

  const token = authHeader.split(" ")[1];
  if (!token) {
    return res.status(403).json({ erro: "Token mal formatado" });
  }

  try {
    const payload = jwt.verify(token, env.JWT_SECRET);
    req.usuario = payload;  
    next();
  } catch (erro: any) {
    if (erro.name === "TokenExpiredError" || erro.name === "NotBeforeError") {
      return res.status(401).json({ erro: "Token expirado" });
    } else if (erro.name === "JsonWebTokenError") {
      return res.status(403).json({ erro: "Token inválido" });
    } else {
      return res.status(500).json({ erro: "Erro inesperado" });
    }
  }
}

export async function verificarAdminMiddleware(req: CustomRequest, res: Response, next: NextFunction) {
  const usuarioId = req.usuario?.id;
  if (!usuarioId) {
    return res.status(401).json({ erro: "Usuário não autenticado" });
  }
  const isAdmin = await verificarAdmin(usuarioId);
  if (!isAdmin) {
    return res.status(403).json({ erro: "Acesso restrito a administradores" });
  }
  next();
}

export async function verificarColetor(req: CustomRequest, res: Response, next: NextFunction) {
  const usuarioId = req.usuario?.id;
  if (!usuarioId) {
    return res.status(401).json({ erro: "Usuário não autenticado" });
  }
  const [isColetor, isAdmin] = await Promise.all([checarColetor(usuarioId), verificarAdmin(usuarioId)]);
  if (!isColetor && !isAdmin) {
    return res.status(403).json({ erro: "Usuário não é um coletor ou administrador" });
  }
  next();
}

export async function verificarAjudante(req: CustomRequest, res: Response, next: NextFunction) {
  const usuarioId = req.usuario?.id;
  if (!usuarioId) {
    return res.status(401).json({ erro: "Usuário não autenticado" });
  }
  const isAjudante = await checarAjudante(usuarioId);
  if (!isAjudante) {
    return res.status(403).json({ erro: "Usuário não é um ajudante" });
  }
  next();
}

export async function realizarLogin(email: string, senha: string): Promise<LoginResultado> {
  const credencial: Credencial | null = await buscarCredencialPorEmail(email);
  if (!credencial) {
    throw new Error("Credenciais inválidas");
  }

  const senhaValida = await bcrypt.compare(senha, credencial.senha_hash);
  if (!senhaValida) {
    throw new Error("Credenciais inválidas");
  }

  const isColetor = await checarColetor(credencial.usuario_id);
  const isAjudante = await checarAjudante(credencial.usuario_id);
  const payload = { id: credencial.usuario_id, coletor: isColetor, ajudante: isAjudante };
  const token = jwt.sign(payload, env.JWT_SECRET, { expiresIn: "1d" });

  const [eh_admin, eh_coletor] = await Promise.all([
    verificarAdmin(credencial.usuario_id),
    checarColetor(credencial.usuario_id),
  ]);

  return {
    token,
    nome: credencial.nome,
    eh_admin,
    eh_coletor,
  };
}

export async function cadastrarUsuario(
  nome: string,
  email: string,
  senha: string,
  contato?: string
) {
  const existente = await client.query(
    `SELECT 1 FROM credenciais WHERE email = $1`,
    [email]
  );
  if (existente.rows.length > 0) {
    throw new Error("Email já cadastrado");
  }

  const senhaHash = await bcrypt.hash(senha, 10);

  try {
    await client.query("BEGIN");

    const usuarioResult = await client.query(
      `INSERT INTO usuarios (nome, contato)
       VALUES ($1, $2)
       RETURNING id`,
      [nome, contato || null]
    );
    const usuarioId = usuarioResult.rows[0].id;

    await client.query(
      `INSERT INTO credenciais (usuario_id, email, senha)
       VALUES ($1, $2, $3)`,
      [usuarioId, email, senhaHash]
    );

    await client.query("COMMIT");

    const isColetor = await checarColetor(usuarioId);
    const isAjudante = await checarAjudante(usuarioId);
    const payload = { id: usuarioId, coletor: isColetor, ajudante: isAjudante };
    const token = jwt.sign(payload, env.JWT_SECRET, { expiresIn: "1d" });

    return { token, id: usuarioId };
  } catch (erro) {
    await client.query("ROLLBACK");
    throw erro;
  }
}

export async function realizarLoginGoogle(googleUser: { email: string; name: string; googleId: string }) {
  let credencial = await buscarCredencialPorEmail(googleUser.email);
  if (!credencial) {
    return { nome: googleUser.name, email: googleUser.email, senha: Math.random().toString(32) };
  }
  const payload = { id: credencial.usuario_id, email: credencial.email };
  const token = jwt.sign(payload, env.JWT_SECRET, { expiresIn: "1d" });

  const [eh_admin, eh_coletor] = await Promise.all([
    verificarAdmin(credencial.usuario_id),
    checarColetor(credencial.usuario_id),
  ]);

  return {
    token,
    nome: credencial.nome,
    eh_admin,
    eh_coletor,
  };
}
