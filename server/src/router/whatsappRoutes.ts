import { Router } from "express";

const router = Router();

router.post("/webhook", async (req, res) => {
  try {
    const { numero, mensagem } = req.body;

    if (!numero || !mensagem) {
      return res.status(400).json({ erro: "numero e mensagem sao obrigatorios" });
    }

    const { processarMensagem } = await import("../services/whatsappBot.js");
    const resposta = await processarMensagem(numero, mensagem);

    res.json({ resposta });
  } catch (erro: any) {
    console.error("Erro no webhook WhatsApp:", erro);
    res.status(500).json({ erro: erro.message });
  }
});

router.get("/status", (req, res) => {
  res.json({ status: "WhatsApp webhook ativo" });
});

router.post("/ativar", async (req, res) => {
  const { ativarBot } = await import("../services/whatsappBot.js");
  ativarBot();
  res.json({ mensagem: "Bot ativado" });
});

router.post("/desativar", async (req, res) => {
  const { desativarBot } = await import("../services/whatsappBot.js");
  desativarBot();
  res.json({ mensagem: "Bot desativado" });
});

export default router;
