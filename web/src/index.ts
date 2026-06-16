import express, { type Request, type Response, type NextFunction } from 'express';
import path from 'path';
import { engine } from 'express-handlebars';
import cookieParser from 'cookie-parser';
import dotenv from 'dotenv';
import webRouter from './router/webRoutes.js';

dotenv.config();

const app = express();

// Configuração do Handlebars
app.engine('handlebars', engine({
    defaultLayout: 'main',
    layoutsDir: path.join(process.cwd(), 'src', 'views', 'layouts'),
    partialsDir: path.join(process.cwd(), 'src', 'views', 'partials'),
    helpers: {
        eq: (a: any, b: any) => a === b,
        or: (a: any, b: any) => a || b,
        not: (a: any) => !a
    }
}));
app.set('view engine', 'handlebars');
app.set('views', path.join(process.cwd(), 'src', 'views'));

app.use(express.json());
app.use(express.urlencoded({ extended: true }));
app.use(cookieParser());

app.use((req: Request, res: Response, next: NextFunction) => {
    res.locals.logado = !!req.cookies?.token;
    next();
});

app.use(express.static(path.join(process.cwd(), 'public')));

// Rotas Web
app.use('/', webRouter);

// Inicia o servidor
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
    console.log(`Servidor rodando em http://localhost:${PORT}`);
});
