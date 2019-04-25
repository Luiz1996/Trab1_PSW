package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.model.M_Usuario;
import br.uem.din.bibliotec.config.model.M_Usuario_DAO;

import javax.faces.bean.ManagedBean;
import java.awt.*;
import java.sql.SQLException;

//declaração do Bean
@ManagedBean(name = "loginBean", eager = true)
public class C_Login {
    //declaração de objetos para manipulação dos estados e troca de dados
    M_Usuario_DAO userDAO = new M_Usuario_DAO();
    M_Usuario user = new M_Usuario("","","","","","","","","","",0,0,"", "");

    //contrutores e gets/sets
    public M_Usuario_DAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(M_Usuario_DAO userDAO) {
        this.userDAO = userDAO;
    }

    public M_Usuario getUser() {
        return user;
    }

    public void setUser(M_Usuario user) {
        this.user = user;
    }

    //realizando a chamado do método de autenticação na Model M_Usuario_DAO
    public String realizarAcesso() throws SQLException, AWTException {
        return userDAO.buscaPermissao(user);
    }
}
