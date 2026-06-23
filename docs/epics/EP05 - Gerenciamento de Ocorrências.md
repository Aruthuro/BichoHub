# EP05 - Gerenciamento de Ocorrências

[Voltar para o Hub de Épicos](./EP00%20-%20Epicos%20do%20projeto.md)

## Objetivo

Implementar o núcleo operacional do sistema: solicitação, registro e acompanhamento de ocorrências.

---

# E01 - Solicitação de Ocorrência (Front-end)

## Área

Front-end

## Objetivo

Criar tela de solicitação de ocorrência com formulário, foto, localização e tipo de chamada.

## Critérios Funcionais

- [x] O sistema deve mostrar um formulário para abrir uma ocorrência (`OcorrenciaScreen.kt`), permitindo:
  - [x] Capturar foto com a câmera do dispositivo (TakePicture);
  - [x] Selecionar foto da galeria (PickVisualMedia);
  - [x] Classificar tipo de chamada (Coleta/Resgate/Condução);
  - [x] Campo opcional de descrição.
- [ ] O sistema deve extrair as coordenadas de GPS do dispositivo no momento da solicitação (atualmente hardcoded: `POINT(-60.0217 -3.1190)`);
- [ ] O sistema deve enviar a foto junto com a solicitação;
- [ ] O formulário deve incluir localização espacial do animal.

## Critérios Técnicos

- [x] Captura de imagem pela câmera (`ActivityResultContracts.TakePicture`);
- [ ] Obter localização real via FusedLocationProviderClient ou LocationManager;
- [ ] Incluir campo de localização espacial no formulário.

## Artefatos

- [x] Tela de solicitação de ocorrência (`OcorrenciaScreen.kt`);
- [x] Botão para acessar a tela (`MainScreen.kt`);
- [x] Mensagem de confirmação (Toast).

## Dependências

- EP01 - Infraestrutura e Ambiente de Desenvolvimento
- EP02 - Arquitetura Backend e API

## Definition of Done

- [ ] Upload de imagem funcionando;
- [x] Envio de informações por API funcionando;
- [ ] GPS real do dispositivo capturado;
- [ ] Campo de localização espacial implementado.

---

# E02 - Endpoint de Solicitação de Ocorrência

## Área

Back-end

## Objetivo

Persistir ocorrências no banco de dados recebidas pela API.

## Critérios Funcionais

- [ ] O sistema deve verificar se a imagem é válida;
- [ ] Salvar a imagem com um nome único;
- [ ] Caso a imagem seja salva, inserir os dados da solicitação no banco de dados;
- [x] Enviar uma mensagem de operação concluída para o frontend;
- [ ] O sistema deve rejeitar ocorrências sem setor/prédio informados (RN01);
- [ ] O sistema deve rejeitar ocorrências fora do campus da UFAM (RN02).

## Critérios Técnicos

- [ ] Usar transação considerando a persistência da imagem e de dados no banco;
- [x] Rota de abertura de ocorrência (`POST /api/v1/usuarios/registrar-ocorrencia`);
- [ ] Middleware para verificar consistência dos dados recebidos.

## Artefatos

- [ ] Validação de setor/prédio no backend;
- [x] Persistência de dados no banco (função `criarOcorrencia` em `bancoDeDados.ts`).

## Dependências

- EP01 - Infraestrutura e Ambiente de Desenvolvimento
- EP02 - Arquitetura Backend e API

## Definition of Done

- [x] Fluxo de abertura de ocorrência funcionando (sem imagem);
- [ ] Validação de setor/prédio implementada;
- [ ] Restrição geográfica ao campus implementada;
- [ ] Persistência de imagens no container funcionando.

---

# E03 - Substituir GPS Hardcoded por Localização Real

## Área

Front-end

## Objetivo

Substituir coordenada fixa pela localização real do dispositivo.

## Critérios Funcionais

- [ ] O app deve solicitar permissão de localização;
- [ ] O app deve obter as coordenadas GPS reais no momento da criação da ocorrência;
- [ ] O app deve funcionar mesmo se a localização não estiver disponível (fallback).

## Critérios Técnicos

- [ ] Usar `FusedLocationProviderClient` (Google Play Services) ou `LocationManager`;
- [ ] Solicitar permissão `ACCESS_FINE_LOCATION` em runtime;
- [ ] Formatar saída como WKT `POINT(lon lat)`.

## Artefatos

- [ ] Atualização do `OcorrenciaScreen.kt`;
- [ ] Permissão no `AndroidManifest.xml`.

## Definition of Done

- [ ] Coordenada real enviada na criação de ocorrência.

---

# E04 - Enviar Foto na Criação de Ocorrência

## Área

Full-stack

## Objetivo

Conectar a captura de foto ao envio multipart na criação da ocorrência.

## Critérios Funcionais

- [ ] O app deve enviar a foto como multipart/form-data junto com os dados da ocorrência;
- [ ] O backend deve receber e processar a imagem no endpoint de criação;
- [ ] A imagem deve ser associada à ocorrência criada.

## Critérios Técnicos

- [ ] Atualizar `OcorrenciaRequest` para aceitar multipart;
- [ ] Atualizar `BichoHubService` para endpoint com `@Multipart`;
- [ ] Configurar multer no backend para aceitar imagem no endpoint de ocorrência.

## Artefatos

- [ ] Atualização do `OcorrenciaScreen.kt`;
- [ ] Atualização do `BichoHubService.kt`;
- [ ] Atualização do `routes.ts` (endpoint de criar ocorrência).

## Dependências

- EP03 - Modelagem e Persistência de Dados (E03 - Upload)

## Definition of Done

- [ ] Foto enviada e persistida na criação de ocorrência.

---

# E05 - Validação de Setor/Prédio e Restrição Geográfica

## Área

Full-stack

## Objetivo

Implementar regras de negócio para validar dados da ocorrência.

## Critérios Funcionais

- [ ] Sistema não deve aceitar solicitações onde setor ou prédio/bloco não foram informados (RN01);
- [ ] Sistema não deve atender solicitações de coleta fora do campus da UFAM (RN02);
- [ ] Sistema deve exibir contato das autoridades responsáveis para ocorrências fora do campus (RN03).

## Critérios Técnicos

- [ ] Validação no front-end e no back-end;
- [ ] Consulta geográfica para verificar se coordenada está dentro do polígono da UFAM.

## Artefatos

- [ ] Validação de campos obrigatórios no formulário;
- [ ] Endpoint ou lógica de verificação de perímetro;
- [ ] Tela de redirecionamento para contatos externos.

## Dependências

- E01 - Solicitação de Ocorrência
- E02 - Endpoint de Solicitação

## Definition of Done

- [ ] Ocorrências inválidas são rejeitadas com mensagem clara.
