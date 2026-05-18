import { Client } from "pg";
import dotenv from "dotenv"

dotenv.config({ path: `.env.${process.env.NODE_ENV}` })

// Conexão ao banco de dados, lembrando de atualizar o .env
export const client = new Client({
  user: process.env.BD_USER,
  host: process.env.BD_HOST,
  password: process.env.BD_PASSWORD,
  port: Number(process.env.BD_PORT),
  database: process.env.BD_DATABASE
});
