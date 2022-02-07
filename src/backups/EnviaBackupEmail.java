package backups;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;

public class EnviaBackupEmail {

    Message message;
    Properties props = new Properties();
    Session session;

    public void enviaBkp() throws AddressException, MessagingException {
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.live.com"); 
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        
      //Mensagem informativa para configurações de envios de e-mails
        JOptionPane.showInternalMessageDialog(null, "Insira as suas configurações de e-mail na classe EmailBackup.java");
        session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("e-mail_remetente", "senha_remetente");
            }
        });
        //Ativa Debug para sessão
        session.setDebug(true);
        message = new MimeMessage(session);
        
        message.setFrom(new InternetAddress("e-mail_remetente")); // Remetente 
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("e-mail_destinatário"));// Destinatário(s)             
        message.setSubject("EDUCAÇÃO FINANCEIRA: BACKUP BANCO DE DADOS");//Assunto   
        try {
            MimeBodyPart mbp2 = new MimeBodyPart();
            FileDataSource fds = new FileDataSource("banco_dados/LANCCONTAS.db3");
            mbp2.setDataHandler(new DataHandler(fds));
            mbp2.setFileName(fds.getName());
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp2);
            message.setContent(mp);
            Transport.send(message);
            JOptionPane.showMessageDialog(null, "E-mail Enviado com Sucesso");
        } catch (MessagingException e1) {
            JOptionPane.showMessageDialog(null, "O e-mail não foi enviado. Erro: " + e1.getMessage());
        }
    }

}
