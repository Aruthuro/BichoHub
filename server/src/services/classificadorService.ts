import { execSync } from "child_process";
import path from "path";
import { fileURLToPath } from "url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));

export interface ClassificacaoResultado {
  classe_geral?: string;
  confianca?: number;
  especie?: string | null;
  confianca_especie?: number | null;
  erro?: string;
}

export function classificarImagem(imagemPath: string): ClassificacaoResultado {
  const scriptPath = path.join(__dirname, "classificar_animal.py");

  try {
    const output = execSync(`python "${scriptPath}" "${imagemPath}"`, {
      encoding: "utf-8",
      timeout: 30000
    });
    return JSON.parse(output.trim());
  } catch (erro: any) {
    return { erro: `Erro ao classificar imagem: ${erro.message}` };
  }
}
