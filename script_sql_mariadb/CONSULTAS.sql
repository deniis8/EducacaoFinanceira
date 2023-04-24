-- Cria a base de dados
CREATE DATABASE GestaoFinanceira_Dev; 

-- Coloca em uso a base de dados
USE GestaoFinanceira_Dev; 

-- ===================================================================================================================
-- Cria a tabela
-- ===================================================================================================================
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
-- ===================================================================================================================

-- ===================================================================================================================
-- Insere registros a partir de um arquivo .txt
-- ===================================================================================================================
LOAD DATA LOCAL INFILE 'C:/Users/adeni/workspace/java/Teste/arquivos/baseLancamentos.txt'
INTO TABLE LANCAMENTOS character set latin1
FIELDS TERMINATED BY ';' LINES TERMINATED BY ';;';
-- ===================================================================================================================

-- ===================================================================================================================
-- View Lancamentos
-- ===================================================================================================================
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
-- ===================================================================================================================

-- ===================================================================================================================
-- Criação da tabela Saldos
-- ===================================================================================================================
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
    DATA_CRIACAO DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	 CONSTRAINT FK_LANCAMENTO_SALDO FOREIGN KEY(ID_LANC) REFERENCES lancamentos(ID_LANC)
);
-- ===================================================================================================================

-- ===================================================================================================================
-- Insere os registros na tabelas saldos a partir de um arquivo .txt
-- ===================================================================================================================
LOAD DATA LOCAL INFILE 'C:/Users/adeni/workspace/java/Teste/arquivos/baseSaldos.txt'
INTO TABLE SALDOS character set latin1
FIELDS TERMINATED BY ';' LINES TERMINATED BY ';;';
-- ===================================================================================================================

-- ===================================================================================================================
-- Cria a tabela Centro de Custo
-- ===================================================================================================================
CREATE TABLE CCUSTO(
ID_CCUSTO INT AUTO_INCREMENT PRIMARY KEY,
DESCRI VARCHAR(50) NOT NULL,
DATA_CRIACAO DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
ID_USUARIO INT NOT NULL,
D_E_L_E_T_ CHAR(1) NOT NULL
);
-- ===================================================================================================================

-- ===================================================================================================================
-- Insere os registros na tabelas Centro de Custo a partir de um arquivo .txt
-- ===================================================================================================================
LOAD DATA LOCAL INFILE 'C:/Users/adeni/workspace/java/Teste/arquivos/baseCCusto.txt'
INTO TABLE CCUSTO character set latin1
FIELDS TERMINATED BY ';' LINES TERMINATED BY ';;';
-- ===================================================================================================================

-- ===================================================================================================================
-- Cria a tabela usuários
-- ===================================================================================================================
CREATE TABLE USUARIOS(
ID_USUARIO INT AUTO_INCREMENT PRIMARY KEY,
NOME_COMPLETO VARCHAR(100) NOT NULL,
EMAIL VARCHAR(100) NOT NULL UNIQUE,
-- SENHA VARBINARY(256) NOT NULL,
SENHA VARCHAR(20) NOT NULL,
DATA_CRIACAO DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
D_E_L_E_T_ CHAR(1) NOT NULL
);
-- ===================================================================================================================

-- ===================================================================================================================
-- Insere um registro na tabela Usuários
-- ===================================================================================================================
INSERT INTO USUARIOS(NOME_COMPLETO, EMAIL, SENHA, D_E_L_E_T_) 
VALUES('Adenilson Soares da Silva', 'adenilson_denis8@hotmail.com', 'TESTE', '');
-- ===================================================================================================================

-- ===================================================================================================================
-- Backup e Restore
-- Execute os comandos no prompt (dieretório: C:\Program Files\MariaDB 10.6\bin)
-- ===================================================================================================================
mysqldump -u root -p gestaofinanceira > C:\EducacaoFinanceira\backup\backup.sql
mysql -u root -p gestaofinanceira_dev < C:\EducacaoFinanceira\backup\backup.sql
-- ===================================================================================================================

-- ===================================================================================================================
-- Procedure e Triggers para atualização de saldos
-- ===================================================================================================================
USE GestaoFinanceira_Dev; 

DROP PROCEDURE IF EXISTS ATUALIZA_SALDOS;

-- PROCEDURE 
DELIMITER $$

CREATE PROCEDURE ATUALIZA_SALDOS (IN DATA_REGISTRO DATETIME, IN ID_USER INT) 

BEGIN
	DECLARE done INT DEFAULT FALSE;
	
	DECLARE INSERT_ID_LANC 			INT; 
	DECLARE INSERT_DATA_HORA 		DATETIME; 
	DECLARE INSERT_VALOR 			NUMERIC(10,2); 
	DECLARE INSERT_DESCRICAO 		VARCHAR(100); 
	DECLARE INSERT_CCUSTO	 		VARCHAR (30); 
	DECLARE INSERT_STATUS_LANC 	VARCHAR(20); 
	DECLARE INSERT_DATA_CRIACAO 	DATETIME;
	DECLARE INSERT_ID_USUARIO 		INT;	
	DECLARE _SALDO 					NUMERIC(10, 2);
	DECLARE QUANTIDADE				INT;
	
	DECLARE FORLANCAMENTOS CURSOR FOR SELECT 
			  LANC.ID_LANC,
			  LANC.DATA_HORA, 
			  LANC.VALOR, 
			  LANC.DESCRICAO,
			  (SELECT DESCRI FROM ccusto AS CC WHERE CC.ID_USUARIO=ID_USER AND CC.ID_CCUSTO = LANC.ID_CCUSTO AND CC.D_E_L_E_T_ <> '*') AS CCENTRO, 
			  LANC.STATUS_LANC, 
			  LANC.DATA_CRIACAO, 
			  LANC.ID_USUARIO 
        FROM 
		  		lancamentos AS LANC
		  	WHERE 
		  		LANC.DATA_HORA>=DATE(DATA_REGISTRO) AND LANC.STATUS_LANC IN('Pago','Recebido') AND 
				ID_USUARIO=ID_USER AND LANC.D_E_L_E_T_<>'*'
        ORDER BY 
		  		DATA_HORA, DATA_CRIACAO;		  		
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
	
	DELETE FROM saldos WHERE DATA_HORA >= DATE(DATA_REGISTRO) AND ID_USUARIO=1;
	SELECT COUNT(*) INTO QUANTIDADE FROM saldos WHERE ID_USUARIO=1;	
	
	IF QUANTIDADE>0 THEN	
		SELECT SALDO INTO _SALDO FROM saldos ORDER BY DATA_HORA DESC LIMIT 1;
	ELSE
		SET _SALDO := 0;
   END IF;
		  		
  	OPEN FORLANCAMENTOS;
  	
  	-- Percorrer Lançamentos e fazer insert na tabela saldos
	read_loop: loop	
		FETCH FORLANCAMENTOS INTO INSERT_ID_LANC, INSERT_DATA_HORA, INSERT_VALOR, INSERT_DESCRICAO, INSERT_CCUSTO, 
			  INSERT_STATUS_LANC, INSERT_DATA_CRIACAO, INSERT_ID_USUARIO;
			  
		IF done THEN
			LEAVE read_loop;
    	END IF;
   
	   IF INSERT_STATUS_LANC='Pago' THEN
	   	SET _SALDO := _SALDO-INSERT_VALOR;
	   ELSEIF INSERT_STATUS_LANC='Recebido' THEN
	   	SET _SALDO := _SALDO+INSERT_VALOR;
	   END IF;
   
   
	INSERT INTO SALDOS(DATA_HORA, VALORLAN, DESCRILAN, SALDO, CCUSTO, STATUS_LANC, ID_LANC, ID_USUARIO) 
		VALUES(INSERT_DATA_HORA, 
				  INSERT_VALOR, 
				  INSERT_DESCRICAO, 
				  _SALDO, 
				  INSERT_CCUSTO,				  
				  INSERT_STATUS_LANC, 
				  INSERT_ID_LANC, 
				  INSERT_ID_USUARIO);
				  
	end loop read_loop;
	
	CLOSE FORLANCAMENTOS;

END $$

-- DELIMITER;


-- Trigger
DROP TRIGGER IF EXISTS ALT_ATUALIZA_SALDOS;

DELIMITER $
CREATE TRIGGER ALT_ATUALIZA_SALDOS AFTER UPDATE
ON lancamentos
FOR EACH ROW
BEGIN
CALL ATUALIZA_SALDOS(NEW.DATA_HORA, NEW.ID_USUARIO);
CALL ATUALIZA_GASTOS_MENSAIS(NEW.ID_USUARIO);
CALL ATUALIZA_SALDOS_INVESTIMENTOS(NEW.ID_USUARIO);
END$

DROP TRIGGER IF EXISTS DEL_ATUALIZA_SALDOS;

DELIMITER $
CREATE TRIGGER DEL_ATUALIZA_SALDOS AFTER DELETE
ON lancamentos
FOR EACH ROW
BEGIN
CALL ATUALIZA_SALDOS(OLD.DATA_HORA, OLD.ID_USUARIO);
CALL ATUALIZA_GASTOS_MENSAIS(OLD.ID_USUARIO);
CALL ATUALIZA_SALDOS_INVESTIMENTOS(OLD.ID_USUARIO);
END$

DROP TRIGGER IF EXISTS INSERT_ATUALIZA_SALDOS;

DELIMITER $
CREATE TRIGGER INSERT_ATUALIZA_SALDOS AFTER INSERT
ON lancamentos
FOR EACH ROW
BEGIN
CALL ATUALIZA_SALDOS(NEW.DATA_HORA, NEW.ID_USUARIO);
CALL ATUALIZA_GASTOS_MENSAIS(NEW.ID_USUARIO);
CALL ATUALIZA_SALDOS_INVESTIMENTOS(NEW.ID_USUARIO);
END$

-- CALL ATUALIZA_SALDOS('2023-03-10 11:00:00');
-- SELECT * FROM saldos;
-- ===================================================================================================================

-- ===================================================================================================================
-- Cria a tabela
-- ===================================================================================================================
CREATE TABLE gastosmensais(
ID_GASTO_MENSAL INT AUTO_INCREMENT PRIMARY KEY,
VALOR NUMERIC(10,2) NOT NULL,
ANO INT NOT NULL,
MES VARCHAR(20) NOT NULL,
DATA_HORA DATETIME NOT NULL,
ID_USUARIO INT NOT NULL
);
-- ===================================================================================================================

-- ===================================================================================================================
-- Procedure Gastos mensais
-- ===================================================================================================================
DROP PROCEDURE IF EXISTS ATUALIZA_GASTOS_MENSAIS;

-- PROCEDURE 
DELIMITER $$

CREATE PROCEDURE ATUALIZA_GASTOS_MENSAIS (IN ID_USER INT) 

BEGIN
	DECLARE done INT DEFAULT FALSE;
	
	DECLARE INSERT_VALOR 			NUMERIC(10,2); 
	DECLARE INSERT_ANO 				INT;
	DECLARE INSERT_MES			 	VARCHAR(20);
	DECLARE INSERT_DATA_HORA 		DATETIME; 
	DECLARE INSERT_ID_USUARIO 		INT;
	
	DECLARE GASTOSMES CURSOR FOR SELECT 
		SUM(VALOR) AS VALOR,
		YEAR(DATA_HORA) AS ANO,
		CASE MONTH(DATA_HORA) 
    	  WHEN 1 THEN 'Janeiro' 
        WHEN 2 THEN 'Fevereiro' 
        WHEN 3 THEN 'Março' 
        WHEN 4 THEN 'Abril' 
        WHEN 5 THEN 'Maio' 
        WHEN 6 THEN 'Junho' 
        WHEN 7 THEN 'Julho' 
		  WHEN 8 THEN 'Agosto' 
		  WHEN 9 THEN 'Setembro' 
		  WHEN 10 THEN 'Outubro' 
		  WHEN 11 THEN 'Novembro' 
		  WHEN 12 THEN 'Dezembro'         
   	END AS MES,
		DATA_HORA,
		ID_USUARIO 
	FROM 
		LANCAMENTOS
	WHERE
		STATUS_LANC='Pago' AND ID_CCUSTO NOT IN(19) AND ID_USUARIO=ID_USER AND D_E_L_E_T_<>'*'
	GROUP BY
		YEAR(DATA_HORA), MONTHNAME(DATA_HORA)
	ORDER BY
		DATA_HORA;		  		
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;	
	
	DELETE FROM GASTOSMENSAIS WHERE ID_USUARIO=ID_USER;
		  		
  	OPEN GASTOSMES;
  	
  	-- Percorrer Lançamentos e fazer insert na tabela saldos
	read_loop: loop	
		FETCH GASTOSMES INTO INSERT_VALOR, INSERT_ANO, INSERT_MES, INSERT_DATA_HORA, INSERT_ID_USUARIO;
			  
		IF done THEN
			LEAVE read_loop;
    	END IF;
   
   
	INSERT INTO GASTOSMENSAIS(VALOR, ANO, MES, DATA_HORA, ID_USUARIO) 
		VALUES(INSERT_VALOR, 
				  INSERT_ANO, 
				  INSERT_MES, 
				  INSERT_DATA_HORA, 
				  INSERT_ID_USUARIO);
				  
	end loop read_loop;
	
	CLOSE GASTOSMES;

END $$
-- ===================================================================================================================

-- ===================================================================================================================
-- Cria a tabela Saldos
-- ===================================================================================================================
CREATE TABLE SALDOSINVESTIMENTOS(
ID_SALDO INT AUTO_INCREMENT PRIMARY KEY,
SALDO NUMERIC(10,2),
INVESTIMENTO_FIXO NUMERIC(10,2),
INVESTIMENTO_VARIAVEL NUMERIC(10,2),
DATA_CRIACAO DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
ID_USUARIO INT NOT NULL
);

SELECT * FROM saldosinvestimentos;
-- ===================================================================================================================

-- ===================================================================================================================
-- Procedure Gastos mensais
-- ===================================================================================================================
DROP PROCEDURE IF EXISTS ATUALIZA_SALDOS_INVESTIMENTOS;

-- PROCEDURE 
DELIMITER $$

CREATE PROCEDURE ATUALIZA_SALDOS_INVESTIMENTOS (IN ID_USER INT) 

BEGIN
	DECLARE done INT DEFAULT FALSE;
	
	DECLARE INSERT_SALDO NUMERIC(10,2);
	DECLARE INSERT_INVESTIMENTO_FIXO NUMERIC(10,2);
	DECLARE INSERT_INVESTIMENTO_VARIAVEL NUMERIC(10,2);
	DECLARE INSERT_ID_USUARIO INT;
	
	DECLARE SALDOINVESTIMEN CURSOR FOR SELECT 
		IF(ISNULL((SELECT SALDO FROM saldos WHERE ID_USUARIO=ID_USER ORDER BY DATA_HORA DESC, ID_LANC DESC LIMIT 1)),0,(SELECT SALDO FROM saldos WHERE ID_USUARIO=ID_USER ORDER BY DATA_HORA DESC, ID_LANC DESC LIMIT 1)) AS SALDO,
		IF(ISNULL((SELECT SUM(VALORLAN) FROM saldos WHERE ccusto='Investimento Fixo' AND ID_USUARIO=ID_USER)),0,(SELECT SUM(VALORLAN) FROM saldos WHERE ccusto='Investimento Fixo' AND ID_USUARIO=ID_USER)) AS INVESTIMENTO_FIXO,
		IF(ISNULL((SELECT SUM(VALORLAN) FROM saldos WHERE ccusto='Investimento Variável' AND ID_USUARIO=ID_USER)),0,(SELECT SUM(VALORLAN) FROM saldos WHERE ccusto='Investimento Variável' AND ID_USUARIO=ID_USER)) AS INVESTIMENTO_VARIAVEL,
		ID_USUARIO 
	FROM 
		lancamentos LIMIT 1;	
	 	  		
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;	
	DELETE FROM saldosinvestimentos WHERE ID_USUARIO=ID_USER;
		  		
  	OPEN SALDOINVESTIMEN;
  	
  	-- Percorrer Lançamentos e fazer insert na tabela saldos
	read_loop: loop	
		FETCH SALDOINVESTIMEN INTO INSERT_SALDO, INSERT_INVESTIMENTO_FIXO, INSERT_INVESTIMENTO_VARIAVEL, INSERT_ID_USUARIO;
			  
		IF done THEN
			LEAVE read_loop;
    	END IF;
   
   
	INSERT INTO SALDOSINVESTIMENTOS(SALDO, INVESTIMENTO_FIXO, INVESTIMENTO_VARIAVEL, ID_USUARIO) 
		VALUES(INSERT_SALDO, 
				  INSERT_INVESTIMENTO_FIXO, 
				  INSERT_INVESTIMENTO_VARIAVEL, 
				  INSERT_ID_USUARIO);
				  
	end loop read_loop;
	
	CLOSE SALDOINVESTIMEN;

END $$
-- ===================================================================================================================