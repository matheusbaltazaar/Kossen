package com.baltazarstudio.kossen.model;

public class Perfil {

    private String nome;
    private String arquivoFotoBase64;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getArquivoFotoBase64() {
        return arquivoFotoBase64;
    }

    public void setArquivoFotoBase64(String arquivoFotoBase64) {
        this.arquivoFotoBase64 = arquivoFotoBase64;
    }
}
