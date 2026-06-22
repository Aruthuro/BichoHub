import { Router } from "express";
import ocorrenciaController from "../controllers/ocorrencia.js";
import authController from "../controllers/authController.js";
import adminController from "../controllers/adminController.js";
import { requireToken, requireColetorOuAdmin, requireAdmin } from "../middleware/auth.js";

const webRouter = Router();

webRouter.get("/", requireToken, requireColetorOuAdmin, (_req, res) => res.redirect("/ocorrencias/abertas"));

webRouter.all("/auth/login", authController.login);
webRouter.all("/auth/signup", authController.signup);
webRouter.get("/auth/logout", authController.logout);
webRouter.post("/auth/google", authController.googleLogin);
webRouter.all("/auth/google/quasela", authController.askContato);

webRouter.get("/ocorrencias/mapa", requireToken, requireColetorOuAdmin, ocorrenciaController.mapa);

webRouter.get("/ocorrencias/abertas", requireToken, requireColetorOuAdmin, ocorrenciaController.listarAbertas);
webRouter.get("/ocorrencias/historico", requireToken, requireColetorOuAdmin, ocorrenciaController.listarMinhas);
webRouter.get("/ocorrencias/:id", requireToken, requireColetorOuAdmin, ocorrenciaController.detalhes);
webRouter.post("/ocorrencias/responder/:id", requireToken, requireColetorOuAdmin, ocorrenciaController.responder);
webRouter.post("/ocorrencias/editar/:id", requireToken, requireColetorOuAdmin, ocorrenciaController.editar);
webRouter.post("/ocorrencias/encerrar/:id", requireToken, requireColetorOuAdmin, ocorrenciaController.encerrar);

webRouter.get("/admin/dashboard", requireToken, requireAdmin, adminController.dashboard);
webRouter.get("/admin/usuarios", requireToken, requireAdmin, adminController.listarUsuarios);
webRouter.post("/admin/usuarios/:id/tornar-admin", requireToken, requireAdmin, adminController.promoverAdmin);
webRouter.post("/admin/usuarios/:id/tornar-coletor", requireToken, requireAdmin, adminController.promoverColetor);
webRouter.post("/admin/usuarios/:id/remover", requireToken, requireAdmin, adminController.remover);

export default webRouter;
