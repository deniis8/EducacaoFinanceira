package janela.principal;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import configuracao.banco.dados.ConexaoBancoDadosSQLite;
import configuracao.banco.dados.CriaTabelas;
import configuracao.email.EnvioEmail;
import consultas.Consultas;
import crud.CrudCentroDeCusto;
import crud.CrudLancamento;
import graficos.GraficosPdf;
import graficos.GeraGraficos;
import mascaras.Mascaras;
import pivot.Pivot;
import recalculo.saldos.RecalculaSaldo;
import relatorios.Relatorios;
import sql.Exclusao;
import testes.IntegSqlServer;

/**
 *
 * @author adenilson.soares
 */
public class JanelaPrincipal {  

	private JFrame janela;
	private JTabbedPane tabPane;
	private JPanel panelGeren;
	private JPanel panelGraf;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem itemAtuali;
	private JMenuItem itemAbrirP;
	private JMenuItem itemSair;
	private JMenu menuRec;
	private JMenuItem itemRec;
	private JMenu menuBKP;
	private JMenuItem itemBKPEmail;
	private JMenuItem itemBKPTxt;
	private JMenu menuCC;
	private JMenuItem itemCC;
	private JLabel lblDe;
	private JLabel lblAte;
	private JDateChooser txtDe;
	private JDateChooser txtAte;
	private JCheckBox checRecebido;
	private JCheckBox checPago;
	private JCheckBox checAReceber;
	private JCheckBox checAPagar;
	private JComboBox<String> boxGCCusto;
	private JButton btnPesq;
	private JLabel lblTotalAP;
	private JLabel lblTotalP;
	private JLabel lblTotalAR;
	private JLabel lblTotalR;
	private JLabel lblSaldo;
	private JLabel lblInvF;
	private JLabel lblInvV;
	private JButton btnInc;
	private JButton btnEdit;
	private JButton btnExc;
	private DefaultTableModel model;
	private JTable tblTab;
	private JScrollPane sp;
	//private JPanel painelTab;
	private JTextField txtCod;
	private JFormattedTextField txtEmissao;
	private JFormattedTextField txtHora;
	private JTextField txtValor;
	private JTextField txtDesc;
	private JComboBox<String> boxStatus;
	private JComboBox<String> boxCCusto;

	private String opc = "";
	Consultas consulta = new Consultas();
	CrudLancamento crudLancamento = new CrudLancamento();
	Exclusao exc = new Exclusao();
	CriaTabelas criaTabs = new CriaTabelas();
	JDialog dialogP;
	//Tabela Centro de Custo
	DefaultTableModel modelCC;
	JTable tblTabCC;
	JScrollPane spCC;

	public JanelaPrincipal() {			
		janela();
		menu();
		ImageIcon icon = new ImageIcon("imagens/logo.png");
		janela.setIconImage(icon.getImage());
		configTab();
		relatorios();
		graficos();
		consulta.selectUlt(lblSaldo);
		consulta.selectInvF(lblInvF);
		consulta.selectInvV(lblInvV);		
	}

	public void janela() {
		janela = new JFrame("Educação Financeira");
		tabPane = new JTabbedPane();
		panelGeren = new JPanel();
		tabPane.addTab("Gerencial", null, panelGeren, null);
		panelGeren.setLayout(null);
		panelGeren.setBackground(new Color(200, 200, 200));
		lblDe = new JLabel("De:");
		lblAte = new JLabel("Até:");
		txtDe = new JDateChooser(new Date(), "dd/MM/yyyy");
		txtAte = new JDateChooser(new Date(), "dd/MM/yyyy");
		checRecebido = new JCheckBox("Recebido");
		checRecebido.setContentAreaFilled(false);
		checPago = new JCheckBox("Pago");
		checPago.setContentAreaFilled(false);
		checAReceber = new JCheckBox("A Receber");
		checAReceber.setContentAreaFilled(false);
		checAPagar = new JCheckBox("A Pagar");
		checAPagar.setContentAreaFilled(false);
		ImageIcon iconPesquisar = new ImageIcon("imagens/pesquisarP.png");
		btnPesq = new JButton(iconPesquisar);
		boxGCCusto = new JComboBox<String>();
		lblTotalAP = new JLabel("Total a Pagar...:");
		lblTotalP = new JLabel("Total Pago........:");
		lblTotalAR = new JLabel("Total a Receber....:");
		lblTotalR = new JLabel("Total Recebido......:");
		btnPesq.setBorderPainted(false);
		btnPesq.setContentAreaFilled(false);
		btnPesq.setToolTipText("Pesquisar");
		lblSaldo = new JLabel("Saldo Bancário:");
		lblInvF = new JLabel("Investimento Fixo:");
		lblInvV = new JLabel("Investimento Variável:");
		menuBar = new JMenuBar();
		menu = new JMenu("Menu");
		itemAtuali = new JMenuItem("Atualizar");
		itemAbrirP = new JMenuItem("Abrir Planilha");
		itemSair = new JMenuItem("Sair");
		menuRec = new JMenu("Recalculo");
		itemRec = new JMenuItem("Recalcular Saldo");
		menuBKP = new JMenu("Backup");
		itemBKPEmail = new JMenuItem("Enviar BKP/E-mail");
		itemBKPTxt = new JMenuItem("Realixar BKP em TXT");
		
		menuCC = new JMenu("Centro de Custo");
		itemCC = new JMenuItem("Cadastro");
		
		
		ImageIcon iconIncluir = new ImageIcon("imagens/incluirP.png");
		btnInc = new JButton(iconIncluir);
		btnInc.setContentAreaFilled(false);
		btnInc.setBorderPainted(false);
		btnInc.setToolTipText("Incluir");
		ImageIcon iconEdit = new ImageIcon("imagens/editarP.png");
		btnEdit = new JButton(iconEdit);
		btnEdit.setContentAreaFilled(false);
		btnEdit.setBorderPainted(false);
		btnEdit.setToolTipText("Editar");
		ImageIcon iconExcluir = new ImageIcon("imagens/excluirP.png");
		btnExc = new JButton(iconExcluir);
		btnExc.setContentAreaFilled(false);
		btnExc.setBorderPainted(false);
		btnExc.setToolTipText("Excluir");

		janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		janela.setSize(845, 595);
		janela.setLocationRelativeTo(null);
		// janela.setLayout(null);
		janela.setResizable(false);

		lblDe.setBounds(05, 40, 80, 10);
		lblAte.setBounds(05, 70, 80, 10);
		txtDe.setBounds(40, 35, 90, 20);
		txtAte.setBounds(40, 65, 90, 20);
		checRecebido.setBounds(140, 35, 85, 20);
		checPago.setBounds(140, 65, 70, 20);

		checAReceber.setBounds(230, 35, 85, 20);
		checAPagar.setBounds(230, 65, 70, 20);
		boxGCCusto.setBounds(320, 63, 220, 20);
		boxGCCusto.addItem("Todos");
		consulta.selecBox(boxGCCusto, "");
		btnPesq.setBounds(530, 56, 70, 32);	
		lblSaldo.setBounds(03, 475, 200, 13);
		lblInvF.setBounds(03, 490, 200, 13);
		lblInvV.setBounds(180, 475, 200, 13);
		lblTotalAP.setBounds(490, 475, 140, 13);
		lblTotalP.setBounds(490, 490, 140, 13);
		lblTotalAR.setBounds(660, 475, 140, 13);
		lblTotalR.setBounds(660, 490, 140, 13);
		btnInc.setBounds(685, 53, 32, 32);
		btnEdit.setBounds(735, 53, 32, 32);
		btnExc.setBounds(780, 53, 32, 32);
		panelGeren.add(lblDe);
		panelGeren.add(lblAte);
		panelGeren.add(txtDe); 
		panelGeren.add(txtAte);
		panelGeren.add(checRecebido);
		panelGeren.add(checPago);

		panelGeren.add(checAReceber);
		panelGeren.add(checAPagar);
		panelGeren.add(boxGCCusto);
		panelGeren.add(btnPesq);
		
		panelGeren.add(lblSaldo);
		panelGeren.add(lblInvF);
		panelGeren.add(lblInvV);
		panelGeren.add(lblTotalAP);
		panelGeren.add(lblTotalP);
		panelGeren.add(lblTotalAR);
		panelGeren.add(lblTotalR);
		
		panelGeren.add(btnInc);
		panelGeren.add(btnEdit);
		panelGeren.add(btnExc);

		janela.add(tabPane);

		itemAtuali.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					consulta.selectLanca(model, tblTab, lblTotalAP, lblTotalP, lblTotalAR, lblTotalR);
				} catch (ParseException e) {
				}
				consulta.selectUlt(lblSaldo);
				consulta.selectInvF(lblInvF);
				consulta.selectInvV(lblInvV);
			}
		});
		itemAbrirP.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				abrirPlan();
			}
		});
		itemSair.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		itemRec.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rodarThred();
			}
		});
		itemBKPEmail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EnvioEmail env = new EnvioEmail(null,"EDUCAÇÃO FINANCEIRA: BACKUP BANCO DE DADOS","2");
				try {
					env.enviar();
				} catch (Exception e1) {
				}
			}
		});
		itemBKPTxt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				IntegSqlServer integ = new IntegSqlServer();
				integ.backupLanc();
				integ.backupCCusto();
				integ.backupSaldos();
			}
		});
		
		itemCC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Cadastro de centro de custo
				janelaCCusto();
			}
		});

		btnPesq.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/pesquisarPC.png");
				btnPesq.setIcon(iconN);
			}

			public void mouseEntered(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/brilho/pesquisarPC.png");
				btnPesq.setIcon(iconN);
			}

			public void mouseClicked(MouseEvent arg0) {
				consulta.pesqLan(model, tblTab, txtDe, txtAte, checRecebido, checPago, checAPagar, checAReceber, lblTotalAP, lblTotalP, lblTotalAR, lblTotalR, boxGCCusto);
			}
		});

		btnInc.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/incluirPC.png");
				btnInc.setIcon(iconN);
			}

			public void mouseEntered(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/brilho/incluirPC.png");
				btnInc.setIcon(iconN);
			}

			public void mouseClicked(MouseEvent arg0) {
				opc = "i";
				janelaInc("Incluir");
				limpCampos(opc);
				consulta.selecBox(boxCCusto, "S");
				dialogP.setVisible(true);
			}
		});

		btnEdit.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/editarPC.png");
				btnEdit.setIcon(iconN);
			}

			public void mouseEntered(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/brilho/editarPC.png");
				btnEdit.setIcon(iconN);
			}

			public void mouseClicked(MouseEvent arg0) {
				opc = "e";
				int indiceLinha = tblTab.getSelectedRow();
				if (indiceLinha != -1) {
					janelaInc("Editar");
					limpCampos(opc);
					txtCod.setText(tblTab.getValueAt(indiceLinha, 0).toString());
					if (!txtCod.getText().equals("")) {
						consulta.selecBox(boxCCusto, "S");
						consulta.selectLanJ(txtCod.getText(), txtEmissao, txtHora, txtValor, txtDesc,
								boxStatus, boxCCusto);
						dialogP.setVisible(true);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Selecione um Registro!");
				}
			}
		});

		btnExc.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/excluirPC.png");
				btnExc.setIcon(iconN);
			}

			public void mouseEntered(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/brilho/excluirPC.png");
				btnExc.setIcon(iconN);
			}

			public void mouseClicked(MouseEvent arg0) {
				opc = "ex";
				int indiceLinha = tblTab.getSelectedRow();
				if (indiceLinha != -1) {
					janelaInc("Excluir");
					limpCampos(opc);
					txtCod.setText(tblTab.getValueAt(indiceLinha, 0).toString());
					if (!txtCod.getText().equals("")) {
						consulta.selecBox(boxCCusto, "S");
						consulta.selectLanJ(txtCod.getText(), txtEmissao, txtHora, txtValor, txtDesc,
								boxStatus, boxCCusto);
						editCampos();
						dialogP.setVisible(true);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Selecione um Registro!");
				}
			}
		});

	}

	public void limpCampos(String opc) {
		txtCod.setText("");
		if (opc != "i") {
			txtEmissao.setText("");
			txtHora.setText("");
		}
		txtValor.setText("");
		txtDesc.setText("");
		// boxStatus.removeAllItems();
		boxCCusto.removeAllItems();
	}

	public void editCampos() {
		txtCod.setEditable(false);
		txtEmissao.setEditable(false);
		txtHora.setEditable(false);
		txtValor.setEditable(false);
		txtDesc.setEditable(false);
		// boxStatus.setEditable(false);
		// boxCCusto.setEditable(false);
	}

	public void menu() {
		menu.add(itemAtuali);
		menu.add(itemAbrirP);
		menu.add(itemSair);
		menuRec.add(itemRec);
		menuBKP.add(itemBKPEmail);
		menuBKP.add(itemBKPTxt);
		menuCC.add(itemCC);
		menuBar.add(menu);
		menuBar.add(menuRec);
		menuBar.add(menuBKP);
		menuBar.add(menuCC);
		janela.setJMenuBar(menuBar);

	}

	public void configTab() {
		model = new DefaultTableModel();
		tblTab = new JTable(model);
		sp = new JScrollPane(tblTab);
		//painelTab = new JPanel();
		model.addColumn("CÓDIGO");
		model.addColumn("EMISSÃO");
		model.addColumn("DIA DA SEMANA");
		model.addColumn("VALOR");
		model.addColumn("DESCRIÇÃO");
		model.addColumn("CENTRO. CUSTO");
		model.addColumn("STATUS");
		//painelTab.setLayout(null);
		//painelTab.setPreferredSize(new Dimension(720, 100));
		sp.setBounds(03, 90, 820, 380);
		panelGeren.add(sp);

		try {
			consulta.selectLanca(model, tblTab, lblTotalAP, lblTotalP, lblTotalAR, lblTotalR);
		} catch (ParseException ex) {
		}
	}

	public void janelaInc(String titulo) {
		int x = 0, y = 30;
		dialogP = new JDialog();
		dialogP.setTitle(titulo);
		dialogP.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogP.setSize(490, 215);
		dialogP.setLocationRelativeTo(null);
		dialogP.setResizable(false);
		dialogP.setModal(true);
		dialogP.setLayout(null);

		JPanel painel = new JPanel();
		painel.setBackground(new Color(200, 200, 200));
		painel.setBounds(0, 0, 473, 30);
		JLabel lblOpc = new JLabel(titulo);
		lblOpc.setFont(new Font("Serif", Font.BOLD, 20));
		JLabel lblCod = new JLabel("Cod:");
		txtCod = new JTextField();
		txtCod.setEnabled(false);
		JLabel lblEmissao = new JLabel("Emissão:");
		txtEmissao = new JFormattedTextField(Mascaras.mascara("##/##/####"));
		JLabel lblHora = new JLabel("Hora:");
		txtHora = new JFormattedTextField(Mascaras.mascara("##:##:##"));
		JLabel lblValor = new JLabel("Valor:");
		txtValor = new JTextField();
		JLabel lblDesc = new JLabel("Descrição:");
		txtDesc = new JTextField();
		JLabel lblStatus = new JLabel("Status:");
		boxStatus = new JComboBox<>();
		boxStatus.addItem("A Pagar");
		boxStatus.addItem("Pago");
		boxStatus.addItem("A Receber");
		boxStatus.addItem("Recebido");
		JLabel lblCCusto = new JLabel("C.Custo:");
		boxCCusto = new JComboBox<>();
		// consulta.selecBox(boxCCusto);
		ImageIcon iconCancel = new ImageIcon("imagens/cancelar.png");
		JButton btnCancel = new JButton(iconCancel);
		btnCancel.setContentAreaFilled(false);
		btnCancel.setBorderPainted(false);
		btnCancel.setToolTipText("Cancelar");
		ImageIcon iconConfirm = new ImageIcon("imagens/confirmar.png");
		JButton btnConfirm = new JButton(iconConfirm);
		btnConfirm.setContentAreaFilled(false);
		btnConfirm.setBorderPainted(false);
		btnConfirm.setToolTipText("Confirmar");
		
		//Captura a data atual (dia/mês/ano)
		Date dt = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
		txtEmissao.setText(formato.format(dt));

		//Tratativa para hora atual 
		Calendar data = Calendar.getInstance();
		String horaStr = Integer.toString(data.get(Calendar.HOUR_OF_DAY));
		String minStr = Integer.toString(data.get(Calendar.MINUTE));
		if (horaStr.length() == 1) {
			horaStr = "0" + horaStr;
		}
		if (minStr.length() == 1) {
			minStr = "0" + minStr;
		}
		txtHora.setText(horaStr + ":" + minStr + ":00");

		
		lblOpc.setBounds(x + 50, y + 0, 80, 30);
		lblCod.setBounds(x + 05, y + 05, 30, 20);
		txtCod.setBounds(x + 47, y + 05, 100, 20);
		lblEmissao.setBounds(x + 180, y + 05, 70, 20);
		txtEmissao.setBounds(x + 245, y + 05, 80, 20);
		lblHora.setBounds(x + 350, y + 05, 80, 20);
		txtHora.setBounds(x + 390, y + 05, 80, 20);
		lblValor.setBounds(x + 05, y + 40, 40, 20);
		txtValor.setBounds(x + 47, y + 40, 50, 20);
		lblDesc.setBounds(x + 110, y + 40, 70, 20);
		txtDesc.setBounds(x + 180, y + 40, 290, 20);
		lblStatus.setBounds(x + 05, y + 75, 70, 20);
		boxStatus.setBounds(x + 47, y + 75, 90, 20);
		lblCCusto.setBounds(x + 150, y + 75, 70, 20);
		boxCCusto.setBounds(x + 210, y + 75, 260, 20);
		btnCancel.setBounds(x + 05, y + 110, 32, 32);
		btnConfirm.setBounds(x + 437, y + 110, 32, 32);

		painel.add(lblOpc);
		dialogP.add(painel);
		dialogP.add(lblCod);
		dialogP.add(txtCod);
		dialogP.add(lblEmissao);
		dialogP.add(txtEmissao);
		dialogP.add(lblHora);
		dialogP.add(txtHora);
		dialogP.add(lblValor);
		dialogP.add(txtValor);
		dialogP.add(lblDesc);
		dialogP.add(txtDesc);
		dialogP.add(lblStatus);
		dialogP.add(boxStatus);
		dialogP.add(lblCCusto);
		dialogP.add(boxCCusto);
		dialogP.add(btnCancel);
		dialogP.add(btnConfirm);
		
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

				if (opc == "i") {
					if (!txtEmissao.getText().equals("") && !txtHora.getText().equals("") && !txtValor.getText().equals("") && !txtDesc.getText().equals("")) {
						ConexaoBancoDadosSQLite conexao = new ConexaoBancoDadosSQLite();
						PreparedStatement pst;
						String sqlSelecId="SELECT MAX(ID_LANC) FROM LANCAMENTOS";
						String id = "";
						conexao.abrir();
					        try {
					            pst = conexao.getConexao().prepareStatement(sqlSelecId);
					            ResultSet rs = pst.executeQuery();
					            while (rs.next()) {
					            	if(rs.getString(1)==null) {
					            		id = "000001";
					            	}else {
					            		id = Integer.toString(Integer.parseInt(rs.getString(1))+1);
					            	}					            	
								}					            
					        } catch (SQLException e) {}
					    conexao.fechar();
					    crudLancamento.incluirLancamento(id,txtEmissao.getText(), txtHora.getText(), Double.parseDouble(txtValor.getText()),
								txtDesc.getText(), boxStatus.getSelectedItem().toString(), boxCCusto);
						limpCampos(opc);
						try {
							consulta.selectLanca(model, tblTab, lblTotalAP, lblTotalP, lblTotalAR, lblTotalR);
						} catch (ParseException e) {
						}
						consulta.selectUlt(lblSaldo);
						consulta.selectInvF(lblInvF);
						consulta.selectInvV(lblInvV);
						consulta.selecBox(boxCCusto, "S");
					} else {
						JOptionPane.showMessageDialog(null, "Favor preencher os campos obrigatórios!");
					}
				} else if (opc == "e") {
					crudLancamento.alterarLancamento(txtCod.getText(), txtEmissao.getText(), txtHora.getText(),
							Double.parseDouble(txtValor.getText()), txtDesc.getText(), boxStatus, boxCCusto);
					try {
						consulta.selectLanca(model, tblTab, lblTotalAP, lblTotalP, lblTotalAR, lblTotalR);
					} catch (ParseException e) {
					}
					consulta.selectUlt(lblSaldo);
					consulta.selectInvF(lblInvF);
					consulta.selectInvV(lblInvV);
					dialogP.dispose();
				} else if (opc == "ex") {
					int resposta = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir esse Lançamento?",
							"Deseja Lançamento?", JOptionPane.YES_NO_OPTION);
					if (resposta == JOptionPane.YES_OPTION) {
						crudLancamento.excluirLancamento(txtCod.getText());
						try {
							consulta.selectLanca(model, tblTab, lblTotalAP, lblTotalP, lblTotalAR, lblTotalR);
						} catch (ParseException e) {
						}
						consulta.selectUlt(lblSaldo);
						consulta.selectInvF(lblInvF);
						consulta.selectInvV(lblInvV);
						dialogP.dispose();
					}
				}
			
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
				dialogP.dispose();
			}
		});
	}
	
	public void janelaCCusto() {
		CrudCentroDeCusto crudCentroCusto = new CrudCentroDeCusto();
		JDialog dialogCC = new JDialog();
		JButton btnCcInc = new JButton("Incluir");
		JButton btnCcAlt = new JButton("Alterar");
		JButton btnCcExc = new JButton("Excluir");
		modelCC = new DefaultTableModel();
		tblTabCC = new JTable(modelCC);
		spCC = new JScrollPane(tblTabCC);
		modelCC.addColumn("CÓDIGO");
		modelCC.addColumn("DESCRIÇÃO");
		spCC.setBounds(03, 40, 400, 315);
		dialogCC.add(spCC);
		
		dialogCC.setTitle("Gerenciamento de Centro de Custo");
		dialogCC.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogCC.setSize(420, 400);
		dialogCC.setLocationRelativeTo(null);
		dialogCC.setResizable(false);
		dialogCC.setLayout(null);		
		
		
		btnCcInc.setBounds(110, 10, 80, 20);
		btnCcAlt.setBounds(210, 10, 80, 20);
		btnCcExc.setBounds(310, 10, 80, 20);
		
		dialogCC.add(btnCcInc);
		dialogCC.add(btnCcAlt);
		dialogCC.add(btnCcExc);
		
		try {
			crudCentroCusto.selecCC(modelCC, tblTabCC);
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage());
		}
		
		
		
		btnCcInc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				janeInserCC("1",null,null);
			}
		});
		
		btnCcAlt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				int indiceLinha = tblTabCC.getSelectedRow();
				if (indiceLinha != -1) {
					if (!tblTabCC.getValueAt(indiceLinha, 0).toString().equals("")) {
						janeInserCC("2",tblTabCC.getValueAt(indiceLinha, 0).toString(),tblTabCC.getValueAt(indiceLinha, 1).toString());
					}
				} else {
					JOptionPane.showMessageDialog(null, "Selecione um Registro!");
				}
			}
		});
		
		btnCcExc.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int indiceLinha = tblTabCC.getSelectedRow();
				if (indiceLinha != -1) {
					if (!tblTabCC.getValueAt(indiceLinha, 0).toString().equals("")) {
						janeInserCC("3",tblTabCC.getValueAt(indiceLinha, 0).toString(),tblTabCC.getValueAt(indiceLinha, 1).toString());
					}
				} else {
					JOptionPane.showMessageDialog(null, "Selecione um Registro!");
				}				
			}
		});
		
		dialogCC.setVisible(true);
		
	}
	
	public void janeInserCC(String opc, String cod, String descri) {
		
		String titulo = "";
		if(opc == "1") {
			titulo = "Incluir";
		}
		if(opc == "2"){
			titulo = "Alterar";
		}else {
			titulo = "Excluir";
		}
				
		CrudCentroDeCusto crudCentroCusto = new CrudCentroDeCusto();
		JDialog dialogCC = new JDialog();
		JLabel lblCodCC = new JLabel("Cód:");
		JTextField txtCodCC = new JTextField();
		JLabel lblDescri = new JLabel("Descrição:");
		JTextField txtDescri = new JTextField();
		JButton btnCancel = new JButton("Cancelar");
		JButton btnConf = new JButton("Confirmar");
		
		txtCodCC.setEnabled(false);
		
		dialogCC.setTitle(titulo);
		dialogCC.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialogCC.setSize(420, 110);
		dialogCC.setLocationRelativeTo(null);
		dialogCC.setResizable(false);
		dialogCC.setModal(true);
		dialogCC.setLayout(null);		
		
		lblCodCC.setBounds(05, 10, 80, 25);
		txtCodCC.setBounds(40, 10, 40, 20);
		lblDescri.setBounds(90, 10, 80, 25);
		txtDescri.setBounds(165, 10, 230, 20);
		btnCancel.setBounds(180, 40, 90, 20);
		btnConf.setBounds(293, 40, 100, 20);
		
		dialogCC.add(lblCodCC);
		dialogCC.add(txtCodCC);
		dialogCC.add(lblDescri);
		dialogCC.add(txtDescri);
		dialogCC.add(btnCancel);
		dialogCC.add(btnConf);
		
		if(opc == "2" || opc == "3"){
			if(opc == "3") {
				txtDescri.setEnabled(false);
			}
			txtCodCC.setText(cod);
			txtDescri.setText(descri);
		}
				
		
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialogCC.dispose();
			}
		});
		
		btnConf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(opc == "1") {
					crudCentroCusto.incluirCentroCusto(txtDescri.getText());
					dialogCC.dispose();
					try {		
						crudCentroCusto.selecCC(modelCC, tblTabCC);
						boxGCCusto.removeAllItems();
						boxGCCusto.addItem("Todos");
						consulta.selecBox(boxGCCusto, "");						
					} catch (ParseException e1) {
						JOptionPane.showMessageDialog(null, "Erro: " + e1.getMessage());
					}
				}
				else if(opc == "2"){
					crudCentroCusto.alterarCentroCusto(txtDescri.getText(), Integer.parseInt(cod));
					dialogCC.dispose();					
					boxGCCusto.removeAllItems();
					boxGCCusto.addItem("Todos");
					consulta.selecBox(boxGCCusto, "");
					try {		
						crudCentroCusto.selecCC(modelCC, tblTabCC);
					} catch (ParseException e1) {
						JOptionPane.showMessageDialog(null, "Erro: " + e1.getMessage());
					}
					
				}else {
					Object[] options = { "Sim", "Não", "Cancelar" };
			    	int resposta = JOptionPane.showOptionDialog(null, "Tem certeza que deseja excluir esse registro?", "Informação", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			    	if(resposta==0) {
			    		crudCentroCusto.excluirCentroCusto(Integer.parseInt(cod));
			    		dialogCC.dispose();			    		
			    		boxGCCusto.removeAllItems();
						boxGCCusto.addItem("Todos");
						consulta.selecBox(boxGCCusto, "");
			    		try {		
			    			crudCentroCusto.selecCC(modelCC, tblTabCC);
						} catch (ParseException e1) {
							JOptionPane.showMessageDialog(null, "Erro: " + e1.getMessage());
						}
			    	}					
				}
			}
		});
		
		dialogCC.setVisible(true);
		
	}

	public void rodarThred() {
		JDialog dlgProgress = new JDialog();
		dlgProgress.setTitle("Calculando...");
		JLabel lblStatus = new JLabel("Calculando...");
		JProgressBar progress = new JProgressBar();
		dlgProgress.setSize(300, 90);
		dlgProgress.setLocationRelativeTo(null);
		// dlgProgress.setModal(true);
		dlgProgress.setLayout(null);
		lblStatus.setBounds(05, 05, 300, 15);
		progress.setBounds(05, 25, 270, 15);
		dlgProgress.add(lblStatus);
		dlgProgress.add(progress);		
		
		String idReg = "";
		String data = "";
		//int resposta = JOptionPane.showConfirmDialog(null, "Recalcular Tudo?", "Recalcular Tudo", JOptionPane.NO_OPTION);
		Object[] options = { "Sim", "Não", "Cancelar" };
    	int resposta = JOptionPane.showOptionDialog(null, "Deseja recalcular tudo?", "Informação", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
    	System.out.println(resposta);
        if (resposta == 1) {
        	int indiceLinha = tblTab.getSelectedRow();			
			if (indiceLinha != -1) {
				idReg= tblTab.getValueAt(indiceLinha, 0).toString();	
				data = tblTab.getValueAt(indiceLinha, 1).toString();
			} else {
				JOptionPane.showMessageDialog(null, "Selecione um Registro!");
			}
        }
        if (resposta == 0 || resposta == 1) {
        	dlgProgress.setVisible(true);
            RecalculaSaldo rec = new RecalculaSaldo(progress, dlgProgress, lblStatus, lblSaldo, lblInvF, idReg, data);
    		Thread t = new Thread(rec);
    		t.start();
        }
	}

	public void relatorios() {
		JLabel lblERel = new JLabel("RELATÓRIOS");
		JLabel lblEDe = new JLabel("De:");
		JLabel lblEAte = new JLabel("Até:");
		JDateChooser txtEDe = new JDateChooser(new Date(), "dd/MM/yyyy");
		JDateChooser txtEAte = new JDateChooser(new Date(), "dd/MM/yyyy");
		JRadioButton radExtrat = new JRadioButton("Extrato");
		radExtrat.setContentAreaFilled(false);
		JRadioButton radSeman = new JRadioButton("Gastos Detalhados");
		radSeman.setContentAreaFilled(false);
		ImageIcon iconGeraRel = new ImageIcon("imagens/geraRel.png");
		JButton btnEGeraRel = new JButton(iconGeraRel);
		btnEGeraRel.setContentAreaFilled(false);
		btnEGeraRel.setBorderPainted(false);
		btnEGeraRel.setToolTipText("Gerar Relatório");
		JTextPane txtPER = new JTextPane();
		JScrollPane sp = new JScrollPane(txtPER);
		JPanel panelRel = new JPanel();
		tabPane.addTab("Relatórios", null, panelRel, null);
		panelRel.setLayout(null);
		panelRel.setBackground(new Color(200, 200, 200));
		lblERel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
		txtPER.setEditable(false);
		sp.setBounds(05, 90, 815, 415);
		lblERel.setBounds(360, 0, 200, 30);
		lblEDe.setBounds(05, 40, 80, 10);
		lblEAte.setBounds(05, 70, 80, 10);
		txtEDe.setBounds(40, 35, 90, 20);
		txtEAte.setBounds(40, 65, 90, 20);
		btnEGeraRel.setBounds(310, 53, 32, 32);
		radExtrat.setBounds(150, 48, 140, 20);
		radSeman.setBounds(150, 68, 140, 20);
		panelRel.add(lblERel);
		panelRel.add(lblEDe);
		panelRel.add(lblEAte);
		panelRel.add(radExtrat);
		panelRel.add(radSeman);
		panelRel.add(txtEDe);
		panelRel.add(txtEAte);
		panelRel.add(btnEGeraRel);
		panelRel.add(sp);

		radExtrat.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				radSeman.setSelected(false);
			}
		});
		radSeman.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				radExtrat.setSelected(false);
			}
		});
		
		btnEGeraRel.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/geraRelC.png");
				btnEGeraRel.setIcon(iconN);
			}

			public void mouseEntered(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/brilho/geraRelC.png");
				btnEGeraRel.setIcon(iconN);
			}

			public void mouseClicked(MouseEvent arg0) {

				Relatorios rel = new Relatorios();
				if (txtEDe.getDate() != null && txtEAte.getDate() != null) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String deN = df.format(txtEDe.getDate());
					String ateN = df.format(txtEAte.getDate());
					if (radSeman.isSelected()) {
						Date data = new Date();
						SimpleDateFormat formatador = new SimpleDateFormat("yyyy-MM-dd");
						formatador.format(data);
						int dia = Integer.parseInt(formatador.format(data).substring(8,10));
						Pivot p = new Pivot();
						p.criarTab(dia);
						rel.relSemanal(deN, ateN, txtPER);
					} else if (radExtrat.isSelected()) {
						rel.extrato(deN, ateN, txtPER);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Favor preencher os campos Data!");
				}
			
			}
		});
	}

	public void graficos() {
		panelGraf = new JPanel();
		tabPane.addTab("Gráficos", null, panelGraf, null);
		panelGraf.setLayout(null);
		panelGraf.setBackground(new Color(200, 200, 200));
		JLabel lblGRel = new JLabel("GRÁFICOS");
		lblGRel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 20));
		JLabel lblGDe = new JLabel("De:");
		JLabel lblGAte = new JLabel("Até:");
		JDateChooser txtGDe = new JDateChooser(new Date(), "dd/MM/yyyy");
		JDateChooser txtGAte = new JDateChooser(new Date(), "dd/MM/yyyy");
		JRadioButton checSCC = new JRadioButton("Saídas CC");
		checSCC.setContentAreaFilled(false);
		JRadioButton checECC = new JRadioButton("Entradas CC");
		checECC.setContentAreaFilled(false);
		JRadioButton checM = new JRadioButton("Gastos Mensais");
		JComboBox<String> boxCcG = new JComboBox<String>();
		boxCcG.addItem("Todos");
		consulta.selecBox(boxCcG, "");
		boxCcG.setEnabled(false);
		checM.setContentAreaFilled(false);
		ImageIcon iconGeraGraf = new ImageIcon("imagens/grafico.png");
		JButton btnGGeraGra = new JButton(iconGeraGraf);
		btnGGeraGra.setContentAreaFilled(false);
		btnGGeraGra.setBorderPainted(false);
		btnGGeraGra.setToolTipText("Gerar Gráfico");
		
		ImageIcon iconEnvG = new ImageIcon("imagens/enviaG.png");
		JButton btnEnviG = new JButton(iconEnvG);
		btnEnviG.setContentAreaFilled(false);
		btnEnviG.setBorderPainted(false);
		btnEnviG.setToolTipText("Envia Gráficos em PDF");
		
		JLabel lblGrafGM = new JLabel();
		JLabel lblGrafE = new JLabel();
		JLabel lblGrafGCC = new JLabel();
		
		
		
		btnGGeraGra.setBounds(500, 60, 32, 32);
		
		lblGRel.setBounds(360, 0, 200, 30);
		lblGDe.setBounds(05, 40, 80, 10);
		lblGAte.setBounds(05, 70, 80, 10);
		txtGDe.setBounds(40, 35, 90, 20);
		txtGAte.setBounds(40, 65, 90, 20);
		checSCC.setBounds(150, 28, 140, 20);
		checECC.setBounds(150, 48, 140, 20);
		checM.setBounds(150, 68, 140, 20);
		boxCcG.setBounds(270, 68, 220, 20);
		lblGrafGM.setBounds(05, 100, 815, 410);
		lblGrafE.setBounds(05, 100, 500, 410);
		lblGrafGCC.setBounds(05, 400, 815, 410);
		btnEnviG.setBounds(787, 05, 32, 32);
		panelGraf.add(lblGRel);
		panelGraf.add(lblGDe);
		panelGraf.add(lblGAte);
		panelGraf.add(txtGDe);
		panelGraf.add(txtGAte);
		panelGraf.add(checSCC);
		panelGraf.add(checECC);
		panelGraf.add(checM);
		panelGraf.add(boxCcG);
		panelGraf.add(btnGGeraGra);
		panelGraf.add(lblGrafE);
		panelGraf.add(lblGrafGCC);
		panelGraf.add(lblGrafGM);
		panelGraf.add(btnEnviG);

		checSCC.setSelected(true);
		lblGrafGM.setIcon(carregaImg("scc"));
		checSCC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checECC.setSelected(false);
				checM.setSelected(false);
				lblGrafGM.setIcon(carregaImg("scc"));
				boxCcG.setEnabled(false);
			}
		});
		checECC.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checSCC.setSelected(false);
				checM.setSelected(false);
				lblGrafGM.setIcon(carregaImg("ecc"));
				boxCcG.setEnabled(false);
			}
		});
		checM.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checECC.setSelected(false);
				checSCC.setSelected(false);
				lblGrafGM.setIcon(carregaImg("m"));
				boxCcG.setEnabled(true);
			}
		});		
		
		btnGGeraGra.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent arg0) {
			}

			public void mousePressed(MouseEvent arg0) {
			}

			public void mouseExited(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/graficoC.png");
				btnGGeraGra.setIcon(iconN);
			}

			public void mouseEntered(MouseEvent arg0) {
				ImageIcon iconN = new ImageIcon("imagens/brilho/graficoC.png");
				btnGGeraGra.setIcon(iconN);
			}			

			public void mouseClicked(MouseEvent arg0) {
				if (txtGDe.getDate() != null && txtGAte.getDate() != null) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String deN = df.format(txtGDe.getDate());
					String ateN = df.format(txtGAte.getDate());
					GeraGraficos graficos = new GeraGraficos();
					if (checSCC.isSelected() || checECC.isSelected() || checM.isSelected()) {
						if (checSCC.isSelected()) {
							try {
								graficos.gastosCC(deN, ateN,0);
								lblGrafGM.setIcon(carregaImg("scc"));
							} catch (Exception e) {
							}

						} else if (checECC.isSelected()) {
							try {
								graficos.entradaCC(deN, ateN,0);
								lblGrafGM.setIcon(carregaImg("ecc"));
							} catch (Exception e) {
							}

						} else if (checM.isSelected()) {
							try {
								graficos.gastosMens(deN, ateN, boxCcG,0);
								lblGrafGM.setIcon(carregaImg("m"));
							} catch (Exception e) {
							}
						}
					} else {
						JOptionPane.showMessageDialog(null, "Selecione um tipo de Gráfico!");
					}

				} else {
					JOptionPane.showMessageDialog(null, "Favor preencher os campos Data!");
				}
			}
		});
		
		btnEnviG.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {				
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				ImageIcon iconN = new ImageIcon("imagens/enviaG.png");
				btnEnviG.setIcon(iconN);
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				ImageIcon iconN = new ImageIcon("imagens/brilho/enviaGC.png");
				btnEnviG.setIcon(iconN);				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				new GraficosPdf();
			}
		});
	}
	
	public ImageIcon carregaImg(String opc) {
		ImageIcon icon = null;
		BufferedImage imgGCC = null;
		BufferedImage imgECC = null;
		BufferedImage imgGM = null;
		try {
			if (opc == "scc") {
				imgECC = ImageIO.read(new File("imagens/graficos/grafico_scc.PNG"));
				ImageIcon icoGCC = new ImageIcon(imgECC);
				icon = icoGCC;
			} else if (opc == "ecc") {
				imgGCC = ImageIO.read(new File("imagens/graficos/grafico_ecc.PNG"));
				ImageIcon icoECC = new ImageIcon(imgGCC);
				icon = icoECC;
			} else if (opc == "m") {
				imgGM = ImageIO.read(new File("imagens/graficos/grafico_m.PNG"));
				ImageIcon icoGM = new ImageIcon(imgGM);
				icon = icoGM;
			}

		} catch (Exception e) {
		}

		return icon;
	}

	public void abrirPlan() {
		try {
			Desktop.getDesktop().open(new File("arquivos/planilhas/GESTÃO FINANCEIRA.xlsm"));
		} catch (IOException e) {
		}
	}

	public static void main(String[] args) {
		new JanelaPrincipal().janela.setVisible(true);
	}
}