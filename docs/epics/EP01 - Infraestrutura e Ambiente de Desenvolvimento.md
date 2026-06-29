# EP01 - Infraestrutura e Ambiente de Desenvolvimento

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Estabelecer toda a base técnica necessária para desenvolvimento, execução, testes e deploy do sistema.

---

# E01 - Ambiente Front-end

## Área

Front-end

## Objetivo

Configurar o ambiente de desenvolvimento front-end (app Android).

## Critérios Funcionais

- [x] Configurações de desenvolvimento padronizadas e de fácil configuração;
- [x] Aplicativo base mostrando uma mensagem genérica;
- [x] Escolher o design do app.

## Artefatos

- [x] Template reutilizável.

## Definition of Done

- [x] Ambiente front-end configurado e funcional.

---

# E02 - Ambiente Back-end

## Área

Back-end

## Objetivo

Configurar o ambiente de desenvolvimento back-end com Docker e banco de dados.

## Critérios Funcionais

- [x] Ambiente Docker configurado;
- [x] Conexão com banco de dados PostgreSQL configurado;
- [x] Servidor com endpoint básico de teste funcionando;
- [x] Servidor capaz de armazenar e recuperar dados do banco de dados.

## Artefatos

- [x] Dockerfile e docker-compose.yaml;
- [x] Script de inicialização.

## Definition of Done

- [x] Back-end rodando em container Docker.

---

# E03 - Logs Estruturados

## Área

Back-end

## Objetivo

Implementar sistema de logs estruturados para monitoramento do servidor.

## Critérios Funcionais

- [x] Logs estruturados (logger.ts com `logs/geral.log` e `logs/erro.log`).

## Definition of Done

- [x] Sistema de logs implementado e funcional.

---
