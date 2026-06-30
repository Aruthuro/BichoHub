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
    const lines = output.trim().split("\n").filter(l => l.trim());
    const lastLine = lines[lines.length - 1];
    return JSON.parse(lastLine);
  } catch (erro: any) {
    return { erro: `Erro ao classificar imagem: ${erro.message}` };
  }
}
