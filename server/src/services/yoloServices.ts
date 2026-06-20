import axios from 'axios';
import FormData from 'form-data';
import { Buffer } from 'buffer';

interface Detection {
  class_name: string;
  confidence: number;
  bbox: number[];
  class_id: number;
}

interface ClassificacaoResultado {
  success: boolean;
  classificacao_final: string | null;
  confidence_final?: number;
  deteccao_geral?: Detection;
  deteccao_especialista?: Detection;
  pipeline?: string[];
  classe_original?: string;
  detalhes?: string;
}

class YOLOService {
  private readonly baseURL: string;
  
  constructor() {
    this.baseURL = process.env.YOLO_SERVICE_URL || 'http://localhost:8001';
  }
  
  async classificarAnimal(buffer: Buffer): Promise<ClassificacaoResultado> {
    try {
      const formData = new FormData();
      formData.append('file', buffer, {
        filename: 'image.jpg',
        contentType: 'image/jpeg'
      });
      
      const response = await axios.post(`${this.baseURL}/detect`, formData, {
        headers: {
          ...formData.getHeaders()
        },
        timeout: 30000 // 30 segundos
      });
      
      return response.data;
    } catch (error) {
      console.error('Erro ao chamar serviço YOLO:', error);
      return {
        success: false,
        classificacao_final: null,
        detalhes: 'Erro na comunicação com serviço de classificação'
      };
    }
  }
  
  async classificarAnimalBase64(base64String: string): Promise<ClassificacaoResultado> {
    try {
      // Remove o header se existir
      const base64Data = base64String.replace(/^data:image\/\w+;base64,/, '');
      
      const response = await axios.post(`${this.baseURL}/detect/base64`, {
        imagem: base64Data
      }, {
        timeout: 30000
      });
      
      return response.data;
    } catch (error) {
      console.error('Erro ao chamar serviço YOLO:', error);
      return {
        success: false,
        classificacao_final: null,
        detalhes: 'Erro na comunicação com serviço de classificação'
      };
    }
  }
}

export default new YOLOService();