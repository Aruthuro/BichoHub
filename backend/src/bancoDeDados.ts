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
  user: env.BD_USER,
  host: env.BD_HOST,
  password: env.BD_PASSWORD,
  port: env.BD_PORT,
  database: env.BD_DATABASE
});
/*
  funcao generica de insercao no banco de dados
  por enquanto vou considerar um tipo genérico que vou inventar em somente uma tabela
*/
async function adicionaBancoDeDados(nome:string) {

  await client.query(
    `
    INSERT INTO comentarios(nome)
    VALUES($1)
    `,
    [nome]
  );

  console.log("Usuário inserido");
}

/*
  funcao generica de remocao no banco de dados
*/
async function removeBancoDeDados(nome: string) {

  await client.query(
    `
    DELETE FROM comentarios
    WHERE nome = $1
    `,
    [nome]
  );

  console.log("Usuário removido");
}

/*
  funcao generica de busca no banco de dados
*/
async function buscaBancoDeDados(nome: string) {

  const result = await client.query(
    `
    SELECT * FROM comentarios
    WHERE nome = $1
    `,
    [nome]
  );

  console.log(result.rows);

  return result.rows;
}