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
