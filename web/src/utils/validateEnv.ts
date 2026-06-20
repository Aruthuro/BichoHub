import { cleanEnv, port } from 'envalid';

const validateEnv = () => {
    cleanEnv(process.env, {
        PORT: port(),
    });
};

export default validateEnv;