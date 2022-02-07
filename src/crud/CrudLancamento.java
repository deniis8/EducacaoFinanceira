package crud;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;

import configuracao.banco.dados.ConexaoBancoDadosSQLite;
import interfaces.CrudLancamentos;
import mascaras.Mascaras;

public class CrudLancamento implements CrudLancamentos{

	ConexaoBancoDadosSQLite conexao = new ConexaoBancoDadosSQLite();
	private PreparedStatement pst;
	private PreparedStatement pst2;
	private String idLan = "";
	
	

	public PreparedStatement getPst() {
		return pst;
	}



	public void setPst(PreparedStatement pst) {
		this.pst = pst;
	}

	@Override
	public void alterarLancamento(String cod, String data, String hora, double valor, String desc, JComboBox<?> status, JComboBox<?> boxCC) {
        String rStatus = status.getSelectedItem().toString();
        String sqlCC = "SELECT ID_CCUSTO FROM CCUSTO WHERE DESCRI='" + boxCC.getSelectedItem() + "'";
        int idCC = 0;

        conexao.abrir();

        try {
            setPst(conexao.getConexao().prepareStatement(sqlCC));
            ResultSet rs = getPst().executeQuery();
            while (rs.next()) {
                idCC = rs.getInt(1);
            }
        } catch (SQLException e) {
        	JOptionPane.showMessageDialog(null,"Erro: " + e.getMessage());
        }

        String sqlCli = "UPDATE LANCAMENTOS SET \n"
                + "    DATA = ?,\n"
                + "    HORA = ?,\n"
                + "    VALOR = ?,\n"
                + "    STATUS = ?,\n"
                + "    DESCRICAO = ?,\n"
                + "    ID_CCUSTO = ?\n"
                + "WHERE \n"
                + "    ID_LANC = '" + cod+"'";

        try {
            setPst(conexao.getConexao().prepareStatement(sqlCli));
            getPst().setString(1, Mascaras.formatData(data, "2"));
            getPst().setString(2, hora);
            getPst().setDouble(3, valor);
            getPst().setString(4, rStatus);
            getPst().setString(5, desc);
            getPst().setInt(6, idCC);

            getPst().executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Erro: " + e.getMessage());
        }
        conexao.fechar();
    }
	
	@Override
	public void incluirLancamento(String id, String data, String hora, Double valor, String descri, String status, JComboBox<String> boxCC) {
		String sqlCC = "SELECT ID_CCUSTO FROM CCUSTO WHERE DESCRI='" + boxCC.getSelectedItem() + "'";
		int idCC = 0;
		idLan = id;

		conexao.abrir();

		try {
			pst = conexao.getConexao().prepareStatement(sqlCC);
			ResultSet rs = pst.executeQuery();
			idCC = rs.getInt(1);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}

		String insert = "";
		insert = "INSERT INTO LANCAMENTOS(ID_LANC, DATA, HORA, VALOR, STATUS, DESCRICAO, ID_CCUSTO, D_E_L_E_T_) ";
		insert += "VALUES(?,?,?,?,?,?,?,?)";
		try {
			pst = conexao.getConexao().prepareStatement(insert);
			pst.setString(1, StringUtils.leftPad(idLan, 6, "0"));
			pst.setString(2, Mascaras.formatData(data, "2"));
			pst.setString(3, hora);
			pst.setDouble(4, valor);
			pst.setString(5, status);
			pst.setString(6, descri);
			pst.setInt(7, idCC);
			pst.setString(8, "");
			pst.executeUpdate();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();
		insereSald(data, hora, valor, descri, status, boxCC, idLan);
		
	}
	
    public void insereSald(String data, String hora, double valorLan, String descriLan, String status, JComboBox<String> ccusto, String idLan) {
        DecimalFormat df = new DecimalFormat("0.00");
        String sqlSaldoF = "SELECT SALDO FROM SALDOS ORDER BY ID_SALDO DESC LIMIT 1";
        double saldoF = 0;
        boolean isExecS = false;
        
        conexao.abrir();
        try {
            pst = conexao.getConexao().prepareStatement(sqlSaldoF);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
            	if(rs.getString(1)!=null) {
            		saldoF = Double.parseDouble(rs.getString(1));
            	}
            }

        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }
        
        String sqlIdSaldo = "SELECT MAX(ID_SALDO) FROM SALDOS";
        String idSaldo = "1";
        try {
        	pst2 = conexao.getConexao().prepareStatement(sqlIdSaldo);
            ResultSet rs = pst2.executeQuery();
            if(rs.getString(1)!=null) {
            	idSaldo = Integer.toString(Integer.parseInt(rs.getString(1))+1);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
        }

        String sql = "INSERT INTO SALDOS(ID_SALDO, DATA, HORA, VALORLAN, DESCRILAN, SALDO, STATUS, CCUSTO, ID_LANC) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            pst = conexao.getConexao().prepareStatement(sql);
            pst.setString(1, StringUtils.leftPad(idSaldo, 6, "0"));
            pst.setString(2, Mascaras.formatData(data, "2"));
            pst.setString(3, hora);
            pst.setDouble(4, valorLan);
            pst.setString(5, descriLan);
            if (status.equals("Pago")) {
                pst.setDouble(6, Double.valueOf(df.format(saldoF - valorLan).replace(",", ".")));
                isExecS = true;
            } 
            else if(status.equals("Recebido")){
                pst.setDouble(6, Double.valueOf(df.format(saldoF + valorLan).replace(",", ".")));
                isExecS = true;
            }
            pst.setString(7, status);
            pst.setString(8, ccusto.getSelectedItem().toString());
            pst.setString(9, StringUtils.leftPad(idLan, 6, "0"));
            if(isExecS) {
            	pst.executeUpdate();
            }            

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro no Recalculo | (Tabela: LANCAMENTOS) Erro: " + e.getMessage());
        }
        conexao.fechar();
        
    }
	
    @Override
	public void excluirLancamento(String cod) {
		String del = "UPDATE LANCAMENTOS SET D_E_L_E_T_=? WHERE ID_LANC='"+cod+"'";
		conexao.abrir();
		try {
			pst = conexao.getConexao().prepareStatement(del);
			pst.setString(1, "*");
			pst.executeUpdate();
		} catch (Exception e) {}		
		conexao.fechar();
	}
}
