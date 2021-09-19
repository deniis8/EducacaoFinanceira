package sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import conexao.Conexao;

public class CriaTabs {

	private PreparedStatement pst;
	Conexao conexao = new Conexao();

	public CriaTabs() {
		criarTab();
	}

	public void criarTab() {

		String[] cCusto;
		cCusto = new String[49];
		
		cCusto[0] = "Lanches";
		cCusto[1] = "Bebidas Alcoolicas";
		cCusto[2] = "Cartão Itau";
		cCusto[3] = "Cartão Nubank";
		cCusto[4] = "Entretenimento/Esporte";
		cCusto[5] = "Cabeleireiro";
		cCusto[6] = "Transporte";
		cCusto[7] = "Animais";
		cCusto[8] = "Curso";
		cCusto[9] = "Acerto";
		cCusto[10] = "Vale";
		cCusto[11] = "Pagamento";
		cCusto[12] = "Dentista";
		cCusto[13] = "Farmácia";
		cCusto[14] = "Investimento Fixo";
		cCusto[15] = "Doação";
		cCusto[16] = "Juros Negativo";
		cCusto[17] = "Férias";
		cCusto[18] = "Empréstimo";
		cCusto[19] = "Internet";
		cCusto[20] = "Vivo Controle";
		cCusto[21] = "Mat. Construção";
		cCusto[22] = "Estacionamento";
		cCusto[23] = "Presente";
		cCusto[24] = "Pedágio";
		cCusto[25] = "Combustível";
		cCusto[26] = "Recarga para Celular";
		cCusto[27] = "Restaurante";
		cCusto[28] = "Saldo Inicial";
		cCusto[29] = "Acessório para Celular";
		cCusto[30] = "Churrasco";
		cCusto[31] = "Suprimentos P/Casa";
		cCusto[32] = "Cofre";
		cCusto[33] = "FGTS";
		cCusto[34] = "Décimo Terceiro";
		cCusto[35] = "Roupas/Calçados";
		cCusto[36] = "Outros";
		cCusto[37] = "Viagem";
		cCusto[38] = "Investimento Variável";
		cCusto[39] = "Móveis/Acessórios para casa";
		cCusto[40] = "Juros Positivo";
		cCusto[41] = "Exame ou Consulta Particilar";
		cCusto[42] = "Livros";
		cCusto[43] = "Eletrônicos";
		cCusto[44] = "Juros Positivos em Geral";
		cCusto[45] = "PLR";
		cCusto[46] = "Conta de Água";	
		cCusto[47] = "Conta de Luz";
		cCusto[48] = "Automotivo";

		String sqlTabCC = "";
		String sqlTabLan = "";
		String sqlTabSald = "";
		String sqlViewGM = "";
		String sqlViewGPD = "";
		String sqlViewLAN = "";
		String sqlViewRM = "";
		String sqlViewREL = "";
		String sqlViewSAL = "";

		sqlTabCC += " CREATE TABLE IF NOT EXISTS CCUSTO ( \n";
		sqlTabCC += "     ID_CCUSTO  INTEGER      PRIMARY KEY AUTOINCREMENT, \n";
		sqlTabCC += "     DESCRI     VARCHAR (30), \n";
		sqlTabCC += "     D_E_L_E_T_ CHAR (1) \n";
		sqlTabCC += " ); ";

		conexao.abrir();
		try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(sqlTabCC);
			pst.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();

		String sqlCC = "SELECT * FROM CCUSTO";
		boolean temReg = false;

		conexao.abrir();
		try {
			pst = conexao.getConexao().prepareStatement(sqlCC);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				temReg = true;
			}
		} catch (Exception e) {
		}
		conexao.fechar();

		if (temReg == false) {
			String sql = "INSERT INTO CCUSTO(DESCRI, D_E_L_E_T_) VALUES (?,?)";
			for(int i=0; i<cCusto.length; i++) {
				conexao.abrir();
				try {
					pst = conexao.getConexao().prepareStatement(sql);
					pst.setString(1, cCusto[i]);
					pst.setString(2, "");
					pst.executeUpdate();

				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null,
							"Erro no Recalculo | (Tabela: LANCAMENTOS) Erro: " + e.getMessage());
				}
				conexao.fechar();
			}
		}

		sqlTabLan += " CREATE TABLE IF NOT EXISTS LANCAMENTOS ( \n";
		sqlTabLan += "     ID_LANC    VARCHAR (6)    UNIQUE \n";
		sqlTabLan += "                               PRIMARY KEY, \n";
		sqlTabLan += "     DATA       DATE, \n";
		sqlTabLan += "     HORA       TIME, \n";
		sqlTabLan += "     VALOR      DECIMAL (7, 2), \n";
		sqlTabLan += "     DESCRICAO  VARCHAR (50), \n";
		sqlTabLan += "     ID_CCUSTO  INTEGER, \n";
		sqlTabLan += "     STATUS     VARCHAR (20), \n";
		sqlTabLan += "     D_E_L_E_T_ CHAR (1) \n";
		sqlTabLan += " ); \n";
		conexao.abrir();
		try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(sqlTabLan);
			pst.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();

		sqlTabSald += " CREATE TABLE IF NOT EXISTS SALDOS ( \n";
		sqlTabSald += "     ID_SALDO  VARCHAR (6)     PRIMARY KEY \n";
		sqlTabSald += "                               UNIQUE, \n";
		sqlTabSald += "     DATA      DATE            NOT NULL, \n";
		sqlTabSald += "     HORA      TIME, \n";
		sqlTabSald += "     VALORLAN  NUMERIC (10, 2), \n";
		sqlTabSald += "     DESCRILAN VARCHAR (50), \n";
		sqlTabSald += "     SALDO     NUMERIC (10, 2) NOT NULL, \n";
		sqlTabSald += "     CCUSTO    VARCHAR (30), \n";
		sqlTabSald += "     STATUS    VARCHAR (20), \n";
		sqlTabSald += "     ID_LANC   VARCHAR (6)     REFERENCES LANCAMENTOS (ID_LANC) \n";
		sqlTabSald += "                               UNIQUE \n";
		sqlTabSald += " ); ";
		conexao.abrir();
		try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(sqlTabSald);
			pst.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();

		sqlViewGM += " CREATE VIEW IF NOT EXISTS VW_GASTOS_MENSAIS AS \n";
		sqlViewGM += " SELECT \n";
		sqlViewGM += "     DATA AS DATA, \n";
		sqlViewGM += "     CASE SUBSTR(DATA, 6, 2) \n";
		sqlViewGM += "         WHEN '01' THEN 'Janeiro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '02' THEN 'Fevereiro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '03' THEN 'Março/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '04' THEN 'Abril/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '05' THEN 'Maio/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '06' THEN 'Junho/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '07' THEN 'Julho/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '08' THEN 'Agosto/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '09' THEN 'Setembro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '10' THEN 'Outubro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '11' THEN 'Novembro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         WHEN '12' THEN 'Dezembro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewGM += "         END AS MESES_ANO, \n";
		sqlViewGM += "     (SELECT \n";
		sqlViewGM += "         SUM(VALORLAN) \n";
		sqlViewGM += "      FROM \n";
		sqlViewGM += "          SALDOS \n";
		sqlViewGM += "      WHERE \n";
		sqlViewGM += "          STATUS='Recebido' AND \n";
		sqlViewGM += "          SUBSTR(DATA,1,4)=SUBSTR(S.DATA,1,4) AND \n";
		sqlViewGM += "          SUBSTR(DATA,6,2)=SUBSTR(S.DATA,6,2) \n";
		sqlViewGM += "      GROUP BY \n";
		sqlViewGM += "          date(DATA, 'start of month', '+1 month', '-1 day') \n";
		sqlViewGM += "      ORDER BY \n";
		sqlViewGM += "          DATA ASC) AS [VALOR_RECEBIDO], \n";
		sqlViewGM += "          SUM(VALORLAN) AS [VALOR_GASTO], \n";
		sqlViewGM += "     (SELECT \n";
		sqlViewGM += "         SUM(VALORLAN) \n";
		sqlViewGM += "      FROM \n";
		sqlViewGM += "          SALDOS \n";
		sqlViewGM += "      WHERE \n";
		sqlViewGM += "          STATUS='Recebido' AND \n";
		sqlViewGM += "          SUBSTR(DATA,1,4)=SUBSTR(S.DATA,1,4) AND \n";
		sqlViewGM += "          SUBSTR(DATA,6,2)=SUBSTR(S.DATA,6,2) \n";
		sqlViewGM += "      GROUP BY \n";
		sqlViewGM += "          date(DATA, 'start of month', '+1 month', '-1 day') \n";
		sqlViewGM += "      ORDER BY \n";
		sqlViewGM += "          DATA ASC)-SUM(VALORLAN) AS [SOBRA_MENSAL] \n";
		sqlViewGM += " FROM \n";
		sqlViewGM += "     SALDOS AS S \n";
		sqlViewGM += " WHERE \n";
		sqlViewGM += "     STATUS = 'Pago' \n";
		sqlViewGM += " GROUP BY \n";
		sqlViewGM += "     date(DATA, 'start of month', '+1 month', '-1 day') \n";
		sqlViewGM += " ORDER BY \n";
		sqlViewGM += "     DATA ASC; \n";
		conexao.abrir();
		try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(sqlViewGM);
			pst.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();

		sqlViewGPD += " CREATE VIEW IF NOT EXISTS VW_GASTOSPD AS \n";
		sqlViewGPD += " SELECT \n";
		sqlViewGPD += "     DATA AS EMISSAO, \n";
		sqlViewGPD += "     CASE strftime('%w', DATA) \n";
		sqlViewGPD += "         WHEN '1' THEN 'Segunda' \n";
		sqlViewGPD += "         WHEN '2' THEN 'Terça' \n";
		sqlViewGPD += "         WHEN '3' THEN 'Quarta' \n";
		sqlViewGPD += "         WHEN '4' THEN 'Quinta' \n";
		sqlViewGPD += "         WHEN '5' THEN 'Sexta' \n";
		sqlViewGPD += "         WHEN '6' THEN 'Sábado' \n";
		sqlViewGPD += "         WHEN '0' THEN 'Domingo' \n";
		sqlViewGPD += "         END AS DIAS_SEMANA, \n";
		sqlViewGPD += "     SUM(VALORLAN) AS VALOR_GASTO, \n";
		sqlViewGPD += "     TRIM(GROUP_CONCAT(' ' || DESCRILAN || ': ' || 'R$ ' || VALORLAN)) AS DESCRICAO \n";
		sqlViewGPD += " FROM \n";
		sqlViewGPD += "     SALDOS \n";
		sqlViewGPD += " WHERE \n";
		sqlViewGPD += "     STATUS = 'Pago' AND \n";
		sqlViewGPD += "     CCUSTO NOT IN ('Juros Positivo') \n";
		sqlViewGPD += " GROUP BY \n";
		sqlViewGPD += "     EMISSAO \n";
		sqlViewGPD += " ORDER BY \n";
		sqlViewGPD += "     EMISSAO; \n";
		conexao.abrir();
		try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(sqlViewGPD);
			pst.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();

		sqlViewLAN += " CREATE VIEW IF NOT EXISTS VW_LANCAMENTOS AS \n";
		sqlViewLAN += " SELECT \n";
		sqlViewLAN += "     LANC.ID_LANC AS ID, \n";
		sqlViewLAN += "     LANC.DATA AS DATA, \n";
		sqlViewLAN += "     CASE strftime('%w', DATA) \n";
		sqlViewLAN += "         WHEN '1' THEN 'Segunda-Feira' \n";
		sqlViewLAN += "         WHEN '2' THEN 'Terça-Feira' \n";
		sqlViewLAN += "         WHEN '3' THEN 'Quarta-Feira' \n";
		sqlViewLAN += "         WHEN '4' THEN 'Quinta-Feira' \n";
		sqlViewLAN += "         WHEN '5' THEN 'Sexta-Feira' \n";
		sqlViewLAN += "         WHEN '6' THEN 'Sábado' \n";
		sqlViewLAN += "         WHEN '0' THEN 'Domingo' \n";
		sqlViewLAN += "         END AS DIAS_SEMANA, \n";
		sqlViewLAN += "     LANC.VALOR [VALOR], \n";
		sqlViewLAN += "     LANC.DESCRICAO AS DESCRICAO, \n";
		sqlViewLAN += "     (SELECT DESCRI FROM CCUSTO WHERE LANC.ID_CCUSTO = ID_CCUSTO AND D_E_L_E_T_ <> '*') AS CCENTRO, \n";
		sqlViewLAN += "     LANC.STATUS AS STATUS \n";
		sqlViewLAN += " FROM \n";
		sqlViewLAN += "     LANCAMENTOS AS LANC \n";
		sqlViewLAN += " WHERE \n";
		sqlViewLAN += "     LANC.D_E_L_E_T_ <> '*' \n";
		sqlViewLAN += " ORDER BY \n";
		sqlViewLAN += "     LANC.DATA, LANC.HORA; \n";
		conexao.abrir();
		try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(sqlViewLAN);
			pst.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();

		sqlViewRM += " CREATE VIEW IF NOT EXISTS VW_RECEBIDOS_MENSAIS AS \n";
		sqlViewRM += " SELECT \n";
		sqlViewRM += "     DATA AS DATA, \n";
		sqlViewRM += "     SUM(VALORLAN) AS VALOR, \n";
		sqlViewRM += "     CASE SUBSTR(DATA, 6, 2) \n";
		sqlViewRM += "         WHEN '01' THEN 'Janeiro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '02' THEN 'Fevereiro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '03' THEN 'Março/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '04' THEN 'Abril/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '05' THEN 'Maio/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '06' THEN 'Junho/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '07' THEN 'Julho/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '08' THEN 'Agosto/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '09' THEN 'Setembro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '10' THEN 'Outubro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '11' THEN 'Novembro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         WHEN '12' THEN 'Dezembro/' || SUBSTR(DATA, 1, 4) \n";
		sqlViewRM += "         END AS MESES_ANO \n";
		sqlViewRM += " FROM \n";
		sqlViewRM += "     SALDOS \n";
		sqlViewRM += " WHERE \n";
		sqlViewRM += "     STATUS = 'Recebido' \n";
		sqlViewRM += " GROUP BY \n";
		sqlViewRM += "     date(DATA, 'start of month', '+1 month', '-1 day') \n";
		sqlViewRM += " ORDER BY \n";
		sqlViewRM += "     DATA ASC; \n";
		conexao.abrir();
		try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(sqlViewRM);
			pst.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();

		sqlViewREL += " CREATE VIEW IF NOT EXISTS VW_REL001 AS \n";
		sqlViewREL += " SELECT \n";
		sqlViewREL += "     DATA AS [DATA], \n";
		sqlViewREL += "     VALORLAN AS [VALORL], \n";
		sqlViewREL += "     DESCRILAN AS [DESCRILAN], \n";
		sqlViewREL += "     SALDO AS [SALDO] \n";
		sqlViewREL += " FROM \n";
		sqlViewREL += "     SALDOS \n";
		sqlViewREL += " ORDER BY \n";
		sqlViewREL += "     DATA, HORA; \n";
		conexao.abrir();
		try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(sqlViewREL);
			pst.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();

		sqlViewSAL += " CREATE VIEW IF NOT EXISTS VW_SALDO AS \n";
		sqlViewSAL += " SELECT \n";
		sqlViewSAL += "     DATA AS [EMISSAO], \n";
		sqlViewSAL += "     CASE strftime('%w', DATA) \n";
		sqlViewSAL += "         WHEN '1' THEN 'Segunda-Feira' \n";
		sqlViewSAL += "         WHEN '2' THEN 'Terça-Feira' \n";
		sqlViewSAL += "         WHEN '3' THEN 'Quarta-Feira' \n";
		sqlViewSAL += "         WHEN '4' THEN 'Quinta-Feira' \n";
		sqlViewSAL += "         WHEN '5' THEN 'Sexta-Feira' \n";
		sqlViewSAL += "         WHEN '6' THEN 'Sábado' \n";
		sqlViewSAL += "         WHEN '0' THEN 'Domingo' \n";
		sqlViewSAL += "         END AS [DIAS_SEMANA], \n";
		sqlViewSAL += "     SUBSTR(HORA, 1, 5) AS [HORA], \n";
		sqlViewSAL += "     VALORLAN AS [VALOR_LAN], \n";
		sqlViewSAL += "     DESCRILAN AS [DESCRI_LAN], \n";
		sqlViewSAL += "     STATUS AS [STATUS], \n";
		sqlViewSAL += "     CCUSTO AS [C_CUSTO], \n";
		sqlViewSAL += "     SALDO AS [SALDO] \n";
		sqlViewSAL += " FROM \n";
		sqlViewSAL += "     SALDOS \n";
		sqlViewSAL += " ORDER BY \n";
		sqlViewSAL += "     DATA, HORA; \n";
		conexao.abrir();
		try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(sqlViewSAL);
			pst.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();

	}

}
