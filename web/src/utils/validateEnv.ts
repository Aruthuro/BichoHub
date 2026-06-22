import { cleanEnv, port, str } from 'envalid';

const validateEnv = () => {
    cleanEnv(process.env, {
        PORT: port(),
        API_URL: str(),
        SESSION_SECRET: str(),
        CLIENT_ID: str()
    });
};

export default validateEnv;