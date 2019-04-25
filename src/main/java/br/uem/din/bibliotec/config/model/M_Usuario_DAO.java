package br.uem.din.bibliotec.config.model;

import br.uem.din.bibliotec.config.conexao.Conexao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class M_Usuario_DAO {
    final String SUCESSO = "green";
    final String FALHA = "red";

    //método que realiza a autenticação do usuário retornando a permissão correta do usuário
    public String buscaPermissao(M_Usuario user) throws SQLException {
        user.setPermissao(0);
        user.setAtivo(0);

        //atribuindo o valor passado no front-end para o atributo e-mail
        user.setEmail(user.getUsuario());

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);


            //consultando se usuário está ativo e sua devida permissão
            st.execute("select ativo, permissao from `bibliotec`.`usuarios` where (email = '"+user.getEmail()+"' or usuario = '"+user.getUsuario()+"') and senha = '"+user.getSenha()+"';");
            user.setUsuario("");

            //obtendo permissão (valor inteiro)
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                user.setAtivo(rs.getInt("ativo"));
                user.setPermissao(rs.getInt("permissao"));
            }

            //tratando possíveis falhas de autenticações
            if (user.getAtivo() == 0) {
                user.setMsg_autenticacao("Usuário inválido ou inativo.");
                return "gestaoBibliotecas";
            }

            if (user.getPermissao() == 0) {
                user.setMsg_autenticacao("Usuário sem permissão.");
                return "gestaoBibliotecas";
            }

            //obtendo permissão (valor inteiro)
            while (rs.next()) {
                user.setPermissao(rs.getInt("permissao"));
            }

            //casos possíveis de usuários e retorno correspondente dependendo da permissão
            if (user.getPermissao() == 1) {
                return "acessoBibliotecario";
            }

            if (user.getPermissao() == 2) {
                return "acessoBalconista";
            }
            if (user.getPermissao() == 3) {
                return "acessoAluno";
            }

            //se chegar a executação até aqui é porque autenticação falhou
            user.setMsg_autenticacao("Usuário inválido ou inativo.");
            return "gestaoBibliotecas";
        } catch (SQLException e) {
            //falha na autenticação
            System.out.println("Usuario/Senha inválidos.");
            user.setMsg_autenticacao("Usuário/Senha inválidos.");
            return "gestaoBibliotecas";
        }
    }

    public String cadastrarUsuario(M_Usuario user){
        //ao realizar o cadastro, entende-se que o usuário ainda não está efetivamente ativo e com a devida permissão, o balconista que dirá qual a permissão do novo usuário
        user.setAtivo(0);
        user.setPermissao(0);

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //realizando a inserção do novo cadastro no banco de dados
            st.executeUpdate("INSERT INTO `bibliotec`.`usuarios` (`email`, `usuario`, `senha`, `nome`, `rg`, `cpf`, `endereco`, `cep`, `cidade`, `estado`, `permissao`, `ativo`) VALUES ('"+user.getEmail()+"', '"+user.getUsuario()+"', '"+user.getSenha()+"', '"+user.getNome()+"', '"+user.getRg()+"', '"+user.getCpf()+"', '"+user.getEndereco()+"', '"+user.getCep()+"', '"+user.getCidade()+"', '"+user.getEstado()+"', '"+user.getPermissao()+"', '"+user.getAtivo()+"');");

            //setando mensagem de retorno
            user.setMsg_autenticacao("Cadastrado com sucesso.");
        } catch (SQLException e) {
            System.out.println("Falha no cadastramento de usuário");
            user.setMsg_autenticacao("Cadastro falhou.");
        }
        return "gestaoBibliotecas";
    }

    public String cadastrarUsuarioBalconista(M_Usuario user){
        //ao realizar o cadastro, entende-se que o usuário ainda não está efetivamente ativo e com a devida permissão, o balconista que dirá qual a permissão do novo usuário
        user.setAtivo(1);

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //realizando a inserção do novo cadastro no banco de dados
            st.executeUpdate("insert into `bibliotec`.`usuarios` (`email`, `usuario`, `senha`, `nome`, `rg`, `cpf`, `endereco`, `cep`, `cidade`, `estado`, `permissao`, `ativo`) values ('"+user.getEmail()+"', '"+user.getUsuario()+"', '"+user.getSenha()+"', '"+user.getNome()+"', '"+user.getRg()+"', '"+user.getCpf()+"', '"+user.getEndereco()+"', '"+user.getCep()+"', '"+user.getCidade()+"', '"+user.getEstado()+"', '"+user.getPermissao()+"', '"+user.getAtivo()+"');");

            //setando mensagem de retorno
            user.setMsg_autenticacao("O usuario '"+user.getUsuario()+"' foi cadastrado com sucesso!");
            user.setColor_msg(SUCESSO);
        } catch (SQLException e) {
            System.out.println("Falha no cadastramento de usuário");
            user.setMsg_autenticacao("Não foi possível cadastrar o usuário '"+user.getUsuario()+"', tente novamente mais tarde.");
            user.setColor_msg(FALHA);
        }
        return "acessoBalconista";
    }

    public List<M_Usuario> consultarUsuarioBalconista(M_Usuario user) throws SQLException {
        //realiza conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        //busca todas as informações de acordo com titulo informado
        st.execute("select * from `bibliotec`.`livro` where titulo like \"%"+ user.getUsuario() +"%\" and ativo = '1' order by 2;");
        ResultSet rs = st.getResultSet();

        //declaração do arrayList para auxiliar na impressão da dataTable do consultar usuarios
        List<M_Usuario> usuarios = new ArrayList<>();

        //obtendo os valores carregados no result set e carregado no arrayList
        while (rs.next()) {
            M_Usuario usuario_temp = new M_Usuario(
                    rs.getString("email"),
                    rs.getString("usuario"),
                    "",
                    rs.getString("nome"),
                    rs.getString("rg"),
                    rs.getString("cpf"),
                    rs.getString("endereco"),
                    rs.getString("cep"),
                    rs.getString("cidade"),
                    rs.getString("estado"),
                    rs.getInt("permissao"),
                    rs.getInt("ativo"),
                    "",
                    "");


            usuarios.add(usuario_temp);
        }

        //fechando as conexões em aberto para evitar locks infinitos no banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return usuarios;
    }
}
