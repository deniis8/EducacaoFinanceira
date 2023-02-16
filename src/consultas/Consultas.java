package consultas;

import mascaras.Mascaras;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import configuracao.banco.dados.ConexaoBancoDadosSQLite;

/**
 *
 * @author adenilson.soares
 */
public class Consultas {
	private double totalAP;
	private double totalP;
	private double totalAR;
	private double totalR;
	DecimalFormat dFN = new DecimalFormat("0.00");
	ConexaoBancoDadosSQLite conexao = new ConexaoBancoDadosSQLite();
	PreparedStatement pst;

	public void selectLanca(DefaultTableModel modTabLanc, JTable table, JLabel lblTotalAP, JLabel lblTotalP, JLabel lblTotalAR, JLabel lblTotalR) throws ParseException {
		totalAP = 0;
		totalP = 0;
		totalAR = 0;
		totalR = 0;
		String sql = "SELECT ID, DATA_HORA, DIAS_SEMANA, VALOR, DESCRICAO, CONCAT_WS(' - ',CODCCUSTO, CCENTRO) AS CENTRO_CUSTO, STATUS_LANC FROM VW_LANCAMENTOS ";
		conexao.abrir();
		zeraTabelaPrincipal(modTabLanc);
		try {
			pst = conexao.getConexao().prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			//table.setModel(modTabLanc);
			while (rs.next()) {
				modTabLanc.addRow(new Object[] { rs.getString(1), Mascaras.formatData(rs.getString(2), "1"), rs.getString(3), rs.getDouble(4),
						rs.getString(5), rs.getString(6), rs.getString(7) });
				table.setModel(modTabLanc);
				if(rs.getString(7).equals("A Pagar")) {
					totalAP += rs.getDouble(4);
				}else if(rs.getString(7).equals("Pago")) {
					totalP += rs.getDouble(4);
				}else if(rs.getString(7).equals("A Receber")) {
					totalAR += rs.getDouble(4);
				}else if(rs.getString(7).equals("Recebido")) {
					totalR += rs.getDouble(4);
				}
			}
			lblTotalAP.setText("A Pagar...: " + new DecimalFormat("#,##0.00").format(totalAP));
			lblTotalP.setText("Pago........: " + new DecimalFormat("#,##0.00").format(totalP));
			lblTotalAR.setText("A Receber....: " + new DecimalFormat("#,##0.00").format(totalAR));
			lblTotalR.setText("Recebido......: " + new DecimalFormat("#,##0.00").format(totalR));
			
		} catch (SQLException e) {
			System.out.println("Erro: " + e.getMessage());
		}

		conexao.fechar();
	}

	public void selectUlt(JLabel lblSaldo) {
		String slqUlt = "SELECT SALDO FROM SALDOS ORDER BY DATA_HORA DESC, ID_LANC DESC LIMIT 1";

		conexao.abrir();
		try {
			pst = conexao.getConexao().prepareStatement(slqUlt);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				lblSaldo.setText("Saldo Bancário......: " + new DecimalFormat("#,##0.00").format(rs.getDouble(1)));
			}
			/*if (rs.getDouble(1) > 0) {
				lblSaldo.setForeground(new Color(5, 113, 0));
			} else {
				lblSaldo.setForeground(new Color(225, 23, 23));
			}*/
		} catch (Exception e) {
		}
		conexao.fechar();
	}

	public void selectInvF(JLabel lblInvF) {
		//String slqUlt = "SELECT PRINTF('%.2f',SUM(VALORLAN)) FROM SALDOS WHERE CCUSTO IN('Investimento Fixo','Juros Positivo')";
		String slqUlt = "SELECT SUM(VALORLAN) FROM SALDOS WHERE CCUSTO IN('Investimento Fixo')";

		conexao.abrir();
		try {
			pst = conexao.getConexao().prepareStatement(slqUlt);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				lblInvF.setText("Investimento Fixo..: " + new DecimalFormat("#,##0.00").format(rs.getDouble(1)));
			}
			/*if (rs.getDouble(1) > 0) {
				lblInvF.setForeground(new Color(5, 113, 0));
			} else {
				lblInvF.setForeground(new Color(225, 23, 23));
			}*/
		} catch (Exception e) {
		}
		conexao.fechar();
	}

	public void selectInvV(JLabel lblInvV) {
		String slqUlt = "SELECT SUM(VALORLAN) FROM SALDOS WHERE CCUSTO IN('Investimento Variável')";

		conexao.abrir();
		try {
			pst = conexao.getConexao().prepareStatement(slqUlt);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				lblInvV.setText("Investimento Variável...: " + new DecimalFormat("#,##0.00").format(rs.getDouble(1)));
			}
			/*if (rs.getDouble(1) > 0) {
				lblInvV.setForeground(new Color(5, 113, 0));
			} else {
				lblInvV.setForeground(new Color(225, 23, 23));
			}*/
		} catch (Exception e) {
		}
		conexao.fechar();
	}

	public void pesqLan(DefaultTableModel modelo, JTable tabela, JDateChooser de, JDateChooser ate, JCheckBox recebido, JCheckBox pago, JCheckBox aPagar, JCheckBox aReceber, JLabel lblTotalAP, JLabel lblTotalP, JLabel lblTotalAR, JLabel lblTotalR, JComboBox<?> boxCC) {	
		totalAP = 0;
		totalP = 0;
		totalAR = 0;
		totalR = 0;
		String receb = "";
		String pag = "";
		String aPag = "";
		String aReceb = "";
		if((recebido.isSelected() || pago.isSelected() || aPagar.isSelected() || aReceber.isSelected()) && (de.getDate() != null && ate.getDate() != null)) {
			zeraTabelaPrincipal(modelo);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String deN = df.format(de.getDate());
	        String ateN = df.format(ate.getDate());
			String query = "SELECT ID, DATA_HORA, DIAS_SEMANA, VALOR, DESCRICAO, CONCAT_WS(' - ',CODCCUSTO, CCENTRO) AS DETALHE_CCUSTO, STATUS_LANC FROM VW_LANCAMENTOS WHERE "; 
			
					
					if(recebido.isSelected()) {
						receb = "Recebido";   
					}
					if(pago.isSelected()) {
						pag = "Pago"; 
					}
					if(aPagar.isSelected()) {
						aPag = "A Pagar"; 
					}
					if(aReceber.isSelected()) {
						aReceb = "A Receber"; 
					}			
					query+="STATUS_LANC IN('"+receb+"','"+pag+"','"+aPag+"','"+aReceb+"') AND ";
					if(!boxCC.getSelectedItem().equals("Todos")) {
						query+="CCENTRO='"+boxCC.getSelectedItem()+"' AND ";
					}					
					query+="DATA_HORA BETWEEN '"+deN+"' AND '"+ateN+"' ";

			conexao.abrir();
			try {
				pst = conexao.getConexao().prepareStatement(query);
				ResultSet rs = pst.executeQuery();
				tabela.setModel(modelo);
				while (rs.next()) {
					modelo.addRow(new Object[]{rs.getString(1),Mascaras.formatData(rs.getString(2), "1"), rs.getString(3), rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7)});
					tabela.setModel(modelo);
					if(rs.getString(7).equals("A Pagar")) {
						totalAP += rs.getDouble(4);
					}else if(rs.getString(7).equals("Pago")) {
						totalP += rs.getDouble(4);
					}else if(rs.getString(7).equals("A Receber")) {
						totalAR += rs.getDouble(4);
					}else if(rs.getString(7).equals("Recebido")) {
						totalR += rs.getDouble(4);
					}
				}
				
				lblTotalAP.setText("A Pagar...: " + new DecimalFormat("#,##0.00").format(totalAP));
				lblTotalP.setText("Pago........: " + new DecimalFormat("#,##0.00").format(totalP));
				lblTotalAR.setText("A Receber....: " + new DecimalFormat("#,##0.00").format(totalAR));
				lblTotalR.setText("Recebido......: " + new DecimalFormat("#,##0.00").format(totalR));
				
			} catch (Exception e) {
			}
			conexao.fechar();
		}		
	}

	public void selectComboboxCentroCusto(JComboBox<String> itens, String opc) {
		String query = "SELECT DESCRI FROM CCUSTO WHERE D_E_L_E_T_<>'*' ORDER BY DESCRI";
		ArrayList<String> cCusto = new ArrayList<>();
		if(opc.equals("S")) {
			itens.removeAllItems();
		}		
		conexao.abrir();
		try {
			pst = conexao.getConexao().prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				cCusto.add(rs.getString(1));
			}
			for (String a : cCusto) {
				itens.addItem(a);
			}
		} catch (SQLException e) {
		}
		conexao.fechar();

	}
	
	public void selectLanJ(String cod, JFormattedTextField txtEmiss, JFormattedTextField txtHora, JTextField txtValor, JTextField txtDesc, JComboBox<?> boxStatus, JComboBox<?> boxCCusto) {
	
		String query = " SELECT "; 	   
		query += "     ID_LANC, ";       
		query += "     CAST(DATA_HORA AS DATE) AS DATA_LANCAMENTO, ";        
		query += "     TIME(DATA_HORA) AS HORA_LANCAMENTO, ";   
		query += "     VALOR, ";       
		query += "     STATUS_LANC, ";       
		query += "     DESCRICAO, ";       
		query += "     (SELECT DESCRI FROM CCUSTO WHERE ID_CCUSTO=LAN.ID_CCUSTO AND D_E_L_E_T_<>'*') AS CCDESC ";   
		query += " FROM ";       
		query += "     LANCAMENTOS AS LAN ";   
		query += " WHERE ";       
		query += "     ID_LANC='"+cod+"' AND D_E_L_E_T_<>'*' "; 
		
		conexao.abrir();
		try {
			pst = conexao.getConexao().prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				txtEmiss.setText(Mascaras.formatData(rs.getString(2),"1"));
				txtHora.setText(rs.getString(3));
				txtValor.setText(rs.getString(4));
				txtDesc.setText(rs.getString(6));
				boxStatus.setSelectedItem(rs.getString(5));
				boxCCusto.setSelectedItem(rs.getString(7));
			}
		} catch (SQLException e) {JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());} 
		conexao.fechar();
	}

	public void zeraTabelaPrincipal(DefaultTableModel modelTable) {
		for (int i = modelTable.getRowCount() - 1; i >= 0; i--) {
			modelTable.removeRow(i);
		}
	}
}