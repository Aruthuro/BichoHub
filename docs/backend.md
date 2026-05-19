# Backend

## Sumário

- [README](../README.md)

- [Tecnologias](#tecnologias)

- [Como executar o backend sem Docker](#como-executar-o-backend-sem-docker)
  - [Instalar dependências](#instalar-dependências)
  - [Modo desenvolvimento](#modo-desenvolvimento)
  - [Build do projeto](#build-do-projeto)
  - [Modo produção](#modo-produção)

- [Como executar o backend com Docker](#como-executar-o-backend-com-docker)
  - [Primeira execução](#primeira-execução)
  - [Execuções seguintes](#execuções-seguintes)
  - [Executar em segundo plano](#executar-em-segundo-plano)

- [Como parar os containers](#como-parar-os-containers)
  - [Parar os containers mantendo os volumes](#parar-os-containers-mantendo-os-volumes)
  - [Parar os containers removendo volumes](#parar-os-containers-removendo-volumes)
  - [Parar containers e remover órfãos](#parar-containers-e-remover-órfãos)

- [Logs](#logs)
  - [Ver logs de todos os containers](#ver-logs-de-todos-os-containers)
  - [Ver logs apenas do backend](#ver-logs-apenas-do-backend)
  - [Ver logs apenas do PostgreSQL](#ver-logs-apenas-do-postgresql)

- [Rebuild da imagem Docker](#rebuild-da-imagem-docker)
  - [Rebuild normal](#rebuild-normal)
  - [Rebuild sem cache](#rebuild-sem-cache)

- [Limpeza do ambiente Docker](#limpeza-do-ambiente-docker)
  - [Listar containers](#listar-containers)
  - [Listar todos os containers (inclusive parados)](#listar-todos-os-containers-inclusive-parados)
  - [Remover um container específico](#remover-um-container-específico)
  - [Remover todos os containers parados](#remover-todos-os-containers-parados)
  - [Listar imagens Docker](#listar-imagens-docker)
  - [Remover uma imagem específica](#remover-uma-imagem-específica)
  - [Remover imagens não utilizadas](#remover-imagens-não-utilizadas)
  - [Listar volumes](#listar-volumes)
  - [Remover um volume específico](#remover-um-volume-específico)
  - [Remover volumes não utilizados](#remover-volumes-não-utilizados)
  - [Limpeza completa do Docker](#limpeza-completa-do-docker)

## [Tecnologias](#sumário)

- Express [5.x];
- Morgan [1.x]: usado para registro de logs do servidor;
- PostgreSQL [15];
- Docker e Docker Compose: usados para criar o ambiente de execução do servidor e do banco de dados;
- TypeScript;
- Node.js [20+];

O projeto utiliza o arquivo ``.env.development``, que contém as variáveis de ambiente utilizadas pelo backend e pelo PostgreSQL.

---

# [Como executar o backend sem Docker](#sumário)

## Instalar dependências

```bash
npm install
```

---

## Modo desenvolvimento

Sem Docker, é necessário que o PostgreSQL esteja instalado e em execução diretamente na máquina, pois o backend irá se conectar a um serviço local de banco de dados. Nesse caso, as variáveis de ambiente normalmente utilizam `localhost` como host do PostgreSQL.

```bash
npm run dev
```

---

## [Build do projeto](#sumário)

Compila TypeScript para JavaScript:

```bash
npm run build
```

---

## Modo produção

Executa o backend compilado:

```bash
npm run start
```

---

# [Como executar o backend com Docker](#sumário)

## Primeira execução

Na primeira vez é necessário construir as imagens Docker:

```bash
docker compose --env-file .env.development up --build
```

---

## Execuções seguintes

Depois da primeira build:

```bash
docker compose --env-file .env.development up
```

---

## Executar em segundo plano

Para deixar os containers rodando em background e não ocupar o terminal:

```bash
docker compose --env-file .env.development up -d
```

---

# [Como parar os containers](#sumário)

## Parar os containers mantendo os volumes

```bash
docker compose --env-file .env.development down
```

---

## Parar os containers removendo volumes

Remove também os dados persistidos do PostgreSQL:

```bash
docker compose --env-file .env.development down -v
```

---

## Parar containers e remover órfãos

Órfãos são containers que foram criados por uma configuração antiga do projeto, mas que não existem mais no ``docker-compose.yml`` atual.

```bash
docker compose --env-file .env.development down -v --remove-orphans
```

---

# [Logs](#sumário)

## Ver logs de todos os containers

```bash
docker compose logs -f
```

---

## Ver logs apenas do backend

```bash
docker compose logs -f backend
```

---

## Ver logs apenas do PostgreSQL

```bash
docker compose logs -f db_postgres
```

---

# [Rebuild da imagem Docker](#sumário)

## Rebuild normal

```bash
docker compose --env-file .env.development build
```

---

## Rebuild sem cache

Usar quando dependências ou configurações mudam:

```bash
docker compose --env-file .env.development build --no-cache
```

---

# [Limpeza do ambiente Docker](#sumário)

## Listar containers

```bash
docker ps
```

---

## Listar todos os containers (inclusive parados)

```bash
docker ps -a
```

---

## Remover um container específico

```bash
docker rm NOME_OU_ID
```

---

## Remover todos os containers parados

```bash
docker container prune
```

---

## Listar imagens Docker

```bash
docker images
```

---

## Remover uma imagem específica

```bash
docker rmi NOME_OU_ID
```

---

## Remover imagens não utilizadas

```bash
docker image prune -a
```

---

## Listar volumes

```bash
docker volume ls
```

---

## Remover um volume específico

```bash
docker volume rm NOME_DO_VOLUME
```

---

## Remover volumes não utilizados

```bash
docker volume prune
```

---

## Limpeza completa do Docker

Remove:
- containers parados;
- imagens não utilizadas;
- cache;
- redes;
- volumes não utilizados.

```bash
docker system prune -a --volumes
```

---