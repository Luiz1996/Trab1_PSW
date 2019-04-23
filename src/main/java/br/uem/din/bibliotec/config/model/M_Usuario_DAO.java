package br.uem.din.bibliotec.config.model;

import br.uem.din.bibliotec.config.conexao.Conexao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class M_Usuario_DAO {
    //método que realiza a autenticação do usuário retornando a permissão correta do usuário
    public String buscaPermissao(M_Usuario user) throws SQLException {
        String retorno = "gestaoBibliotecas";
        int permissao = 0;

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.execute("select u.permissao from `bibliotec`.`usuarios` u where u.usuario = '"+ user.getUsuario()+"' and u.senha = '"+ user.getSenha() +"';");

            //obtendo permissão (valor inteiro)
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                permissao = rs.getInt("permissao");
            }

            //casos possíveis de usuários e retorno correspondente dependendo da permissão
            if (permissao == 1) {
                retorno = "acessoBibliotecario";
            }

            if (permissao == 2) {
                retorno = "acessoBalconista";
            }
            if (permissao == 3) {
                retorno = "acessoAluno";
            }
        } catch (SQLException e) {
            System.out.println("Usuario/Senha inválidos.");
        }

        //caso o usuario/senha informados forem inválidos retornará mensagem de retorno do erro
        if(permissao != 1 && permissao != 2 && permissao != 3){
            user.setMsg_autenticacao("Usuario/Senha inválidos.");
            user.setUsuario("");
            user.setSenha("");
            return retorno;
        }

        //se o login for efetuado com sucesso o as variáveis sao reinicializadas com valor vazio
        user.setUsuario("");
        user.setSenha("");
        user.setMsg_autenticacao("");

        return retorno;
    }
}
