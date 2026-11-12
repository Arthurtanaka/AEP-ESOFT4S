-- =====================================================================
-- MoveCity - Dados de exemplo (OPCIONAL, apenas para demonstração e testes)
-- Local no repositório: /database/dados_exemplo.sql
--
-- Execute DEPOIS do schema.sql:
--   mysql -u root -p < database/dados_exemplo.sql
--
-- Cria:
--   * 2 usuários de demonstração (senhas guardadas como hash SHA-256, regra RN02):
--       administrador:  admin@movecity.com      senha: admin123
--       passageiro:     passageiro@movecity.com senha: senha123
--   * 3 trajetos com várias alternativas de rota, para testar a comparação.
-- Este script pode ser executado mais de uma vez: ele apaga os dados de demonstração
-- anteriores (usuários demo e os 3 trajetos abaixo) antes de recriá-los.
-- Em um ambiente real, estes usuários de demonstração devem ser removidos.
-- =====================================================================

USE movecity;

-- Garante que acentos sejam lidos corretamente (UTF-8) em qualquer sistema operacional.
SET NAMES utf8mb4;

-- Limpa a demonstração anterior (os trechos, favoritos, histórico e alertas saem junto: ON DELETE CASCADE).
DELETE FROM usuario WHERE email IN ('admin@movecity.com', 'passageiro@movecity.com');
DELETE FROM rota WHERE (origem, destino) IN
    (('Centro', 'Zona Norte'), ('Centro', 'Universidade'), ('Terminal Rodoviário', 'Aeroporto'));

INSERT INTO usuario (nome, email, senha_hash, tipo_usuario, preferencia, nivel_acesso) VALUES
    ('Administrador Demo', 'admin@movecity.com',      '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMINISTRADOR', NULL, 1),
    ('Passageiro Demo',    'passageiro@movecity.com', '55a5e9e78207b4df8699d60886fa070079463547b095d1a05bc719bb4e6cd251', 'PASSAGEIRO', 'MAIS_SUSTENTAVEL', NULL);

-- Guardamos os ids dos transportes (inseridos no schema.sql) em variáveis, procurando pelo tipo.
SET @carro     = (SELECT id_transporte FROM transporte WHERE tipo = 'CARRO'     ORDER BY id_transporte LIMIT 1);
SET @onibus    = (SELECT id_transporte FROM transporte WHERE tipo = 'ONIBUS'    ORDER BY id_transporte LIMIT 1);
SET @bicicleta = (SELECT id_transporte FROM transporte WHERE tipo = 'BICICLETA' ORDER BY id_transporte LIMIT 1);

-- ---------- Trajeto 1: Centro -> Zona Norte (3 alternativas) ----------
INSERT INTO rota (origem, destino, descricao) VALUES ('Centro', 'Zona Norte', 'De carro, direto');
SET @r = LAST_INSERT_ID();
INSERT INTO trecho (id_rota, id_transporte, ordem, distancia_km) VALUES (@r, @carro, 1, 12.0);

INSERT INTO rota (origem, destino, descricao) VALUES ('Centro', 'Zona Norte', 'De ônibus, direto');
SET @r = LAST_INSERT_ID();
INSERT INTO trecho (id_rota, id_transporte, ordem, distancia_km) VALUES (@r, @onibus, 1, 13.0);

INSERT INTO rota (origem, destino, descricao) VALUES ('Centro', 'Zona Norte', 'Ônibus + bicicleta');
SET @r = LAST_INSERT_ID();
INSERT INTO trecho (id_rota, id_transporte, ordem, distancia_km) VALUES (@r, @onibus, 1, 9.0), (@r, @bicicleta, 2, 3.0);

-- ---------- Trajeto 2: Centro -> Universidade (2 alternativas) ----------
INSERT INTO rota (origem, destino, descricao) VALUES ('Centro', 'Universidade', 'De carro, direto');
SET @r = LAST_INSERT_ID();
INSERT INTO trecho (id_rota, id_transporte, ordem, distancia_km) VALUES (@r, @carro, 1, 6.0);

INSERT INTO rota (origem, destino, descricao) VALUES ('Centro', 'Universidade', 'De bicicleta, ciclovia');
SET @r = LAST_INSERT_ID();
INSERT INTO trecho (id_rota, id_transporte, ordem, distancia_km) VALUES (@r, @bicicleta, 1, 6.5);

-- ---------- Trajeto 3: Terminal Rodoviário -> Aeroporto (1 alternativa) ----------
INSERT INTO rota (origem, destino, descricao) VALUES ('Terminal Rodoviário', 'Aeroporto', 'Ônibus + carro (aplicativo)');
SET @r = LAST_INSERT_ID();
INSERT INTO trecho (id_rota, id_transporte, ordem, distancia_km) VALUES (@r, @onibus, 1, 7.0), (@r, @carro, 2, 4.0);
