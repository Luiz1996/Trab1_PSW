package br.uem.din.bibliotec.config.model;

import java.util.Objects;

public class M_Livro {
    //declaracao dos atributos do livro
    private Integer codlivro = 0;
    private String codcatalogacao = "";
    private String numchamada = "";
    private String titulo = "";
    private String autor = "";
    private String editora = "";
    private String anolancamento = "";
    private String cidade = "";
    private Integer volume = 0;
    private Integer ativo = 0;
    private String status = "";
    private String msg_retorno = "";
    private String color_msg_retorno = "";

    //contrutores e gets/sets
    public M_Livro(Integer codlivro, String codcatalogacao, String numchamada, String titulo, String autor, String editora, String anolancamento, String cidade, Integer volume, Integer ativo) {
        this.codlivro = codlivro;
        this.codcatalogacao = codcatalogacao;
        this.numchamada = numchamada;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.anolancamento = anolancamento;
        this.cidade = cidade;
        this.volume = volume;
        this.ativo = ativo;
        this.color_msg_retorno = color_msg_retorno;
        this.msg_retorno = msg_retorno;
    }

    public M_Livro(Integer codlivro, String codcatalogacao, String numchamada, String titulo, String autor, String editora, String anolancamento, String cidade, Integer volume, Integer ativo, String status) {
        this.codlivro = codlivro;
        this.codcatalogacao = codcatalogacao;
        this.numchamada = numchamada;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.anolancamento = anolancamento;
        this.cidade = cidade;
        this.volume = volume;
        this.ativo = ativo;
        this.status = status;
        this.color_msg_retorno = color_msg_retorno;
        this.msg_retorno = msg_retorno;
    }

    public String getColor_msg_retorno() {
        return color_msg_retorno;
    }

    public void setColor_msg_retorno(String color_msg_retorno) {
        this.color_msg_retorno = color_msg_retorno;
    }

    public String getMsg_retorno() {
        return msg_retorno;
    }

    public void setMsg_retorno(String msg_retorno) { this.msg_retorno = msg_retorno; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Integer getCodlivro() {
        return codlivro;
    }

    public void setCodlivro(Integer codlivro) {
        this.codlivro = codlivro;
    }

    public String getCodcatalogacao() {
        return codcatalogacao;
    }

    public void setCodcatalogacao(String codcatalogacao) {
        this.codcatalogacao = codcatalogacao;
    }

    public String getNumchamada() {
        return numchamada;
    }

    public void setNumchamada(String numchamada) {
        this.numchamada = numchamada;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public String getAnolancamento() {
        return anolancamento;
    }

    public void setAnolancamento(String anolancamento) {
        this.anolancamento = anolancamento;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public Integer getAtivo() { return ativo; }

    public void setAtivo(Integer ativo) { this.ativo = ativo; }

    //declaração da equals e hashcode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        M_Livro m_livro = (M_Livro) o;
        return codlivro.equals(m_livro.codlivro) &&
                codcatalogacao.equals(m_livro.codcatalogacao) &&
                numchamada.equals(m_livro.numchamada) &&
                titulo.equals(m_livro.titulo) &&
                autor.equals(m_livro.autor) &&
                editora.equals(m_livro.editora) &&
                anolancamento.equals(m_livro.anolancamento) &&
                cidade.equals(m_livro.cidade) &&
                volume.equals(m_livro.volume) &&
                ativo.equals(m_livro.ativo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codlivro, codcatalogacao, numchamada, titulo, autor, editora, anolancamento, cidade, volume, ativo);
    }
}
