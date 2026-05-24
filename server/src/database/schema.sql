CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    reputacao real NOT NULL DEFAULT 0.0,
    contato VARCHAR(20),
    ajudante boolean NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS coletores (
    usuario_id INTEGER PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    cpf VARCHAR(11) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS administrador (
    usuario_id INTEGER PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    cpf VARCHAR(11) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS credenciais (
    usuario_id INTEGER PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS plantoes (
    id SERIAL PRIMARY KEY,
    coletor_id INTEGER NOT NULL REFERENCES coletores(usuario_id) ON DELETE CASCADE,
    inicio TIMESTAMPTZ NOT NULL,
    fim TIMESTAMPTZ NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CHECK (fim > inicio)
);

CREATE TABLE IF NOT EXISTS ocorrencias(
    id SERIAL PRIMARY KEY,
    coletor_id INTEGER REFERENCES coletores(usuario_id) ON DELETE RESTRICT,
    origem_solicitacao_id INTEGER NOT NULL REFERENCES usuarios(id),
    origem_gps GEOGRAPHY(Point, 4326) NOT NULL, 
    destino_gps GEOGRAPHY(Point, 4326),       
    data_captura TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descricao_origem VARCHAR(255),
    descricao_destino VARCHAR(255),
    observacoes VARCHAR(255),
    risco VARCHAR (30),
    equipamento_captura VARCHAR(100),
    referencia_imagem VARCHAR(255),
    estado INTEGER NOT NULL DEFAULT 0,
    tipo INTEGER NOT NULL 
);

CREATE TABLE IF NOT EXISTS animais(
    id SERIAL PRIMARY KEY,
    nome_popular VARCHAR(100),
    especie VARCHAR(100),
    quant_avistamentos INTEGER DEFAULT 0
);

CREATE INDEX idx_gps_origem_ocorrencias ON ocorrencias USING GIST(origem_gps);
CREATE INDEX idx_gps_destino_ocorrencias ON ocorrencias USING GIST(destino_gps);