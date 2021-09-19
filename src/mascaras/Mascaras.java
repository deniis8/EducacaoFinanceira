package mascaras;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;

public class Mascaras {

	public static MaskFormatter mascara(String Mascara) {

		MaskFormatter F_Mascara = new MaskFormatter();
		try {
			F_Mascara.setMask(Mascara); // Atribui a mascara
			F_Mascara.setPlaceholderCharacter(' '); // Caracter para preencimento
		} catch (Exception excecao) {
			excecao.printStackTrace();
		}
		return F_Mascara;
	}
	
	public static String formatData(String dataF, String opc) {
        dataF = dataF.replace("-", "");
        SimpleDateFormat formato = null;
        Date data = null;
        if (opc.equals("1")) {
            formato = new SimpleDateFormat("yyyyMMdd");
        } else if (opc.equals("2")) {
            formato = new SimpleDateFormat("dd/MM/yyyy");
        }

        try {
            data = formato.parse(dataF);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(null, "Erro:" + ex.getMessage());
        }
        if (opc.equals("1")) {
            formato.applyPattern("dd/MM/yyyy");
        } else if (opc.equals("2")) {
            formato.applyPattern("yyyy-MM-dd");
        }

        return formato.format(data);
    }
}
