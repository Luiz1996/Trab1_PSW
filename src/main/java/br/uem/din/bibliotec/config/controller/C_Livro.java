package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.model.M_Livro;
import br.uem.din.bibliotec.config.model.M_Livro_DAO;

import javax.faces.bean.ManagedBean;
import java.awt.*;
import java.sql.SQLException;

@ManagedBean(name = "livroBean", eager = true)
public class C_Livro {
    M_Livro livro = new M_Livro("","","","","","","",0,1);
    M_Livro_DAO livroDAO = new M_Livro_DAO();

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

    //método pra cadastramento de livro
    public String realizarCadastroLivro() throws SQLException {
        return livroDAO.cadastrarLivro(livro);
    }

    public String realizarConsultaLivro(){
        return livroDAO.consultarLivro(livro);
    }


}
