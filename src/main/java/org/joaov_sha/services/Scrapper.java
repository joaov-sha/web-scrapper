package org.joaov_sha.services;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public class Scrapper {

    public static void lerPDF() {

        // Código responsável pela obtenção do caminho dos arquivos
        String caminhoPDF = System.getProperty("user.home") + "/Downloads/Anexo_I_Rol_2021RN_465.2021_RN627L.2024.pdf";
        String caminhoCSV = System.getProperty("user.home") + "/Downloads/Arquivo.csv";

        // Código responsável pela leitura do PDF, a partir de determinada página, até determinada página, separando as linhas e colunas para a formação do CSV.
        try (PDDocument document = PDDocument.load(new File(caminhoPDF));
        FileWriter escritorCSV = new FileWriter(caminhoCSV);
        CSVPrinter csvPrinter = new CSVPrinter(escritorCSV, CSVFormat.DEFAULT.withHeader("Coluna1", "Coluna2", "Coluna3"))) {

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(3);
            stripper.setEndPage(181);
            stripper.setSortByPosition(true);

            String texto = stripper.getText(document);
            String[] linhas = texto.split("\n");

            for (String linha : linhas) {
                String[] colunas = linha.split("\\t");
                csvPrinter.printRecord((Object[]) colunas);
            }

            JOptionPane.showMessageDialog(null, "Arquivo CSV criado com sucesso!\nLocal: " + caminhoCSV, "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("CSV gerado com sucesso em: " + caminhoCSV);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao processar o PDF: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
