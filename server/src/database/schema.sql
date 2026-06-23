CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    reputacao REAL NOT NULL DEFAULT 0.0,
    contato VARCHAR(12) NOT NULL,
    numero_whatsapp VARCHAR(20),
    ajudante BOOLEAN NOT NULL DEFAULT FALSE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS coletores (
    usuario_id INTEGER PRIMARY KEY
        REFERENCES usuarios(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS administrador (
    usuario_id INTEGER PRIMARY KEY
        REFERENCES usuarios(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS credenciais (
    usuario_id INTEGER PRIMARY KEY
        REFERENCES usuarios(id)
        ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS plantoes (
    id SERIAL PRIMARY KEY,
    coletor_id INTEGER NOT NULL
        REFERENCES coletores(usuario_id)
        ON DELETE CASCADE,
    inicio TIMESTAMPTZ NOT NULL,
    fim TIMESTAMPTZ NOT NULL,
    disponivel BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (fim > inicio)
);

CREATE TABLE IF NOT EXISTS ocorrencias (
    id SERIAL PRIMARY KEY,

    coletor_id INTEGER
        REFERENCES coletores(usuario_id)
        ON DELETE RESTRICT,

    origem_solicitacao_id INTEGER NOT NULL
        REFERENCES usuarios(id),

    origem_gps GEOGRAPHY(Point, 4326) NOT NULL,
    destino_gps GEOGRAPHY(Point, 4326),

    data_captura TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    descricao_origem VARCHAR(255),
    descricao_destino VARCHAR(255),
    observacoes VARCHAR(255),

    risco VARCHAR(30),
    equipamento_captura VARCHAR(100),
    referencia_imagem VARCHAR(255),

    classificacao VARCHAR(100),
    confianca_classificacao REAL,

    estado INTEGER NOT NULL DEFAULT 0,
    tipo INTEGER NOT NULL,

    status_saude VARCHAR(100),
    desfecho VARCHAR(30),
    descricao_soltura VARCHAR(255),
    ultimo_caso BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS animais (
    id SERIAL PRIMARY KEY,
    nome_popular VARCHAR(100),
    especie VARCHAR(100),
    quant_avistamentos INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS whatsapp_sessoes (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(20) NOT NULL UNIQUE,
    estado VARCHAR(50) NOT NULL,
    dados JSONB,
    criado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION atualizar_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_whatsapp_atualizado ON whatsapp_sessoes;
CREATE TRIGGER trg_whatsapp_atualizado
    BEFORE UPDATE ON whatsapp_sessoes
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_timestamp();

CREATE INDEX IF NOT EXISTS idx_whatsapp_numero
    ON whatsapp_sessoes (numero);

CREATE INDEX IF NOT EXISTS idx_gps_origem_ocorrencias
    ON ocorrencias USING GIST (origem_gps);

CREATE INDEX IF NOT EXISTS idx_gps_destino_ocorrencias
    ON ocorrencias USING GIST (destino_gps);
