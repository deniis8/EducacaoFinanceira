package configuracao.banco.dados;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class ConexaoBancoDados {

    /*
    
    //===================================================
	// Conex�o com MariaDB
	//===================================================
    private static Connection con = null;

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
            //System.out.println("Conex�o fechada..."); 
        } catch (SQLException e) {
            //System.out.println("Erro ao fechar a conex�o | Erro: " + e.getMessage());
        }
    }*/
	
	
	//===================================================
	// Conex�o com MariaDB
	//===================================================
	public String userName, password, url, driver;
	public Connection con;
	public Statement st;
	
	public Connection getConexao() {
        return con;
    }

	public Connection abrir() {
		userName = "admin";
		password = "asvezesfalo8";
		url = "jdbc:mariadb://192.168.0.110:3306/GestaoFinanceira";
		driver = "org.mariadb.jdbc.Driver";
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userName, password);
			st = con.createStatement();
			//System.out.println("Conex�o realizada com sucesso");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}
	
	public void fechar() {
        try {
            con.close();
            //System.out.println("Conex�o fechada..."); 
        } catch (SQLException e) {
            //System.out.println("Erro ao fechar a conex�o | Erro: " + e.getMessage());
        }
    }
}