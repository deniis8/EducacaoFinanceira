package interfaces;

import javax.swing.JComboBox;

public interface CrudLancamentos {

	public void incluirLancamento(String id, String data, String hora, Double valor, String descri, String status, JComboBox<String> boxCC);
	public void alterarLancamento(String cod, String data, String hora, double valor, String desc, JComboBox<?> status, JComboBox<?> boxCC);
	public void excluirLancamento(String cod);
}
