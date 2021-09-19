package graficos;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


public class GrafPDF {

     public static final String RESULT ="arquivos/pdf/graficos.pdf";

     public static final Font BOLD_UNDERLINED =
       new Font(FontFamily.TIMES_ROMAN, 12, Font.BOLD | Font.UNDERLINE);
     public static final Font NORMAL = new Font
       (FontFamily.TIMES_ROMAN, 12);

     public static final String GRAFSCC ="imagens/graficos/grafico_scc_pdf.PNG";
     public static final String GRAFECC ="imagens/graficos/grafico_ecc_pdf.PNG";
     public static final String GRAFM ="imagens/graficos/grafico_m_pdf.PNG";
     
     public GrafPDF() throws DocumentException, IOException {
    	 geraPDF();
	}

     public void geraPDF() throws
      DocumentException, IOException {
           Document document = new Document();
           document.setPageSize(PageSize.A4.rotate());
           PdfWriter.getInstance(document, new FileOutputStream(RESULT));
           document.open();
           document.add(new Paragraph("Gráficos:"));
           document.add(Image.getInstance(String.format(GRAFSCC)));
           document.add(Image.getInstance(String.format(GRAFECC)));
           document.add(Image.getInstance(String.format(GRAFM)));
           document.close();
     }

}