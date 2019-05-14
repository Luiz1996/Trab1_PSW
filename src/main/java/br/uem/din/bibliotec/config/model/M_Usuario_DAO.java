package br.uem.din.bibliotec.config.model;

import br.uem.din.bibliotec.config.conexao.Conexao;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class M_Usuario_DAO {
    final String SUCESSO = "green";
    final String FALHA = "red";

    //como o dado é informado como DD/MM/AAAA precisamos convertê-la para o formato do banco de dados
    public String formatadorDatasMySQL(String data) {
        String[] dataSeparada = data.split("/");
        LocalDate data_formatada = LocalDate.of(Integer.parseInt(dataSeparada[2]), Integer.parseInt(dataSeparada[1]), Integer.parseInt(dataSeparada[0]));

        return data_formatada.toString().trim();
    }

    //como o dado é informado como AAAA-MM-DD precisamos convertê-la para o formato do brasileiro ao imprimir no front-end ao usuário
    public String formatadorDatasBrasil(String data) {
        if (data == null) {
            return " ";
        } else {
            String[] dataSeparada = data.split("-");
            String dataPadraoBrasil = dataSeparada[2] + "/" + dataSeparada[1] + "/" + dataSeparada[0];
            return dataPadraoBrasil.trim();
        }
    }

    //método que realiza a autenticação do usuário retornando a permissão correta do usuário
    public String buscaPermissao(M_Usuario user, String usuario, String senha) throws SQLException {
        user.setPermissao(0);
        user.setAtivo(0);

        //atribuindo o valor passado no front-end para o atributo e-mail
        user.setEmail(user.getUsuario());
        user.setCpf(user.getUsuario());

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);



            //consultando se usuário está ativo e sua devida permissão
            st.execute("select ativo, permissao from `bibliotec`.`usuarios` where (email = '" + usuario + "' or usuario = '" + usuario + "' or cpf = '" + usuario + "') and senha = '" + senha + "';");
            //user.setUsuario("");

            //obtendo dados
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                user.setAtivo(rs.getInt("ativo"));
                user.setPermissao(rs.getInt("permissao"));
            }

            //limpando reservas antigas em aberto ou geradas incorretamente...
            st.executeUpdate("update `bibliotec`.`livro` l set l.datares = null, l.usuariores = null where (l.datares < current_date());");

            //tratando possíveis falhas de autenticações
            if (user.getAtivo() == 0) {
                user.setMsg_autenticacao("Usuário inválido ou inativo.");
                return "/gestaoBibliotecas?faces-redirect=true";
            }

            if (user.getPermissao() == 0) {
                user.setMsg_autenticacao("Usuário sem permissão.");
                return "/gestaoBibliotecas?faces-redirect=true";
            }

            if (user.getPermissao() != 0) {
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                session.setAttribute("usuario", usuario);

                //casos possíveis de usuários e retorno correspondente dependendo da permissão
                if (user.getPermissao() == 1) {
                    return "/acessoBibliotecario?faces-redirect=true";
                }
                if (user.getPermissao() == 2) {
                    return "/acessoBalconista?faces-redirect=true";
                }
                if (user.getPermissao() == 3) {
                    return "/acessoAluno?faces-redirect=true";
                }
            }

            //se chegar a executação até aqui é porque autenticação falhou
            user.setMsg_autenticacao("Usuário inválido ou inativo.");
            return "/gestaoBibliotecas?faces-redirect=true";
        } catch (SQLException e) {
            //falha na autenticação
            System.out.println("Usuario/Senha inválidos.");
            user.setMsg_autenticacao("Usuário/Senha inválidos.");
            return "/gestaoBibliotecas?faces-redirect=true";
        }
    }

    public String cadastrarUsuario(M_Usuario user) {
        //ao realizar o cadastro, entende-se que o usuário ainda não está efetivamente ativo e com a devida permissão, o balconista que dirá qual a permissão do novo usuário
        user.setAtivo(0);
        user.setPermissao(0);

        //convertendo a data para padrão do banco de dados
        user.setDatanasc(formatadorDatasMySQL(user.getDatanasc()));

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //realizando a inserção do novo cadastro no banco de dados
            st.executeUpdate("INSERT INTO `bibliotec`.`usuarios` (`email`, `usuario`, `senha`, `nome`, `rg`, `cpf`, `endereco`, `cep`, `cidade`, `estado`, `permissao`, `ativo`, `datacad`, `datanasc`) VALUES ('" + user.getEmail() + "', '" + user.getUsuario() + "', '" + user.getSenha() + "', '" + user.getNome() + "', '" + user.getRg() + "', '" + user.getCpf() + "', '" + user.getEndereco() + "', '" + user.getCep() + "', '" + user.getCidade() + "', '" + user.getEstado() + "', '" + user.getPermissao() + "', '" + user.getAtivo() + "', current_date(), '" + user.getDatanasc() + "');");

            //setando mensagem de retorno
            user.setMsg_autenticacao("Cadastrado com sucesso.");
            st.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Falha no cadastramento de usuário");
            user.setMsg_autenticacao("Cadastro falhou.");
        }
        return "/gestaoBibliotecas?faces-redirect=true";
    }

    public String cadastrarUsuarioBalconista(M_Usuario user) {
        //ao realizar o cadastro, entende-se que o usuário ainda não está efetivamente ativo e com a devida permissão, o balconista que dirá qual a permissão do novo usuário
        user.setAtivo(1);

        //convertendo a data para padrão do banco de dados
        user.setDatanasc(formatadorDatasMySQL(user.getDatanasc()));

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //realizando a inserção do novo cadastro no banco de dados
            st.executeUpdate("insert into `bibliotec`.`usuarios` (`email`, `usuario`, `senha`, `nome`, `rg`, `cpf`, `endereco`, `cep`, `cidade`, `estado`, `permissao`, `ativo`, `datacad`, `datanasc`) values ('" + user.getEmail() + "', '" + user.getUsuario() + "', '" + user.getSenha() + "', '" + user.getNome() + "', '" + user.getRg() + "', '" + user.getCpf() + "', '" + user.getEndereco() + "', '" + user.getCep() + "', '" + user.getCidade() + "', '" + user.getEstado() + "', '" + user.getPermissao() + "', '" + user.getAtivo() + "', current_date(), '" + user.getDatanasc() + "');");

            //setando mensagem de retorno
            user.setMsg_autenticacao("Retorno: O usuário '" + user.getUsuario() + "' foi cadastrado com sucesso!");
            user.setColor_msg(SUCESSO);
        } catch (SQLException e) {
            System.out.println("Falha no cadastramento de usuário");
            user.setMsg_autenticacao("Retorno: Não foi possível cadastrar o usuário '" + user.getUsuario() + "', tente novamente mais tarde.");
            user.setColor_msg(FALHA);
        }
        return "/acessoBalconista?faces-redirect=true";
    }

    public List<M_Usuario> consultarUsuarioBalconista(M_Usuario user, int ativo) throws SQLException {
        //declaração do arrayList para auxiliar na impressão da dataTable do consultar usuarios
        List<M_Usuario> usuarios = new ArrayList<>();

        //realiza conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        user.setEmail(user.getNome());
        user.setUsuario(user.getNome());
        user.setCpf(user.getNome());

        //busca todas as informações de acordo com os dados fornecidos
        if(ativo == 0){
            st.execute("SELECT u.email, u.usuario, u.nome, u.rg, u.cpf, u.endereco, u.cep, u.cidade, u.estado, CASE WHEN u.permissao = 1 THEN 'Bibliotecário' WHEN u.permissao = 2 THEN 'Balconista' WHEN u.permissao = 3 THEN 'Aluno' WHEN u.permissao = 0 THEN 'Sem Permissões' END AS perfil, CASE WHEN u.ativo = 1 THEN 'Ativo' ELSE 'Inativo' END AS status, u.codusuario, u.datacad, u.dataalt, u.datanasc FROM `bibliotec`.`usuarios` u WHERE u.nome LIKE '%" + user.getNome() + "%' or u.email LIKE '%" + user.getEmail() + "%' or u.cpf LIKE '" + user.getCpf() + "' or u.usuario LIKE '" + user.getUsuario() + "';");
        }else{
            st.execute("SELECT u.email, u.usuario, u.nome, u.rg, u.cpf, u.endereco, u.cep, u.cidade, u.estado, CASE WHEN u.permissao = 1 THEN 'Bibliotecário' WHEN u.permissao = 2 THEN 'Balconista' WHEN u.permissao = 3 THEN 'Aluno' WHEN u.permissao = 0 THEN 'Sem Permissões' END AS perfil, CASE WHEN u.ativo = 1 THEN 'Ativo' ELSE 'Inativo' END AS status, u.codusuario, u.datacad, u.dataalt, u.datanasc FROM `bibliotec`.`usuarios` u WHERE u.ativo = '1' order by 3;");
        }

        ResultSet rs = st.getResultSet();

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
                    "",
                    "",
                    0,
                    0,
                    rs.getString("status"),
                    rs.getString("perfil"),
                    rs.getInt("codusuario"),
                    formatadorDatasBrasil(rs.getString("datacad")),
                    formatadorDatasBrasil(rs.getString("dataalt")),
                    formatadorDatasBrasil(rs.getString("datanasc"))
            );

            usuarios.add(usuario_temp);
        }

        //fechando as conexões em aberto para evitar locks infinitos no banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return usuarios;
    }

    public String deletarUsuario(M_Usuario user) {
        int codusuario = 0;

        try {
            //abre conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.execute("select nome, codusuario from `bibliotec`.`usuarios` where codusuario = " + user.getCodusuario() + ";");
            ResultSet rs = st.getResultSet();

            //carrega os dados do resultSet dentro das variáveis locais
            while (rs.next()) {
                user.setNome(rs.getString("nome"));
                codusuario = rs.getInt("codusuario");
            }

            if (codusuario == 0) {
                user.setMsg_autenticacao("Retorno: Não existe usuário com Id informado em nosso sistema, deleção falhou.");
                user.setColor_msg(FALHA);
                return "/acessoBalconista?faces-redirect=true";
            }

            //executa a EXCLUSÃO LÓGICA do usuário no banco de dados, ou seja, ativo recebe 0 e permissao recebe 0 (isso impossibilitará o usuário de efetuar login)
            st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET `ativo` = '0', `permissao` = '0', dataalt = current_date() WHERE (`codusuario` =" + user.getCodusuario() + ");");

            user.setMsg_autenticacao("Retorno: O usuário '" + user.getNome() + "' foi deletado com sucesso.");
            user.setColor_msg(SUCESSO);

            //fecha conexões para evitar lock nas tabelas do banco de dados
            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
            user.setMsg_autenticacao("Retorno: A deleção do usuário falhou, contacte o administrador do sistema.");
            user.setColor_msg(FALHA);
        }

        return "/acessoBalconista?faces-redirect=true";
    }

    public String editarUsuario(M_Usuario user) {
        //declaração de varáveis locais que nos ajudará nas tratativas de erros
        String nome_anterior = "";
        Integer codusuario = 0;

        //valida se o código do livro não foi fornecido, caso contrário retorna msg de erro na cor vermelha
        if (user.getCodusuario() == 0) {
            user.setMsg_autenticacao("Retorno: O código Id do usuário é inválido, edição falhou.");
            user.setColor_msg(FALHA);
            return "/acessoBalconista?faces-redirect=true";
        }

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //realiza a consulta que vai nos auxiliar na tratativa de erros em caso de edição de usuário que não existe no banco de dados
            //ou seja, codusuario informado pelo usuário é inválido
            st.execute("SELECT nome, codusuario FROM `bibliotec`.`usuarios` WHERE codusuario = " + user.getCodusuario() + ";");
            ResultSet rs = st.getResultSet();

            //carrega as variáveis locais com os valores do resultSet
            while (rs.next()) {
                nome_anterior = rs.getString("nome");
                codusuario = rs.getInt("codusuario");
            }

            //valida se o código do usuário foi fornecido de forma incorreta, ou seja, usuário inexistente na base de dados
            if (codusuario == 0) {
                user.setMsg_autenticacao("Retorno: O usuário com Id informado não existe, edição falhou.");
                user.setColor_msg(FALHA);
                return "/acessoBalconista?faces-redirect=true";
            }

            //este bloco realiza os updates apenas nos campos que foram preenchidos pelo balconista(campos deixados em branco não serão atualizados)
            st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET dataalt = current_date() WHERE codusuario = " + user.getCodusuario() + ";");

            if (!user.getDatanasc().equals("")) {
                //convertendo a data para padrão do banco de dados
                user.setDatanasc(formatadorDatasMySQL(user.getDatanasc()));

                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET datanasc = '" + user.getDatanasc() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getNome().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET nome = '" + user.getNome() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getUsuario().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET usuario = '" + user.getUsuario() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getEmail().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET email = '" + user.getEmail() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getRg().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET rg = '" + user.getRg() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getCpf().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cpf = '" + user.getCpf() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getEndereco().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET endereco = '" + user.getEndereco() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getCep().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cep = '" + user.getCep() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getCidade().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cidade = '" + user.getCidade() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getEstado().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET estado = '" + user.getEstado() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (user.getPermissao() != 0) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET permissao = '" + user.getPermissao() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (user.getAtivo() == 0) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET ativo = '0', permissao = '0' WHERE codusuario = " + user.getCodusuario() + ";");
            } else {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET ativo = '1' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            //dependendo se o nome informado na tela for vazio/nulo ele imprime na mensagem de retorno com base no nome buscado no banco de dados
            //caso contrário, ele imprimirá o novo nome
            if (user.getNome().equals("")) {
                user.setMsg_autenticacao("Retorno: As informações do usuário '" + nome_anterior + "' foram atualizadas com sucesso.");
            } else {
                user.setMsg_autenticacao("Retorno: As informações do usuário '" + user.getNome() + "' foram atualizadas com sucesso.");
            }
            user.setColor_msg(SUCESSO);

            //fechando as conexões para evitar lock
            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            //caso algum update ou buscas em base de dados falhe, ele retornará mensagem de erro na cor vermelha
            System.out.println("Dados informados são inválidos!");
            user.setMsg_autenticacao("Retorno: A operação de alteração do usuário '" + nome_anterior + "' falhou, contacte o administrador.");
            user.setColor_msg(FALHA);
        }
        return "/acessoBalconista?faces-redirect=true";
    }

    public List<M_Usuario> meusDados() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String) session.getAttribute("usuario");

        List<M_Usuario> dados = new ArrayList<M_Usuario>();

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            //busca todas as informações de acordo com os dados de acesso do usuário
            st.execute("SELECT * FROM `bibliotec`.`usuarios` WHERE usuario = '" + login + "';");
            rs = st.getResultSet();

            while (rs.next()) {
                M_Usuario user = new M_Usuario(
                        rs.getString("email"),
                        rs.getString("usuario"),
                        rs.getString("senha"),
                        rs.getString("nome"),
                        rs.getString("rg"),
                        rs.getString("cpf"),
                        rs.getString("endereco"),
                        rs.getString("cep"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        "",
                        "",
                        0,
                        0,
                        "",
                        "",
                        0,
                        "",
                        "",
                        formatadorDatasBrasil(rs.getString("datanasc")));
                dados.add(user);
            }

            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            rs.close();
            con.conexao.close();
            return dados;
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
        }
        return dados;
    }

    public String minhaHomePage() {
        try {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            String login = (String) session.getAttribute("usuario");

            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //busca todas as informações de acordo com os dados de acesso do usuário
            st.execute("SELECT permissao FROM `bibliotec`.`usuarios` WHERE usuario = '" + login + "';");
            ResultSet rs = st.getResultSet();

            int minhaPermissao = 0;
            while (rs.next()) {
                minhaPermissao = rs.getInt("permissao");
            }

            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            rs.close();
            con.conexao.close();

            //redirecionando para HomePage
            if (minhaPermissao == 1) {
                return "/acessoBibliotecario?faces-redirect=true";
            }

            if (minhaPermissao == 2) {
                return "/acessoBalconista?faces-redirect=true";
            }

            if (minhaPermissao == 3) {
                return "/acessoAluno?faces-redirect=true";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "/gestaoBibliotecas?faces-redirect=true";
        }
        return "/gestaoBibliotecas?faces-redirect=true";
    }

    public String atualizaMeusDados(M_Usuario user){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String) session.getAttribute("usuario");

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            if (!user.getNome().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET nome = '"+user.getNome()+"' WHERE usuario = '" + login + "';");
            }

            if (!user.getSenha().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET senha = '"+user.getSenha()+"' WHERE usuario = '" + login + "';");
            }

            if (!user.getRg().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET rg = '"+user.getRg()+"' WHERE usuario = '" + login + "';");
            }

            if (!user.getCpf().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cpf = '"+user.getCpf()+"' WHERE usuario = '" + login + "';");
            }

            if (!user.getEmail().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET email = '"+user.getEmail()+"' WHERE usuario = '" + login + "';");
            }

            if (!user.getEndereco().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET endereco = '"+user.getEndereco()+"' WHERE usuario = '" + login + "';");
            }

            if (!user.getCep().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cep = '"+user.getCep()+"' WHERE usuario = '" + login + "';");
            }

            if (!user.getCidade().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cidade = '"+user.getCidade()+"' WHERE usuario = '" + login + "';");
            }

            if (!user.getEstado().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET estado = '"+user.getEstado()+"' WHERE usuario = '" + login + "';");
            }

            if (!user.getDatanasc().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET datanasc = '"+formatadorDatasMySQL(user.getDatanasc())+"' WHERE usuario = '" + login + "';");
            }

            st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET dataalt = current_date() WHERE usuario = '" + login + "';");

            if (!user.getUsuario().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET usuario = '"+user.getUsuario()+"' WHERE usuario = '" + login + "';");
            }

            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            con.conexao.close();

            //setando mensagem de retorno
            user.setMsg_autenticacao("Dados atualizados com sucesso!");
            user.setColor_msg(SUCESSO);
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
            user.setMsg_autenticacao("Falha ao atualizar dados.");
            user.setColor_msg(FALHA);
        }
        return minhaHomePage();
    }
}
