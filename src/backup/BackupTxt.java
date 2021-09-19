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

/**
 *
 * @author adenilson.soares 
 */
public class BackupTxt { 

    Conexao conexao = new Conexao();
    PreparedStatement pst;

    public void backupLanc() {
        String sql = "SELECT DATA, HORA, VALOR, STATUS, DESCRICAO, ID_CCUSTO, D_E_L_E_T_ FROM LANCAMENTOS";
        String inform = ""; 
        conexao.abrir();
        try {
            pst = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                inform += "INSERT INTO LANCAMENTOS(DATA, HORA, VALOR, STATUS, DESCRICAO, ID_CCUSTO, D_E_L_E_T_)\n"
                        + "VALUES('" + rs.getString(1) + "' ,'" + rs.getString(2) + "' ," + rs.getDouble(3) + ",' " + rs.getString(4)
                        + "', '" + rs.getString(5) + "', '" + rs.getString(6) + "' , '" + rs.getString(7) + "');\n";
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
        String sql = "SELECT DESCRI, D_E_L_E_T_ FROM CCUSTO";
        String inform = ""; 
        conexao.abrir();
        try {
            pst = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                inform += "INSERT INTO CCUSTO(DESCRI, D_E_L_E_T_)\n"
                        + "VALUES('" + rs.getString(1) + "' ,'" + rs.getString(2) + "');\n";
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
        String sql = "SELECT DATA, HORA, VALORLAN, DESCRILAN, SALDO, STATUS, CCUSTO FROM SALDOS";
        String inform = ""; 
        conexao.abrir();
        try {
            pst = conexao.getConexao().prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                inform += "INSERT INTO SALDOS(DATA, HORA, VALORLAN, DESCRILAN, SALDO, STATUS, CCUSTO)\n"
                        + "VALUES('" + rs.getString(1) + "' ,'" + rs.getString(2) + "', '" + rs.getString(3) + "', '" + rs.getString(4) + "', '" + rs.getString(5) + "', '" + rs.getString(6) + "', '" + rs.getString(7) + "');\n";
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
            os = new FileOutputStream("backup/Lancamentos.txt");
        }else if(tabela.equals("CCusto")){
            os = new FileOutputStream("backup/CCusto.txt");
        }else if(tabela.equals("Saldos")){
            os = new FileOutputStream("backup/Saldos.txt");
        }
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(infor);
        bw.close();
    }
    
    public static void main(String[] args) {
		BackupTxt b = new BackupTxt();
		b.backupSaldos();
		b.backupLanc();
		b.backupCCusto();
	}
    
}
