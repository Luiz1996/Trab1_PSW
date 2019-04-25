package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.model.M_Usuario;
import br.uem.din.bibliotec.config.model.M_Usuario_DAO;

import javax.faces.bean.ManagedBean;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

@ManagedBean(name = "userBean", eager = true)
public class C_Usuario {
    //atributos do controller
    M_Usuario user = new M_Usuario("","","","","","","","","","",0,-1,"","");
    M_Usuario_DAO userDAO = new M_Usuario_DAO();

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
        return userDAO.consultarUsuarioBalconista(user);
    }

    //chama método de deleção de usuários no model
    public String realizaDelecaoUsuario() throws SQLException{
        return userDAO.deletarUsuario(user);
    }

    public String realizaEdicaoUsuario() throws SQLException{
        return userDAO.editarUsuario(user);
    }
}
