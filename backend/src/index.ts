import express from "express";
import { client } from "./BancoDeDados.js";

const app = express();

app.use(express.json());

async function IniciarServidor() {
  await client.connect();

  console.log("Banco conectado");

  app.get("/teste", (req, res) => {
    res.json({
      mensagem: "Servidor funcionando"
    });
  });

  app.post("/usuarios", async (req, res) => {
    const { nome } = req.body;

    const resultado = await client.query(
      "INSERT INTO usuarios(nome) VALUES($1) RETURNING *",
      [nome]
    );

    res.json(resultado.rows[0]);
  });

  app.get("/usuarios", async (req, res) => {
    const resultado = await client.query(
      "SELECT * FROM usuarios"
    );

    res.json(resultado.rows);
  });

  app.listen(3000, () => {
    console.log("Servidor rodando na porta 3000");
  });
}

IniciarServidor();