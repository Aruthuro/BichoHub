import { Client } from "pg";
import { env } from "./env.js"

/*
  incialmente considerando uma tabela de banco de dados simples como 
  CREATE TABLE comentarios (
  id SERIAL PRIMARY KEY,
  nome VARCHAR(255) NOT NULL
  );
 */

/*
  conexao ao banco de dados, lembrando de atualizar o .env
*/

export const client = new Client({
  user: env.POSTGRES_USER,
  host: env.POSTGRES_HOST,
  password: env.POSTGRES_PASSWORD,
  database: env.POSTGRES_DB,
  port: env.POSTGRES_PORT
});

export interface Credencial {
  usuario_id: number;
  email: string;
  senha_hash: string;
  nome: string;
}

/*
  cria tabela
*/
export async function criarTabelaMensagens() {

  await client.query(`
    CREATE TABLE IF NOT EXISTS mensagens (
      id SERIAL PRIMARY KEY,
      texto TEXT NOT NULL
    )
  `);
}

/*
  inserir mensagem
*/
export async function inserirMensagem(texto: string) {

  const resultado = await client.query(
    `
      INSERT INTO mensagens (texto)
      VALUES ($1)
      RETURNING *
    `,
    [texto]
  );

  return resultado.rows[0];
}

/*
  buscar mensagens
*/
export async function buscarMensagens() {

  const resultado = await client.query(`
    SELECT * FROM mensagens
  `);

  return resultado.rows;
}

/*
  remover mensagem
*/
export async function removerMensagem(id: number) {

  const resultado = await client.query(
    `
      DELETE FROM mensagens
      WHERE id = $1
      RETURNING *
    `,
    [id]
  );

  return resultado.rows[0];
}

/*
  mostrar a lista de plantao
*/
export async function listarColetoresDisponiveisNoHorario(horario: string) {
  const resultado = await client.query(
    `
    SELECT u.nome, u.contato, p.inicio AS inicio_plantao, p.fim AS fim_plantao
    FROM plantoes p
    JOIN coletores c ON c.usuario_id = p.coletor_id
    JOIN usuarios u ON u.id = c.usuario_id
    WHERE p.disponivel = TRUE
      AND $1::timestamptz BETWEEN p.inicio AND p.fim
    ORDER BY u.nome
    `,
    [horario]
  );

  return resultado.rows;
}

export async function listarSolicitacoes(usuarioId: number) {
  const resultado = await client.query(
    `
    SELECT o.id, o.data_captura, o.descricao_origem, o.descricao_destino,
          o.observacoes, o.risco, o.tipo, o.estado, o.referencia_imagem,
          u_coletor.nome AS coletor_nome
    FROM ocorrencias o
    LEFT JOIN coletores c ON o.coletor_id = c.usuario_id
    LEFT JOIN usuarios u_coletor ON c.usuario_id = u_coletor.id
    WHERE o.origem_solicitacao_id = $1
    ORDER BY o.data_captura DESC
    `,
    [usuarioId]
  );
  return resultado.rows;
}


export async function buscarCredencialPorEmail(email: string): Promise<Credencial | null> {
  const resultado = await client.query(
    `
    SELECT c.usuario_id, c.email, c.senha AS senha_hash, u.nome
    FROM credenciais c
    JOIN usuarios u ON u.id = c.usuario_id
    WHERE c.email = $1
    `,
    [email]
  );

  return resultado.rows[0] || null;
}

export async function inserirOcorrencia(
  origem_id: number,
  gps_origem: [number, number],
  descricao: string | null,
  imagem: string | null,
  tipo: number
) {
  const resultado = await client.query(
    `
      INSERT INTO ocorrencias (origem_solicitacao_id, gps_origem, descricao_origem, referencia_imagem, tipo)
      VALUES ($1, $2, $3, $4, $5)
      RETURNING id
    `,
    [origem_id, gps_origem, descricao, imagem, tipo]
  );

  return resultado.rows[0].id;
}
