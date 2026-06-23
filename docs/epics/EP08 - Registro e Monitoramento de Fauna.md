# EP08 - Registro e Monitoramento de Fauna

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Permitir catalogação, análise e visualização de aparições de animais no campus da UFAM.

---

# E01 - Feed de Resgates Concluídos

## Área

Full-stack

## Objetivo

Criar feed de resgates concluídos recentemente para engajar o usuário (F02).

## Critérios Funcionais

- [ ] Exibir foto do animal resgatado;
- [ ] Exibir nome popular e nome científico do animal;
- [ ] Exibir local onde foi encontrado;
- [ ] Feed ordenado dos resgates mais recentes;
- [ ] Link para pesquisa no Google do nome do animal (F16).

## Critérios Técnicos

- [ ] Endpoint para listar ocorrências encerradas com dados do animal;
- [ ] Cache do feed para performance;
- [ ] Atualizar feed em tempo real quando novo resgate é concluído.

## Artefatos

- [ ] Endpoint REST para feed;
- [ ] Tela/componente de feed no app.

## Dependências

- EP03 - Modelagem e Persistência de Dados
- EP05 - Gerenciamento de Ocorrências

## Definition of Done

- [ ] Feed de resgates concluídos disponível no app.

---

# E02 - Mapa da UFAM com Aparições de Animais

## Área

Full-stack

## Objetivo

Exibir mapa do campus da UFAM com pontos de aparições recentes de animais (F03).

## Critérios Funcionais

- [ ] Mapa do campus da UFAM com pontos de aparições de animais;
- [ ] Pontos indicam espécie, data e local exato;
- [ ] Filtro por período e tipo de animal;
- [ ] Utilizar API do Google Maps (NF12).

## Critérios Técnicos

- [ ] PostGIS para consultas espaciais;
- [ ] Google Maps SDK no app Android;
- [ ] Clustering de pontos para performance.

## Dependências

- EP02 - Arquitetura Backend e API (E04 - Google Maps API)
- EP03 - Modelagem e Persistência de Dados (PostGIS)

## Definition of Done

- [ ] Mapa com aparições de animais funcional no app.

---

# E03 - Catalogação de Avistamentos e Banco de Espécies

## Área

Full-stack

## Objetivo

Criar funcionalidade para registrar e consultar avistamentos de animais, alimentando banco de dados de espécies (F13).

## Critérios Funcionais

- [ ] Endpoint para listar espécies cadastradas;
- [ ] Endpoint para registrar avistamento (espécie, localização, data);
- [ ] Tela no app para consultar fauna da região;
- [ ] Incrementar `quant_avistamentos` automaticamente a cada nova ocorrência;
- [ ] Dados das coletas enviados para banco de espécies e locais de aparição (F13).

## Critérios Técnicos

- [ ] Usar PostGIS para consultas espaciais (animais próximos);
- [ ] Integrar com a classificação de IA (EP09) para sugerir espécie.

## Artefatos

- [ ] Endpoints REST para espécies e avistamentos;
- [ ] Telas no app;
- [ ] Integração com ML para identificação.

## Dependências

- EP09 - Inteligência e Identificação de Animais

## Definition of Done

- [ ] Usuário pode consultar fauna da região;
- [ ] Cada ocorrência registra avistamento automaticamente.

---

# E04 - Gráfico de Tendências Sazonais

## Área

Full-stack

## Objetivo

Criar gráfico de tendências mostrando épocas de maior movimentação de animais (F14).

## Critérios Funcionais

- [ ] Gráfico exibindo tendências sazonais de aparecimento de animais;
- [ ] Dados computados a partir do banco de espécies e avistamentos;
- [ ] Estatísticas computadas em menos de 10 segundos (NF10);
- [ ] Filtro por espécie e período.

## Critérios Técnicos

- [ ] Agregação de dados no backend por mês/estação;
- [ ] Cache das estatísticas para performance;
- [ ] Biblioteca de gráficos no Android (MPAndroidChart ou similar).

## Dependências

- E03 - Catalogação de Avistamentos

## Definition of Done

- [ ] Gráfico de tendências disponível no app;
- [ ] Dados atualizados periodicamente (< 10s para computar).

---

# E05 - Mapa de Calor de Ocorrências

## Área

Full-stack

## Objetivo

Visualizar geograficamente as ocorrências para identificar padrões de aparição.

## Critérios Funcionais

- [ ] Endpoint com dados agregados por região;
- [ ] Visualização em mapa de calor (admin);
- [ ] Filtro por período e tipo de animal.

## Definition of Done

- [ ] Mapa de calor disponível no admin.
