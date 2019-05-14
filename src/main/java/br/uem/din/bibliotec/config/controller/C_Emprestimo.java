package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.model.M_Emprestimo;
import br.uem.din.bibliotec.config.model.M_Emprestimo_DAO;
import br.uem.din.bibliotec.config.model.M_Livro;
import br.uem.din.bibliotec.config.model.M_Usuario;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@Named
@SessionScoped
public class C_Emprestimo implements Serializable {
    M_Emprestimo emp = new M_Emprestimo(0,0,0,"","","",0, "", "","","","","","","","");
    M_Emprestimo_DAO empDAO = new M_Emprestimo_DAO();
    String login;

    //declaracao de gets/sets
    public C_Emprestimo(){

    }

    public C_Emprestimo(String login){
        login = new String();
    }

    public M_Emprestimo getEmp() { return emp; }

    public void setEmp(M_Emprestimo emp) { this.emp = emp; }

    //métodos de consultas
    public List<M_Emprestimo> consultarMeusEmprestimos() throws SQLException { return empDAO.meusEmprestimos();}

    public List<M_Usuario> consultaUsuariosEmprestimo() throws SQLException { return empDAO.consultaUsuariosEmp(); }

    public List<M_Livro> consultaLivrosEmprestimo() throws SQLException { return empDAO.consultaLivrosEmp(); }

    public List<M_Emprestimo> consultaEmprestimos(){
        return empDAO.consultarEmprestimos(emp);
    }

    public List<M_Emprestimo> consultaEmprestimosEmVigor(){
        return empDAO.consultarEmprestimosEmVigor(emp);
    }

    //metodo que chama cadastrar novo empréstimo no model
    public String realizaCadastroEmprestimo() throws SQLException { return empDAO.cadastrarEmprestimo(emp); }

    //metodo para finalizar um determinado emprestimo
    public String realizaFinalizarEmprestimo(){
        return empDAO.finalizarEmprestimo(emp);
    }

    //metodo para realizar edição em um determinado emprestimo
    public String realizaEdicaoEmprestimo(){
        return empDAO.editarEmprestimo(emp);
    }
}