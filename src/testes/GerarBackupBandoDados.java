package testes;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import configuracao.banco.dados.ConexaoBancoDados;

public class GerarBackupBandoDados {
	
	ConexaoBancoDados conexao = new ConexaoBancoDados();
	PreparedStatement pst;
	
	public void abrirPlan() {
		try {
			Desktop.getDesktop().open(new File("mysqldump -u root -p gestaofinanceira > C:\\EducacaoFinanceira\\backup\\backup.sql"));
		} catch (IOException e) {
		}
	}

    
    public static void main(String[] args) {
		GerarBackupBandoDados gs = new GerarBackupBandoDados();
		gs.abrirPlan();
	}

}