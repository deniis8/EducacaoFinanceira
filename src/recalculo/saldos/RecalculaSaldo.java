package recalculo.saldos;

import mascaras.Mascaras;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import configuracao.banco.dados.ConexaoBancoDados;
import consultas.Consultas;

/**
 *
 * @author adenilson.soares
 * Essa classe deixou de ser usada, o recalculo passou a ser realizado pela 
 * procedure ATUALIZA_SALDOS
 */
public class RecalculaSaldo implements Runnable {

    ConexaoBancoDados conexao = new ConexaoBancoDados();
    Consultas consulta = new Consultas();
    PreparedStatement pst;
    JProgressBar pB;
    JDialog dlg;
    JLabel lblValor;
    JLabel lblSaldo;
    JLabel lblInvF;
    JLabel lblInvV;
    String idReg;
    int quantReg = 0;
    int contReg = 1;
    String data = "";

    public RecalculaSaldo(JProgressBar pB, JDialog dlg, JLabel lblValor, JLabel lblSaldo, JLabel lblInvF, JLabel lblInvV, String idReg, String data) {
        this.pB = pB;
        this.dlg = dlg;
        this.lblValor = lblValor;
        this.lblSaldo = lblSaldo;
        this.lblInvF = lblInvF;
        this.lblInvV = lblInvV;
        this.idReg = idReg;
        this.data = data;
    }
    
    public void quantReg(){        
        String sqlCont = "SELECT COUNT(*) FROM LANCAMENTOS WHERE ";
        if(idReg!="") {
        	sqlCont +=" ID_LANC >="+idReg+" AND";
        }
        sqlCont +=" D_E_L_E_T_<>'*'";
        
        conexao.abrir();
        try {
            pst = conexao.getConexao().prepareStatement(sqlCont);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
            	quantReg = rs.getInt(1);
            }
            pB.setMaximum(quantReg);
        } catch (SQLException ex) {
        	JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }        
    }

    public void run() {
        quantReg();
        DecimalFormat df = new DecimalFormat("0.00");
        double saldo = 0;

        String sql = "SELECT \n";
        sql += "     LAN.ID_LANC, \n";
        sql += "     LAN.DATA_HORA, \n";
        sql += "     LAN.VALOR, \n";
        sql += "     LAN.STATUS_LANC, \n";
        sql += "     CC.DESCRI, \n";
        sql += "     LAN.DESCRICAO, \n";
        sql += "     TIME(DATA_HORA) AS HORA_LANCAMENTO \n";
        sql += " FROM \n";  
        sql += "     LANCAMENTOS AS LAN INNER JOIN \n"; 
        sql += "     CCUSTO AS CC ON LAN.ID_CCUSTO=CC.ID_CCUSTO \n";    
        sql += " WHERE \n";
        if(idReg!="") {
        	sql += " LAN.DATA_HORA >='"+Mascaras.formatData(data, "2")+"' AND \n";
        }
        sql += "     LAN.D_E_L_E_T_<>'*' AND CC.D_E_L_E_T_<>'*' \n";
        sql += " ORDER BY \n";
        sql += "	 LAN.DATA_HORA, LAN.DATA_CRIACAO ";  
        
        String sqlDel="";
        if(idReg=="") {
        	sqlDel = "TRUNCATE TABLE SALDOS ";
        }
        else {        
        	sqlDel = "DELETE FROM SALDOS WHERE DATA_HORA >='"+Mascaras.formatData(data, "2")+"'"; 
        }
        try {
            pst = conexao.getConexao().prepareStatement(sqlDel);
            pst.executeUpdate();
        } catch (SQLException ex) {
        	JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        }    
        
        if(idReg!="") {	        
	        String ultSa = "SELECT ID_SALDO, SALDO FROM SALDOS ORDER BY DATA_HORA DESC LIMIT 1";
	        try {
	        	PreparedStatement pst = conexao.getConexao().prepareStatement(ultSa);
	            ResultSet rst = pst.executeQuery();
	            while (rst.next()) {
	            	saldo = saldo+rst.getDouble(2);
	            }
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
			}	        
        } 

        try {
            pst = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                if (rs.getString(4).equals("Pago")) {
                    saldo = saldo - rs.getFloat(3);
                    insertRec(rs.getString(2), rs.getDouble(3), rs.getString(6), Double.valueOf(df.format(saldo).replace(",", ".")), rs.getString(4), rs.getString(5), rs.getString(1));
                    lblValor.setText("Calculando valores... " + Double.valueOf(df.format(saldo).replace(",", ".")));
                } 
                else if (rs.getString(4).equals("Recebido")) {
                    saldo = saldo + rs.getFloat(3);
                    insertRec(rs.getString(2), rs.getDouble(3), rs.getString(6), Double.valueOf(df.format(saldo).replace(",", ".")), rs.getString(4), rs.getString(5), rs.getString(1));
                    lblValor.setText("Calculando valores... " + Double.valueOf(df.format(saldo).replace(",", ".")));
                }
                pB.setValue(contReg);
                contReg++;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
        }
        pB.setValue(0);        
        dlg.dispose();
        conexao.fechar();        
        consulta.selectUlt(lblSaldo);
        consulta.selectInvF(lblInvF);
        consulta.selectInvV(lblInvV);
    }

    public void insertRec(String data, double valorLan, String descriLan, double saldo, String status, String ccusto, String idLanc) {

        String sql = "INSERT INTO SALDOS(DATA_HORA, VALORLAN, DESCRILAN, SALDO, STATUS_LANC, CCUSTO, ID_LANC, ID_USUARIO) VALUES (?,?,?,?,?,?,?,?)";
        try {
            pst = conexao.getConexao().prepareStatement(sql);
            pst.setString(1, data);
            pst.setDouble(2, valorLan);
            pst.setString(3, descriLan);
            pst.setDouble(4, saldo);
            pst.setString(5, status);
            pst.setString(6, ccusto);
            pst.setString(7, idLanc);
            pst.setInt(8, 1);
            pst.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro no Recalculo | (Tabela: LANCAMENTOS) Erro: " + e.getMessage());
        }
    }

}