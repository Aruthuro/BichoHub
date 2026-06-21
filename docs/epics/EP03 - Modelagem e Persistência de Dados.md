# EP03 - Modelagem e Persistência de Dados

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Estruturar o banco de dados e garantir persistência consistente das informações.

---

# E01 - Implementação do Esquema do Banco de Dados

## Área

Back-end

## Objetivo

Implementar a estrutura inicial do banco de dados do sistema.

## Critérios Funcionais

- [x] O banco deve permitir armazenamento de usuários;
- [x] O banco deve permitir diferenciação entre perfis:
  - [x] Usuário comum;
  - [x] Coletor;
  - [x] Ajudante;
  - [x] Administrador.
- [x] O banco deve permitir armazenamento de ocorrências;
- [x] O banco deve permitir categorização de ocorrências:
  - [x] Resgate;
  - [x] Coleta;
  - [x] Condução.
- [x] O banco deve permitir persistência do estado operacional das ocorrências:
  - [x] Em aberto;
  - [x] Em andamento;
  - [x] Finalizada;
  - [x] Cancelada.
- [x] O banco deve permitir armazenamento geográfico de origem e destino das ocorrências;
- [x] O banco deve permitir associação de imagens às ocorrências;
- [x] O banco deve permitir gerenciamento de plantões e disponibilidade de coletores;
- [x] O banco deve possuir tabela para registro de espécies animais (`animais`) e avistamentos (F13).

## Critérios Técnicos

- [x] Definir chaves primárias e estrangeiras;
- [x] Definir constraints de integridade;
- [x] Definir índices necessários para consultas geográficas;
- [x] Utilizar PostGIS para dados geoespaciais.

## Artefatos

- [x] Script SQL do esquema do banco (`database/schema.sql`).

## Dependências

- EP01 - Infraestrutura e Ambiente de Desenvolvimento

## Definition of Done

- [x] Esquema sem erros de execução SQL.

---

# E02 - Transações, Consistência e Recuperação de Falhas

## Área

Back-end

## Objetivo

Definir e implementar transações, consistência e recuperação de falhas no sistema.

## Critérios Funcionais

- [ ] O sistema deve garantir consistência entre registros no banco de dados e armazenamento de imagens;
- [ ] O sistema não deve registrar chamadas incompletas;
- [ ] O sistema deve remover imagens não ligadas a uma chamada.

## Cenários de Falha

Falha no upload da imagem:

- [ ] Caso a imagem não possa ser salva, a chamada não deve ser registrada.

Falha na persistência da chamada:

- [ ] Caso a imagem seja salva mas a ocorrência não possa ser registrada:
  - [ ] A operação deve ser abortada;
  - [ ] A imagem salva deve ser removida.

Falha durante transação SQL:

- [ ] O sistema deve executar rollback da transação.

## Critérios Técnicos

- [ ] Utilizar rollback em caso de falha;
- [ ] Garantir integridade de referência entre entidades;
- [ ] Recuperação de dados em menos de 5 segundos (NF04).

## Fluxos Cobertos

- [x] Cadastro de usuário (transação `usuarios` + `credenciais`);
- [ ] Registro de chamada;
- [ ] Associação de imagens a uma chamada;
- [x] Atualização de estado de chamada.

## Artefatos

- [ ] Serviços transacionais implementados;
- [ ] Fluxo de rollback implementado;
- [ ] Estratégia de remoção de arquivos órfãos.

## Dependências

- E01 - Esquema do banco de dados
- E03 - Upload e Armazenamento de Imagens

## Definition of Done

- [ ] Transações funcionando corretamente;
- [ ] Rollback funcionando;
- [ ] Imagens órfãs sendo removidas corretamente;
- [ ] Operações inconsistentes impedidas de serem executadas.

---

# E03 - Sistema de Upload e Armazenamento de Imagens

## Área

Back-end

## Objetivo

Implementar o sistema de upload, armazenamento e recuperação de imagens do BichoHub.

## Critérios Funcionais

- [x] O sistema deve permitir upload de imagens (multer configurado em `modeloRoutes.ts`);
- [x] O sistema deve armazenar imagens no servidor (diretório `uploads/`);
- [ ] O sistema deve permitir recuperação de imagens armazenadas;
- [ ] O sistema deve associar imagens a chamadas (referencia_imagem na tabela ocorrencias);
- [x] O sistema deve suportar formato JPEG, padrão para fotos de câmera (NF07).

## Critérios Técnicos

- [x] Utilizar armazenamento em sistema de arquivos local;
- [ ] Organizar imagens em diretórios padronizados;
- [x] Gerar nomes únicos para arquivos armazenados (multer + timestamp);
- [ ] Permitir acesso HTTP às imagens armazenadas;
- [x] Garantir compatibilidade com volumes do Docker (volume montado);
- [x] Armazenar apenas referências/paths no PostgreSQL (coluna `referencia_imagem`).

## Exemplo de diretórios

`/uploads/chamadas`

## Artefatos

- [x] Middleware de upload implementado (multer);
- [x] Sistema de armazenamento local implementado;
- [x] Serviço de geração de nomes únicos;
- [ ] Serviço de recuperação de imagens;
- [x] Diretório de uploads configurado;
- [ ] Integração HTTP para acesso às imagens.

## Dependências

- E01 - Esquema do banco de dados
- EP02 - Arquitetura Backend e API

## Definition of Done

- [x] Upload de imagens funcionando (para classificação de IA);
- [ ] Recuperação HTTP de imagens funcionando;
- [ ] Arquivos inválidos sendo rejeitados;
- [x] Arquivos armazenados corretamente no servidor;
- [x] Nomes únicos sendo gerados corretamente;
- [x] Integração com Docker funcionando.
