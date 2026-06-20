import { cleanEnv, str, port, url } from "envalid";
import dotenv from "dotenv";

// configuracao do env com base na variavel escolhida (DEVELOPMENT ou PRODUCTION)
dotenv.config({
  path: `.env.${process.env.NODE_ENV || "development"}`
});

export const env = cleanEnv(process.env, {
  NODE_ENV: str(),
  PORT: port(),
  PORT_YOLO: port(),

  DIR_LOG: str({ default: "logs" }),
  FORMAT_LOG: str({ default: "simple" }),

  POSTGRES_USER: str(),
  POSTGRES_PASSWORD: str(),
  POSTGRES_DB: str(),
  POSTGRES_HOST: str(),
  POSTGRES_PORT: port(),

  YOLO_SERVICE_URL: url({ 
    default: `http://localhost:${process.env.PORT_YOLO || 8001}` 
  }),

  JWT_SECRET: str() 
})