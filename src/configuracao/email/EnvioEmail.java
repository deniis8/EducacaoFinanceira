package configuracao.email;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

import ler.arquivo.LerEmailSenha;

public class EnvioEmail {
    
	private Message message;
	private Properties props = new Properties();
	private Session session;
	private FileDataSource fds;
	
	private String texto;
	private String titulo; 
	private String opc;
	
    /**
     * 
     * @param texto
     * @param titulo
     * @param opc = 1 - Relatório 2 - Backup 3 - Gráfico
     */
	
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getOpc() {
		return opc;
	}

	public void setOpc(String opc) {
		this.opc = opc;
	}
	
	public EnvioEmail(String texto, String titulo, String opc) {
		setTexto(texto);
		setTexto(titulo);
		setOpc(opc);		
	}
    
    public void enviar(){
        
        /**
         * * Parametros de conex?o com servidor Hotmail
         */
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.live.com");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        
        //Ler e-mail e senha
        LerEmailSenha ler = new LerEmailSenha();
        
        session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ler.getEmail(), ler.getSenha());
            }
        });
        
        /**
         * * Ativa Debug para sessao
         */
        session.setDebug(true);
        try {
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ler.getEmail())); // Remetente 
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ler.getEmail()));
            
            message.setSubject(getTitulo());
            if(getOpc() == "1") {
            	message.setContent(getTexto(), "text/html;charset=utf-8");
            }else if(getOpc() == "2" || getOpc() == "3") {
            	
            	MimeBodyPart mbp2 = new MimeBodyPart();
            	if(getOpc() == "3") {
            		fds = new FileDataSource("arquivos/pdf/graficos.pdf");
            	}else {
            		fds = new FileDataSource("banco_dados/LANCCONTAS.db3");
            	}                
                mbp2.setDataHandler(new DataHandler(fds));
                mbp2.setFileName(fds.getName());
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(mbp2);
                message.setContent(mp);            	
            }
            
            Transport.send(message);
            JOptionPane.showMessageDialog(null, "E-mail enviado com sucesso!");
        } catch (MessagingException ex) {
            JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
        } 
    }
}
