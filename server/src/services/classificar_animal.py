import json
import sys
import os
import logging
logging.getLogger("ultralytics").setLevel(logging.ERROR)
from ultralytics import YOLO

MODELS_DIR = os.environ.get("YOLO_MODELS_DIR") or os.path.join(
    os.path.dirname(__file__), "..", "..", "..", "models", "runs", "classify", "Triagem"
)

CLASSES_GERAIS = [
    "Aranha", "Calango-de-Pedra", "Cobra", "Cutia-de-Crista",
    "Iguana-Verde", "Jacaretinga", "Macaco-de-Cheiro",
    "Parauacu-de-Cara-Branca", "Preguica-de-Bentinho", "Sapo-Cururu"
]

CLASSES_ARANHAS = ["Aranha-Marrom", "Armadeira", "Viuva-Negra"]
CLASSES_COBRAS = ["Cascavel", "Jararaca", "Sucuri", "Jiboia", "Cobra-Coral"]

def carregar_modelo(caminho):
    if os.path.exists(caminho):
        return YOLO(caminho, verbose=False)
    return None

def classificar(imagem_path):
    if not os.path.exists(imagem_path):
        return {"erro": "Imagem nao encontrada"}

    modelo_geral = carregar_modelo(os.path.join(MODELS_DIR, "Geral", "weights", "best.pt"))
    if modelo_geral is None:
        return {"erro": "Modelo geral nao encontrado"}

    resultados_geral = modelo_geral(imagem_path)
    top1_idx = int(resultados_geral[0].probs.top1)
    top1_conf = float(resultados_geral[0].probs.top1conf)
    classe_geral = CLASSES_GERAIS[top1_idx]

    resultado = {
        "classe_geral": classe_geral,
        "confianca": round(top1_conf, 4),
        "especie": None,
        "confianca_especie": None
    }

    if classe_geral == "Aranha":
        especialista = carregar_modelo(os.path.join(MODELS_DIR, "Especialista_Aranhas", "weights", "best.pt"))
        if especialista:
            res_esp = especialista(imagem_path)
            esp_idx = int(res_esp[0].probs.top1)
            esp_conf = float(res_esp[0].probs.top1conf)
            resultado["especie"] = CLASSES_ARANHAS[esp_idx] if esp_idx < len(CLASSES_ARANHAS) else f"Desconhecida({esp_idx})"
            resultado["confianca_especie"] = round(esp_conf, 4)

    elif classe_geral == "Cobra":
        especialista = carregar_modelo(os.path.join(MODELS_DIR, "Especialista_Cobras", "weights", "best.pt"))
        if especialista:
            res_esp = especialista(imagem_path)
            esp_idx = int(res_esp[0].probs.top1)
            esp_conf = float(res_esp[0].probs.top1conf)
            resultado["especie"] = CLASSES_COBRAS[esp_idx] if esp_idx < len(CLASSES_COBRAS) else f"Desconhecida({esp_idx})"
            resultado["confianca_especie"] = round(esp_conf, 4)

    return resultado

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print(json.dumps({"erro": "Uso: python classificar_animal.py <caminho_imagem>"}))
        sys.exit(1)

    imagem = sys.argv[1]
    resultado = classificar(imagem)
    print(json.dumps(resultado))
