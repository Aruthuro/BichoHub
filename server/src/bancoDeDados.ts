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
export async function mostrarPlantoesAtuais() {
    const resultado = await client.query(
      `
      SELECT
        p.inicio,
        p.fim,
        u.nome,
        u.contato,
      FROM plantoes p
      JOIN coletores c
        ON c.usuario_id = p.coletor_id
      JOIN usuarios u
        ON u.id = c.usuario_id
      WHERE p.disponivel = TRUE
      `
  );

  return resultado.rows;
}

export async function mostrarResgatesConcluidos(id: number) {

  const resultado = await client.query(
    `
    SELECT
      o.id,
      o.data_captura,
      o.descricao_origem,
      o.descricao_destino,
      o.observacoes,
      o.risco,
      o.tipo,
      o.referencia_imagem,
      u.nome AS solicitante_nome
    FROM ocorrencias o

    JOIN usuarios u
      ON u.id = o.origem_solicitacao_id

    WHERE o.coletor_id = $1
      AND o.estado = 2

    ORDER BY o.data_captura DESC
    `,
    [id]
  );

  return resultado.rows;
}