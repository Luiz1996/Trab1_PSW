package br.uem.din.bibliotec.config.model;

public class M_Emprestimo {
    private int codemprestimo = 0;
    private int codusuario = 0;
    private int codlivro = 0;
    private String dataemp = "";
    private String datadev = "";
    private String dataalt = "";
    private int ativo = 0;

    //declaração dos contrutores e gets/sets
    public M_Emprestimo(int codemprestimo, int codusuario, int codlivro, String dataemp, String datadev, String dataalt, int ativo) {
        this.codemprestimo = codemprestimo;
        this.codusuario = codusuario;
        this.codlivro = codlivro;
        this.dataemp = dataemp;
        this.datadev = datadev;
        this.dataalt = dataalt;
        this.ativo = ativo;
    }

    public int getCodemprestimo() {
        return codemprestimo;
    }

    public void setCodemprestimo(int codemprestimo) {
        this.codemprestimo = codemprestimo;
    }

    public int getCodusuario() {
        return codusuario;
    }

    public void setCodusuario(int codusuario) {
        this.codusuario = codusuario;
    }

    public int getCodlivro() {
        return codlivro;
    }

    public void setCodlivro(int codlivro) {
        this.codlivro = codlivro;
    }

    public String getDataemp() {
        return dataemp;
    }

    public void setDataemp(String dataemp) {
        this.dataemp = dataemp;
    }

    public String getDatadev() {
        return datadev;
    }

    public void setDatadev(String datadev) {
        this.datadev = datadev;
    }

    public String getDataalt() {
        return dataalt;
    }

    public void setDataalt(String dataalt) {
        this.dataalt = dataalt;
    }
}

