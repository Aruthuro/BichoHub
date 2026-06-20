import { Router } from "express";
import multer from "multer";
import path from "path";
import fs from "fs";
import { classificarImagem } from "../services/classificadorService.js";

const router = Router();

const uploadsDir = path.join(process.cwd(), "uploads");
if (!fs.existsSync(uploadsDir)) {
  fs.mkdirSync(uploadsDir, { recursive: true });
}

const upload = multer({ dest: uploadsDir });

router.post("/classificar", upload.single("imagem"), async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ erro: "Envie uma imagem no campo 'imagem'" });
    }

    const resultado = classificarImagem(req.file.path);

    fs.unlink(req.file.path, () => {});

    if (resultado.erro) {
      return res.status(500).json(resultado);
    }

    res.json(resultado);
  } catch (erro: any) {
    if (req.file) {
      fs.unlink(req.file.path, () => {});
    }
    res.status(500).json({ erro: erro.message });
  }
});

export default router;
