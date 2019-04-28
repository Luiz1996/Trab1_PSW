package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.model.M_Emprestimo;
import br.uem.din.bibliotec.config.model.M_Emprestimo_DAO;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

@Named
@RequestScoped
public class C_Emprestimo {
    M_Emprestimo emp = new M_Emprestimo(0,0,0,"","","",0);
    M_Emprestimo_DAO empDAO = new M_Emprestimo_DAO();

    //declaracao de gets/sets

    public M_Emprestimo getEmp() { return emp; }

    public void setEmp(M_Emprestimo emp) { this.emp = emp; }

    //metodo que chama cadastrar novo empréstimo no model
    public String realizaCadastroEmprestimo(){
        return empDAO.cadastrarEmprestimo(emp);
    }
}

