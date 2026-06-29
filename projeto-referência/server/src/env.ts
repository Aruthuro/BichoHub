import { cleanEnv, str, port } from "envalid";
import dotenv from "dotenv";

// configuracao do env com base na variavel escolhida (DEVELOPMENT ou PRODUCTION)
dotenv.config({
  path: `.env.${process.env.NODE_ENV || "development"}`
});

export const env = cleanEnv(process.env, {
  NODE_ENV: str(),
  PORT: port(),

  DIR_LOG: str({ default: "logs" }),
  FORMAT_LOG: str({ default: "simple" }),

  POSTGRES_USER: str(),
  POSTGRES_PASSWORD: str(),
  POSTGRES_DB: str(),
  POSTGRES_HOST: str(),
  POSTGRES_PORT: port(),

  JWT_SECRET: str()
})