import ww from "whatsapp-web.js";
import qrcode from "qrcode-terminal";
import path from "path";
import fs from "fs";
import os from "os";

const { Client, LocalAuth } = ww;
type Message = ww.Message;

let botPronto = false;

interface SessaoWhatsApp {
  estado:
    | "menu"
    | "aguardando_ocorrencia_tipo"
    | "aguardando_ocorrencia_imagem"
    | "aguardando_ocorrencia_descricao"
    | "ocorrencia_confirmacao";
  dados?: {
    tipo: number;
    gps?: string;
    descricao?: string;
    classificacao?: string;
  };
}

const sessoes = new Map<string, SessaoWhatsApp>();
let contadorMock = 42;

const client = new Client({
  authStrategy: new LocalAuth({ clientId: "bichohub" }),
  puppeteer: { headless: true, args: ["--no-sandbox"] }
});

client.on("qr", (qr) => {
  qrcode.generate(qr, { small: true });
  console.log("Escaneie o QR code acima com o WhatsApp");
});

client.on("ready", () => {
  console.log('WhatsApp Bot conectado! Para ativar: .\\run.ps1 bot-on');
});

function classificarImagemSilencioso(caminho: string): Promise<string | null> {
  return (async () => {
    try {
      const { classificarImagem } = await import("./classificadorService.js");
      const resultado = classificarImagem(caminho);
      if (resultado.erro) return null;
      let texto = resultado.classe_geral || "Desconhecido";
      if (resultado.especie) texto += ` (${resultado.especie})`;
      return texto;
    } catch {
      return null;
    }
  })();
}

async function processarImagem(message: Message, sessao: SessaoWhatsApp): Promise<string> {
  try {
    const media = await message.downloadMedia();
    if (!media || !media.data) {
      sessao.estado = "aguardando_ocorrencia_descricao";
      return "Não foi possível receber a imagem. Descreva a situação do animal:";
    }
    const ext = media.mimetype?.split("/")[1] || "jpg";
    const tmpFile = path.join(os.tmpdir(), `whatsapp_bot_${Date.now()}.${ext}`);
    fs.writeFileSync(tmpFile, Buffer.from(media.data, "base64"));
    const classificacao = await classificarImagemSilencioso(tmpFile);
    try { fs.unlinkSync(tmpFile); } catch {}
    if (classificacao) {
      sessao.dados!.classificacao = classificacao;
    }
    sessao.estado = "aguardando_ocorrencia_descricao";
    const msg = "Imagem recebida";
    return classificacao
      ? `${msg}! Classificação: ${classificacao}\n\nAgora descreva a situação do animal:`
      : `${msg}. Agora descreva a situação do animal:`;
  } catch {
    sessao.estado = "aguardando_ocorrencia_descricao";
    return "Erro ao processar imagem. Descreva a situação do animal:";
  }
}

export async function processarMensagem(numero: string, texto: string): Promise<string> {
  const sessao = sessoes.get(numero);

  if (!sessao) {
    sessoes.set(numero, { estado: "menu" });
    return "Escolha uma opção:\n1 - Nova ocorrência\n\nDigite o número:";
  }

  switch (sessao.estado) {
    case "menu": {
      if (texto === "1") {
        sessao.estado = "aguardando_ocorrencia_tipo";
        return "Qual o tipo da ocorrência?\n1 - Coleta\n2 - Resgate\n3 - Condução\n\nDigite o número:";
      }
      return "Opção inválida.\n1 - Nova ocorrência";
    }
    case "aguardando_ocorrencia_tipo": {
      const tipo = parseInt(texto);
      if (![1, 2, 3].includes(tipo)) {
        return "Opção inválida. Digite 1 (Coleta), 2 (Resgate) ou 3 (Condução):";
      }
      sessao.dados = { tipo };
      sessao.estado = "aguardando_ocorrencia_imagem";
      return "Envie uma foto do animal (ou digite 'pular' para seguir sem foto):";
    }
    case "aguardando_ocorrencia_imagem": {
      if (texto.toLowerCase() === "pular") {
        sessao.estado = "aguardando_ocorrencia_descricao";
        return "Descreva brevemente a situação do animal:";
      }
      return "Envie uma foto do animal ou digite 'pular' para seguir sem foto:";
    }
    case "aguardando_ocorrencia_descricao": {
      sessao.dados!.descricao = texto;
      sessao.estado = "ocorrencia_confirmacao";
      const tipoTexto = ["", "Coleta", "Resgate", "Condução"][sessao.dados!.tipo];
      let msg = `Confirme os dados:\n\nTipo: ${tipoTexto}\nDescrição: ${texto}`;
      if (sessao.dados!.gps) msg += `\nGPS: ${sessao.dados!.gps}`;
      if (sessao.dados!.classificacao) msg += `\nClassificação: ${sessao.dados!.classificacao}`;
      msg += "\n\nDigite SIM para confirmar ou NÃO para cancelar:";
      return msg;
    }
    case "ocorrencia_confirmacao": {
      sessoes.delete(numero);
      if (texto.toUpperCase() !== "SIM") {
        return "Ocorrência cancelada. Envie qualquer mensagem para voltar ao menu.";
      }
      contadorMock++;
      let msg = `Ocorrência #${contadorMock} registrada com sucesso!`;
      if (sessao.dados?.classificacao) {
        msg += `\nAnimal classificado como: ${sessao.dados.classificacao}`;
      }
      msg += "\n\nEnvie qualquer mensagem para voltar ao menu.";
      return msg;
    }
    default: {
      sessoes.delete(numero);
      return "Envie qualquer mensagem para voltar ao menu.";
    }
  }
}

client.on("message", async (message: Message) => {
  if (!botPronto) return;
  if (message.fromMe) return;
  if (message.from.includes("@g.us")) return;
  if (message.from.includes("@broadcast")) return;

  const numero = message.from.replace(/@(c\.us|s\.whatsapp\.net)/, "");
  const sessao = sessoes.get(numero);

  if (sessao && sessao.estado === "aguardando_ocorrencia_imagem" && message.hasMedia) {
    try {
      const resposta = await processarImagem(message, sessao);
      await client.sendMessage(message.from, resposta);
    } catch (erro) {
      console.error("Erro processando imagem:", erro);
      await client.sendMessage(message.from, "Erro ao processar imagem. Tente novamente.");
    }
    return;
  }

  const texto = message.body.trim();
  try {
    const resposta = await processarMensagem(numero, texto);
    await client.sendMessage(message.from, resposta);
  } catch (erro) {
    console.error("Erro no WhatsApp:", erro);
    await client.sendMessage(message.from, "Ocorreu um erro interno. Tente novamente.");
  }
});

export function ativarBot() {
  botPronto = true;
  console.log("WhatsApp Bot ativado!");
}

export function desativarBot() {
  botPronto = false;
  console.log("WhatsApp Bot desativado.");
}

export function iniciarWhatsAppBot() {
  client.initialize();
}
