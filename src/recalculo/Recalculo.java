package recalculo;

import sql.Consultas;
import conexao.Conexao;
import mascaras.Mascaras;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author adenilson.soares
 */
public class Recalculo implements Runnable {

    Conexao conexao = new Conexao();
    Consultas consulta = new Consultas();
    PreparedStatement pst;
    JProgressBar pB;
    JDialog dlg;
    JLabel lblValor;
    JLabel lblSaldo;
    JLabel lblInvF;
    String idReg;
    int quantReg = 0;
    int contReg = 1;
    String data = "";
    String idSaldo ="000001";

    public Recalculo(JProgressBar pB, JDialog dlg, JLabel lblValor, JLabel lblSaldo, JLabel lblInvF, String idReg, String data) {
        this.pB = pB;
        this.dlg = dlg;
        this.lblValor = lblValor;
        this.lblSaldo = lblSaldo;
        this.lblInvF = lblInvF;
        this.idReg = idReg;
        this.data = data;
    }
    
    public void quantReg(){        
        String sqlCont = "SELECT COUNT(*) FROM LANCAMENTOS WHERE ";
        if(idReg!="") {
        	sqlCont +=" ID_LANC >='"+StringUtils.leftPad(idReg, 6, "0")+"' AND";
        }
        sqlCont +=" D_E_L_E_T_<>'*'";
        
        conexao.abrir();
        try {
            pst = conexao.getConexao().prepareStatement(sqlCont);
            ResultSet rs = pst.executeQuery();
            quantReg = rs.getInt(1);
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
        sql += "     LAN.DATA, \n";
        sql += "     LAN.VALOR, \n";
        sql += "     LAN.STATUS, \n";
        sql += "     CC.DESCRI, \n";
        sql += "     LAN.DESCRICAO, \n";
        sql += "     LAN.HORA \n";
        sql += " FROM \n";  
        sql += "     LANCAMENTOS AS LAN INNER JOIN \n"; 
        sql += "     CCUSTO AS CC ON LAN.ID_CCUSTO=CC.ID_CCUSTO \n";    
        sql += " WHERE \n";
        if(idReg!="") {
        	sql += " LAN.DATA >='"+Mascaras.formatData(data, "2")+"' AND \n";
        }
        sql += "     LAN.D_E_L_E_T_<>'*' AND CC.D_E_L_E_T_<>'*' \n";
        sql += " ORDER BY \n";
        sql += "	 LAN.DATA, LAN.HORA ";          
        
        String sqlDel = "DELETE FROM SALDOS ";
        if(idReg!="") {
        	sqlDel += " WHERE DATA>='"+Mascaras.formatData(data, "2")+"'"; 
        }
        try {
            pst = conexao.getConexao().prepareStatement(sqlDel);
            pst.executeUpdate();
        } catch (SQLException ex) {
        	JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        } 
        
        if(idReg!="") {	        
	        String ultSa = "SELECT ID_SALDO, SALDO FROM SALDOS ORDER BY DATA DESC, HORA DESC LIMIT 1";
	        try {
	        	PreparedStatement pst = conexao.getConexao().prepareStatement(ultSa);
	            ResultSet rst = pst.executeQuery();
	            idSaldo = Integer.toString(Integer.parseInt(rst.getString(1))+1);
	            saldo = saldo+rst.getDouble(2);
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
                    insertRec(StringUtils.leftPad(idSaldo,6,"0"),rs.getString(2), rs.getString(7), rs.getDouble(3), rs.getString(6), Double.valueOf(df.format(saldo).replace(",", ".")), rs.getString(4), rs.getString(5), rs.getString(1));
                    lblValor.setText("Calculando valores... " + Double.valueOf(df.format(saldo).replace(",", ".")));
                    idSaldo = Integer.toString(Integer.parseInt(idSaldo)+1);
                } 
                else if (rs.getString(4).equals("Recebido")) {
                    saldo = saldo + rs.getFloat(3);
                    insertRec(StringUtils.leftPad(idSaldo,6,"0"),rs.getString(2), rs.getString(7), rs.getDouble(3), rs.getString(6), Double.valueOf(df.format(saldo).replace(",", ".")), rs.getString(4), rs.getString(5), rs.getString(1));
                    lblValor.setText("Calculando valores... " + Double.valueOf(df.format(saldo).replace(",", ".")));
                    idSaldo = Integer.toString(Integer.parseInt(idSaldo)+1);
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
    }

    public void insertRec(String idSaldo, String data, String hora, double valorLan, String descriLan, double saldo, String status, String ccusto, String idLanc) {

        String sql = "INSERT INTO SALDOS(ID_SALDO, DATA, HORA, VALORLAN, DESCRILAN, SALDO, STATUS, CCUSTO, ID_LANC) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            pst = conexao.getConexao().prepareStatement(sql);
            pst.setString(1, idSaldo);
            pst.setString(2, data);
            pst.setString(3, hora);
            pst.setDouble(4, valorLan);
            pst.setString(5, descriLan);
            pst.setDouble(6, saldo);
            pst.setString(7, status);
            pst.setString(8, ccusto);
            pst.setString(9, idLanc);
            pst.executeUpdate();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro no Recalculo | (Tabela: LANCAMENTOS) Erro: " + e.getMessage());
        }
    }

}