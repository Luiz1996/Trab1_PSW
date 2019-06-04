package br.uem.din.bibliotec.config.model;

public class Emprestimo {
    private int codemprestimo = 0;
    private int codusuario = 0;
    private int codlivro = 0;
    private String dataemp = "";
    private String datadev = "";
    private String dataalt = "";
    private int ativo = 0;
    private String msg_retorno = "";
    private String color_msg_retorno = "";
    private String nome_user = "";
    private String email_user = "";
    private String titulo_book = "";
    private String autor_book = "";
    private String editora_book = "";
    private String anolancamento_book = "";
    private String status_emp = "";
    private String rg_user = "";
    private String cpf_user = "";
    private int atrasado = 0;

    //declaração dos contrutores e gets/sets
    public Emprestimo(int codemprestimo, int codusuario, int codlivro, String dataemp, String datadev, String dataalt, int ativo, String msg_retorno, String color_msg_retorno, String nome_user, String email_user, String titulo_book, String autor_book, String editora_book, String anolancamento_book, String status_emp) {
        this.codemprestimo = codemprestimo;
        this.codusuario = codusuario;
        this.codlivro = codlivro;
        this.dataemp = dataemp;
        this.datadev = datadev;
        this.dataalt = dataalt;
        this.ativo = ativo;
        this.msg_retorno = msg_retorno;
        this.color_msg_retorno = color_msg_retorno;
        this.nome_user = nome_user;
        this.email_user = email_user;
        this.titulo_book = titulo_book;
        this.autor_book = autor_book;
        this.editora_book = editora_book;
        this.anolancamento_book = anolancamento_book;
        this.status_emp = status_emp;
        this.rg_user = rg_user;
        this.cpf_user = cpf_user;
    }

    public Emprestimo(String titulo_book, String autor_book, String editora_book, String dataemp, String datadev, int atrasado) {
        this.titulo_book = titulo_book;
        this.autor_book = autor_book;
        this.editora_book = editora_book;
        this.dataemp = dataemp;
        this.datadev = datadev;
        this.atrasado = atrasado;
    }

    public Emprestimo(int codemprestimo, int codusuario, int codlivro, String dataemp, String datadev, String dataalt, int ativo, String msg_retorno, String color_msg_retorno, String nome_user, String email_user, String titulo_book, String autor_book, String editora_book, String anolancamento_book, String status_emp, String rg_user, String cpf_user) {
        this.codemprestimo = codemprestimo;
        this.codusuario = codusuario;
        this.codlivro = codlivro;
        this.dataemp = dataemp;
        this.datadev = datadev;
        this.dataalt = dataalt;
        this.ativo = ativo;
        this.msg_retorno = msg_retorno;
        this.color_msg_retorno = color_msg_retorno;
        this.nome_user = nome_user;
        this.email_user = email_user;
        this.titulo_book = titulo_book;
        this.autor_book = autor_book;
        this.editora_book = editora_book;
        this.anolancamento_book = anolancamento_book;
        this.status_emp = status_emp;
        this.rg_user = rg_user;
        this.cpf_user = cpf_user;
    }

    public int getAtrasado() { return atrasado; }

    public void setAtrasado(int atrasado) { this.atrasado = atrasado; }

    public String getRg_user() { return rg_user; }

    public void setRg_user(String rg_user) { this.rg_user = rg_user; }

    public String getCpf_user() { return cpf_user; }

    public void setCpf_user(String cpf_user) { this.cpf_user = cpf_user; }

    public String getNome_user() { return nome_user; }

    public void setNome_user(String nome_user) { this.nome_user = nome_user; }

    public String getEmail_user() { return email_user; }

    public void setEmail_user(String email_user) { this.email_user = email_user; }

    public String getTitulo_book() { return titulo_book; }

    public void setTitulo_book(String titulo_book) { this.titulo_book = titulo_book; }

    public String getAutor_book() { return autor_book; }

    public void setAutor_book(String autor_book) { this.autor_book = autor_book; }

    public String getEditora_book() { return editora_book; }

    public void setEditora_book(String editora_book) { this.editora_book = editora_book; }

    public String getAnolancamento_book() { return anolancamento_book; }

    public void setAnolancamento_book(String anolancamento_book) { this.anolancamento_book = anolancamento_book; }

    public String getStatus_emp() { return status_emp; }

    public void setStatus_emp(String status_emp) { this.status_emp = status_emp; }

    public int getAtivo() { return ativo; }

    public void setAtivo(int ativo) { this.ativo = ativo; }

    public String getMsg_retorno() { return msg_retorno; }

    public void setMsg_retorno(String msg_retorno) { this.msg_retorno = msg_retorno; }

    public String getColor_msg_retorno() { return color_msg_retorno; }

    public void setColor_msg_retorno(String color_msg_retorno) { this.color_msg_retorno = color_msg_retorno; }

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

