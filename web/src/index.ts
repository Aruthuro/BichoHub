import express, { type Request, type Response, type NextFunction } from 'express';
import morgan from "morgan";
import cookieParser from 'cookie-parser';
import dotenv from 'dotenv';
import webRouter from './router/webRoutes.js';
import session from "express-session";
import { engine } from 'express-handlebars';
import { v4 as uuidv4 } from "uuid";
import validateEnv from './utils/validateEnv.js';
import { erroFaltando, erroServidor } from './middleware/erros.js';

dotenv.config();
validateEnv();

const app = express();
const PORT = process.env.PORT || 3000;
export const publicPath = `${process.cwd()}/public`;

app.use(morgan("short"));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());
app.use(session({
	name: "sid",
	genid: () => uuidv4(),
	secret: process.env.SESSION_SECRET!,
	resave: false,
	saveUninitialized: false,
	rolling: true,
	cookie: {
		httpOnly: true,
        sameSite: "lax",
		maxAge: 24 * 60 * 60 * 1000
	}
}));
app.use((req: Request, res: Response, next: NextFunction) => {
    res.locals.logado = !!req.session?.logado;
    res.locals.eh_admin = !!req.session?.eh_admin;
    next();
});

app.engine('handlebars', engine({
    defaultLayout: 'main',
    layoutsDir: "src/views/layouts",
    helpers: {
        eq: (a: any, b: any) => a === b,
        or: (...args: any[]) => args.slice(0, -1).some(Boolean)
    }
}));
app.set("view engine", "handlebars");
app.set("views", "src/views");

app.use('/js', express.static(`${publicPath}/js`));
app.use('/css', express.static(`${publicPath}/css`));
app.use('/img', express.static(`${publicPath}/img`));

app.use('/', webRouter);
app.use("/sobre", (_req, res) => {
    res.render("sobre", {
        heading: "Sobre o BichoHub",
        description: "O BichoHub é um serviço para o registro de fauna silvestre em espaços urbanos.",
        heading2: "Sobre este site",
        description2: "O intúito é permitir a fácil vizualização desses registros."
    });
});
app.use(erroFaltando);
app.use(erroServidor);

app.listen(PORT, () => {
    console.log(`Servidor rodando em http://localhost:${PORT}`);
});
