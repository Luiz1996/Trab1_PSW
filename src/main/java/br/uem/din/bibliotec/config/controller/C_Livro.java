package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.model.M_Livro;
import br.uem.din.bibliotec.config.model.M_Livro_DAO;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

//declaração do Bean
@Named
@SessionScoped
public class C_Livro implements Serializable {
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
        return livroDAO.consultarLivro(livro, 0);
    }

    public List<M_Livro> realizarConsultaLivroEmp() throws SQLException {
        return livroDAO.consultarLivro(livro, 1);
    }

    public List<M_Livro> realizarConsultaLivroBibliotecario() throws SQLException {
        return livroDAO.consultarLivroBibliotecario(livro, 0);
    }

    public List<M_Livro> realizarConsultaLivroBibliotecarioSoAtivos() throws SQLException {
        return livroDAO.consultarLivroBibliotecario(livro, 1);
    }

    //método para exclusão de livro (exclusão lógica, ativo = 0)
    public String realizarDelecaoLivro(){
        return livroDAO.deletarLivro(livro);
    }

    //método para editar as informações de um livro
    public String realizarEdicaoLivro(){
        return livroDAO.editarLivro(livro);
    }

    public String realizaReservaLivro(){
        return livroDAO.cadastrarReserva(livro);
    }

    public List<M_Livro> realizaConsultaReservas() {
        return livroDAO.consultaMinhasReservas(livro);
    }

    public String realizaCancelamentoReserva(){
        return livroDAO.cancelarReserva(livro);
    }
}
