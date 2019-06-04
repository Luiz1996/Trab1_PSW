package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.dao.UsuarioDao;
import br.uem.din.bibliotec.config.model.Emprestimo;
import br.uem.din.bibliotec.config.dao.EmprestimoDao;
import br.uem.din.bibliotec.config.model.Livro;
import br.uem.din.bibliotec.config.model.Usuario;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@Named
@SessionScoped
public class EmprestimoController implements Serializable {
    private Emprestimo emp = new Emprestimo(0,0,0,"","","",0, "", "","","","","","","","");
    private EmprestimoDao empDao = new EmprestimoDao();
    private UsuarioDao userDao = new UsuarioDao();
    private int retorno = 0;
    private final String FALHA = "red";
    private final String SUCESSO = "green";
    private String login;

    //contrutores e gets/sets
    public EmprestimoController(){}

    public EmprestimoController(String login){
        login = new String();
    }

    public Emprestimo getEmp() { return emp; }

    public void setEmp(Emprestimo emp) { this.emp = emp; }

    public EmprestimoDao getEmpDAO() { return empDao; }

    public void setEmpDAO(EmprestimoDao empDAO) { this.empDao = empDAO; }

    public String getLogin() { return login; }

    public void setLogin(String login) { this.login = login; }

    public String getFALHA() { return FALHA; }

    public String getSUCESSO() { return SUCESSO; }

    public EmprestimoDao getEmpDao() { return empDao; }

    public void setEmpDao(EmprestimoDao empDao) { this.empDao = empDao; }

    public UsuarioDao getUserDao() { return userDao; }

    public void setUserDao(UsuarioDao userDao) { this.userDao = userDao; }

    public int getRetorno() { return retorno; }

    public void setRetorno(int retorno) { this.retorno = retorno; }

    //chama métodos de consultas referente aos empréstimos
    public List<Emprestimo> consultarMeusEmprestimos() throws SQLException {
        return empDao.meusEmprestimos();
    }

    public List<Usuario> consultaUsuariosEmprestimo() throws SQLException {
        return empDao.consultaUsuariosEmp();
    }

    public List<Livro> consultaLivrosEmprestimo() throws SQLException {
        return empDao.consultaLivrosEmp();
    }

    public List<Emprestimo> consultaEmprestimos(){
        return empDao.consultarEmprestimos(emp);
    }

    public List<Emprestimo> consultaEmprestimosEmVigor(){
        return empDao.consultarEmprestimosEmVigor(emp);
    }

    //metodo que chama cadastrar novo empréstimo
    public String realizaCadastroEmprestimo() throws SQLException {
        retorno = empDao.cadastrarEmprestimo(emp);

        if(retorno == 0){
            emp.setColor_msg_retorno(FALHA);
            emp.setMsg_retorno("Retorno: O livro encontra-se reservado para outra pessoa.");
        }else{
            if(retorno == 1){
                emp.setColor_msg_retorno(SUCESSO);
                emp.setMsg_retorno("Retorno: O empréstimo do livro foi realizado com sucesso.");
            }else{
                if(retorno == -1){
                    emp.setColor_msg_retorno(FALHA);
                    emp.setMsg_retorno("Retorno: O empréstimo para falhou, contacte o administrador.");
                }else{
                    emp.setColor_msg_retorno(FALHA);
                    emp.setMsg_retorno("Retorno: O usuário possui empréstimos em atraso. Favor, REGULARIZAR!");
                }
            }
        }
        return userDao.homePage();
    }

    //metodo para finalizar um determinado emprestimo
    public String realizaFinalizarEmprestimo(){
        retorno =  empDao.finalizarEmprestimo(emp);

        if(retorno == 0){
            emp.setColor_msg_retorno(FALHA);
            emp.setMsg_retorno("Retorno: Não foi possível finalizar o empréstimo, contacte o administrador.");
        }else{
            //atualizando mensageria de retorno
            emp.setColor_msg_retorno(SUCESSO);
            emp.setMsg_retorno("Retorno: O empréstimo foi finalizado com sucesso. O livro encontra-se disponível para outros empréstimos.");
        }
        return userDao.homePage();
    }

    //metodo para realizar edição em um determinado emprestimo
    public String realizaEdicaoEmprestimo(){
        retorno =  empDao.editarEmprestimo(emp);

        if(retorno == 1){
            emp.setColor_msg_retorno(SUCESSO);
            emp.setMsg_retorno("Retorno: O empréstimo foi alterado com sucesso.");
        }else{
            if(retorno == 0){
                emp.setColor_msg_retorno(FALHA);
                emp.setMsg_retorno("Retorno: O livro já possui reserva, não foi possível alterar data de devolução.");
            }else{
                if(retorno == -1){
                    emp.setColor_msg_retorno(FALHA);
                    emp.setMsg_retorno("Retorno: As alterações falharam, contacte o administrador.");
                }else{
                    if(retorno == -2){
                        emp.setColor_msg_retorno(FALHA);
                        emp.setMsg_retorno("Retorno: O livro já possui reserva, não foi possível alterar data de devolução.");
                    }else{
                        //atualizando mensageria de retorno
                        emp.setColor_msg_retorno(FALHA);
                        emp.setMsg_retorno("Retorno: O livro já está reservado em nome de outra pessoa.");
                    }
                }
            }
        }
        return userDao.homePage();
    }

    public int consultaQtdeEmpAtrasados(){
        return empDao.consultarMeusEmpAtrasados();
    }
}