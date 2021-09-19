package graficos;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

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

    public void gastosCC(String de, String ate, int tip) throws SQLException {
        conexao.abrir();
        nome = "Gastos por Centro de Custo (" + de.substring(8, 10)+"/"+de.substring(5, 7)+"/"+de.substring(0,4)+ " a "+ate.substring(8, 10)+"/"+ate.substring(5, 7)+"/"+ate.substring(0,4)+")";
        tipo = "Centro de Custo";
        String sql = "SELECT \n"
                + "    SUM(VALORLAN),\n"
                + "    CCUSTO\n"
                + "FROM \n"
                + "    SALDOS\n"
                + "WHERE \n"
                + "    STATUS='Pago' AND DATA BETWEEN '" + de + "' AND '" + ate + "' AND \n"
                + "    CCUSTO NOT IN('Investimento Fixo','Investimento Variável') \n"
                + "GROUP BY\n"
                + "    CCUSTO\n"
                + "ORDER BY\n"
                + "    SUM(VALORLAN)";
        
        dados = new DefaultCategoryDataset();
		grafico = ChartFactory.createBarChart(nome, tipo, "Valores", dados, PlotOrientation.VERTICAL, true, true, false);

        pst = conexao.getConexao().prepareStatement(sql);
        rs = pst.executeQuery();
        try {
            while (rs.next()) {
            	dados.setValue(rs.getDouble(1), rs.getString(2), "");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        conexao.fechar();
        try {
        	if(tip==0) {
        		salvar(new FileOutputStream("imagens/graficos/grafico_scc.PNG"),815, 400);
        	}else {
        		salvar(new FileOutputStream("imagens/graficos/grafico_scc_pdf.PNG"),790, 400);
        	}
			
		} catch (Exception e) {}        
       
    }

    public void gastosMens(String de, String ate, JComboBox<String> comboB, int tip) throws SQLException {
        String mes = "";
        nome = "Gastos por Meses do Ano (" + de.substring(8, 10)+"/"+de.substring(5, 7)+"/"+de.substring(0,4)+ " a "+ate.substring(8, 10)+"/"+ate.substring(5, 7)+"/"+ate.substring(0,4)+")";
        tipo = "Meses";
        conexao.abrir();
        String sql = "SELECT\n"
                + "    SUM(VALORLAN),\n"
                + "    date(DATA,'start of month','+1 month','-1 day')\n"
                + "FROM \n"
                + "    SALDOS\n"
                + "WHERE \n"
                + "    STATUS='Pago' AND DATA BETWEEN '" + de + "' AND '" + ate + "' AND \n";
        		if(tip==0) {
	                if(!comboB.getSelectedItem().equals("Todos")){
	                	sql += " CCUSTO='" +comboB.getSelectedItem() + "' AND \n";
	                }else {
	                	//int resposta = JOptionPane.showConfirmDialog(null, "Deseja Desconsiderar os Investimentos?", "Desconsiderar Investimentos", JOptionPane.YES_NO_OPTION);
	                	Object[] options = { "Sim", "Não" };
	                	int resposta = JOptionPane.showOptionDialog(null, "Deseja Desconsiderar os Investimentos?", "Informação", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
	        			if (resposta == 0) {
	                    	sql += " CCUSTO NOT IN('Investimento Fixo','Investimento Variável') AND \n";
	                    }
	                }
        		}else {
        			//int resposta = JOptionPane.showConfirmDialog(null, "Deseja Desconsiderar os Investimentos do gráfico Gastos por Meses do Ano?", "Desconsiderar Investimentos", JOptionPane.YES_NO_OPTION);
        			Object[] options = { "Sim", "Não" };
        			int resposta = JOptionPane.showOptionDialog(null, "Deseja Desconsiderar os Investimentos do gráfico Gastos por Meses do Ano?", "Informação", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
        			if (resposta == 0) {
                    	sql += " CCUSTO NOT IN('Investimento Fixo','Investimento Variável') AND \n";
                    }
        		}
                
                sql += "1=1 \n" 
                + "GROUP BY\n"
                + "    date(DATA,'start of month','+1 month','-1 day')\n"
                + "ORDER BY\n"
                + "    DATA ASC";
        
        dados = new DefaultCategoryDataset();
		grafico = ChartFactory.createBarChart(nome, tipo, "Valores", dados, PlotOrientation.VERTICAL, true, true, false);

        pst = conexao.getConexao().prepareStatement(sql);
        rs = pst.executeQuery();
        try {
            while (rs.next()) {
                switch (rs.getString(2).substring(5, 7)) {
                    case "01":
                        mes = "Janeiro (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "02":
                        mes = "Fevereiro (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "03":
                        mes = "Março (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "04":
                        mes = "Abril (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "05":
                        mes = "Maio (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "06":
                        mes = "Junho (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "07":
                        mes = "Julho (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "08":
                        mes = "Agosto (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "09":
                        mes = "Setembro (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "10":
                        mes = "Outubro (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "11":
                        mes = "Novembro (" + rs.getString(2).substring(0,4)+")";
                        break;
                    case "12":
                        mes = "Dezembro (" + rs.getString(2).substring(0,4)+")";
                        break;
                }
                //System.out.println(rs.getString(2).substring(5, 7));
                dados.setValue(rs.getDouble(1), mes, "");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        conexao.fechar();
        try {
        	if(tip==0) {
        		salvar(new FileOutputStream("imagens/graficos/grafico_m.PNG"),815, 400);
        	}else {
        		salvar(new FileOutputStream("imagens/graficos/grafico_m_pdf.PNG"),790, 400);
        	}
			
		} catch (Exception e) {}
    }

    public void entradaCC(String de, String ate, int tip) throws SQLException {
        nome = "Entradas por Centro de Custo (" + de.substring(8, 10)+"/"+de.substring(5, 7)+"/"+de.substring(0,4)+ " a "+ate.substring(8, 10)+"/"+ate.substring(5, 7)+"/"+ate.substring(0,4)+")";
        tipo = "Centro de Custo";
        conexao.abrir();
        String sql = "SELECT \n"
                + "    SUM(VALORLAN),\n"
                + "    CCUSTO\n"
                + "FROM \n"
                + "    SALDOS\n"
                + "WHERE \n"
                + "    STATUS='Recebido' AND DATA BETWEEN '" + de + "' AND '" + ate + "' AND\n"
                + "    CCUSTO NOT IN('Investimento Fixo','Investimento Variável')\n"
                + "GROUP BY\n"
                + "    CCUSTO\n"
                + "ORDER BY\n"
                + "    SUM(VALORLAN) ASC";
        
        dados = new DefaultCategoryDataset();
		grafico = ChartFactory.createBarChart(nome, tipo, "Valores", dados, PlotOrientation.VERTICAL, true, true, false);

        pst = conexao.getConexao().prepareStatement(sql);
        rs = pst.executeQuery();
        try {
            while (rs.next()) {
            	dados.setValue(rs.getDouble(1), rs.getString(2), "");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        conexao.fechar();
        try {
        	if(tip==0) {
        		salvar(new FileOutputStream("imagens/graficos/grafico_ecc.PNG"),815, 400);
        	}else {
        		salvar(new FileOutputStream("imagens/graficos/grafico_ecc_pdf.PNG"),790, 400);
        	}
			
		} catch (Exception e) {}
    }
    
    public void salvar(FileOutputStream out, int largura, int comprimento) throws IOException {
		ChartUtils.writeChartAsPNG(out, grafico, largura, comprimento);	
		out.close();
	}
    
    /*
    public static void main(String[] args) {
		Graficos g = new Graficos();
		try {
			g.gastosCC("2019-03-01", "2020-04-13");
			g.entradaCC("2019-03-01", "2020-04-13");
			g.gastosMens("2019-03-01", "2020-04-13",null);
		} catch (SQLException e) {}
	}*/

}
