package pivot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import configuracao.banco.dados.ConexaoBancoDadosSQLite;

public class Pivot {

	ConexaoBancoDadosSQLite conexao = new ConexaoBancoDadosSQLite();

	/**
	 * Cria a tabela dinâmica e insere os registros
	 */
	public void criarTab(int dia) {
		String campos[] = null;
		double valor[] = null;
		int i = 0;
		boolean exec = true;
		String StringDia = "";
		boolean temReg = true;
		//int resposta = JOptionPane.showConfirmDialog(null, "Deseja Desconsiderar os Investimentos?", "Desconsiderar Investimentos", JOptionPane.YES_NO_OPTION);
		Object[] options = { "Sim", "Não" };
		int resposta = JOptionPane.showOptionDialog(null, "Deseja Desconsiderar os Investimentos?", "Informação", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		
		while (dia > 0) {			
			
			///////////////////////////////////////
			String dropVw = "DROP VIEW VW_COMPARATIVO_DIA_MES";
			conexao.abrir();
			try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(dropVw);
			pst.execute();
			} catch (SQLException e) {
				//JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
			}
			conexao.fechar();
			///////////////////////////////////////
			if(Integer.toString(dia).length()==1) {
				StringDia = "0"+Integer.toString(dia);
			}else {
				StringDia = Integer.toString(dia);
			}
			String createVw = "";
			createVw += " CREATE VIEW VW_COMPARATIVO_DIA_MES \n";
			createVw += " AS \n";
			createVw += " SELECT \n";
			createVw += "     CASE SUBSTR(DATA,6,2) \n";
			createVw += "         WHEN '01' THEN 'JANEIRO' \n";
			createVw += "         WHEN '02' THEN 'FEVEREIRO' \n";
			createVw += "         WHEN '03' THEN 'MARCO' \n";
			createVw += "         WHEN '04' THEN 'ABRIL' \n";
			createVw += "         WHEN '05' THEN 'MAIO' \n";
			createVw += "         WHEN '06' THEN 'JUNHO' \n";
			createVw += "         WHEN '07' THEN 'JULHO' \n";
			createVw += "         WHEN '08' THEN 'AGOSTO' \n";
			createVw += "         WHEN '09' THEN 'SETEMBRO' \n";
			createVw += "         WHEN '10' THEN 'OUTUBRO' \n";
			createVw += "         WHEN '11' THEN 'NOVEMBRO' \n";
			createVw += "         WHEN '12' THEN 'DEZEMBRO' \n";
			createVw += "     END || '_'||SUBSTR(DATA,1,4) AS [MES], \n";
			createVw += "     PRINTF('%.2f',SUM(VALORLAN)) AS [VALOR] \n";
			createVw += " FROM \n";
			createVw += "     SALDOS \n";
			createVw += " WHERE \n";			
            if (resposta == 0) {
            	createVw += " CCUSTO NOT IN('Investimento Fixo','Investimento Variável') AND \n";
            }
            createVw += " STATUS='Pago' AND DATA >= date('now','start of month','-5 month','0 day') AND SUBSTR(DATA,9,2)<='" + StringDia + "' \n";
			createVw += " GROUP BY \n";
			createVw += "     date(DATA, 'start of month', '+1 month', '-1 day') \n";
			createVw += " ORDER BY \n";
			createVw += "     DATA \n";
			
			conexao.abrir();
			
			try {
			PreparedStatement pst = conexao.getConexao().prepareStatement(createVw);
			pst.execute();
			} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
			}
			conexao.fechar();
			if(exec) {				
				///////////////////////////////////////
				String drop = "DROP TABLE TAB_DINAMICA";
				conexao.abrir();
				try {
				PreparedStatement pst = conexao.getConexao().prepareStatement(drop);
				pst.execute();
				} catch (SQLException e) {
					//JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
				}
				conexao.fechar();
			}
			
			///////////////////////////////////////
			String queryCont = "SELECT COUNT(*) FROM VW_COMPARATIVO_DIA_MES";
			conexao.abrir();
			try {
				PreparedStatement pst = conexao.getConexao().prepareStatement(queryCont);
				ResultSet rs = pst.executeQuery();
				while (rs.next()) {
					if(rs.getInt(1)==0) {
						temReg = false;
					}else {
						campos = new String[rs.getInt(1)];
						valor = new double[rs.getInt(1)];
					}					
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
			}
			conexao.fechar();
			
			///////////////////////////////////////
			if(temReg==true) {				
				String query = "SELECT MES, VALOR FROM VW_COMPARATIVO_DIA_MES";
				conexao.abrir();
				try {
					PreparedStatement pst = conexao.getConexao().prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					while (rs.next()) {
						campos[i] = rs.getString(1);
						valor[i] = rs.getDouble(2);
						i++;
					}
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
				}
				conexao.fechar();

				if(exec) {
					///////////////////////////////////////
					String create = "CREATE TABLE TAB_DINAMICA\n(\n   DIA_MES INTEGER, \n";
					conexao.abrir();
					for (int j = 0; j < campos.length; j++) {
						create += "  " + campos[j] + " VARCHAR(20) DEFAULT (0), \n";
					}
					int tamanho = create.length();
					create = create.substring(0, tamanho - 3);
					create += "\n)";
					try {
						PreparedStatement pst = conexao.getConexao().prepareStatement(create);
						pst.execute();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
					}
					conexao.fechar();	
				}
				
				///////////////////////////////////////
				String insert = " INSERT INTO TAB_DINAMICA(DIA_MES,";
				conexao.abrir();
				for (int j = 0; j < campos.length; j++) {
					insert += campos[j] + ", ";
				}
				int tamanho2 = insert.length();
				insert = insert.substring(0, tamanho2 - 2);
				insert += ")VALUES(" + dia + ", ";
				for (int j = 0; j < valor.length; j++) {
					insert += valor[j] + ", ";
				}
				int tamanho3 = insert.length();
				insert = insert.substring(0, tamanho3 - 2);
				insert += ")";

				try {
					PreparedStatement pst = conexao.getConexao().prepareStatement(insert);
					pst.execute();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
				}
				conexao.fechar();

			}			
			exec = false;
			temReg = true;
			dia--;
			i = 0;
		}
	}
}
