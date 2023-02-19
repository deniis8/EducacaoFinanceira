package crud;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;

import configuracao.banco.dados.ConexaoBancoDados;
import interfaces.CrudLancamentos;
import mascaras.Mascaras;

public class CrudLancamento implements CrudLancamentos{

	ConexaoBancoDados conexao = new ConexaoBancoDados();
	private PreparedStatement pst;
	private int idLan = 0;
	
	

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
                + "    DATA_HORA = ?,\n"
                + "    VALOR = ?,\n"
                + "    STATUS_LANC = ?,\n"
                + "    DESCRICAO = ?,\n"
                + "    ID_CCUSTO = ?\n"
                + "WHERE \n"
                + "    ID_LANC = '" + cod+"'";

        try {
            setPst(conexao.getConexao().prepareStatement(sqlCli));
            getPst().setString(1, Mascaras.formatData(data, "2") + " " + hora);
            getPst().setDouble(2, valor);
            getPst().setString(3, rStatus);
            getPst().setString(4, desc);
            getPst().setInt(5, idCC);

            getPst().executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Erro: " + e.getMessage());
        }
        conexao.fechar();
    }
	
	@Override
	public void incluirLancamento(String data, String hora, Double valor, String descri, String status, JComboBox<String> boxCC) {
		String sqlCC = "SELECT ID_CCUSTO FROM CCUSTO WHERE DESCRI='" + boxCC.getSelectedItem() + "'";
		int idCC = 0;

		conexao.abrir();

		try {
			pst = conexao.getConexao().prepareStatement(sqlCC);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				idCC = rs.getInt(1);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}

		String insert = "";
		insert = "INSERT INTO LANCAMENTOS(DATA_HORA, VALOR, STATUS_LANC, DESCRICAO, ID_CCUSTO, ID_USUARIO, D_E_L_E_T_) ";
		insert += "VALUES(?,?,?,?,?,?,?)";
		try {
			pst = conexao.getConexao().prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			pst.setString(1, Mascaras.formatData(data, "2") + " " + hora);
			pst.setDouble(2, valor);
			pst.setString(3, status);
			pst.setString(4, descri);
			pst.setInt(5, idCC);
			pst.setInt(6, 1);
			pst.setString(7, "");
			pst.executeUpdate();
			
			ResultSet rs = pst.getGeneratedKeys();
			if(rs.next()){
				idLan = rs.getInt(1);
			}		
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();
		insereSaldo(data, hora, valor, descri, status, boxCC, idLan);
		
	}
	
    public void insereSaldo(String data, String hora, double valorLan, String descriLan, String status, JComboBox<String> ccusto, int idLan) {
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
        
        String sql = "INSERT INTO SALDOS(DATA_HORA, VALORLAN, DESCRILAN, SALDO, STATUS_LANC, CCUSTO, ID_LANC, ID_USUARIO) VALUES (?,?,?,?,?,?,?,?)";
        try {
            pst = conexao.getConexao().prepareStatement(sql);
            pst.setString(1, Mascaras.formatData(data, "2") + " " + hora);
            pst.setDouble(2, valorLan);
            pst.setString(3, descriLan);
            if (status.equals("Pago")) {
                pst.setDouble(4, Double.valueOf(df.format(saldoF - valorLan).replace(",", ".")));
                isExecS = true;
            } 
            else if(status.equals("Recebido")){
                pst.setDouble(4, Double.valueOf(df.format(saldoF + valorLan).replace(",", ".")));
                isExecS = true;
            }
            pst.setString(5, status);
            pst.setString(6, ccusto.getSelectedItem().toString());
            pst.setInt(7, idLan);
            pst.setInt(8, 1);
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
