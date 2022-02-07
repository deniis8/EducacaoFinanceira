package configuracao.email;

import java.util.Properties;   
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JOptionPane;

public class EnvioEmail {
    
    public void email(String texto, String titulo){
        Properties props = new Properties();
        /**
         * * Par?metros de conex?o com servidor Hotmail
         */
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "smtp.live.com");
        props.put("mail.smtp.socketFactory.port", "587");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        Session session;
        
        //Mensagem informativa para configurações de envios de e-mails
        JOptionPane.showInternalMessageDialog(null, "Insira as suas configurações de e-mail na classe EnvioEmail.java");
        
        session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("seu_e-mail", "sua_senha");
            }
        });
        /**
         * * Ativa Debug para sess?o
         */
        session.setDebug(true);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("seu_e-mail")); // Remetente 
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("seu_e-mail"));
            // Destinat?rio(s) 
            message.setSubject(titulo);
            // Assunto //
            //message.setText(texto);
            message.setContent(texto, "text/html;charset=utf-8");
            /**
             * * M?todo para enviar a mensagem criada
             */
            Transport.send(message);
            //System.out.println("Feito!!!");
            JOptionPane.showMessageDialog(null, "E-mail enviado com sucesso!");
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(null, "O e-mail não foi enviado, verifique a conexão com a internet.");
        } 
    }
}
