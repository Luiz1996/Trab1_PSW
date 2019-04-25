package br.uem.din.bibliotec.config.model;

import java.util.Objects;

public class M_Usuario {
    //atributos dos usuarios
    private String email = "";
    private String usuario = "";
    private String senha = "";
    private String nome = "";
    private String rg = "";
    private String cpf = "";
    private String endereco = "";
    private String cep = "";
    private String cidade = "";
    private String estado = "";
    private String msg_autenticacao = "";
    private String color_msg = "";
    private int    permissao  = 0;
    private int    ativo = -1;
    private String status = "";
    private String perfil = "";
    private int    codusuario = 0;

    //contrutores e gets/sets
    public M_Usuario(String email, String usuario, String senha, String nome, String rg, String cpf, String endereco, String cep, String cidade, String estado, int permissao, int ativo, String msg_autenticacao, String color_msg) {
        this.email = email;
        this.usuario = usuario;
        this.senha = senha;
        this.nome = nome;
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.permissao = permissao;
        this.ativo = ativo;
        this.msg_autenticacao = msg_autenticacao;
        this.color_msg = color_msg;
    }

    public M_Usuario(String email, String usuario, String senha, String nome, String rg, String cpf, String endereco, String cep, String cidade, String estado, String msg_autenticacao, String color_msg, int permissao, int ativo, String status, String perfil, int codusuario) {
        this.email = email;
        this.usuario = usuario;
        this.senha = senha;
        this.nome = nome;
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.msg_autenticacao = msg_autenticacao;
        this.color_msg = color_msg;
        this.permissao = permissao;
        this.ativo = ativo;
        this.status = status;
        this.perfil = perfil;
        this.codusuario = codusuario;
    }

    public int getCodusuario() { return codusuario; }

    public void setCodusuario(int codusuario) { this.codusuario = codusuario; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getPerfil() { return perfil; }

    public void setPerfil(String perfil) { this.perfil = perfil; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getRg() { return rg; }

    public void setRg(String rg) { this.rg = rg; }

    public String getCpf() { return cpf; }

    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEndereco() { return endereco; }

    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCep() { return cep; }

    public void setCep(String cep) { this.cep = cep; }

    public String getCidade() { return cidade; }

    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    public int getPermissao() { return permissao; }

    public void setPermissao(int permissao) { this.permissao = permissao; }

    public int getAtivo() { return ativo; }

    public void setAtivo(int ativo) { this.ativo = ativo; }

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

    public String getColor_msg() { return color_msg; }

    public void setColor_msg(String color_msg) { this.color_msg = color_msg; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        M_Usuario m_usuario = (M_Usuario) o;
        return permissao == m_usuario.permissao &&
                ativo == m_usuario.ativo &&
                email.equals(m_usuario.email) &&
                usuario.equals(m_usuario.usuario) &&
                senha.equals(m_usuario.senha) &&
                nome.equals(m_usuario.nome) &&
                rg.equals(m_usuario.rg) &&
                cpf.equals(m_usuario.cpf) &&
                endereco.equals(m_usuario.endereco) &&
                cep.equals(m_usuario.cep) &&
                cidade.equals(m_usuario.cidade) &&
                estado.equals(m_usuario.estado) &&
                msg_autenticacao.equals(m_usuario.msg_autenticacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, usuario, senha, nome, rg, cpf, endereco, cep, cidade, estado, permissao, ativo, msg_autenticacao);
    }
}
