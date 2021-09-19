package conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Conexao {

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
            //System.out.println("Conexão fechada..."); 
        } catch (SQLException e) {
            //System.out.println("Erro ao fechar a conexão | Erro: " + e.getMessage());
        }
    }/*
    public static void main(String[] args) {
		Conexao conexao = new Conexao();
		conexao.abrir();
	}*/
}