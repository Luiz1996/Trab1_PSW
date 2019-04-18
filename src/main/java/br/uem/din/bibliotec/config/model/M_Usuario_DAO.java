package br.uem.din.bibliotec.config.model;

import br.uem.din.bibliotec.config.conexao.Conexao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class M_Usuario_DAO {

    public String buscaPermissao(M_Usuario user) throws SQLException {
        String retorno = "index";
        int permissao = 0;

        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.execute("select u.permissao from `bibliotec`.`usuarios` u where u.usuario = '"+ user.getUsuario()+"' and u.senha = '"+ user.getSenha() +"';");

            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                permissao = rs.getInt("permissao");
            }

            //casos possíveis de usuários e retorno correspondente
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
            System.out.println("Usuário ou senha inválidos!");
        }
        return retorno;
    }
}
