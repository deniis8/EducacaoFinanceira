package backup;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import conexao.Conexao;

public class IntegSqlServer {
	 Conexao conexao = new Conexao();
	    PreparedStatement pst;

	    public void backupLanc() { 
	        String sql = "SELECT ID_LANC, DATA, HORA, VALOR, DESCRICAO, ID_CCUSTO, STATUS, D_E_L_E_T_ FROM LANCAMENTOS ORDER BY ID_LANC";
	        String inform = ""; 
	        String deletado = "";
	        conexao.abrir();
	        try {
	            pst = conexao.getConexao().prepareStatement(sql);
	            ResultSet rs = pst.executeQuery();

	            while (rs.next()) {
	            	if(rs.getString(8).equals("")) {
	            		deletado = " ";
	            	}else {
	            		deletado = "*";
	            	}
	                inform += rs.getInt(1) + ";;" + rs.getString(2) + ";;" + rs.getString(3) + ";;" + rs.getDouble(4)
	                        + ";;" + rs.getString(5) + ";;" + rs.getString(6) + ";;" + rs.getString(7) + ";;" + deletado + "//";
	            }
	        } catch (SQLException e) {
	            System.out.println("Erro ao se conectar: " + e.getMessage());
	        }
	        try {
	            salvarArquivo("Lancamentos", inform);
	        } catch (IOException ex) {}
	        conexao.fechar();
	    }

	    public void backupCCusto() {
	        String sql = "SELECT ID_CCUSTO, DESCRI, D_E_L_E_T_ FROM CCUSTO";
	        String inform = ""; 
	        String deletado = "";
	        conexao.abrir();
	        try {
	            pst = conexao.getConexao().prepareStatement(sql);
	            ResultSet rs = pst.executeQuery();

	            while (rs.next()) {
	            	if(rs.getString(3).equals("")) {
	            		deletado = " ";
	            	}else {
	            		deletado = "*";
	            	}
	                inform += rs.getString(1) + ";;" + rs.getString(2) + ";;" + deletado + "//";
	            }
	        } catch (SQLException e) {
	            System.out.println("Erro ao se conectar: " + e.getMessage());
	        }
	        try {
	            salvarArquivo("CCusto", inform);
	        } catch (IOException ex) {}
	        conexao.fechar();
	    }

	    public void backupSaldos() {
	        String sql = "SELECT ID_SALDO, DATA, HORA, VALORLAN, DESCRILAN, SALDO, CCUSTO, STATUS FROM SALDOS";
	        String inform = ""; 
	        conexao.abrir();
	        try {
	            pst = conexao.getConexao().prepareStatement(sql);
	            ResultSet rs = pst.executeQuery();

	            while (rs.next()) {
	                inform += rs.getString(1) + ";;" + rs.getString(2) + ";;" + rs.getString(3) + ";;" + rs.getString(4) + ";;" + rs.getString(5) + ";;" + rs.getString(6) + ";;" + rs.getString(7) + ";;" + rs.getString(8) + "//";
	            }
	        } catch (SQLException e) {
	            System.out.println("Erro ao se conectar: " + e.getMessage());
	        }
	        try {
	            salvarArquivo("Saldos", inform);
	        } catch (IOException ex) {}
	        conexao.fechar();
	    }

	    public static void salvarArquivo(String tabela, String infor) throws FileNotFoundException, IOException {
	        OutputStream os = null;
	        if(tabela.equals("Lancamentos")){
	            os = new FileOutputStream("integracao_sql_server/Lancamentos.txt");
	        }else if(tabela.equals("CCusto")){
	            os = new FileOutputStream("integracao_sql_server/CCusto.txt");
	        }else if(tabela.equals("Saldos")){
	            os = new FileOutputStream("integracao_sql_server/Saldos.txt");
	        }
	        OutputStreamWriter osw = new OutputStreamWriter(os);
	        BufferedWriter bw = new BufferedWriter(osw);
	        bw.write(infor);
	        bw.close();
	    }
	    
	    public static void main(String[] args) {
			IntegSqlServer sqlint = new IntegSqlServer();
			sqlint.backupLanc();
			sqlint.backupCCusto();
			sqlint.backupSaldos();
		}
}
