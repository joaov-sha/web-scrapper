package org.joaov_sha.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.swing.JOptionPane;

public class Scrapper {
    
    public Scrapper(){}

    public static void scrap(List<String> urlArquivos, String caminhoPasta, String caminhoZip){
        
        try {
            Files.createDirectories(Paths.get(caminhoPasta));
            baixarEZiparArquivos(urlArquivos, caminhoZip);
            JOptionPane.showConfirmDialog(null, "Todos os arquivos foram baixados e compactados com sucesso!\n Este estão disponíveis em: " + caminhoPasta, "Sucesso!", 0);
        } catch (IOException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    public static void baixarEZiparArquivos(List<String> urlArquivos, String caminhoZip) throws IOException{
        try(ZipOutputStream zipar = new ZipOutputStream(new FileOutputStream(caminhoZip))){
            for (String url : urlArquivos) {
                String nomeDoArquivo = url.substring(url.lastIndexOf("/") + 1);
                Path arquivoTemporario = Files.createTempFile("download_", "_" + nomeDoArquivo);
            }
        }
    }
}
