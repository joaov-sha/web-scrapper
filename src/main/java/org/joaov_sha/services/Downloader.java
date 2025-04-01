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

public class Downloader {
    
    public Downloader(){}

    // Este código obtem o caminho da pasta onde o arquivo será salvo, caso este seja um diretório este, adiciona o arquivos em uma pasta criada dentro do diretório e logo após cria o arquivo zip.
    public static void downloadECompactacao(List<String> urlArquivos, String caminhoPasta){
        try{
            Path caminho = Paths.get(caminhoPasta);

            if(Files.isDirectory(caminho)){
                caminhoPasta = caminho.resolve("arquivos_baixados").toString();
            }

            Files.createDirectories(Paths.get(caminhoPasta).getParent());

            baixarEZiparArquivos(urlArquivos, caminhoPasta);

            JOptionPane.showMessageDialog(null, "Todos os arquivos foram baixados e compactados com sucesso!\nOs arquivos estão disponíveis em: " + caminhoPasta, "Sucesso!", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erro durante o processo: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cógio responsável pela obtenção do nome do arquivo a ser salvo como arquivo zip
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

    // Código responsável pelo download dos arquivos
    public static void baixarArquivo(String urlArquivo, String caminhoPasta) throws IOException{
        URL url = new URL(urlArquivo);
        try(InputStream entrada = url.openStream()){
            Files.copy(entrada, Path.of(caminhoPasta), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    // Código responsável pela compactação dos arquivos
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
