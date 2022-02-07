/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package relatorios;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import configuracao.banco.dados.ConexaoBancoDadosSQLite;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author DeniisDell
 */
public class RelatorioPdf {

    Document document;
    PdfWriter write;
    ConexaoBancoDadosSQLite conexao = new ConexaoBancoDadosSQLite();
    PreparedStatement pst;
	ResultSet rs;
	
    public void configPDF() throws FileNotFoundException, DocumentException {
    	
    	
        document = new Document(PageSize.A4, 50, 50, 50, 50);
        write = PdfWriter.getInstance(document, new FileOutputStream("relatorios/rel.PDF")); 
        document.open();
        //document.setMargins(36, 72, 108, 180);
        //document.setMarginMirroring(true);
        PdfPTable table = new PdfPTable(4);
        
        PdfPCell cell1 = new PdfPCell(new Paragraph("Data",FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
        PdfPCell cell4 = new PdfPCell(new Paragraph("Valor",FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));       
        PdfPCell cell7 = new PdfPCell(new Paragraph("Descrição",FontFactory.getFont(FontFactory.COURIER_BOLD, 10)));
        PdfPCell cell8 = new PdfPCell(new Paragraph("Saldo",FontFactory.getFont(FontFactory.COURIER_BOLD, 10))); 
        
        table.addCell(cell1);
        table.addCell(cell4);
        table.addCell(cell7);
        table.addCell(cell8);
        
        document.add(new Paragraph("EXTRATO BANCARIO",FontFactory.getFont(FontFactory.COURIER, 15))); 
        document.add(new Paragraph(" "));
        selectExtrato("2019-03-19", "2021-05-13");
         
         try {
			while(rs.next()) {
				cell1 = new PdfPCell(new Paragraph(rs.getString(1),FontFactory.getFont(FontFactory.COURIER_BOLD, 5)));
				cell4 = new PdfPCell(new Paragraph(rs.getString(2),FontFactory.getFont(FontFactory.COURIER_BOLD, 5)));
				if(rs.getString(3).length()>=40) {
					cell7 = new PdfPCell(new Paragraph(rs.getString(3).substring(0,40),FontFactory.getFont(FontFactory.COURIER_BOLD, 5)));
				}else {
					cell7 = new PdfPCell(new Paragraph(rs.getString(3),FontFactory.getFont(FontFactory.COURIER_BOLD, 5)));
				}
				cell8 = new PdfPCell(new Paragraph(rs.getString(4),FontFactory.getFont(FontFactory.COURIER_BOLD, 5)));	
					
				
				table.addCell(cell1);
		        table.addCell(cell4);
		        table.addCell(cell7);
		        table.addCell(cell8);
			 }
		} catch (SQLException e) {}
         document.add(table);
        document.close();
        try {
			Desktop.getDesktop().open(new File("relatorios/rel.PDF"));
		} catch (IOException e) {}
        conexao.fechar();
    }

    public void selectExtrato(String de, String ate) throws DocumentException {
        String sql = "SELECT STRFTIME('%d/%m/%Y', DATA) AS [DATAF], VALORL, DESCRILAN, SALDO FROM VW_REL001 WHERE DATA BETWEEN'"+de+"' AND '"+ate+"'";
        conexao.abrir();

        try {
            pst = conexao.getConexao().prepareStatement(sql);
            rs = pst.executeQuery();
        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }

        
    }

    public static void main(String[] args) throws FileNotFoundException, DocumentException {
        RelatorioPdf pdf = new RelatorioPdf();
        pdf.configPDF();
    }

}
