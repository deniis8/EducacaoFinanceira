package sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import mascaras.Mascaras;
import conexao.Conexao;

public class Atualizacao {
	
	private PreparedStatement pst;
	Conexao conexao = new Conexao();

	public void atualiLanc(String cod, String data, String hora, double valor, String desc, JComboBox<?> status, JComboBox<?> boxCC) {
        String rStatus = status.getSelectedItem().toString();
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
            pst = conexao.getConexao().prepareStatement(sqlCli);
            pst.setString(1, Mascaras.formatData(data, "2"));
            pst.setString(2, hora);
            pst.setDouble(3, valor);
            pst.setString(4, rStatus);
            pst.setString(5, desc);
            pst.setInt(6, idCC);

            pst.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Erro: " + e.getMessage());
        }
        conexao.fechar();
    }
}
