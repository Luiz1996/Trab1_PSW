package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.model.M_Usuario;
import br.uem.din.bibliotec.config.model.M_Usuario_DAO;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.awt.*;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

//declaração do Bean
@Named
@SessionScoped
public class C_Usuario implements Serializable {
    //atributos do controller
    M_Usuario user = new M_Usuario("","","","","","","","","","",0,-1,"","");
    M_Usuario_DAO userDAO = new M_Usuario_DAO();

    public C_Usuario() {}

    public C_Usuario(String login){
        login = new String();
    }

    //gets e sets
    public M_Usuario getUser() {
        return user;
    }

    public void setUser(M_Usuario user) {
        this.user = user;
    }

    public M_Usuario_DAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(M_Usuario_DAO userDAO) {
        this.userDAO = userDAO;
    }

    //chama método de cadastramento de usuários no model
    public String realizarCadastroUsuario() throws SQLException, AWTException {
        return userDAO.cadastrarUsuario(user);
    }

    public String realizarCadastroUsuarioBalconista() throws SQLException, AWTException {
        return userDAO.cadastrarUsuarioBalconista(user);
    }

    //chama método de consulta de usuários no model
    public List<M_Usuario> realizaConsultaUsuario() throws SQLException {
        return userDAO.consultarUsuarioBalconista(user, 0);
    }

    public List<M_Usuario> realizaConsultaUsuariosAtivos () throws SQLException {
        return userDAO.consultarUsuarioBalconista(user, 1);
    }

    //chama método de deleção de usuários no model
    public String realizaDelecaoUsuario() throws SQLException{
        return userDAO.deletarUsuario(user);
    }

    public String realizaEdicaoUsuario() throws SQLException{
        return userDAO.editarUsuario(user);
    }

    public String chamaMenuInicial(){
        return userDAO.minhaHomePage();
    }

    public String realizaAtualizacaoMeusDados(){
        return userDAO.atualizaMeusDados(user);
    }
}
