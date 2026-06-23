# EP11 - Qualidade de Software

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Garantir estabilidade, qualidade e conformidade do sistema com os requisitos não-funcionais.

---

# E01 - Testes de Integração da API

## Área

Back-end

## Objetivo

Criar testes de integração para os principais fluxos da API.

## Critérios Funcionais

- [ ] Teste de cadastro de usuário;
- [ ] Teste de login e geração de JWT;
- [ ] Teste de criação de ocorrência;
- [ ] Teste de ciclo completo (abrir -> aceitar -> editar -> encerrar);
- [ ] Teste de autorização (rotas protegidas sem token);
- [ ] Teste de tempo de resposta (NF01 — resposta em < 20000ms).

## Critérios Técnicos

- [ ] Usar Jest ou Vitest com Supertest;
- [ ] Banco de dados de teste (isolado);
- [ ] CI/CD rodando testes automaticamente.

## Artefatos

- [ ] Arquivos de teste em `server/tests/`;
- [ ] Script `npm test`;
- [ ] Configuração de CI.

## Definition of Done

- [ ] Testes passando no CI.

---

# E02 - Testes Unitários do App Android

## Área

Front-end

## Objetivo

Adicionar testes para validação de inputs e fluxos de UI.

## Critérios Funcionais

- [ ] Testes dos ViewModels (NomeViewModel, EmailViewModel);
- [ ] Testes de navegação (NavHost);
- [ ] Testes de UI dos formulários.

## Critérios Técnicos

- [ ] Usar JUnit + Compose UI Test;
- [ ] Testes rodando no CI.

## Definition of Done

- [ ] Testes do app passando.

---

# E03 - Pipeline CI/CD

## Área

Infra

## Objetivo

Configurar pipeline de integração e deploy contínuo.

## Critérios Funcionais

- [ ] CI rodando testes a cada push;
- [ ] Build da imagem Docker;
- [ ] Deploy automático em staging.

## Critérios Técnicos

- [ ] GitHub Actions ou similar;
- [ ] Docker build + push.

## Definition of Done

- [ ] CI/CD funcional.

---

# E04 - Lint e Formatação de Código

## Área

Full-stack

## Objetivo

Padronizar estilo de código em todo o projeto.

## Critérios Técnicos

- [ ] ESLint + Prettier para o servidor;
- [ ] ktlint para o app Android;
- [ ] Rodar no CI.

## Definition of Done

- [ ] Lint configurado e passando.
