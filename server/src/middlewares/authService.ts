import bcrypt from "bcrypt";
import { type Request, type Response, type NextFunction } from "express";
import { env } from "../env.js"
import jwt from "jsonwebtoken";
import { buscarCredencialPorEmail, type Credencial, client, verificarColetor as checarColetor, verificarAdmin as checarAdmin } from "../bancoDeDados.js";
import { OAuth2Client } from "google-auth-library";

const googleClient = new OAuth2Client(env.WEB_CLIENT_ID);

export interface CustomRequest extends Request {
  usuario?: any;
}

export interface LoginResultado {
  token: string;
  nome: string;
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
    req.googleUser = { googleId: payload.sub, email: payload.email, name: payload.name };
    
    next();
  } catch (erro) {
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
    return res.status(401).json({ erro: "Token mal formatado" });
  }

  try {
    // env.JWT_SECRET já é validado na inicialização, não precisa testar
    const payload = jwt.verify(token, env.JWT_SECRET);
    req.usuario = payload;  
    next();
  } catch (erro) {
    return res.status(401).json({ erro: "Token inválido ou expirado" });
  }
}

export async function verificarAdminMiddleware(req: CustomRequest, res: Response, next: NextFunction) {
  const usuarioId = req.usuario?.id;
  if (!usuarioId) {
    return res.status(401).json({ erro: "Usuário não autenticado" });
  }
  const isAdmin = await checarAdmin(usuarioId);
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
  const [isColetor, isAdmin] = await Promise.all([checarColetor(usuarioId), checarAdmin(usuarioId)]);
  if (!isColetor && !isAdmin) {
    return res.status(403).json({ erro: "Usuário não é um coletor ou administrador" });
  }
  next();
}

/*
 * Autentica um usuário com email e senha.
 * Retorna o token JWT e o nome do usuário em caso de sucesso.
 * Lança erro se as credenciais forem inválidas ou ocorrer problema interno.
 */
export async function realizarLogin(email: string, senha: string): Promise<LoginResultado> {
  // 1. Busca a credencial no banco
  const credencial: Credencial | null = await buscarCredencialPorEmail(email);
  if (!credencial) {
    throw new Error("Credenciais inválidas");
  }

  // 2. Verifica a senha com bcrypt
  const senhaValida = await bcrypt.compare(senha, credencial.senha_hash);
  if (!senhaValida) {
    throw new Error("Credenciais inválidas");
  }

  // 3. Gera o token JWT
  const payload = { id: credencial.usuario_id, email: credencial.email };
  const token = jwt.sign(payload, env.JWT_SECRET, { expiresIn: "1d" });

  return {
    token,
    nome: credencial.nome,
  };
}

export async function cadastrarUsuario(
  nome: string,
  email: string,
  senha: string,
  contato: number
) {
  // Verifica se o email já está em uso
  const existente = await client.query(
    `SELECT 1 FROM credenciais WHERE email = $1`,
    [email]
  );
  if (existente.rows.length > 0) {
    throw new Error("Email já cadastrado");
  }

  // Gera o hash da senha
  const senhaHash = await bcrypt.hash(senha, 10);

  // Insere o usuário e a credencial em uma transação
  try {
    await client.query("BEGIN");

    const usuarioResult = await client.query(
      `INSERT INTO usuarios (nome, contato)
       VALUES ($1, $2)
       RETURNING id`,
      [nome, contato]
    );
    const usuarioId = usuarioResult.rows[0].id;

    await client.query(
      `INSERT INTO credenciais (usuario_id, email, senha)
       VALUES ($1, $2, $3)`,
      [usuarioId, email, senhaHash]
    );

    await client.query("COMMIT");

    return { id: usuarioId, nome, email };
  } catch (erro) {
    await client.query("ROLLBACK");
    throw erro;
  }
}

export async function realizarLoginGoogle(googleUser: { email: string; name: string; googleId: string }): Promise<LoginResultado> {
  let credencial = await buscarCredencialPorEmail(googleUser.email);
  if (!credencial) {
    // Não precisa criar uma senha de verdade, já que o usuário fará login com o google
    credencial = await cadastrarUsuario(googleUser.name, googleUser.email, Math.random().toString(32));
  }
  const payload = { id: credencial.usuario_id, email: credencial.email };
  const token = jwt.sign(payload, env.JWT_SECRET, { expiresIn: "1d" });

  return {
    token,
    nome: credencial.nome,
  };
}
