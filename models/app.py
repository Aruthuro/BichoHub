from fastapi import FastAPI, File, UploadFile, HTTPException, Request
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware
import cv2
import numpy as np
from PIL import Image
import io
import torch
from ultralytics import YOLO
import uvicorn
from typing import List, Dict, Any
import logging

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI()

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:3000", "http://localhost:5000", "http://backend:5000"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class AnimalClassifier:
    def __init__(self):
        # Carregar modelo geral (classificação)
        logger.info("Carregando modelo geral...")
        self.modelo_geral = YOLO("runs/classify/Triagem/Geral/weights/best.pt")
        
        # Carregar modelos especialistas (classificação)
        logger.info("Carregando modelo especialista para aranhas...")
        self.modelo_aranhas = YOLO("runs/classify/Triagem/Especialista_Aranhas/weights/best.pt")
        
        logger.info("Carregando modelo especialista para cobras...")
        self.modelo_cobras = YOLO("runs/classify/Triagem/Especialista_Cobras/weights/best.pt")
        
        # Classes que precisam de especialista (em minúsculas para comparação)
        self.classes_especialistas = {
            "aranha": "especialista_aranhas",
            "cobra": "especialista_cobras",
            "Aranha": "especialista_aranhas",
            "Cobra": "especialista_cobras",
        }
        
        # Mapeamento de nomes
        self.modelos_especialistas = {
            "especialista_aranhas": self.modelo_aranhas,
            "especialista_cobras": self.modelo_cobras,
        }
        
        logger.info("Modelos carregados com sucesso!")
    
    def processar_imagem(self, imagem_bytes: bytes) -> Image.Image:
        """Converte bytes para PIL Image"""
        return Image.open(io.BytesIO(imagem_bytes))
    
    def classificar_geral(self, imagem: Image.Image) -> Dict[str, Any]:
        """Classificação com modelo geral (sem bounding boxes)"""
        results = self.modelo_geral(imagem)
        
        # Para modelo de classificação, os resultados estão em result.probs
        result = results[0]  # Primeira imagem
        
        # Pega as top predictions
        top1_idx = result.probs.top1
        top1_conf = float(result.probs.top1conf)
        top1_class = result.names[top1_idx]
        
        # Pega top 5 classes
        top5_indices = result.probs.top5
        top5_confs = [float(result.probs.top5conf[i]) for i in range(len(top5_indices))]
        top5_classes = [result.names[idx] for idx in top5_indices]
        
        return {
            "classificacao": top1_class,
            "confidence": top1_conf,
            "top5": [
                {"classe": cls, "confidence": conf}
                for cls, conf in zip(top5_classes, top5_confs)
            ]
        }
    
    def classificar_especialista(self, imagem: Image.Image, modelo: YOLO) -> Dict[str, Any]:
        """Classificação com modelo especialista"""
        results = modelo(imagem)
        result = results[0]
        
        top1_idx = result.probs.top1
        top1_conf = float(result.probs.top1conf)
        top1_class = result.names[top1_idx]
        
        return {
            "classificacao": top1_class,
            "confidence": top1_conf
        }
    
    def classificar_animal(self, imagem_bytes: bytes) -> Dict[str, Any]:
        """Pipeline completo de classificação"""
        
        # Processar imagem
        imagem = self.processar_imagem(imagem_bytes)
        
        # Passo 1: Classificação geral
        resultado_geral = self.classificar_geral(imagem)
        classe_geral = resultado_geral["classificacao"]
        confidence_geral = resultado_geral["confidence"]
        
        pipeline_info = [f"geral: {classe_geral} (confiança: {confidence_geral:.2f})"]
        
        # Passo 2: Verifica se precisa de especialista (case insensitive)
        classe_lower = classe_geral.lower()
        if classe_lower in self.classes_especialistas:
            logger.info(f"Classe '{classe_geral}' detectada, usando modelo especialista...")
            
            # Passo 3: Classificação especialista
            modelo_key = self.classes_especialistas[classe_lower]
            modelo_especialista = self.modelos_especialistas[modelo_key]
            resultado_especialista = self.classificar_especialista(imagem, modelo_especialista)
            
            pipeline_info.append(f"especialista: {resultado_especialista['classificacao']} (confiança: {resultado_especialista['confidence']:.2f})")
            
            return {
                "success": True,
                "classificacao_final": resultado_especialista["classificacao"],
                "confidence_final": resultado_especialista["confidence"],
                "classificacao_geral": resultado_geral,
                "usou_especialista": True,
                "pipeline": pipeline_info,
                "classe_original": classe_geral
            }
        
        # Passo 4: Classificação final do modelo geral
        pipeline_info.append(f"classificação final: {classe_geral}")
        
        return {
            "success": True,
            "classificacao_final": classe_geral,
            "confidence_final": confidence_geral,
            "classificacao_geral": resultado_geral,
            "usou_especialista": False,
            "pipeline": pipeline_info,
            "classe_original": classe_geral
        }

# Instanciar classificador
classificador = AnimalClassifier()

@app.post("/detect")
async def detect_animal(file: UploadFile = File(...)):
    """Endpoint principal para classificação de animais"""
    try:
        contents = await file.read()
        resultado = classificador.classificar_animal(contents)
        return JSONResponse(content=resultado)
        
    except Exception as e:
        logger.error(f"Erro na detecção: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/detect/base64")
async def detect_animal_base64(request: Request):
    """Endpoint que recebe imagem em base64"""
    try:
        import base64
        
        body = await request.json()
        image_data = base64.b64decode(body['imagem'])
        resultado = classificador.classificar_animal(image_data)
        
        return JSONResponse(content=resultado)
        
    except Exception as e:
        logger.error(f"Erro na detecção: {str(e)}")
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/health")
async def health():
    return {
        "status": "ok",
        "modelos_carregados": True,
        "tipos_modelo": {
            "geral": "classificacao",
            "aranhas": "classificacao",
            "cobras": "classificacao"
        }
    }

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8001)