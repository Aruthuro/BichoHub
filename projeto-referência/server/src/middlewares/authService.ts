import bcrypt from "bcrypt";
import { type Request, type Response, type NextFunction } from "express";
import { env } from "../env.js"
import jwt from "jsonwebtoken";
import { buscarCredencialPorEmail, type Credencial, client, checarColetor, checarAjudante } from "../bancoDeDados.js";

export interface CustomRequest extends Request {
  usuario?: any;
}

export interface LoginResultado {
  token: string;
  id: number;
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
    // env.JWT_SECRET já é validado na inicialização, não precisa testar
    const payload = jwt.verify(token, env.JWT_SECRET);
    req.usuario = payload;  
    next();
  } catch (erro) {
	  if (erro.name == TokenExpiredError || erro.name == NotBeforeError){
		return res.status(401).json({ erro: "Token expirado" });
	  } else if (erro.name == JsonWebTokenError){
    		return res.status(403).json({ erro: "Token inválido" });
	  } else {
		return res.staus(500).json({ erro: "Erro inexperado" })
	  }
  }
}

export async function verificarColetor(req: CustomRequest, res: Response, next: NextFunction) {
  const usuarioId = req.usuario?.id;
  if (!usuarioId) {
    return res.status(401).json({ erro: "Usuário não autenticado" });
  }
  const isColetor = await checarColetor(usuarioId);
  if (!isColetor) {
    return res.status(403).json({ erro: "Usuário não é um coletor" });
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
    return res.status(403).json({ erro: "Usuário não é um coletor" });
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
  const isColetor = await checarColetor(credencial.usuario_id);
  const isAjudante = await checarAjudante(credencial.usuario_id);
  const payload = { id: credencial.usuario_id, coletor: isColetor, ajudante: isAjudante };
  const token = jwt.sign(payload, env.JWT_SECRET, { expiresIn: "1d" });

  return {
    token,
    id: credencial.usuario_id,
  };
}

export async function cadastrarUsuario(nome: string, email: string, senha: string, contato?: string): Promise<LoginResultado>{
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
      [nome, contato || null]
    );
    const usuarioId = usuarioResult.rows[0].id;

    await client.query(
      `INSERT INTO credenciais (usuario_id, email, senha)
       VALUES ($1, $2, $3)`,
      [usuarioId, email, senhaHash]
    );

    await client.query("COMMIT");

    // Gera o token JWT
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
