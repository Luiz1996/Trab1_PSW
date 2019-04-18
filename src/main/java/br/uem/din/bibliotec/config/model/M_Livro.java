package br.uem.din.bibliotec.config.model;

public class M_Livro {
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


    public M_Livro(String codcatalogacao, String numchamada, String titulo, String autor, String editora, String anolancamento, String cidade, Integer volume, Integer ativo) {
        this.codcatalogacao = codcatalogacao;
        this.numchamada = numchamada;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.anolancamento = anolancamento;
        this.cidade = cidade;
        this.volume = volume;
        this.ativo = ativo;
    }

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
}
