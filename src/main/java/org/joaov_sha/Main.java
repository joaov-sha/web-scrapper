package org.joaov_sha;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.joaov_sha.services.Downloader;

public class Main {
    public static void main(String[] args) {
        int quantidadeDeArquivos = 0;
        List<String> urlsDeArquivos = new ArrayList<>();

        String caminhoPastaDownloads = System.getProperty("user.home") + "/Downloads/";

        try {
            quantidadeDeArquivos = Integer.parseInt(JOptionPane.showInputDialog("Quantos arquivos você deseja baixar?", "1"));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Quantidade de arquivos informada inválida, favor informe um número entre 1 e 10.");
            quantidadeDeArquivos = Integer.parseInt(JOptionPane.showInputDialog("Quantos arquivos você deseja baixar?", "1"));
        }

        for (int i = 0; i < quantidadeDeArquivos; i++) {
            String urlArquivo;
            do {
                urlArquivo = JOptionPane.showInputDialog("Informe a URL do arquivo a ser baixado:", "");
                if (!urlArquivo.startsWith("http://") && !urlArquivo.startsWith("https://")) {
                    JOptionPane.showMessageDialog(null, "URL inválida! Tente novamente.");
                }
            } while (!urlArquivo.startsWith("http://") && !urlArquivo.startsWith("https://"));

            urlsDeArquivos.add(urlArquivo);
        }

        Downloader.downloadECompactacao(urlsDeArquivos, caminhoPastaDownloads + "arquivos_compactados.zip");
    }
}