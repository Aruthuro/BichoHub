import type { NextFunction, Request, Response } from "express";
import yoloService from "../services/yoloServices.js";

// tipos para identificação do animal
interface Detection {
  class_name: string;
  confidence: number;
  bbox: number[];
  class_id: number;
}

interface AnimalIdentificado {
  especie: string;
  confidence: number;
  todasDetections: Detection[];
  imagemBuffer: Buffer;
  imagemBase64: string;
  pipeline: string[] | undefined; // Adicionar pipeline para debug
}

export interface RequestComAnimal extends Request {
  animalIdentificado?: AnimalIdentificado;
}


async function identificarAnimal(req: RequestComAnimal, res: Response, next: NextFunction) {
  try {
    if (!req.body || Object.keys(req.body).length === 0) {
      console.log("Requisição veio com o body vazio ou null");
      return res.status(400).json({ 
        erro: "Body da requisição não pode estar vazio",
        mensagem: "Envie os dados no corpo da requisição em formato JSON"
      });
    }
    
    const base64String = req.body.imagem;
    if (!base64String) {
      return res.status(400).json({ error: 'Imagem não fornecida' });
    }

    const base64Data = base64String.replace(/^data:image\/\w+;base64,/, '');
    const buffer = Buffer.from(base64Data, 'base64');
    
    const resultado = await yoloService.classificarAnimalBase64(base64String);
    
    // Verificar se a classificação foi bem sucedida
    if (!resultado.success || !resultado.classificacao_final) {
      return res.status(404).json({ 
        error: 'Nenhum animal identificado',
        detalhes: resultado.detalhes || 'Não foi possível identificar o animal na imagem'
      });
    }
    
    // Converter o resultado do serviço para o formato esperado pelo middleware
    const detection: Detection = {
      class_name: resultado.classificacao_final,
      confidence: resultado.confidence_final || 0,
      bbox: resultado.deteccao_geral?.bbox || [0, 0, 0, 0],
      class_id: resultado.deteccao_geral?.class_id || 0
    };
    
    req.animalIdentificado = {
      especie: resultado.classificacao_final,
      confidence: resultado.confidence_final || 0,
      todasDetections: [detection], // Pode expandir para múltiplas detecções depois
      imagemBuffer: buffer,
      imagemBase64: base64String,
      pipeline: resultado.pipeline // Para debug
    };
    
    // Log do pipeline de classificação
    if (resultado.pipeline) {
      console.log('Pipeline de classificação:', resultado.pipeline.join(' -> '));
    }
    console.log(`Animal identificado: ${resultado.classificacao_final} (${(resultado.confidence_final || 0) * 100}% confiança)`);
    
    next();
  } catch (error) {
    console.error('Erro no middleware de identificação:', error);
    next(error);
  }
}

export { identificarAnimal };