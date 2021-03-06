package graficos;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.itextpdf.text.DocumentException;
import com.toedter.calendar.JDateChooser;

import configuracao.email.EnvioEmail;

public class GraficosPdf {

	Message message;
	Properties props = new Properties();
	Session session;

	private JDialog dlgJanela;
	private JLabel lblGrafSai;
	private JLabel lblSDatD;
	private JDateChooser txtSDatD;
	private JLabel lblSDatA;
	private JDateChooser txtSDatA;
	
	private JLabel lblGrafEnt;
	private JLabel lblEDatD;
	private JDateChooser txtEDatD;
	private JLabel lblEDatA;
	private JDateChooser txtEDatA;	
	
	private JLabel lblGrafGM;
	private JLabel lblGMDatD;
	private JDateChooser txtGMDatD;
	private JLabel lblEGMatA;
	private JDateChooser txtEGMatA;
	
	private ImageIcon iconCancel;
	private JButton btnCancel;
	private ImageIcon iconConfirm;
	private JButton btnConfirm;
	
	public GraficosPdf() {
		janela();
	}
	
	public void janela() {
		int x = 10, y = 20;
		dlgJanela = new JDialog();
		dlgJanela.setTitle("Envios de Gr?ficos - PDF");
		dlgJanela.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dlgJanela.setSize(280, 380);
		dlgJanela.setLocationRelativeTo(null);
		dlgJanela.setResizable(false);
		dlgJanela.setModal(true);
		dlgJanela.setLayout(null);		
		
		lblGrafSai = new JLabel("Sa?das por Centro de Custo");
		lblSDatD = new JLabel("Data de:");
		txtSDatD = new JDateChooser(new Date(), "dd/MM/yyyy");		
		lblSDatA = new JLabel("Data at?:");
		txtSDatA = new JDateChooser(new Date(), "dd/MM/yyyy");
		
		lblGrafEnt = new JLabel("Entradas por Centro de Custo");
		lblEDatD = new JLabel("Data de:");
		txtEDatD = new JDateChooser(new Date(), "dd/MM/yyyy");
		lblEDatA = new JLabel("Data at?:");
		txtEDatA = new JDateChooser(new Date(), "dd/MM/yyyy");
		
		lblGrafGM = new JLabel("Gastos Mensais");
		lblGMDatD = new JLabel("Data de:");
		txtGMDatD = new JDateChooser(new Date(), "dd/MM/yyyy");
		//txtGMDatD.setDateFormatString("19/03/2019");
		lblEGMatA = new JLabel("Data at?:");
		txtEGMatA = new JDateChooser(new Date(), "dd/MM/yyyy");
		
		iconCancel = new ImageIcon("imagens/cancelar.png");
		btnCancel = new JButton(iconCancel);
		btnCancel.setContentAreaFilled(false);
		btnCancel.setBorderPainted(false);
		btnCancel.setToolTipText("Cancelar");
		
		iconConfirm = new ImageIcon("imagens/confirmar.png");
		btnConfirm = new JButton(iconConfirm);
		btnConfirm.setContentAreaFilled(false);
		btnConfirm.setBorderPainted(false);
		btnConfirm.setToolTipText("Confirmar");
		
		lblGrafSai.setBounds(x, y, 200, 20);
		y+=30;
		lblSDatD.setBounds(x, y, 50, 20);
		txtSDatD.setBounds(x+60, y, 90, 20);
		y+=25;
		lblSDatA.setBounds(x, y, 50, 20);
		txtSDatA.setBounds(x+60, y, 90, 20);
		
		y+=40;
		lblGrafEnt.setBounds(x, y, 200, 20);
		y+=30;
		lblEDatD.setBounds(x, y, 50, 20);
		txtEDatD.setBounds(x+60, y, 90, 20);
		y+=25;
		lblEDatA.setBounds(x, y, 50, 20);
		txtEDatA.setBounds(x+60, y, 90, 20);
		
		y+=40;
		lblGrafGM.setBounds(x, y, 200, 20);
		y+=30;
		lblGMDatD.setBounds(x, y, 50, 20);
		txtGMDatD.setBounds(x+60, y, 90, 20);
		y+=25;
		lblEGMatA.setBounds(x, y, 50, 20);
		txtEGMatA.setBounds(x+60, y, 90, 20);
		
		y+=35;
		btnCancel.setBounds(x, y, 32, 32);
		btnConfirm.setBounds(x+213, y, 32, 32);
		
		dlgJanela.add(lblGrafSai);
		dlgJanela.add(lblSDatD);
		dlgJanela.add(txtSDatD);
		dlgJanela.add(lblSDatA);
		dlgJanela.add(txtSDatA);
		
		dlgJanela.add(lblGrafEnt);
		dlgJanela.add(lblEDatD);
		dlgJanela.add(txtEDatD);
		dlgJanela.add(lblEDatA);
		dlgJanela.add(txtEDatA);
		
		dlgJanela.add(lblGrafGM);
		dlgJanela.add(lblGMDatD);
		dlgJanela.add(txtGMDatD);
		dlgJanela.add(lblEGMatA);
		dlgJanela.add(txtEGMatA);
		dlgJanela.add(btnCancel);
		dlgJanela.add(btnConfirm);
		
		
		btnConfirm.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/confirmarCa.png");
				btnConfirm.setIcon(iconN);
			}

			public void mouseEntered(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/brilho/confirmarCa.png");
				btnConfirm.setIcon(iconN);
			}

			public void mouseClicked(MouseEvent arg0) {
				if (txtSDatD.getDate() != null && txtSDatA.getDate() != null && txtEDatD.getDate() != null && txtEDatA.getDate() != null && txtGMDatD.getDate() != null && txtEGMatA.getDate() != null) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String deN = df.format(txtSDatD.getDate());
					String ateN = df.format(txtSDatA.getDate());
					GeraGraficos graficos = new GeraGraficos();
					try {
						graficos.gastosCC(deN, ateN,1);
					} catch (Exception e) {}
					
					deN = df.format(txtEDatD.getDate());
					ateN = df.format(txtEDatA.getDate());
					try {
						graficos.entradaCC(deN, ateN,1);
					} catch (Exception e) {}
					
					deN = df.format(txtGMDatD.getDate());
					ateN = df.format(txtEGMatA.getDate());
					try {
						graficos.gastosMens(deN, ateN, null,1);
					} catch (Exception e) {}
					
					try {
						new GerarGraficoPdf();
					} catch (DocumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
					EnvioEmail env = new EnvioEmail(null, "Gr?ficos em PDF", "3");
					env.enviar();
				}
				dlgJanela.dispose();
			}
		});		

		btnCancel.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/cancelarC.png");
				btnCancel.setIcon(iconN);
			}

			public void mouseEntered(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/brilho/cancelarC.png");
				btnCancel.setIcon(iconN);
			}

			public void mouseClicked(MouseEvent arg0) {
				dlgJanela.dispose();
			}
		});
		dlgJanela.setVisible(true);
	}
}
