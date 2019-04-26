package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.model.M_Livro;
import br.uem.din.bibliotec.config.model.M_Livro_DAO;

import javax.faces.bean.ManagedBean;
import java.sql.SQLException;
import java.util.List;

//declaração do Bean
@ManagedBean(name = "livroBean", eager = true)
public class C_Livro {
    //Objetos para manipulação dos estados e trocas de dados
    M_Livro livro = new M_Livro(0, "", "", "", "", "", "", "", 0, -1);
    M_Livro_DAO livroDAO = new M_Livro_DAO();

    //contrutores e gets/sets
    public M_Livro getLivro() {
        return livro;
    }

    public void setLivro(M_Livro livro) {
        this.livro = livro;
    }

    public M_Livro_DAO getLivroDAO() {
        return livroDAO;
    }

    public void setLivroDAO(M_Livro_DAO livroDAO) {
        this.livroDAO = livroDAO;
    }

    //método pra cadastramento de livro(s)
    public String realizarCadastroLivro() throws SQLException {
        return livroDAO.cadastrarLivro(livro);
    }

    //método para consultar livro(s)
    public List<M_Livro> realizarConsultaLivro() throws SQLException {
        return livroDAO.consultarLivro(livro);
    }

    public List<M_Livro> realizarConsultaLivroBibliotecario() throws SQLException {
        return livroDAO.consultarLivroBibliotecario(livro);
    }

    //método para exclusão de livro (exclusão lógica, ativo = 0)
    public String realizarDelecaoLivro(){
        return livroDAO.deletarLivro(livro);
    }

    //método para editar as informações de um livro
    public String realizarEdicaoLivro(){
        return livroDAO.editarLivro(livro);
    }
}
