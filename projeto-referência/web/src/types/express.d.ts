import 'express';

declare module 'express' {
    interface Request {
        token?: string;
    }
}
