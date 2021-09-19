package testes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import conexao.Conexao;

/**
 *
 * @author adenilson.soares
 */
public class Graficos{

	private DefaultCategoryDataset dados;
	private JFreeChart grafico;
    Conexao conexao = new Conexao();
    PreparedStatement pst;
    ResultSet rs;
    String nome = "";
    String tipo = "";
    String de = "";
    String ate = "";

    public void gastosCC() throws SQLException {
        conexao.abrir();
        nome = "Gastos por Centro de Custo";
        tipo = "Centro de Custo";
        String sql = "SELECT MES, VALOR FROM VW_COMPARATIVO_DIA_MES ORDER BY VALOR ASC";
        
        dados = new DefaultCategoryDataset();
		grafico = ChartFactory.createBarChart(nome, tipo, "Valores", dados, PlotOrientation.VERTICAL, true, true, false);

        pst = conexao.getConexao().prepareStatement(sql);
        rs = pst.executeQuery();
        try {
            while (rs.next()) {
            	dados.setValue(rs.getDouble(2), rs.getString(1), "");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        conexao.fechar();
        try {
			salvar(new FileOutputStream("imagens/graficos/TESTE.PNG"));
		} catch (Exception e) {}
    }

    
    public void salvar(FileOutputStream out) throws IOException {
		ChartUtils.writeChartAsPNG(out, grafico, 815, 400);	
		out.close();
	}
    
    public static void main(String[] args) {
		Graficos g = new Graficos();
		try {
			g.gastosCC();
		} catch (SQLException e) {}
	}

}
