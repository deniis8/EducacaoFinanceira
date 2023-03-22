-- Cria a base de dados
CREATE DATABASE GestaoFinanceira_Dev; 

-- Coloca em uso a base de dados
USE GestaoFinanceira_Dev; 

-- Deleta a tabela

-- Cria a tabela
CREATE TABLE lancamentos(
ID_LANC INT AUTO_INCREMENT PRIMARY KEY,
DATA_HORA DATETIME NOT NULL,
VALOR DECIMAL(10,2) NOT NULL,
DESCRICAO VARCHAR(100) NOT NULL,
ID_CCUSTO INT NOT NULL,
STATUS_LANC VARCHAR(20) NOT NULL,
DATA_CRIACAO DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
ID_USUARIO INT NOT NULL,
D_E_L_E_T_ VARCHAR(1) NOT NULL
);

-- Insere registros a partir de um arquivo .txt
LOAD DATA LOCAL INFILE 'C:/Users/adeni/workspace/java/Teste/arquivos/baseLancamentos.txt'
INTO TABLE LANCAMENTOS character set latin1
FIELDS TERMINATED BY ';' LINES TERMINATED BY ';;';

-- Seleciona os registros
SELECT 
                                            DATA_HORA,
                                            VALORLAN,
                                            DESCRILAN,
                                            STATUS_LANC,
                                            SALDO
                                        FROM 
                                            SALDOS AS SALDO 
                                        ORDER BY 
                                            DATA_HORA DESC LIMIT 1

-- View Lancamentos
CREATE VIEW VW_LANCAMENTOS AS
SELECT 
    LANC.ID_LANC AS ID, 
    STR_TO_DATE(LANC.DATA_HORA, '%Y-%m-%d') AS DATA_HORA, 
    CASE DAYOFWEEK(DATA_HORA) 
    	  WHEN '1' THEN 'Domingo' 
        WHEN '2' THEN 'Segunda-Feira' 
        WHEN '3' THEN 'Terça-Feira' 
        WHEN '4' THEN 'Quarta-Feira' 
        WHEN '5' THEN 'Quinta-Feira' 
        WHEN '6' THEN 'Sexta-Feira' 
        WHEN '7' THEN 'Sábado'         
        END AS DIAS_SEMANA, 
    LANC.VALOR VALOR, 
    LANC.DESCRICAO AS DESCRICAO, 
    LANC.ID_CCUSTO AS CODCCUSTO,
    (SELECT DESCRI FROM CCUSTO WHERE LANC.ID_CCUSTO = ID_CCUSTO AND D_E_L_E_T_ <> '*') AS CCENTRO, 
    LANC.STATUS_LANC AS STATUS_LANC 
FROM 
    LANCAMENTOS AS LANC 
WHERE 
    LANC.D_E_L_E_T_ <> '*' 
ORDER BY 
    LANC.DATA_HORA;
    
SELECT * FROM lancamentos WHERE ID_CCUSTO=60
SELECT * FROM CCUSTO WHERE D_E_L_E_T_='*'

-- Criação da tabela Saldos
CREATE TABLE SALDOS (
    ID_SALDO INT AUTO_INCREMENT PRIMARY KEY,
    DATA_HORA DATETIME NOT NULL,
    VALORLAN  NUMERIC (10, 2),
    DESCRILAN VARCHAR (100),
    SALDO     NUMERIC (10, 2) NOT NULL,
    CCUSTO    VARCHAR (30),
    STATUS_LANC    VARCHAR (20),
    ID_LANC   INT,
    ID_USUARIO INT NOT NULL,
	 CONSTRAINT FK_LANCAMENTO_SALDO FOREIGN KEY(ID_LANC) REFERENCES lancamentos(ID_LANC)
);

LOAD DATA LOCAL INFILE 'C:/Users/adeni/workspace/java/Teste/arquivos/baseSaldos.txt'
INTO TABLE SALDOS character set latin1
FIELDS TERMINATED BY ';' LINES TERMINATED BY ';;';

SELECT * FROM saldos;

--- CENTRO DE CUSTO ---
CREATE TABLE CCUSTO(
ID_CCUSTO INT AUTO_INCREMENT PRIMARY KEY,
DESCRI VARCHAR(50) NOT NULL,
DATA_CRIACAO DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
ID_USUARIO INT NOT NULL,
D_E_L_E_T_ CHAR(1) NOT NULL
);

LOAD DATA LOCAL INFILE 'C:/Users/adeni/workspace/java/Teste/arquivos/baseCCusto.txt'
INTO TABLE CCUSTO character set latin1
FIELDS TERMINATED BY ';' LINES TERMINATED BY ';;';

SELECT * FROM ccusto;


-- USUÁRIOS --
CREATE TABLE USUARIOS(
ID_USUARIO INT AUTO_INCREMENT PRIMARY KEY,
NOME_COMPLETO VARCHAR(100) NOT NULL,
EMAIL VARCHAR(100) NOT NULL UNIQUE,
-- SENHA VARBINARY(256) NOT NULL,
SENHA VARCHAR(20) NOT NULL,
DATA_CRIACAO DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
D_E_L_E_T_ CHAR(1) NOT NULL
);

INSERT INTO USUARIOS(NOME_COMPLETO, EMAIL, SENHA, D_E_L_E_T_) 
VALUES('Adenilson Soares da Silva', 'adenilson_denis8@hotmail.com', 'TESTE', '');

/*
INSERT INTO USUARIOS(NOME_COMPLETO, EMAIL, SENHA, D_E_L_E_T_) 
VALUES('Adenilson Soares da Silva', 'adenilson_denis8@hotmail.com', CONVERT(VARBINARY(256),pwdencrypt('Asvezesfalo8')), '');
*/

-- SELECT pwdcompare('Asvezesfalo8', SENHA), * FROM USUARIOS;
SELECT * FROM usuarios;

-- Backup e Restore
-- Execute os comandos no prompt (dieretório: C:\Program Files\MariaDB 10.6\bin)
mysqldump -u root -p gestaofinanceira > C:\EducacaoFinanceira\backup\backup.sql
mysql -u root -p gestaofinanceira_dev < C:\EducacaoFinanceira\backup\backup.sql

-- PROCEDURE 
DELIMITER $$

CREATE PROCEDURE ATUALIZA_SALDOS (IN DATA_REGISTRO DATETIME) 
BEGIN
DELETE FROM saldos WHERE DATA_HORA >= DATE(DATA_REGISTRO);
SELECT * FROM lancamentos WHERE DATA_HORA>=DATA_REGISTRO AND D_E_L_E_T_<>'*';
SELECT * FROM saldos ORDER BY DATA_HORA DESC LIMIT 1;
END $$

DELIMITER;

CALL ATUALIZA_SALDOS('2023-03-19 21:20:00')
