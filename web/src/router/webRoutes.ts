import { Router } from "express";
import ocorrenciaController from "../controllers/ocorrencia.js";
import authController from "../controllers/authController.js";
import { requireToken } from "../middleware/auth.js";

const webRouter = Router();

webRouter.get("/", requireToken, (_req, res) => res.redirect("/ocorrencias/abertas"));

webRouter.all("/login", authController.login);
webRouter.all("/register", authController.signin);
webRouter.get("/logout", authController.logout);

webRouter.get("/ocorrencias/abertas", requireToken, ocorrenciaController.listarAbertas);
webRouter.get("/ocorrencias/listar", requireToken, ocorrenciaController.listarMinhas);
webRouter.get("/ocorrencias/:id", requireToken, ocorrenciaController.detalhes);

webRouter.post("/ocorrencias/responder/:id", requireToken, ocorrenciaController.responder);
webRouter.post("/ocorrencias/editar/:id", requireToken, ocorrenciaController.editar);
webRouter.post("/ocorrencias/encerrar/:id", requireToken, ocorrenciaController.encerrar);

export default webRouter;
