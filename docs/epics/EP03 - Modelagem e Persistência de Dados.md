# EP03 - Modelagem e Persistência de Dados

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Estruturar o banco de dados e garantir persistência consistente das informações.

---

# E04 - Implementação do Esquema do Banco de Dados

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
- [X] O banco deve permitir gerenciamento de plantões e disponibilidade de coletores.

## Critérios Técnicos

- [x] Definir chaves primárias e estrangeiras;
- [x] Definir constraints de integridade;
- [x] Definir índices necessários para consultas geográficas.

## Artefatos

- [x] Script SQL do esquema do banco.

## Dependências

- E03 - Ambiente de desenvolvimento Back-end

## Definition of Done

- [ ] Esquema sem erros de execução SQL.

---

# E06 - Transações, Consistência e Recuperação de Falhas

## Área

Back-end

## Objetivo

Definir e implementar transação, consistência e recuperação de falhas no sistema BichoHub.

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
- [ ] Garantir integridade de referencia entre entidades.

## Fluxos Cobertos

- [ ] Cadastro de usuário;
- [ ] Registro de chamada;
- [ ] Associação de imagens a uma chamada;
- [ ] Atualização de status de chamada.

## Artefatos

- [ ] Serviços transacionais implementados;
- [ ] Fluxo de rollback implementado;
- [ ] Estratégia de remoção de arquivos órfãos;
- [ ] Documentação ou descrição básica do fluxo transacional.

## Dependências

- E04 - Implementação do esquema do banco de dados
- E05 - Estrutura da API
- Implementação do sistema de upload de imagens

## Definition of Done

- [ ] Transações funcionando corretamente;
- [ ] Rollback funcionando;
- [ ] Imagens órfãs sendo removidas corretamente;
- [ ] Operações inconsistentes impedidas de serem executadas.

---

# E07 — Sistema de Upload e Armazenamento de Imagens

## Área

Back-end

## Objetivo

Implementar o sistema de upload, armazenamento e recuperação de imagens do BichoHub utilizando armazenamento em sistema de arquivos local, por simplicidade.

## Critérios Funcionais

- [ ] O sistema deve permitir upload de imagens;
- [ ] O sistema deve armazenar imagens no servidor;
- [ ] O sistema deve permitir recuperação de imagens armazenadas;
- [ ] O sistema deve associar imagens a chamadas.

## Critérios Técnicos

- [ ] Utilizr armazenamento em sistema de arquivos local;
- [ ] Organizar imagens em diretórios padronizados;
- [ ] Gerar nomes únicos para arquivos armazenados;
- [ ] Permitir acesso HTTP às imagens armazenadas;
- [ ] Garantir compatibilidade com volumes do Docker;
- [ ] Armazenar apenas referências/paths no PostgreSQL.

## Exemplo de diretórios

``/uploads/chamadas``

## Entregáveis / Artefatos

- [ ] Middleware de upload implementado;
- [ ] Sistema de armazenamento local implementado;
- [ ] Serviço de geração de nomes únicos;
- [ ] Serviço de recuperação de imagens;
- [ ] Diretório de uploads configurado;
- [ ] Integração HTTP para acesso às imagens.

## Dependências

- E03 — Ambiente de desenvolvimento Back-end
- E04 — Implementação do esquema do banco de dados
- E05 — Estrutura da API

## Definition of Done

- [ ] Upload de imagens funcionando corretamente;
- [ ] Recuperação HTTP de imagens funcionando;
- [ ] Arquivos inválidos sendo rejeitados;
- [ ] Arquivos armazenados corretamente no servidor;
- [ ] Nomes únicos sendo gerados corretamente;
- [ ] Integração com Docker funcionando.