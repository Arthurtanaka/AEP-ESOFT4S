-- =====================================================================
-- MoveCity - Script de criação do banco de dados (MySQL 8.0 ou superior)
-- AEP 4º Semestre (2026.2) - UniCesumar
-- Local no repositório: /database/schema.sql
--
-- Como executar:
--   mysql -u root -p < database/schema.sql
-- ou abra este arquivo no MySQL Workbench / phpMyAdmin e execute tudo.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS movecity
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE movecity;

-- Garante que acentos sejam lidos corretamente (UTF-8) em qualquer sistema operacional.
SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- Tabela usuario
-- Guarda Passageiro e Administrador na MESMA tabela (herança com tabela
-- única). A coluna tipo_usuario diz qual é o perfil. As colunas
-- preferencia e nivel_acesso são específicas de cada perfil e por isso
-- aceitam NULL.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario    INT AUTO_INCREMENT PRIMARY KEY,
    nome          VARCHAR(150) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    senha_hash    VARCHAR(255) NOT NULL,          -- nunca guardar senha em texto puro
    tipo_usuario  VARCHAR(20)  NOT NULL,
    preferencia   VARCHAR(50)  NULL,              -- só para PASSAGEIRO
    nivel_acesso  INT          NULL,              -- só para ADMINISTRADOR
    CONSTRAINT ck_usuario_tipo CHECK (tipo_usuario IN ('PASSAGEIRO', 'ADMINISTRADOR'))
);

-- ---------------------------------------------------------------------
-- Tabela transporte
-- Guarda Carro, Onibus e Bicicleta na mesma tabela (herança com tabela
-- única). A coluna tipo é o "discriminador" que diz qual subclasse Java
-- deve ser criada ao ler a linha.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transporte (
    id_transporte          INT AUTO_INCREMENT PRIMARY KEY,
    nome                   VARCHAR(100)  NOT NULL,
    tipo                   VARCHAR(30)   NOT NULL,
    velocidade_media_kmh   DECIMAL(5,2)  NOT NULL,   -- usada em calcularTempo()
    emissao_co2_g_km       DECIMAL(8,2)  NOT NULL,   -- usada em calcularImpacto()
    capacidade_passageiros INT           NULL,       -- só para ONIBUS
    fator_congestionamento DECIMAL(4,2)  NULL,       -- só para CARRO
    CONSTRAINT ck_transporte_tipo CHECK (tipo IN ('CARRO', 'ONIBUS', 'BICICLETA'))
);

-- ---------------------------------------------------------------------
-- Tabela rota
-- Uma rota (alternativa de deslocamento) entre uma origem e um destino.
-- Distância, tempo e impacto são calculados a partir dos trechos.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS rota (
    id_rota    INT AUTO_INCREMENT PRIMARY KEY,
    origem     VARCHAR(200) NOT NULL,
    destino    VARCHAR(200) NOT NULL,
    descricao  VARCHAR(255) NULL
);

-- ---------------------------------------------------------------------
-- Tabela trecho
-- Relação 1:N de COMPOSIÇÃO: uma rota possui 1..N trechos e, se a rota
-- for apagada, os trechos dela também são (ON DELETE CASCADE).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS trecho (
    id_trecho      INT AUTO_INCREMENT PRIMARY KEY,
    id_rota        INT           NOT NULL,
    id_transporte  INT           NOT NULL,
    ordem          INT           NOT NULL,
    distancia_km   DECIMAL(8,2)  NOT NULL,
    CONSTRAINT fk_trecho_rota
        FOREIGN KEY (id_rota) REFERENCES rota (id_rota)
        ON DELETE CASCADE,
    CONSTRAINT fk_trecho_transporte
        FOREIGN KEY (id_transporte) REFERENCES transporte (id_transporte),
    CONSTRAINT uq_trecho_ordem UNIQUE (id_rota, ordem)
);

-- ---------------------------------------------------------------------
-- Tabela favorito: rotas salvas pelo usuário (um usuário não pode
-- favoritar a mesma rota duas vezes).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS favorito (
    id_favorito  INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario   INT      NOT NULL,
    id_rota      INT      NOT NULL,
    criado_em    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorito_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE,
    CONSTRAINT fk_favorito_rota
        FOREIGN KEY (id_rota) REFERENCES rota (id_rota)
        ON DELETE CASCADE,
    CONSTRAINT uq_favorito UNIQUE (id_usuario, id_rota)
);

-- ---------------------------------------------------------------------
-- Tabela historico: consultas de rota feitas por usuários autenticados.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS historico (
    id_historico   INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario     INT      NOT NULL,
    id_rota        INT      NOT NULL,
    data_consulta  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_historico_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
        ON DELETE CASCADE,
    CONSTRAINT fk_historico_rota
        FOREIGN KEY (id_rota) REFERENCES rota (id_rota)
        ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- Tabela alerta: avisos sobre uma rota, registrados por um usuário com
-- perfil ADMINISTRADOR (essa regra é verificada na camada Service).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS alerta (
    id_alerta    INT AUTO_INCREMENT PRIMARY KEY,
    id_rota      INT          NOT NULL,
    id_usuario   INT          NOT NULL,
    mensagem     VARCHAR(255) NOT NULL,
    data_alerta  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status       VARCHAR(20)  NOT NULL DEFAULT 'ATIVO',
    CONSTRAINT fk_alerta_rota
        FOREIGN KEY (id_rota) REFERENCES rota (id_rota)
        ON DELETE CASCADE,
    CONSTRAINT fk_alerta_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT ck_alerta_status CHECK (status IN ('ATIVO', 'INATIVO'))
);

-- ---------------------------------------------------------------------
-- Dados iniciais (valores ILUSTRATIVOS, apenas para testar o sistema).
-- Cada INSERT só acontece se ainda não existir um transporte daquele tipo,
-- então este script pode ser executado mais de uma vez sem duplicar dados.
-- A equipe pode ajustar os valores na 2ª entrega.
-- ---------------------------------------------------------------------
INSERT INTO transporte (nome, tipo, velocidade_media_kmh, emissao_co2_g_km, capacidade_passageiros, fator_congestionamento)
SELECT 'Automóvel particular', 'CARRO', 30.00, 120.00, NULL, 1.30 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM transporte WHERE tipo = 'CARRO');

INSERT INTO transporte (nome, tipo, velocidade_media_kmh, emissao_co2_g_km, capacidade_passageiros, fator_congestionamento)
SELECT 'Ônibus urbano', 'ONIBUS', 20.00, 70.00, 40, NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM transporte WHERE tipo = 'ONIBUS');

INSERT INTO transporte (nome, tipo, velocidade_media_kmh, emissao_co2_g_km, capacidade_passageiros, fator_congestionamento)
SELECT 'Bicicleta', 'BICICLETA', 15.00, 0.00, NULL, NULL FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM transporte WHERE tipo = 'BICICLETA');
