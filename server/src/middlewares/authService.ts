import bcrypt from "bcrypt";
import { type Request, type Response, type NextFunction } from "express";
import { env } from "../env.js"
import jwt from "jsonwebtoken";
import { buscarCredencialPorEmail, type Credencial, client} from "../bancoDeDados.js";

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
    return res.status(401).json({ erro: "Token não fornecido" });   // 401, não 403
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
    id: credencial.usuario_id,
  };
}

export async function cadastrarUsuario(
  nome: string,
  email: string,
  senha: string,
  contato?: string
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
    const payload = { id: usuarioId, email: email };
    const token = jwt.sign(payload, env.JWT_SECRET, { expiresIn: "1d" });

    return { id: usuarioId, token };
  } catch (erro) {
    await client.query("ROLLBACK");
    throw erro;
  }
}