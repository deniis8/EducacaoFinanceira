package crud;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import configuracao.banco.dados.ConexaoBancoDadosSQLite;
import interfaces.CrudCentroCusto;

public class CrudCentroDeCusto implements CrudCentroCusto{
	
	ConexaoBancoDadosSQLite conexao = new ConexaoBancoDadosSQLite();
	PreparedStatement pst;
	
	public void selecCC(DefaultTableModel modTabLanc, JTable table) throws ParseException {		
		
		String sql = "SELECT ID_CCUSTO, DESCRI FROM CCUSTO WHERE D_E_L_E_T_<>'*' ORDER BY DESCRI";
		conexao.abrir();
		zeraTabelaPrincipal(modTabLanc);
		try {
			pst = conexao.getConexao().prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			//table.setModel(modTabLanc);
			while (rs.next()) {
				modTabLanc.addRow(new Object[] { rs.getString(1),rs.getString(2)});
				table.setModel(modTabLanc);
			}
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}

		conexao.fechar();
	}
	
	@Override
	public void incluirCentroCusto(String descri) {

		conexao.abrir();
		String insert = "";
		insert = "INSERT INTO CCUSTO(DESCRI, D_E_L_E_T_) ";
		insert += "VALUES(?,?)";
		try {
			pst = conexao.getConexao().prepareStatement(insert);
			pst.setString(1, descri);
			pst.setString(2, "");
			pst.executeUpdate();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		conexao.fechar();
		
	}
		
	@Override
	public void alterarCentroCusto(String descri, int codCC) {
		
        String sqlCC = "SELECT ID_CCUSTO FROM LANCAMENTOS WHERE ID_CCUSTO="+codCC;
        boolean temReg = false;
        
        conexao.abrir();

        try {
            pst = conexao.getConexao().prepareStatement(sqlCC);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
            	temReg = true;
            }
        } catch (SQLException e) {
        	JOptionPane.showMessageDialog(null,"Erro: " + e.getMessage());
        }
        
        if(temReg==true) {
        	Object[] options = { "Sim", "Não", "Cancelar" };
        	int resposta = JOptionPane.showOptionDialog(null, "Esse centro de custo está em uso. Deseja alterar mesmo assim?", "Informação", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        	
        	if(resposta == 0) {
        		 String sqlCli = "UPDATE CCUSTO SET \n"
                         + "    DESCRI = ?\n"
                         + "WHERE \n"
                         + "    ID_CCUSTO =" + codCC;

                 try {
                     pst = conexao.getConexao().prepareStatement(sqlCli);
                     pst.setString(1, descri);

                     pst.executeUpdate();
                 } catch (SQLException e) {
                     JOptionPane.showMessageDialog(null,"Erro: " + e.getMessage());
                 }
        	}        	
        }else {
        	 String sqlCli = "UPDATE CCUSTO SET \n"
                     + "    DESCRI = ?\n"
                     + "WHERE \n"
                     + "    ID_CCUSTO = " + codCC;

             try {
                 pst = conexao.getConexao().prepareStatement(sqlCli);
                 pst.setString(1, descri);

                 pst.executeUpdate();
             } catch (SQLException e) {
                 JOptionPane.showMessageDialog(null,"Erro: " + e.getMessage());
             }             
        }   
        conexao.fechar();
    }
	
	@Override
	public void excluirCentroCusto(int cod) {
		String del = "UPDATE CCUSTO SET D_E_L_E_T_=? WHERE ID_CCUSTO="+ cod;
		conexao.abrir();
		
		 String sqlCC = "SELECT ID_CCUSTO FROM LANCAMENTOS WHERE ID_CCUSTO="+cod;
	        boolean temReg = false;
	        
	        conexao.abrir();

	        try {
	            pst = conexao.getConexao().prepareStatement(sqlCC);
	            ResultSet rs = pst.executeQuery();
	            while (rs.next()) {
	            	temReg = true;
	            }
	        } catch (SQLException e) {
	        	JOptionPane.showMessageDialog(null,"Erro: " + e.getMessage());
	        }
		
		if(temReg == false) {
			try {
				pst = conexao.getConexao().prepareStatement(del);
				pst.setString(1, "*");
				pst.executeUpdate();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null,"Erro: " + e.getMessage());
			}	
		}else {
			JOptionPane.showMessageDialog(null,"Não é possível excluir. Existe lançamento(s) com esse centro de custo atrelado.");
		}
			
			
		conexao.fechar();
	}
	
	public void zeraTabelaPrincipal(DefaultTableModel modelTable) {
		for (int i = modelTable.getRowCount() - 1; i >= 0; i--) {
			modelTable.removeRow(i);
		}

}
}
