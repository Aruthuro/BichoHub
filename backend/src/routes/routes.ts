import { Router } from "express";

const router = Router();

/*
    rota do caso '/', caso genérico
*/
router.get("/", (req, res) => {
    res.send("Página genérica")
});

/*
    rota do caso '/usuarios', caso genérico
*/
router.get("/usuarios", (req, res) => {
    res.send("Criar usuarios");
})

export default router;