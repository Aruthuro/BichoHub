import { cleanEnv, str, port } from "envalid";
import dotenv from "dotenv";

// configuracao do env com base na variavel escolhida (DEVELOPMENT ou PRODUCTION)
dotenv.config({path: `.env.${process.env.NODE_ENV}`});

export const env = cleanEnv(process.env, {

  PORT: port(),

  DIR_LOG: str({
    default: "logs"
  }),

  FORMAT_LOG: str({
    default: "simple"
  }),

  BD_USER: str(),
  BD_HOST: str(),
  BD_PASSWORD: str(),
  BD_PORT: port(),
  BD_DATABASE: str()
});