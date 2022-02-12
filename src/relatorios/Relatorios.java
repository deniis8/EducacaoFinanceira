package relatorios;

import mascaras.Mascaras;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;

import configuracao.banco.dados.ConexaoBancoDadosSQLite;
import configuracao.email.EnvioEmail;

/**
 *
 * @author adenilson.soares
 */
public class Relatorios {

	ConexaoBancoDadosSQLite conexao = new ConexaoBancoDadosSQLite();	
	PreparedStatement pst;
	DecimalFormat df = new DecimalFormat("0.00");
	String titulo = "";
	boolean temTab = false;

	public void extrato(String de, String ate, JTextPane texto) {
		titulo = "Relatório: Extrato Bancário.";
		String html = " <!DOCTYPE html> \n";
		html += " <html lang='pt-br'> \n";
		html += " <head> \n";
		html += " 	<meta charset='UTF-8'/> \n";
		html += " 	<title>Gastos</title> \n";
		html += " 	<link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css' integrity='sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T' crossorigin='anonymous'> \n";
		html += " 	<style> \n";		
		html += " 		body{ \n";
		html += " 			font-family: Arial, Helvetica, sans-serif";
		html += " 			font-size: 12pt; \n";
		html += " 			background-color: write; \n";
		html += " 			color: black; \n";
		html += " 		} \n";
		html += " 	</style> \n";
		html += " </head> \n";
		html += " <body> \n";

		String sqlExtrato = "SELECT STRFTIME('%d/%m/%Y', EMISSAO) AS EMISSAO, DIAS_SEMANA, HORA, VALOR_LAN, DESCRI_LAN, C_CUSTO, STATUS, SALDO FROM VW_SALDO WHERE EMISSAO BETWEEN '"
				+ de + "' AND '" + ate + "'";

		conexao.abrir();
		try {
			pst = conexao.getConexao().prepareStatement(sqlExtrato);
			ResultSet rs = pst.executeQuery();
			html += "<h2 align='center'>EXTRATO BANCÁRIO</h2> \n";
			html += "<b>De: </b>" + Mascaras.formatData(de, "1") + "<br/>";
			html += "<b>Até: </b>" + Mascaras.formatData(ate, "1") + "<br/><br/>";
			html += "<table border='1'>\n";
			html += "<thead> \n";
			html += " 	<tr bgcolor='#CCC'> \n";
			html += " 		<th scope='col'>EMISSÃO</th> \n";
			html += " 		<th scope='col'>DIA DA SEMANA</th> \n";
			html += " 		<th scope='col'>HORA</th> \n";
			html += " 		<th scope='col'>VALOR DO LANÇAMENTO</th> \n";
			html += " 		<th scope='col'>DESCRIÇÃO DO LANÇAMENTO</th> \n";
			html += " 		<th scope='col'>CENTRO DE CUSTO</th> \n";
			html += " 		<th scope='col'>STATUS</th> \n";
			html += " 		<th scope='col'>SALDO</th> \n";
			html += " 	</tr> \n";
			html += " </thead> \n";
			html += " <tbody> ";

			while (rs.next()) {

				if (rs.getString(7).equals("Pago")) {
					html += "	<tr>\n";
					html += "		<th scope='row'>" + rs.getString(1) + "</th> \n";
					html += "		<td>" + rs.getString(2) + "</td>\n";
					html += "		<td>" + rs.getString(3) + "</td>\n";
					html += "		<td>" + rs.getString(4) + "</td>\n";
					html += "		<td>" + rs.getString(5) + "</td>\n";
					html += "		<td>" + rs.getString(6) + "</td>\n";
					html += "		<td>" + rs.getString(7) + "</td>\n";
					html += "		<td>" + rs.getString(8) + "</td>\n";
					html += "	</tr>\n";
				} else {
					html += "		<th scope='row' style='background-color: rgb(220,220,220)'>" + rs.getString(1) + "</th>\n";
					html += "		<td style='background-color: rgb(220,220,220)'>" + rs.getString(2) + "</td>\n";
					html += "		<td style='background-color: rgb(220,220,220)'>" + rs.getString(3) + "</td>\n";
					html += "		<td style='background-color: rgb(220,220,220)'>" + rs.getString(4) + "</td>\n";
					html += "		<td style='background-color: rgb(220,220,220)'>" + rs.getString(5) + "</td>\n";
					html += "		<td style='background-color: rgb(220,220,220)'>" + rs.getString(6) + "</td>\n";
					html += "		<td style='background-color: rgb(220,220,220)'>" + rs.getString(7) + "</td>\n";
					html += "		<td style='background-color: rgb(220,220,220)'>" + rs.getString(8) + "</td>\n";
					html += "	</tr>\n";
				}
			}
			html += " </tbody> ";
			html += " </table>";

			pst = conexao.getConexao().prepareStatement(sqlExtrato);
			ResultSet rsTotal = pst.executeQuery();
			double totalGastos = 0;
			while (rsTotal.next()) {
				if (rsTotal.getString(7).equals("Pago")) {
					totalGastos += rsTotal.getDouble(4);
				}
			}

			html += "<br><b>Total de Gastos: </br></b>" + Double.valueOf(df.format(totalGastos).replace(",", "."));
			html += "</body>\n";
			html += "\n" + "</html>";

		} catch (SQLException e) {
		}

		conexao.fechar();
		texto.setContentType("text/html");
		texto.setText(html);
		/*
		int resposta = JOptionPane.showConfirmDialog(null, "Deseja receber o relatório por e-mail?",
				"Receber por E-mail", JOptionPane.YES_NO_OPTION);
		*/
		
		Object[] options = { "Sim", "Não" };
		int resposta = JOptionPane.showOptionDialog(null, "Deseja receber o relatório por e-mail?", "Informação", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		if (resposta == 0) {
			EnvioEmail env = new EnvioEmail(html, titulo, "1");
			env.enviar();
		}

	}

	public void relSemanal(String de, String ate, JTextPane texto) {
		titulo = "Relatório: Gastos Detalhados.";
		String html = " <!DOCTYPE html> \n";
		html += " <html lang='pt-br'> \n";
		html += " <head> \n";
		html += " 	<meta charset='UTF-8'/> \n";
		html += " 	<title>Gastos</title> \n";
		html += " 	<link rel='stylesheet' href='https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css' integrity='sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T' crossorigin='anonymous'> \n";
		html += " 	<style> \n";		
		html += " 		body{ \n";
		html += " 			font-family: Arial, Helvetica, sans-serif";
		html += " 			font-size: 12pt; \n";
		html += " 			background-color: write; \n";
		html += " 			color: black; \n";
		html += " 		} \n";
		html += " 	</style> \n";
		html += " </head> \n";
		html += " <body> \n";

		String sqlSemanal = " SELECT ";
		sqlSemanal += "    STRFTIME('%d/%m/%Y',EMISSAO) AS [EMISSÃO], ";
		sqlSemanal += "    DIAS_SEMANA AS [DIA DA SEMANA], ";
		sqlSemanal += "    VALOR_GASTO AS [VALOR GASTO], ";
		sqlSemanal += "    DESCRICAO AS [DESCRIÇÃO] ";
		sqlSemanal += " FROM ";
		sqlSemanal += "    VW_GASTOSPD ";
		sqlSemanal += " WHERE ";
		sqlSemanal += "    EMISSAO BETWEEN '" + de + "' AND '" + ate + "' ";

		conexao.abrir();
		try {
			pst = conexao.getConexao().prepareStatement(sqlSemanal);
			ResultSet rs = pst.executeQuery();
			html += "<h2 align='center'>GASTOS DETALHADOS</h2> \n";
			html += "<b>De: </b>" + Mascaras.formatData(de, "1") + "<br/>";
			html += "<b>Até: </b>" + Mascaras.formatData(ate, "1") + "<br/><br/>";
			html += "<h3 align='left'>GASTOS DIÁRIOS:</h3> \n";
			html += "<table border='1'>\n";
			html += "<thead> \n";
			html += "<tbody> ";
			html += "<tr bgcolor='#CCC'>\n";
			html += "<th scope='col'>EMISSÃO</th>\n";
			html += "<th scope='col'>DIA_DA_SEMANA</th>\n";
			html += "<th scope='col'>VALOR_GASTO</th>\n";
			html += "<th scope='col'>DETALHES</th>\n";
			html += "</tr>\n";
			html += " </thead> \n";
			html += " <tbody> ";

			while (rs.next()) {
				html += "	<tr>\n";
				html += "		<th scope='row'>" + rs.getString(1) + "</th>\n";
				html += "		<td>" + rs.getString(2) + "</td>\n";
				html += "		<td>" + rs.getString(3) + "</td>\n";
				html += "		<td>" + rs.getString(4) + "</td>\n";
				html += "	</tr>\n";
			}
			html += "</table> \n";
			html += "<br><b>GASTOS POR CENTRO DE CUSTO:</b>\n";

			String sqlCC = "SELECT \n" + "    SUM(VALORLAN),\n" + "    CCUSTO\n" + "FROM \n" + "    SALDOS\n"
					+ "WHERE \n" + "    STATUS='Pago' AND DATA BETWEEN '" + de + "' AND '" + ate + "' AND \n"
					+ "    CCUSTO NOT IN('Investimento Fixo','Investimento Variável') \n" + "GROUP BY\n"
					+ "    CCUSTO\n" + "ORDER BY\n" + "    SUM(VALORLAN) DESC";

			pst = conexao.getConexao().prepareStatement(sqlCC);
			ResultSet rs2 = pst.executeQuery();

			html += "<table border='1'>\n"; 
			html += "<thead>\n";
			html += "<tr bgcolor='#CCC'>\n";
			html += "	<th scope='col'>VALOR</th>\n";
			html += "	<th scope='col'>CENTRO DE CUSTO</th>\n";
			html += "</tr>\n";
			html += "</thead>\n";
			html += "<tbody>";

			while (rs2.next()) {
				html += "	<tr>\n";
				html += "		<td>" + rs2.getString(1) + "</td>\n";
				html += "		<td>" + rs2.getString(2) + "</td>\n";
				html += "	</tr>\n";
			}
			html += "</tbody> \n";
			html += "</table>";
			
			html += "<br><b>COMPARATIVO MENSAL (ÚLTIMOS 6 MESES):</b>";

			//Verifica se existe a tabela TAB_DINAMINA
			String query = "SELECT name FROM sqlite_master WHERE name='TAB_DINAMICA'";
			try {
				PreparedStatement pst = conexao.getConexao().prepareStatement(query);
				ResultSet rs1 = pst.executeQuery();
				while (rs1.next()) {
					if(rs1.getString(1)!="") {
						temTab = true;
					}
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
			}
			
			if(temTab==true) {
				String queryMD = "SELECT * FROM TAB_DINAMICA ORDER BY DIA_MES";
				pst = conexao.getConexao().prepareStatement(queryMD);
				ResultSet rs3 = pst.executeQuery();
				ResultSetMetaData rsmd = rs3.getMetaData();
				
				
				html += "<table border='1'>\n";
				html += "<thead>";
				html += " <tr bgcolor='#CCC'>\n";
				for (int i = 1; i < rsmd.getColumnCount()+1; i++) {
					html += " <th scope='col'>"+rsmd.getColumnName(i).toString()+"</th>\n";
				}
				html += "</tr>\n";
				html += "</thead>\n";
				html += "<tbody>\n";
				 
				
				
				while(rs3.next()) {
					html += "	<tr>\n";
					for (int i = 1; i < rsmd.getColumnCount()+1; i++) {					
							html += " <td>" + rs3.getString(i) + "</td>\n";
						}
					html += "	</tr>\n";
				}
				html += "</tbody>\n";
				html += "</table> \n";
			}
			
			
			String qryGA2 = "SELECT MESES_ANO, PRINTF('%.2f', MAX(VALOR_GASTO)) AS MAXIMO FROM VW_GASTOS_MENSAIS";
			pst = conexao.getConexao().prepareStatement(qryGA2);
			ResultSet rs4 = pst.executeQuery();
			html += "<br><b>Maior: "+rs4.getString(1)+" | R$ "+rs4.getDouble(2)+"</b>";
			
			String qryGA3 = "SELECT MESES_ANO, PRINTF('%.2f', MIN(VALOR_GASTO)) AS MINIMO FROM VW_GASTOS_MENSAIS";
			pst = conexao.getConexao().prepareStatement(qryGA3);
			ResultSet rs5 = pst.executeQuery();
			html += "<br><b>Menor: "+rs5.getString(1)+" | R$ "+rs5.getDouble(2)+"</b>";
			html += "</body>\n";
			html += "</html>";

		} catch (SQLException e) {JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());}
		
		

		conexao.fechar();
		texto.setContentType("text/html");
		texto.setText(html);
		
		/*
		int resposta = JOptionPane.showConfirmDialog(null, "Deseja receber o relatório por e-mail?",
				"Receber por E-mail", JOptionPane.YES_NO_OPTION);
		*/		
		Object[] options = { "Sim", "Não" };
		int resposta = JOptionPane.showOptionDialog(null, "Deseja receber o relatório por e-mail?", "Informação", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		if (resposta == 0) {
			EnvioEmail env = new EnvioEmail(html, titulo, "1");
			env.enviar();
		}
		

	}
}
