# Modelo YOLO (Classificação de Animais)

## Visão Geral

Sistema de classificação de animais silvestres baseado em YOLOv8 (Ultralytics). Utiliza uma arquitetura em cascata (pipeline) com um modelo geral e dois especialistas para identificação de fauna brasileira a partir de imagens.

## Stack Tecnológica

| Categoria | Tecnologia |
|---|---|
| Linguagem | Python 3 |
| Deep Learning | PyTorch 2.6 + CUDA 12.4 / CPU |
| ML Library | Ultralytics YOLO 8.4.60 |
| Modelo | YOLOv8n-cls (classificação, nano) |
| API Server | FastAPI 0.104, Uvicorn 0.24, python-multipart |
| Image Processing | OpenCV 4.13, Pillow 12.2, NumPy |
| Treinamento | Jupyter Notebook (ajustefino.ipynb) |

## Arquitetura do Pipeline

```
                    ┌──────────────┐
                    │   Imagem     │
                    │   (input)    │
                    └──────┬───────┘
                           │
                    ┌──────▼───────┐
                    │  Modelo      │
                    │  Geral       │
                    │ (10 classes) │
                    └──────┬───────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
         ┌────▼────┐ ┌────▼────┐ ┌────▼────┐
         │ Aranha  │ │  Cobra  │ │ Outras  │
         │         │ │         │ │ (8 cls) │
         └────┬────┘ └────┬────┘ └────┬────┘
              │            │           │
    ┌─────────▼──┐  ┌─────▼────────┐  │
    │ Especialista│  │ Especialista │  │
    │ Aranhas    │  │ Cobras       │  │
    │ (3 spp.)   │  │ (5 spp.)     │  │
    └─────────┬──┘  └─────┬────────┘  │
              │            │           │
              └────────────┼───────────┘
                           │
                    ┌──────▼───────┐
                    │ Classificação│
                    │ Final        │
                    └──────────────┘
```

### Classes do Modelo Geral (10)
`Aranha`, `Calango-de-Pedra`, `Cobra`, `Cutia-de-Crista`, `Iguana-Verde`, `Jacaretinga`, `Macaco-de-Cheiro`, `Parauacu-de-Cara-Branca`, `Preguica-de-Bentinho`, `Sapo-Cururu`

### Classes do Especialista Aranhas (3)
`Aranha-Marrom`, `Armadeira`, `Viuva-Negra`

### Classes do Especialista Cobras (5)
`Cascavel`, `Jararaca`, `Sucuri`, `Jiboia`, `Cobra-Coral`

## Modelos Treinados

```
models/runs/classify/Triagem/
├── Geral/
│   └── weights/
│       ├── best.pt    # Modelo geral (10 classes)
│       └── last.pt    # Último checkpoint
├── Especialista_Aranhas/
│   └── weights/
│       ├── best.pt    # Especialista aranhas (3 classes)
│       └── last.pt
└── Especialista_Cobras/
    └── weights/
        ├── best.pt    # Especialista cobras (5 classes)
        └── last.pt
```

## Serviço FastAPI (`models/app.py`)

### Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/detect` | Upload de arquivo de imagem → classificação |
| POST | `/detect/base64` | Imagem em base64 no body JSON → classificação |
| GET | `/health` | Health check + status dos modelos |

### Exemplo de resposta (`/detect`)

```json
{
  "success": true,
  "classificacao_final": "Armadeira",
  "confidence_final": 0.92,
  "classificacao_geral": {
    "classificacao": "Aranha",
    "confidence": 0.95,
    "top5": [
      {"classe": "Aranha", "confidence": 0.95},
      {"classe": "Cobra", "confidence": 0.02},
      ...
    ]
  },
  "usou_especialista": true,
  "pipeline": [
    "geral: Aranha (confiança: 0.95)",
    "especialista: Armadeira (confiança: 0.92)"
  ],
  "classe_original": "Aranha"
}
```

## Integração com o Servidor

O servidor Node.js se conecta ao YOLO de duas formas:

1. **Subprocesso Python direto** (`classificadorService.ts` + `classificar_animal.py`):
   - Chama `python3 classificar_animal.py <caminho_imagem>`
   - Lê o JSON de saída do stdout
   - Usado pelo middleware `deteccaoAnimal.ts`

2. **Cliente HTTP** (`yoloServices.ts`):
   - Envia imagem via multipart (`/detect`) ou base64 (`/detect/base64`)
   - URL configurável via `YOLO_SERVICE_URL` (padrão `http://localhost:8001`)
   - Usado no fluxo de criação de ocorrências com classificação automática

## Treinamento (`ajustefino.ipynb`)

### Hiperparâmetros do Modelo Geral
- **Modelo**: YOLOv8n-cls (nano classification)
- **Épocas**: 100
- **Tamanho da imagem**: 224x224
- **Batch**: 16
- **Otimizador**: AdamW (lr=0.001)
- **Data augmentation agressiva**:
  - Rotação: ±30°
  - Brilho: ±0.2
  - Escala: ±0.3
  - Flip horizontal: 0.5
- **Regularização**:
  - Dropout: 0.2
  - RandAugment
  - Erasing: 0.4 (randerase)

### Hiperparâmetros dos Especialistas
- **Épocas**: 50
- **Learning rate**: 0.002 (mais conservador)
- **Flip vertical**: 0.5 (para simetria 3D)
- **Rotação**: 90° (para diferentes orientações)
- **Dataset pequeno**: algumas classes com 10-40 imagens

### Divisão dos dados
- Treino: 70%
- Validação: 20%
- Teste: 10%

## Docker

O diretório `yolo-services/Dockerfile` existe como placeholder vazio. Atualmente, a execução do YOLO é feita de duas maneiras:

1. **Diretamente no Docker do servidor**: o `server/Dockerfile` instala Python + PyTorch CPU + Ultralytics no mesmo container do Node.js
2. **Serviço independente**: `python models/app.py` para rodar o FastAPI separadamente na porta 8001

## Dependências

### GPU (`requirements.txt`)
- torch 2.6.0 + CUDA 12.4
- torchvision 0.21.0
- CUDA toolchain (nvidia-*)
- ultralytics 8.4.60
- fastapi 0.104.1, uvicorn 0.24.0

### CPU (`requirements-cpu.txt`)
- Versão mais leve sem CUDA
- Ideal para ambientes sem GPU (Render, Docker CPU-only)

## Formas de Execução

### Como serviço standalone
```bash
cd models
pip install -r requirements.txt
python app.py  # → http://localhost:8001
```

### Como subprocesso do servidor
```bash
cd server
python src/services/classificar_animal.py <caminho_imagem>
```

### Via Docker Compose (integrado ao backend)
- Modelos montados em `/usr/src/app/models/runs/classify/Triagem`
- Variável `YOLO_MODELS_DIR` define o diretório dos pesos
