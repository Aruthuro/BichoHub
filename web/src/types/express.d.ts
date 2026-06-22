import 'express';

declare module 'express' {
    interface Request {
        token?: string;
    }
}

declare module 'express-session' {
    interface SessionData {
        token?: string;
        logado?: boolean;
        eh_admin?: boolean;
    }
}
