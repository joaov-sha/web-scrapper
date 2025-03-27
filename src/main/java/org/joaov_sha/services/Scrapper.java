package org.joaov_sha.services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
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
                
                baixarArquivo(url, arquivoTemporario.toString());

                adicionarArquivosParaZip(arquivoTemporario, nomeDoArquivo, zipar);

                Files.delete(arquivoTemporario);
            }
        }
    }

    public static void baixarArquivo(String urlArquivo, String caminhoPasta) throws IOException{
        URL url = new URL(urlArquivo);
        try(InputStream entrada = url.openStream()){
            Files.copy(entrada, Path.of(caminhoPasta), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static void adicionarArquivosParaZip(Path arquivoTemporario, String nomeDoArquivo, ZipOutputStream zipar) throws IOException{
        try(FileInputStream fis = new FileInputStream(arquivoTemporario.toFile())){
            zipar.putNextEntry(new ZipEntry(nomeDoArquivo));

            byte[] buffer = new byte[1024];
            int length;
            while((length = fis.read(buffer)) >= 0){
                zipar.write(buffer, 0, length);
            }

            zipar.closeEntry();
        }
    }
}
