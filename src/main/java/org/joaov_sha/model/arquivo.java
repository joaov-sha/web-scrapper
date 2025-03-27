package org.joaov_sha.model;

import java.util.Date;

public class arquivo{

    private String nomeArquivo;
    private String urlOriginal;
    private Date dataDeDownload;

    public arquivo(){}

    public arquivo(String nome, String url){
        this.nomeArquivo = nome;
        this.urlOriginal = url;
        this.dataDeDownload = new Date();
    }

    public void setNomeArquivo(String nome){
        this.nomeArquivo = nome;
    }

    public void setUrlOriginal(String url){
        this.urlOriginal = url;
    }

    public void setDataDeDownload(Date date){
        this.dataDeDownload = new Date();
    }

    public String getNomeArquivo(){
        return nomeArquivo;
    }

    public String getUrlOriginal(){
        return urlOriginal;
    }

    public Date getDataDeDownload(){
        return dataDeDownload;
    }
}