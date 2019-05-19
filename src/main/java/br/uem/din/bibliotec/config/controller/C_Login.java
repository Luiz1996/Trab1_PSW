package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.model.M_Usuario;
import br.uem.din.bibliotec.config.model.M_Usuario_DAO;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;

//declaração do Bean
@Named
@SessionScoped
public class C_Login implements Serializable {
    //declaração de objetos para manipulação dos estados e troca de dados
    private M_Usuario_DAO userDAO = new M_Usuario_DAO();
    private M_Usuario login;
    private String usuario;
    private String senha;

    //contrutores e gets/sets
    public C_Login(){ login = new M_Usuario(); }

    public M_Usuario_DAO getUserDAO() { return userDAO; }

    public void setUserDAO(M_Usuario_DAO userDAO) { this.userDAO = userDAO; }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public M_Usuario getLogin() {
        return login;
    }

    public void setLogin(M_Usuario login) {
        this.login = login;
    }

    //realizando a chamado do método de autenticação na Model M_Usuario_DAO
    public String realizarAcesso() throws SQLException {
        return userDAO.buscaPermissao(login, usuario, senha);
    }

    //encerrando sessão do usuário
    public String logoutSession(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/gestaoBibliotecas?faces-redirect=true";
    }

    //metodo que retorna página de acesso restrito
    public String acessoRestrito(){
        return "/acessoRestrito?faces-redirect=true";
    }
}
