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


export async function tornarColetor(usuarioId: number, cpf?: string) {
  const cpfFinal = cpf || String(usuarioId).padStart(11, "0");
  const resultado = await client.query(
    `INSERT INTO coletores (usuario_id, cpf)
     VALUES ($1, $2)
     ON CONFLICT (usuario_id) DO NOTHING
     RETURNING usuario_id`,
    [usuarioId, cpfFinal]
  );
  return resultado.rows[0] || null;
}

export async function verificarColetor(usuarioId: number): Promise<boolean> {
  const resultado = await client.query(
    `SELECT 1 FROM coletores WHERE usuario_id = $1`,
    [usuarioId]
  );
  return resultado.rows.length > 0;
}

export async function listarOcorrenciasAbertas() {
  const resultado = await client.query(`
    SELECT o.id, o.tipo, o.data_captura, o.descricao_origem,
           o.observacoes, o.risco, o.ultimo_caso, o.estado,
           ST_AsText(o.origem_gps) AS origem_gps,
           u.nome AS solicitante_nome, u.contato AS solicitante_contato
    FROM ocorrencias o
    JOIN usuarios u ON u.id = o.origem_solicitacao_id
    WHERE o.estado = 0
    ORDER BY o.ultimo_caso DESC, o.data_captura ASC
  `);
  return resultado.rows;
}

export async function buscarOcorrenciaPorId(id: number) {
  const resultado = await client.query(
    `SELECT o.*, ST_AsText(o.origem_gps) AS origem_gps,
            ST_AsText(o.destino_gps) AS destino_gps,
            u.nome AS solicitante_nome, u.contato AS solicitante_contato
     FROM ocorrencias o
     JOIN usuarios u ON u.id = o.origem_solicitacao_id
     WHERE o.id = $1`,
    [id]
  );
  return resultado.rows[0] || null;
}

export async function aceitarOcorrencia(id: number, coletorId: number) {
  const resultado = await client.query(
    `UPDATE ocorrencias
     SET coletor_id = $1, estado = 2
     WHERE id = $2 AND estado = 0
     RETURNING id`,
    [coletorId, id]
  );
  return resultado.rows[0] || null;
}

export async function listarOcorrenciasDoColetor(coletorId: number) {
  const resultado = await client.query(
    `SELECT o.id, o.tipo, o.data_captura, o.descricao_origem,
            o.observacoes, o.risco, o.estado, o.status_saude,
            o.desfecho, o.descricao_soltura,
            ST_AsText(o.origem_gps) AS origem_gps,
            ST_AsText(o.destino_gps) AS destino_gps,
            u.nome AS solicitante_nome
     FROM ocorrencias o
     JOIN usuarios u ON u.id = o.origem_solicitacao_id
     WHERE o.coletor_id = $1
     ORDER BY o.data_captura DESC`,
    [coletorId]
  );
  return resultado.rows;
}

export async function editarOcorrencia(
  id: number,
  coletorId: number,
  dados: {
    estado?: number;
    status_saude?: string;
    observacoes?: string;
    risco?: string;
    equipamento_captura?: string;
    descricao_destino?: string;
    destino_gps?: string;
  }
) {
  const campos: string[] = [];
  const valores: any[] = [];
  let idx = 1;

  if (dados.estado !== undefined) {
    campos.push(`estado = $${idx++}`);
    valores.push(dados.estado);
  }
  if (dados.status_saude !== undefined) {
    campos.push(`status_saude = $${idx++}`);
    valores.push(dados.status_saude);
  }
  if (dados.observacoes !== undefined) {
    campos.push(`observacoes = $${idx++}`);
    valores.push(dados.observacoes);
  }
  if (dados.risco !== undefined) {
    campos.push(`risco = $${idx++}`);
    valores.push(dados.risco);
  }
  if (dados.equipamento_captura !== undefined) {
    campos.push(`equipamento_captura = $${idx++}`);
    valores.push(dados.equipamento_captura);
  }
  if (dados.descricao_destino !== undefined) {
    campos.push(`descricao_destino = $${idx++}`);
    valores.push(dados.descricao_destino);
  }
  if (dados.destino_gps !== undefined) {
    campos.push(`destino_gps = ST_GeomFromText($${idx++}, 4326)`);
    valores.push(dados.destino_gps);
  }

  if (campos.length === 0) return null;

  valores.push(id);
  valores.push(coletorId);

  const resultado = await client.query(
    `UPDATE ocorrencias
     SET ${campos.join(", ")}
     WHERE id = $${idx++} AND coletor_id = $${idx}
     RETURNING id`,
    valores
  );
  return resultado.rows[0] || null;
}

export async function encerrarOcorrencia(
  id: number,
  coletorId: number,
  desfecho: string,
  descricaoSoltura?: string
) {
  const resultado = await client.query(
    `UPDATE ocorrencias
     SET estado = 3, desfecho = $1, descricao_soltura = $2
     WHERE id = $3 AND coletor_id = $4 AND estado IN (2, 5)
     RETURNING id`,
    [desfecho, descricaoSoltura || null, id, coletorId]
  );
  return resultado.rows[0] || null;
}

export async function criarOcorrencia(
  origemSolicitacaoId: number,
  tipo: number,
  origemGps: string,
  dataCaptura: string,
  descricaoOrigem?: string,
  observacoes?: string,
  risco?: string,
  referenciaImagem?: string
) {
  const coletoresDisponiveis = await client.query(
    `SELECT COUNT(*) as total FROM plantoes
     WHERE disponivel = TRUE
       AND $1::timestamptz BETWEEN inicio AND fim`,
    [dataCaptura]
  );
  const ultimoCaso = parseInt(coletoresDisponiveis.rows[0].total) === 0;

  const resultado = await client.query(
    `INSERT INTO ocorrencias
       (origem_solicitacao_id, tipo, origem_gps, data_captura,
        descricao_origem, observacoes, risco, referencia_imagem, ultimo_caso, estado)
     VALUES ($1, $2, ST_GeomFromText($3, 4326), $4, $5, $6, $7, $8, $9, 0)
     RETURNING id, ultimo_caso`,
    [
      origemSolicitacaoId, tipo, origemGps, dataCaptura,
      descricaoOrigem || null, observacoes || null, risco || null,
      referenciaImagem || null, ultimoCaso
    ]
  );
  return resultado.rows[0];
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