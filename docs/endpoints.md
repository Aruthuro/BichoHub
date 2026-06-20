# Endpoints

# Endpoints de teste

## Endpoint básico

```http
GET /api/status
```

Exemplo:

```txt
http://localhost:6969/api/status
```

---

## Criar tabela de teste

```http
GET /api/setup
```

---

## Inserir mensagem

```http
POST /api/mensagens
```

Body JSON:

```json
{
  "texto": "teste"
}
```

---

## Recuperar mensagens

```http
GET /api/mensagens
```

---

## Deletar mensagens

```http
DELETE /api/mensagens/:id
```

---

# Endpoints de Usuário

## Login

```http
POST /api/v1/usuarios/login
```

Body:

```json
{ "email": "usuario@email.com", "senha": "123456" }
```

Resposta:

```json
{ "token": "jwt...", "nome": "Usuario" }
```

---

## Cadastro

```http
POST /api/v1/usuarios/cadastrar
```

Body:

```json
{ "nome": "Usuario", "email": "usuario@email.com", "senha": "123456", "contato": "11999999999" }
```

---

## Listar solicitações do usuário

```http
GET /api/v1/usuarios/listar
```

Authorization: Bearer token

---

## Registrar ocorrência

```http
POST /api/v1/usuarios/registrar-ocorrencia
```

Authorization: Bearer token

Body:

```json
{
  "tipo": 1,
  "gps_origem": "POINT(-46.6333 -23.5505)",
  "data_captura": "2025-06-16T10:00:00Z",
  "descricao_origem": "Animal na rua",
  "observacoes": "Parece ferido",
  "risco": "medio"
}
```

---

# Endpoints de Coletor

## Listar ocorrências abertas

```http
GET /api/v1/coletores/ocorrencias/abertas
```

Authorization: Bearer token (coletor)

---

## Listar histórico do coletor

```http
GET /api/v1/coletores/ocorrencias/listar
```

Authorization: Bearer token (coletor)

---

## Detalhar ocorrência

```http
GET /api/v1/coletores/ocorrencias/:id
```

Authorization: Bearer token (coletor)

---

## Aceitar/rejeitar ocorrência

```http
PATCH /api/v1/coletores/responder/:id
```

Authorization: Bearer token (coletor)

Body:

```json
{ "resposta": "aceitar" }
```

---

## Editar ocorrência em andamento

```http
PATCH /api/v1/coletores/ocorrencias/editar/:id
```

Authorization: Bearer token (coletor)

Body:

```json
{ "estado": 2, "status_saude": "Bom", "observacoes": "Animal resgatado" }
```

---

## Encerrar ocorrência

```http
PATCH /api/v1/coletores/ocorrencias/encerrar/:id
```

Authorization: Bearer token (coletor)

Body:

```json
{ "desfecho": "solto", "descricao_soltura": "Animal solto na reserva" }
```

---

# Endpoints de Administrador

## Dashboard (estatísticas)

```http
GET /api/v1/admin/dashboard
```

Authorization: Bearer token (admin)

---

## Listar todos os usuários

```http
GET /api/v1/admin/usuarios
```

Authorization: Bearer token (admin)

---

## Listar todas as ocorrências

```http
GET /api/v1/admin/ocorrencias?filtro=abertas
```

Authorization: Bearer token (admin)

Filtros opcionais: `abertas`, `andamento`, `encerradas`

---

## Detalhar ocorrência (admin)

```http
GET /api/v1/admin/ocorrencias/:id
```

Authorization: Bearer token (admin)

---

## Promover a administrador

```http
POST /api/v1/admin/usuarios/:id/tornar-admin
```

Authorization: Bearer token (admin)

---

## Promover a coletor

```http
POST /api/v1/admin/usuarios/:id/tornar-coletor
```

Authorization: Bearer token (admin)

---

## Remover usuário

```http
DELETE /api/v1/admin/usuarios/:id
```

Authorization: Bearer token (admin)

---

# Endpoints do Modelo de IA

## Classificar imagem

```http
POST /api/modelo/classificar
```

multipart/form-data com campo `imagem` (arquivo de imagem)

Resposta:

```json
{
  "classe_geral": "Cobra",
  "confianca": 0.9523,
  "especie": "Jararaca",
  "confianca_especie": 0.8841
}
```

---

# Endpoints do WhatsApp

## Webhook para integração externa

```http
POST /api/whatsapp/webhook
```

Body:

```json
{ "numero": "5511999999999", "mensagem": "1" }
```

Resposta:

```json
{ "resposta": "Qual o tipo da ocorrência?..." }
```

```http
GET /api/whatsapp/status
```
