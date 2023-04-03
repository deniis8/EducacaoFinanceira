package testes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import configuracao.banco.dados.ConexaoBancoDados;

public class GerarCsv {
	
	ConexaoBancoDados conexao = new ConexaoBancoDados();
	PreparedStatement pst;
	
	public void lancamentos(){
        try (PrintWriter writer = new PrintWriter(new File("arquivos/lancamentos.csv"))) {
        	StringBuilder sb = new StringBuilder();
        	
        	String slqUlt = " SELECT ";
        	slqUlt +="    STR_TO_DATE(DATA_HORA, '%Y-%m-%d'), "; 
        	slqUlt +="    TIME(DATA_HORA), "; 
        	slqUlt +="    VALOR, "; 
        	slqUlt +="    DESCRICAO, "; 
        	slqUlt +="    (SELECT DESCRI FROM CCUSTO AS CC WHERE CC.ID_CCUSTO=LAN.ID_CCUSTO AND CC.D_E_L_E_T_<>'*') AS CCUSTO,";
        	slqUlt +="    STATUS_LANC ";
        	slqUlt +=" FROM "; 
        	slqUlt +="     LANCAMENTOS AS LAN "; 
        	slqUlt +=" WHERE ";
        	slqUlt +="     D_E_L_E_T_<>'*' ";

    		conexao.abrir(); 
    		try {
    			pst = conexao.getConexao().prepareStatement(slqUlt);
    			ResultSet rs = pst.executeQuery();
    			sb.append("Data;Hora;ValorGasto;Descricao;CentroCusto;Status");
    			sb.append('\n');
    			while(rs.next()) {
    				sb.append(rs.getString(1));
    				sb.append(';');
    				sb.append(rs.getString(2));
    				sb.append(';');
    				sb.append(rs.getString(3).replace(".", ","));
    				sb.append(';');
    				sb.append(rs.getString(4));
    				sb.append(';');
    				sb.append(rs.getString(5));
    				sb.append(';');
    				sb.append(rs.getString(6));
    				sb.append('\n');
    			}
    		} catch (Exception e) {
    		}
    		conexao.fechar();

            writer.write(sb.toString());
            writer.close();
            System.out.println("Arquivo lancamentos.csv gerado com sucesso!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
	
	public void saldos(){
        try (PrintWriter writer = new PrintWriter(new File("arquivos/saldos.csv"))) {
        	StringBuilder sb = new StringBuilder();
        	
        	String slqUlt = " SELECT ";
        	slqUlt +="    STR_TO_DATE(DATA_HORA, '%Y-%m-%d'), "; 
        	slqUlt +="    TIME(DATA_HORA), "; 
        	slqUlt +="    VALORLAN, "; 
        	slqUlt +="    DESCRILAN, "; 
        	slqUlt +="    STATUS_LANC, ";
        	slqUlt +="    SALDO "; 
        	slqUlt +=" FROM "; 
        	slqUlt +="     SALDOS "; 
        	slqUlt +=" ORDER BY ";  
        	slqUlt +="	   DATA_HORA DESC LIMIT 1 "; 

    		conexao.abrir(); 
    		try {
    			pst = conexao.getConexao().prepareStatement(slqUlt);
    			ResultSet rs = pst.executeQuery();
    			sb.append("Data;Hora;ValorLancamento;Descricao;Status;Saldo");
    			sb.append('\n');
    			while(rs.next()) {
    				sb.append(rs.getString(1));
    				sb.append(';');
    				sb.append(rs.getString(2));
    				sb.append(';');
    				sb.append(rs.getString(3).replace(".", ","));
    				sb.append(';');
    				sb.append(rs.getString(4));
    				sb.append(';');
    				sb.append(rs.getString(5));
    				sb.append(';');
    				sb.append(rs.getString(6).replace(".", ","));
    			}
    		} catch (Exception e) {
    		}
    		conexao.fechar();

            writer.write(sb.toString());
            writer.close();
            System.out.println("Arquivo saldos.csv gerado com sucesso!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
	public void gastosMensais(){
        try (PrintWriter writer = new PrintWriter(new File("arquivos/gastoMensal.csv"))) {
        	StringBuilder sb = new StringBuilder();

        	String sqlConsultaGastosMensais = " SELECT ";
        	sqlConsultaGastosMensais += " 	SUM(VALOR) AS VALOR_GASTO_MES, ";
        	sqlConsultaGastosMensais += " 	DATA_HORA AS ANO, ";
        	sqlConsultaGastosMensais += " 	MONTHNAME(DATA_HORA) AS MES ";
        	sqlConsultaGastosMensais += " FROM ";
        	sqlConsultaGastosMensais += " 	LANCAMENTOS ";
        	sqlConsultaGastosMensais += " WHERE ";
        	sqlConsultaGastosMensais += " 	STATUS_LANC='Pago' AND ID_CCUSTO NOT IN(19) AND D_E_L_E_T_<>'*' ";
        	sqlConsultaGastosMensais += " GROUP BY ";
        	sqlConsultaGastosMensais += " 	YEAR(DATA_HORA), MONTHNAME(DATA_HORA) ";
        	sqlConsultaGastosMensais += " ORDER BY ";
        	sqlConsultaGastosMensais += " 	DATA_HORA ";
        		


    		conexao.abrir(); 
    		try {
    			pst = conexao.getConexao().prepareStatement(sqlConsultaGastosMensais);
    			ResultSet rs = pst.executeQuery();
    			sb.append("ValorGastoMes;Ano;Mes");
    			sb.append('\n');
    			while(rs.next()) {
    				sb.append(rs.getString(1).replace(".", ","));
    				sb.append(';');
    				sb.append(rs.getString(2));
    				sb.append(';');
    				sb.append(rs.getString(3));
    				sb.append('\n');
    			}
    		} catch (Exception e) {
    		}
    		conexao.fechar();

            writer.write(sb.toString());
            writer.close();
            System.out.println("Arquivo gastoMensal.csv gerado com sucesso!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void main(String[] args) {
		GerarCsv gs = new GerarCsv();
		gs.lancamentos();
		gs.saldos();
		gs.gastosMensais();
	}

}