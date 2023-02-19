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
	
	public void exec(){
        try (PrintWriter writer = new PrintWriter(new File("arquivos/baseLancamentos.txt"))) {
        	StringBuilder sb = new StringBuilder();
        	
        	String slqUlt = " SELECT ";
        	slqUlt +="    ID_LANC, "; 
        	slqUlt +="    DATA_HORA, "; 
        	slqUlt +="    VALOR, "; 
        	slqUlt +="    DESCRICAO, "; 
        	slqUlt +="    ID_CCUSTO, ";
        	//slqUlt +="    (SELECT DESCRI FROM CCUSTO AS CC WHERE LAN.ID_CCUSTO=CC.ID_CCUSTO AND D_E_L_E_T_ <>'*') AS CC_DESCRI, ";
        	slqUlt +="    STATUS_LANC, "; 
        	slqUlt +="    D_E_L_E_T_ "; 
        	slqUlt +=" FROM "; 
        	slqUlt +="     LANCAMENTOS AS LAN "; 
        	slqUlt +=" WHERE ";
        	slqUlt +="     D_E_L_E_T_<>'*' ";

    		conexao.abrir(); 
    		try {
    			pst = conexao.getConexao().prepareStatement(slqUlt);
    			ResultSet rs = pst.executeQuery();
    			while(rs.next()) {
    				sb.append(Integer.parseInt(rs.getString(1)));
    				sb.append(';');
    				sb.append(rs.getString(2));
    				sb.append(';');
    				sb.append(rs.getString(3));
    				sb.append(';');
    				sb.append(rs.getString(4));
    				sb.append(';');
    				sb.append(rs.getString(5));
    				sb.append(';');
    				sb.append(rs.getString(6));
    				sb.append(';');
    				sb.append(rs.getString(2));
    				sb.append(";");
    				sb.append("1");
    				sb.append(";");
    				sb.append(" ");
    				sb.append(";;");
    			}
    		} catch (Exception e) {
    		}
    		conexao.fechar();

            writer.write(sb.toString());
            writer.close();
            System.out.println("Arquivo .txt gerado com sucesso!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
	
	public void saldos(){
        try (PrintWriter writer = new PrintWriter(new File("arquivos/baseSaldos.txt"))) {
        	StringBuilder sb = new StringBuilder();
        	
        	String slqUlt = " SELECT ";
        	slqUlt +="    ID_SALDO, "; 
        	slqUlt +="    DATA_HORA, "; 
        	slqUlt +="    VALORLAN, "; 
        	slqUlt +="    DESCRILAN, "; 
        	slqUlt +="    SALDO, ";
        	slqUlt +="    CCUSTO, ";
        	slqUlt +="    STATUS_LANC "; 
        	slqUlt +=" FROM "; 
        	slqUlt +="     SALDOS "; 

    		conexao.abrir(); 
    		try {
    			pst = conexao.getConexao().prepareStatement(slqUlt);
    			ResultSet rs = pst.executeQuery();
    			while(rs.next()) {
    				sb.append(Integer.parseInt(rs.getString(1)));
    				sb.append(';');
    				sb.append(rs.getString(2));
    				sb.append(';');
    				sb.append(rs.getString(3));
    				sb.append(';');
    				sb.append(rs.getString(4));
    				sb.append(';');
    				sb.append(rs.getString(5));
    				sb.append(';');
    				sb.append(rs.getString(6));
    				sb.append(';');
    				sb.append(rs.getString(7));
    				sb.append(';');
    				sb.append("");
    				sb.append(';');
    				sb.append(1);
    				sb.append(";;");
    			}
    		} catch (Exception e) {
    		}
    		conexao.fechar();

            writer.write(sb.toString());
            writer.close();
            System.out.println("Arquivo .txt gerado com sucesso!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
	
	public void centroCusto(){
        try (PrintWriter writer = new PrintWriter(new File("arquivos/baseCCusto.txt"))) {
        	StringBuilder sb = new StringBuilder();
        	
        	String slqUlt = " SELECT ID_CCUSTO, DESCRI FROM CCUSTO WHERE D_E_L_E_T_ <>'*'";

    		conexao.abrir(); 
    		try {
    			pst = conexao.getConexao().prepareStatement(slqUlt);
    			ResultSet rs = pst.executeQuery();
    			while(rs.next()) {
    				sb.append(Integer.parseInt(rs.getString(1)));
    				sb.append(';');
    				sb.append(rs.getString(2));
    				sb.append(';');
    				sb.append("2022-10-17 20:58:00");
    				sb.append(';');
    				sb.append("1");
    				sb.append(';');
    				sb.append(" ");
    				sb.append(";;");
    			}
    		} catch (Exception e) {
    		}
    		conexao.fechar();

            writer.write(sb.toString());
            writer.close();
            System.out.println("Arquivo .txt gerado com sucesso!");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void main(String[] args) {
		GerarCsv gs = new GerarCsv();
		gs.exec();
		gs.centroCusto();
		gs.saldos();
	}

}