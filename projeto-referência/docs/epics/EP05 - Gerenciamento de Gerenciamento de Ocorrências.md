# EP05 - Gerenciamento de Ocorrências

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Implementar o núcleo operacional do sistema.

---

# F08 - Solicitação de Ocorrência

## Área

Frontend

## História de Usuário

Como usuário, eu quero abrir uma ocorrência (resgate/condução/coleta) via formulário com foto e localização, para que um profissional lide com um animal selvagem.

## Critérios Funcionais

- [ ] O sistema deve mostrar um formulário para abrir uma ocorrência, e permitir que o usuário insira:
  - [ ] Uma foto, retirada com a câmera do dispositivo;
  - [ ] Opções para classificar que tipo de chamada está sendo feita, de forma exclusiva;
  - [ ] O formulário deve exibir um campo opcional de descrição.
- [ ] O sistema deve extrair as coordenadas de GPS do dispositivo no momento da solicitação.

## Critérios Técnicos

- [ ] O sistema deve possibilitar a captura de imagem pela cÂmera do dispositivo.

# Artefatos

- [ ] Tela de solicitação de abertura de ocorrência;
- [ ] Botão para acessar a tela;
- [ ] Mensagem de abertura de ocorrência.

## Dependências

- E01 - Ambiente de desenvolvimento Front-end
- E05 - Implementação de esquema de API

## Definition of Done

- [ ] Upload de imagem funcionando;
- [ ] Envio de informações por API funcionando.

---

# F09 - Endpoint de Solicitação de Ocorrência

## Área

Backend

## História de Usuário

Como sistema, eu quero persistir uma ocorrência no banco de dados, para que o resgistro de ocorrência seja feito.

## Critérios Funcionais

- [ ] O sistema deve verificar se a imagem é válida;
- [ ] Salvar a imagem com um nome único;
- [ ] Caso a imagem seja salva, inserir os dados da solicitação no banco de dados;
- [ ] Enviar uma mensagem de operação concluída para o frontend.

## Critérios Técnicos

- [ ] Usar transação considerando a persistencia da imagem em um diretório e de dados no banco;
- [ ] Rota de abertura de ocorrência.

## Artefatos

- [ ] Middleware para verificar consistência dos dados recebidos na solicitação;
- [ ] Persistência de arquivos local.

## Dependências

- E03 - Ambiente de desenvolvimento Back-end
- E05 - Implementação de esquema de API

## Definition of Done

- [ ] Fluxo de abertura de ocorrência funcionando;
- [ ] Persistencia de imagens no conteiner funcionando.