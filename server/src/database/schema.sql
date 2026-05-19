CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    reputacao real NOT NULL DEFAULT 0.0,
    contato VARCHAR(20),
    ajudante boolean NOT NULL DEFAULT FALSE,
    administrador boolean NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS coletores (
    usuario_id INTEGER NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    reputacao real NOT NULL,
    contato VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS credenciais (
    usuario_id INTEGER NOT NULL UNIQUE REFERENCES usuarios(id) ON DELETE CASCADE,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS registros(
    id SERIAL PRIMARY KEY,
    coletor_id INTEGER NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    origem_socilitacao_id INTEGER NOT NULL REFERENCES usuarios(id),
    origem_gps GEOGRAPHY(Point, 4326) NOT NULL, 
    descricao VARCHAR(255),
    data_captura TIMESTAMPTZ NOT NULL,
    referencia_imagem VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ocorrencias(
    id SERIAL PRIMARY KEY,
    coletor_id INTEGER NOT NULL REFERENCES coletores(usuario_id) ON DELETE RESTRICT,
    origem_socilitacao_id INTEGER NOT NULL REFERENCES usuarios(id),
    origem_gps GEOGRAPHY(Point, 4326) NOT NULL, 
    destino_gps GEOGRAPHY(Point, 4326) NOT NULL,       
    data_captura TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descricao_destino VARCHAR(255),
    observacoes VARCHAR(255),
    risco VARCHAR (30),
    equipamento_captura VARCHAR(100),
    referencia_imagem VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS animais(
    id SERIAL PRIMARY KEY,
    nome_popular VARCHAR(100),
    especie VARCHAR(100),
    quant_avistamentos INTEGER DEFAULT 0
);

CREATE INDEX idx_gps_origem_registros ON registros USING GIST(origem_gps);
CREATE INDEX idx_gps_origem_ocorrencias ON ocorrencias USING GIST(origem_gps);
CREATE INDEX idx_gps_destino_ocorrencias ON ocorrencias USING GIST(destino_gps);
