package configuracao.banco.dados;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class ConexaoBancoDadosSQLite {

    /*private static Connection con = null;

    public Connection getConexao() {
        return con;
    }

    public Connection abrir() { 
        try {
            //Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:banco_dados/LANCCONTAS.db3");
            //System.out.println("conectado");
            return con;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao se conectar | Erro: " + e.getMessage());
            System.exit(0);
            return null;
        }
    }

    public void fechar() {
        try {
            con.close();
            //System.out.println("Conexão fechada..."); 
        } catch (SQLException e) {
            //System.out.println("Erro ao fechar a conexão | Erro: " + e.getMessage());
        }
    }*/
	
	
	//===================================================
	// Conexão com MariaDB
	//===================================================
	public String userName, password, url, driver;
	public Connection con;
	public Statement st;
	
	public Connection getConexao() {
        return con;
    }

	public Connection abrir() {
		userName = "root";
		password = "1204";
		url = "jdbc:mariadb://localhost:3307/GestaoFinanceira";
		driver = "org.mariadb.jdbc.Driver";
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userName, password);
			st = con.createStatement();
			//System.out.println("Conexão realizada com sucesso");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public void fechar() {
        try {
            con.close();
            //System.out.println("Conexão fechada..."); 
        } catch (SQLException e) {
            //System.out.println("Erro ao fechar a conexão | Erro: " + e.getMessage());
        }
    }
}