package testes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import conexao.Conexao;

public class TESTE {
	Conexao conexao = new Conexao();
	PreparedStatement pst;
	PreparedStatement pst2;
	public static void main(String[] args) {
		
		TESTE teste = new TESTE();
		//teste.atualizaTabLan();
		teste.atualizaTabSald();
		System.out.println("Fim");
		//System.out.println(StringUtils.leftPad("1232", 6, "0"));
	}
	
	public void atualizaTabLan() {
		String id ="";
        conexao.abrir();
        String sql = "SELECT ID_LANC FROM LANCAMENTOS";
        try {
            pst = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
            	id = rs.getString(1);
            	System.out.println(id);
                String sqlCli = "UPDATE LANCAMENTOS SET ID_LANC=? WHERE ID_LANC='"+id+"'";

                try {
                	pst2 = conexao.getConexao().prepareStatement(sqlCli);
                	pst2.setString(1, StringUtils.leftPad(id, 6, "0"));

                	pst2.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            }
        } catch (SQLException e) {

        }
       
        conexao.fechar();
    }
	
	public void atualizaTabSald() {
		String id ="";
        conexao.abrir();
        String sql = "SELECT ID_LANC FROM SALDOS";
        try {
            pst = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
            	id = rs.getString(1);
            	//System.out.println(id);
                String sqlCli = "UPDATE SALDOS SET ID_LANC=? WHERE ID_LANC='"+id+"'";

                try {
                	pst2 = conexao.getConexao().prepareStatement(sqlCli);
                	pst2.setString(1, StringUtils.leftPad(id, 6, "0"));

                	pst2.executeUpdate();
                } catch (SQLException e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            }
        } catch (SQLException e) {

        }
       
        conexao.fechar();
    }
}
