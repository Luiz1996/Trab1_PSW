package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.model.Usuario;
import br.uem.din.bibliotec.config.dao.UsuarioDao;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;

@Named
@SessionScoped
public class LoginController implements Serializable {
    private UsuarioDao userDao = new UsuarioDao();
    private Usuario login =  new Usuario();
    private int retorno = 0;
    private String usuario;
    private String senha;

    //contrutores e gets/sets
    public LoginController(){ login = new Usuario(); }

    public UsuarioDao getUserDAO() { return userDao; }

    public void setUserDAO(UsuarioDao userDAO) { this.userDao = userDAO; }

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

    public Usuario getLogin() {
        return login;
    }

    public void setLogin(Usuario login) {
        this.login = login;
    }

    public UsuarioDao getUserDao() { return userDao; }

    public void setUserDao(UsuarioDao userDao) { this.userDao = userDao; }

    public int getRetorno() { return retorno; }

    public void setRetorno(int retorno) { this.retorno = retorno; }

    //realizando a chamado do método de autenticação na Model UsuarioDao
    public String realizarAcesso() throws SQLException {
        retorno =  userDao.buscaPermissao(login, usuario, senha);

        if(retorno == -1){
            login.setMsg_autenticacao("Usuário inativo.");
        }else{
            if(retorno == 0){
                login.setMsg_autenticacao("Usuário sem permissão.");
            }else{
                login.setMsg_autenticacao("Usuário/Senha inválido(s).");
            }
        }
        return userDao.homePage();
    }

    //encerrando sessão do usuário
    public String logoutSession(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/gestaoBibliotecas?faces-redirect=true";
    }
}
