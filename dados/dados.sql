-- DROP DATABASE ANS -- código que faz com que a base de dados de nome ANS seja excluída

-- código para criar a base de dados de nome ANS

CREATE DATABASE IF NOT EXISTS ANS;

-- código para utilizar a base de dados criada

USE ANS;

-- O código a seguir cria a tabela operadoras com seus campos e atributos

CREATE TABLE operadoras(
	registro_ans INTEGER(6) NOT NULL PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL,
    razao_social VARCHAR(256) NOT NULL,
    nome_fantasia VARCHAR(256),
    modalidade VARCHAR(64) NOT NULL,
    logradouro VARCHAR(64) NOT NULL,
    numero VARCHAR(6),
    complemento VARCHAR(64),
    bairro VARCHAR(32) NOT NULL,
    cidade VARCHAR(32) NOT NULL,
    uf VARCHAR(2) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    ddd VARCHAR(2),
    telefone VARCHAR(20),
    fax VARCHAR(20),
    email VARCHAR(256),
    representante VARCHAR(256) NOT NULL,
    cargo_representante VARCHAR(64) NOT NULL,
    regiao_comercializacao TINYINT,
    data_registro_ans DATE NOT NULL
);

-- O código a seguir cria a tabela creditos_operacoes_assistencia com seus campos e atributos

CREATE TABLE creditos_operacoes_assistencia(
	id INT AUTO_INCREMENT PRIMARY KEY,
    data_registro DATE NOT NULL,
    registro_ans INTEGER(6) NOT NULL,
    cd_conta_contabil VARCHAR(50) NOT NULL,
    descricao TEXT NOT NULL,
    vl_saldo_inicial DECIMAL(15,2) NOT NULL,
    vl_saldo_final DECIMAL(15,2) NOT NULL,
    outros_valores JSON,
    FOREIGN KEY (registro_ans) REFERENCES operadoras(registro_ans) ON DELETE CASCADE
);

-- O código abaixo, importa o arquivo, e ao fazer as validações, insere os dados na tabela operadoras

LOAD DATA INFILE '/var/lib/mysql-files/Relatorio_cadop.csv'
INTO TABLE operadoras
FIELDS TERMINATED BY ';'
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(@Registro_ANS, @CNPJ, @Razao_Social, @Nome_Fantasia, @Modalidade, @Logradouro, @Numero, @Complemento, @Bairro, @Cidade, @UF, @CEP, @DDD, @Telefone, @Fax, @Endereco_eletronico, @Representante, @Cargo_Representante, @Regiao_de_Comercializacao, @Data_Registro_ANS)
SET
    registro_ans = NULLIF(TRIM(@Registro_ANS), ''),
    cnpj = IF(TRIM(@CNPJ) REGEXP '^[0-9]+$', TRIM(@CNPJ), NULL),
    razao_social = TRIM(@Razao_Social),
    nome_fantasia = NULLIF(TRIM(@Nome_Fantasia), ''),
    modalidade = TRIM(@Modalidade),
    logradouro = TRIM(@Logradouro),
    numero = IF(TRIM(@Numero) REGEXP '^[0-9]+$', TRIM(@Numero), NULL),
    complemento = NULLIF(TRIM(@Complemento), ''),
    bairro = TRIM(@Bairro),
    cidade = TRIM(@Cidade),
    uf = TRIM(@UF),
    cep = IF(TRIM(@CEP) REGEXP '^[0-9]+$', TRIM(@CEP), NULL),
    ddd = IF(TRIM(@DDD) REGEXP '^[0-9]{1,2}$', TRIM(@DDD), NULL),
    telefone = IF(TRIM(@Telefone) REGEXP '^[0-9]+$', TRIM(@Telefone), NULL),
    fax = IF(TRIM(@Fax) REGEXP '^[0-9]+$', TRIM(@Fax), NULL),
    email = NULLIF(TRIM(@Endereco_eletronico), ''),
    representante = TRIM(@Representante),
    cargo_representante = TRIM(@Cargo_Representante),
    regiao_comercializacao = NULLIF(TRIM(@Regiao_de_Comercializacao), ''),
    data_registro_ans = STR_TO_DATE(NULLIF(TRIM(@Data_Registro_ANS), ''), '%Y-%m-%d');

-- O código abaixo, importa o arquivo e ao fazer as validações, insere os dados na tabela creditos_operacoes_assistencia

LOAD DATA INFILE '/var/lib/mysql-files/1T2023.csv'
INTO TABLE creditos_operacoes_assistencia
FIELDS TERMINATED BY ';'
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(@data, @registro_ans, @cd_conta_contabil, @descricao, @vl_saldo_inicial, @vl_saldo_final)
SET 
    data_registro = STR_TO_DATE(NULLIF(TRIM(@data), ''), '%Y-%m-%d'),
    registro_ans = CAST(TRIM(@registro_ans) AS UNSIGNED),
    cd_conta_contabil = NULLIF(TRIM(@cd_conta_contabil), ''),
    descricao = NULLIF(TRIM(@descricao), ''),
    vl_saldo_inicial = CAST(REPLACE(NULLIF(TRIM(@vl_saldo_inicial), ''), ',', '.') AS DECIMAL(15,2)),
    vl_saldo_final = CAST(REPLACE(NULLIF(TRIM(@vl_saldo_final), ''), ',', '.') AS DECIMAL(15,2)),
    outros_valores = NULL;

-- código que seleciona dez registros da tabela operadoras

SELECT * FROM operadoras LIMIT 10;

-- código que seleciona dez registros da tabela creditos_operacoes_assistencia;

SELECT * FROM creditos_operacoes_assistencia LIMIT 10;

-- Código que seleciona as dez operadoras com maiores despesas do tipo informado no último trimestre

SELECT 
    o.registro_ans,
    o.razao_social,
    SUM(c.vl_saldo_final) AS total_despesa
FROM creditos_operacoes_assistencia c
JOIN operadoras o ON c.registro_ans = o.registro_ans
WHERE 
    c.descricao LIKE '%EVENTOS%SINISTROS%CONHECIDOS%MEDICO%'
    AND c.data_registro >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
GROUP BY o.registro_ans, o.razao_social
ORDER BY total_despesa DESC
LIMIT 10;

-- Código que informa as dez operadoras com maiores despesas no último ano

SELECT 
    o.registro_ans,
    o.razao_social,
    SUM(c.vl_saldo_final) AS total_despesa
FROM creditos_operacoes_assistencia c
JOIN operadoras o ON c.registro_ans = o.registro_ans
WHERE 
    c.descricao LIKE '%EVENTOS%SINISTROS%CONHECIDOS%MEDICO%'
    AND c.data_registro >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
GROUP BY o.registro_ans, o.razao_social
ORDER BY total_despesa DESC
LIMIT 10;

CREATE TABLE creditos_operacoes_assistencia_tmp LIKE creditos_operacoes_assistencia;

LOAD DATA INFILE '/var/lib/mysql-files/1T2023.csv'
INTO TABLE creditos_operacoes_assistencia_tmp
FIELDS TERMINATED BY ';'
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(@data, @registro_ans, @cd_conta_contabil, @descricao, @vl_saldo_inicial, @vl_saldo_final)
SET 
    data_registro = STR_TO_DATE(NULLIF(TRIM(@data), ''), '%Y-%m-%d'),
    registro_ans = CAST(TRIM(@registro_ans) AS UNSIGNED),
    cd_conta_contabil = NULLIF(TRIM(@cd_conta_contabil), ''),
    descricao = NULLIF(TRIM(@descricao), ''),
    vl_saldo_inicial = CAST(REPLACE(NULLIF(TRIM(@vl_saldo_inicial), ''), ',', '.') AS DECIMAL(15,2)),
    vl_saldo_final = CAST(REPLACE(NULLIF(TRIM(@vl_saldo_final), ''), ',', '.') AS DECIMAL(15,2)),
    outros_valores = NULL;

SELECT DISTINCT registro_ans
FROM creditos_operacoes_assistencia_tmp
WHERE registro_ans NOT IN (
    SELECT registro_ans FROM operadoras
);

INSERT INTO creditos_operacoes_assistencia (
    data_registro, registro_ans, cd_conta_contabil, descricao,
    vl_saldo_inicial, vl_saldo_final, outros_valores
)
SELECT 
    data_registro, registro_ans, cd_conta_contabil, descricao,
    vl_saldo_inicial, vl_saldo_final, outros_valores
FROM creditos_operacoes_assistencia_tmp
WHERE registro_ans IN (
    SELECT registro_ans FROM operadoras
);

