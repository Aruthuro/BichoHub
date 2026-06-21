# EP09 - Inteligência e Identificação de Animais

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Adicionar recursos inteligentes de apoio ao usuário, incluindo classificação de espécies por imagem.

---

## Status atual

- [x] **Triagem digital por imagem:** Pipeline YOLO implementado e funcional
  - [x] Modelo Geral (10 classes) com 90.6% top-1 accuracy;
  - [x] Especialista Aranhas (3 espécies) com 92.9% top-1 accuracy;
  - [x] Especialista Cobras (5 espécies) com 100% top-1 accuracy;
  - [x] Endpoint `POST /api/modelo/classificar` para classificação;
  - [x] Integração no Bot WhatsApp (classifica foto durante fluxo);
  - [x] Docker com Python + PyTorch + Ultralytics.
- [ ] **Inferência offline:** Nada implementado.

---

# E01 - Correção de Mismatch de Classes nos Modelos Especialistas

## Área

ML

## Objetivo

Corrigir divergência entre classes treinadas e classes mapeadas em produção.

## Problemas identificados

1. **Especialista Aranhas:** Treinado com 5 espécies, mas `classificar_animal.py` mapeia apenas 3 (índices 0-2). Índices 3 e 4 retornam `"Desconhecida(N)"`.
2. **Especialista Cobras:** Treinado com apenas 2 espécies (Jiboia, Jararaca-do-Norte), mas produção espera 5 (Cascavel, Jararaca, Sucuri, Jiboia, Cobra-Coral). Três classes nunca são identificadas.

## Critérios Funcionais

- [ ] Alinhar classes de treino com classes de produção;
- [ ] Re-treinar modelo Especialista Cobras com dataset contendo as 5 espécies.

## Critérios Técnicos

- [ ] Atualizar `CLASSES_ARANHAS` e `CLASSES_COBRAS` em `classificar_animal.py`;
- [ ] Coletar/ampliar dataset para classes faltantes;
- [ ] Re-treinar e validar.

## Dependências

- Dataset ampliado de fauna

## Definition of Done

- [ ] Modelo especialista Aranhas retorna todas as 5 espécies corretamente;
- [ ] Modelo especialista Cobras retorna as 5 espécies corretamente.

---

# E02 - Servidor de Inferência Persistente

## Área

ML / Back-end

## Objetivo

Substituir chamada `execSync` por servidor de inferência persistente para eliminar cold start.

## Problema

Atualmente cada classificação:
- spawna um processo Python;
- carrega 3 modelos YOLO do disco (~10MB);
- executa inferência;
- finaliza o processo.

Isso adiciona latência significativa e bloqueia o event loop do Node.js.

## Critérios Técnicos

- [ ] Criar servidor Python persistente (FastAPI ou similar);
- [ ] Manter modelos carregados em memória;
- [ ] Substituir `execSync` por requisição HTTP ou IPC.

## Artefatos

- [ ] Servidor Python de inferência;
- [ ] Docker multi-stage ou serviço separado;
- [ ] Atualização do `classificadorService.ts`.

## Definition of Done

- [ ] Inferência sem cold start; resposta em < 2s.

---

# E03 - Inferência Offline no Dispositivo

## Área

ML / Front-end

## Objetivo

Permitir classificação de animais diretamente no dispositivo Android sem conexão com o servidor.

## Critérios Funcionais

- [ ] Modelo YOLO convertido para TensorFlow Lite ou ONNX Mobile;
- [ ] Classificação básica (Geral) disponível offline;
- [ ] Fallback para servidor quando online.

## Artefatos

- [ ] Modelo convertido para TFLite;
- [ ] Módulo de inferência no app Android.

## Definition of Done

- [ ] Classificação funcionando offline no dispositivo.
