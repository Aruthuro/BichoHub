import 'express';

declare module 'express' {
    interface Request {
        token?: string;
        nome?: string;
        email?: string;
        senha?: string;
    }
}

declare module 'express-session' {
    interface SessionData {
	uid: string;
        token?: string;
        logado?: boolean;
        eh_admin?: boolean;
        eh_coletor?: boolean;
        pending?: any;
    }
}
