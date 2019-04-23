package br.uem.din.bibliotec.config.model;

public class M_Usuario {
    //atributos dos usuarios
    private String usuario = "";
    private String senha = "";
    private String msg_autenticacao = "";

    //contrutores e gets/sets
    public M_Usuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    public M_Usuario(String usuario, String senha, String msg_autenticacao) {
        this.usuario = usuario;
        this.senha = senha;
        this.msg_autenticacao = msg_autenticacao;
    }

    public String getMsg_autenticacao() {
        return msg_autenticacao;
    }

    public void setMsg_autenticacao(String msg_autenticacao) {
        this.msg_autenticacao = msg_autenticacao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
