package sql;

import java.sql.PreparedStatement;

import conexao.Conexao;

public class Exclusao {

	PreparedStatement pst;
	Conexao conexao = new Conexao();
	
	public void excluirLan(String cod) {
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
