# Diagramas C4 do BichoHub

Arquivo: `bichohub-c4.dsl` (Structurizr DSL)

## Como visualizar

1. Acesse https://playground.structurizr.com/
2. Copie o conteúdo de `bichohub-c4.dsl`
3. Cole no editor à esquerda
4. Os diagramas aparecerão automaticamente à direita

## Diagramas disponíveis

| Nome | Chave | Nível | Descrição |
|------|-------|-------|-----------|
| Contexto | `Contexto` | 1 | Sistema, atores e sistemas externos |
| Containers | `Containers` | 2 | App Android, Servidor, Bot WhatsApp, ML Pipeline |
| Componentes - Servidor | `Componentes-Servidor` | 3 | Rotas, Middlewares, Serviços, Data Access |
| Componentes - Android | `Componentes-Android` | 3 | Telas, API Layer, Tema, ViewModels |
| Componentes - ML | `Componentes-ML` | 3 | Pipeline em cascata (Geral -> Especialistas) |