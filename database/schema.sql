-- schema.sql
-- Script de criação das tabelas do MoveCity, com base no DER da 1ª Entrega.
-- Ajustar tipos/tamanhos se necessário durante a implementação (2ª Entrega).

CREATE TABLE usuario (
    id_usuario   INT AUTO_INCREMENT PRIMARY KEY,
    nome         VARCHAR(150) NOT NULL,
    email        VARCHAR(150) NOT NULL UNIQUE,
    senha_hash   VARCHAR(255) NOT NULL,
    tipo_usuario VARCHAR(20)  NOT NULL -- 'PASSAGEIRO' ou 'ADMINISTRADOR'
);

CREATE TABLE rota (
    id_rota      INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario   INT NOT NULL,
    origem       VARCHAR(200) NOT NULL,
    destino      VARCHAR(200) NOT NULL,
    distancia_km DECIMAL(8,2),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE transporte (
    id_transporte     INT AUTO_INCREMENT PRIMARY KEY,
    nome              VARCHAR(100) NOT NULL,
    tipo              VARCHAR(30)  NOT NULL,
    impacto_ambiental DECIMAL(8,2)
);

CREATE TABLE trecho (
    id_trecho     INT AUTO_INCREMENT PRIMARY KEY,
    id_rota       INT NOT NULL,
    id_transporte INT NOT NULL,
    distancia_km  DECIMAL(8,2),
    ordem         INT,
    FOREIGN KEY (id_rota) REFERENCES rota(id_rota),
    FOREIGN KEY (id_transporte) REFERENCES transporte(id_transporte)
);

CREATE TABLE favorito (
    id_favorito INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario  INT NOT NULL,
    id_rota     INT NOT NULL,
    criado_em   DATE,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_rota) REFERENCES rota(id_rota)
);

CREATE TABLE historico (
    id_historico  INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario    INT NOT NULL,
    id_rota       INT NOT NULL,
    data_consulta DATETIME,
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_rota) REFERENCES rota(id_rota)
);

CREATE TABLE alerta (
    id_alerta   INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario  INT NOT NULL,
    mensagem    VARCHAR(255),
    data_alerta DATETIME,
    status      VARCHAR(20),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);
