package ler.arquivo;

import java.io.BufferedReader;
import java.io.FileReader;

public class LerEmailSenha {
	
	private String email;
	private String senha;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}	
	
	public LerEmailSenha() {
		ler();
	}
	
	public void ler() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("arquivos/config_email.txt"));
			setEmail(br.readLine());
			setSenha(br.readLine());
			br.close();	
		} catch (Exception e) {}		
	}
}
