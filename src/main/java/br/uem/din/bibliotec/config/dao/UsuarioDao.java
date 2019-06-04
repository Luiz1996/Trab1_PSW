package br.uem.din.bibliotec.config.dao;

import br.uem.din.bibliotec.config.conexao.Conexao;
import br.uem.din.bibliotec.config.model.Usuario;
import br.uem.din.bibliotec.config.services.DataFormat;
import br.uem.din.bibliotec.config.services.Email;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {
    private Email email = new Email();
    private DataFormat dtFormat =  new DataFormat();

    //método que realiza a autenticação do usuário retornando a permissão correta do usuário
    public int buscaPermissao(Usuario user, String usuario, String senha) throws SQLException {
        user.setPermissao(0);
        user.setAtivo(0);
        user.setUsuario("");

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);


            //consultando se usuário está ativo e sua devida permissão
            st.execute( "SELECT \n" +
                            "    ativo, permissao, usuario\n" +
                            "FROM\n" +
                            "    `bibliotec`.`usuarios`\n" +
                            "WHERE\n" +
                            "    (email like '"+usuario.trim()+"'\n" +
                            "\tOR usuario like '"+usuario.trim()+"'\n" +
                            "        OR cpf like '"+usuario.trim()+"')\n" +
                            "        AND senha = '"+senha.trim()+"';");

            //obtendo dados
            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                user.setAtivo(rs.getInt("ativo"));
                user.setPermissao(rs.getInt("permissao"));
                user.setUsuario(rs.getString("usuario").trim());
            }

            //tratando possíveis falhas de autenticações
            if (user.getAtivo() == 0 && !user.getUsuario().equals("".trim())) {
                return -1;
            }

            if (user.getPermissao() == 0 && !user.getUsuario().equals("".trim())) {
                return 0;
            }

            if (user.getPermissao() != 0) {
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                session.setAttribute("usuario", user.getUsuario().trim());
                senha = "";
                usuario = "";
                user.setUsuario("");

                return 1;
            }
        } catch (SQLException e) {
            return -2;
        }
        return -2;
    }

    public int cadastrarUsuario(Usuario user) {
        //ao realizar o cadastro, entende-se que o usuário ainda não está efetivamente ativo e com a devida permissão, o balconista que dirá qual a permissão do novo usuário
        user.setAtivo(0);
        user.setPermissao(0);

        //convertendo a data para padrão do banco de dados
        user.setDatanasc(dtFormat.formatadorDatasMySQL(user.getDatanasc()));

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //setando sigla dos estados com letras maiusculas
            user.setEstado(user.getEstado().toUpperCase());

            //corrigindo CPF e RG
            user.setCpf(user.getCpf().replace(".", ""));
            user.setCpf(user.getCpf().replace("-", ""));
            user.setRg(user.getRg().replace(".", ""));
            user.setRg(user.getRg().replace("-", ""));
            user.setCep(user.getCep().replace("-", ""));

            //realizando a inserção do novo cadastro no banco de dados
            st.executeUpdate("INSERT INTO `bibliotec`.`usuarios` (`email`, `usuario`, `senha`, `nome`, `rg`, `cpf`, `endereco`, `cep`, `cidade`, `estado`, `permissao`, `ativo`, `datacad`, `datanasc`, `jaativado`) VALUES ('" + user.getEmail() + "', '" + user.getUsuario() + "', '" + user.getSenha() + "', '" + user.getNome() + "', '" + user.getRg() + "', '" + user.getCpf() + "', '" + user.getEndereco() + "', '" + user.getCep() + "', '" + user.getCidade() + "', '" + user.getEstado() + "', '" + user.getPermissao() + "', '" + user.getAtivo() + "', current_date(), '" + user.getDatanasc() + "', '0');");

            //enviando e-mail para comunicar que recebemos os dados do usuário e em breve analisaremos suas informações e ativaremos seu cadastro
            email.setAssunto("Recebemos seus Dados - Biblioteca X");
            email.setEmailDestinatario(user.getEmail().trim());
            email.setMsg("Olá " + user.getNome().trim() + ", <br><br>Recebemos seus dados em nosso sistema.<br><br>Eles serão analisados e caso não exista inconsistência(s) nos dados fornecidos seu cadastro será ativado.<br><br>Username: <i>" + user.getUsuario().trim() + "</i>.<br>Senha: <i>" + user.getSenha().trim() + "</i>.");
            email.enviarGmail();

            st.close();
            con.conexao.close();
        } catch (SQLException e) {
            return 0;
        }
        return 1;
    }

    public int cadastrarUsuarioBalconista(Usuario user) {
        //ao realizar o cadastro, entende-se que o usuário ainda não está efetivamente ativo e com a devida permissão, o balconista que dirá qual a permissão do novo usuário
        user.setAtivo(1);

        //convertendo a data para padrão do banco de dados
        user.setDatanasc(dtFormat.formatadorDatasMySQL(user.getDatanasc()));

        try {
            //realizando conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //corrigindo CPF, RG e Cep
            user.setRg(user.getRg().replace(".", ""));
            user.setRg(user.getRg().replace("-", ""));
            user.setCep(user.getCep().replace("-", ""));
            user.setCpf(user.getCpf().replace(".", ""));
            user.setCpf(user.getCpf().replace("-", ""));


            //setando sigla dos estados com letras maiusculas
            user.setEstado(user.getEstado().toUpperCase());

            //realizando a inserção do novo cadastro no banco de dados
            st.executeUpdate("insert into `bibliotec`.`usuarios` (`email`, `usuario`, `senha`, `nome`, `rg`, `cpf`, `endereco`, `cep`, `cidade`, `estado`, `permissao`, `ativo`, `datacad`, `datanasc`, `jaativado`) values ('" + user.getEmail() + "', '" + user.getUsuario() + "', '" + user.getSenha() + "', '" + user.getNome() + "', '" + user.getRg() + "', '" + user.getCpf() + "', '" + user.getEndereco() + "', '" + user.getCep() + "', '" + user.getCidade() + "', '" + user.getEstado().toUpperCase() + "', '" + user.getPermissao() + "', '" + user.getAtivo() + "', current_date(), '" + user.getDatanasc() + "', '1');");

            //enviando e-mail para confirma cadastramento de novo usuário.
            email.setAssunto("Confirmação de Cadastro - Biblioteca X");
            email.setEmailDestinatario(user.getEmail().trim());
            email.setMsg("Olá " + user.getNome().trim() + ", <br><br>Seu cadastro foi realizado com sucesso.<br><br>Username: <i>" + user.getUsuario().trim() + "</i>.<br>Senha: <i>" + user.getSenha().trim() + "</i>.");
            email.enviarGmail();

            st.close();
        } catch (SQLException e) {
            return 0;
        }
        return 1;
    }

    public List<Usuario> consultarUsuarioBalconista(Usuario user, int ativo) throws SQLException {
        //declaração do arrayList para auxiliar na impressão da dataTable do consultar usuarios
        List<Usuario> usuarios = new ArrayList<>();

        //realiza conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        user.setEmail(user.getNome());
        user.setUsuario(user.getNome());
        user.setCpf(user.getNome());

        //busca todas as informações de acordo com os dados fornecidos
        if (ativo == 0) {
            st.execute("SELECT u.email, u.usuario, u.nome, u.rg, u.cpf, u.endereco, u.cep, u.cidade, u.estado, CASE WHEN u.permissao = 1 THEN 'Bibliotecário' WHEN u.permissao = 2 THEN 'Balconista' WHEN u.permissao = 3 THEN 'Aluno' WHEN u.permissao = 0 THEN 'Sem Permissões' END AS perfil, CASE WHEN u.ativo = 1 THEN 'Ativo' ELSE 'Inativo' END AS status, u.codusuario, u.datacad, u.dataalt, u.datanasc FROM `bibliotec`.`usuarios` u WHERE u.nome LIKE '%" + user.getNome() + "%' or u.email LIKE '%" + user.getEmail() + "%' or u.cpf LIKE '" + user.getCpf() + "' or u.usuario LIKE '" + user.getUsuario() + "';");
        } else {
            st.execute("SELECT u.email, u.usuario, u.nome, u.rg, u.cpf, u.endereco, u.cep, u.cidade, u.estado, CASE WHEN u.permissao = 1 THEN 'Bibliotecário' WHEN u.permissao = 2 THEN 'Balconista' WHEN u.permissao = 3 THEN 'Aluno' WHEN u.permissao = 0 THEN 'Sem Permissões' END AS perfil, CASE WHEN u.ativo = 1 THEN 'Ativo' ELSE 'Inativo' END AS status, u.codusuario, u.datacad, u.dataalt, u.datanasc FROM `bibliotec`.`usuarios` u WHERE u.ativo = '1' order by 3;");
        }

        ResultSet rs = st.getResultSet();

        //obtendo os valores carregados no result set e carregado no arrayList
        while (rs.next()) {
            Usuario usuario_temp = new Usuario(
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
                    dtFormat.formatadorDatasBrasil(rs.getString("datacad")),
                    dtFormat.formatadorDatasBrasil(rs.getString("dataalt")),
                    dtFormat.formatadorDatasBrasil(rs.getString("datanasc"))
            );

            usuarios.add(usuario_temp);
        }

        //fechando as conexões em aberto para evitar locks infinitos no banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return usuarios;
    }

    public int deletarUsuario(Usuario user) {
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
                return -1;
            }

            //executa a EXCLUSÃO LÓGICA do usuário no banco de dados, ou seja, ativo recebe 0 e permissao recebe 0 (isso impossibilitará o usuário de efetuar login)
            st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET `ativo` = '0', `permissao` = '0', dataalt = current_date() WHERE (`codusuario` =" + user.getCodusuario() + ");");

            //fecha conexões para evitar lock nas tabelas do banco de dados
            st.close();
            rs.close();
            con.conexao.close();

            return 1;
        } catch (SQLException e) {
            return 0;
        }
    }

    public int editarUsuario(Usuario user) {
        //declaração de varáveis locais que nos ajudará nas tratativas de erros
        String nome_anterior = "";
        Integer codusuario = 0;

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
                return -1;
            }

            //este bloco realiza os updates apenas nos campos que foram preenchidos pelo balconista(campos deixados em branco não serão atualizados)
            st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET dataalt = current_date() WHERE codusuario = " + user.getCodusuario() + ";");

            if (!user.getDatanasc().equals("")) {
                //convertendo a data para padrão do banco de dados
                user.setDatanasc(dtFormat.formatadorDatasMySQL(user.getDatanasc()));

                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET datanasc = '" + user.getDatanasc() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getNome().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET nome = '" + user.getNome() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getUsuario().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET usuario = '" + user.getUsuario() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getSenha().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET senha = '" + user.getSenha() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getEmail().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET email = '" + user.getEmail() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getRg().equals("")) {
                //corrigindo RG
                user.setRg(user.getRg().replace(".", ""));
                user.setRg(user.getRg().replace("-", ""));

                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET rg = '" + user.getRg() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getCpf().equals("")) {
                //corrigindo CPF
                user.setCpf(user.getCpf().replace(".", ""));
                user.setCpf(user.getCpf().replace("-", ""));

                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cpf = '" + user.getCpf() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getEndereco().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET endereco = '" + user.getEndereco() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getCep().equals("")) {
                //corrigindo Cep
                user.setCep(user.getCep().replace("-", ""));

                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cep = '" + user.getCep() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getCidade().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cidade = '" + user.getCidade() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (!user.getEstado().equals("")) {
                //setando sigla dos estados com letras maiusculas
                user.setEstado(user.getEstado().toUpperCase());

                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET estado = '" + user.getEstado() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (user.getPermissao() != 0) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET permissao = '" + user.getPermissao() + "' WHERE codusuario = " + user.getCodusuario() + ";");
            }

            if (user.getAtivo() == 0) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET ativo = '0', permissao = '0' WHERE codusuario = " + user.getCodusuario() + ";");
            } else {
                if (user.getAtivo() == 1 && user.getPermissao() != 0) {
                    st.execute("select u.jaativado, u.email, u.nome, u.usuario, u.senha from `bibliotec`.`usuarios` u where u.codusuario = '" + user.getCodusuario() + "';");
                    rs = st.getResultSet();

                    int jaativado = 0;
                    String usuario = "", senha = "";
                    while (rs.next()) {
                        jaativado = rs.getInt("jaativado");
                        user.setEmail(rs.getString("email"));
                        user.setNome((rs.getString("nome")));
                        usuario = rs.getString("usuario");
                        senha = rs.getString("senha");
                    }

                    //se jaativado == 0, então é porque o usuário está sendo ativado pela primeira vez(se usuário for inativado e depois reativado a intenção é que não mande e-mail de novo avisando que foi ativado novamente seu cadastro)
                    if (jaativado == 0) {
                        //enviando e-mail comunicando ativação do cadastro do usuário(este e-mail só será enviado no máxima uma vez)
                        email.setAssunto("Confirmação de Cadastro - Biblioteca X");
                        email.setEmailDestinatario(user.getEmail().trim());
                        email.setMsg("Olá " + user.getNome().trim() + ", <br><br>Seu cadastro foi ativado com sucesso.<br>Agora você tem acesso ao nosso acervo de livros e demais funcionalidades, aproveite!");
                        email.enviarGmail();
                    }
                    st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET ativo = '1', jaativado = '1' WHERE codusuario = " + user.getCodusuario() + ";");

                    if((!user.getUsuario().equals("") || !user.getSenha().equals(""))){
                        //enviando e-mail com novos dados de acesso
                        email.setAssunto("Alteração de Acessos - Biblioteca X");
                        email.setEmailDestinatario(user.getEmail().trim());
                        email.setMsg("Olá " + user.getNome().trim() + ", <br><br>Seus dados de acesso foram alterados com sucesso.<br><br>Username: <i>" + usuario.trim() + "</i>.<br>Senha: <i>" + senha.trim() + "</i>.");
                        email.enviarGmail();
                    }
                }
            }

            //fechando as conexões para evitar lock
            st.close();
            rs.close();
            con.conexao.close();

            return 1;
        } catch (SQLException e) {
            return 0;
        }
    }

    public List<Usuario> meusDados() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String) session.getAttribute("usuario");

        List<Usuario> dados = new ArrayList<Usuario>();

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
                Usuario user = new Usuario(
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
                        dtFormat.formatadorDatasBrasil(rs.getString("datanasc")));
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

    public String homePage() {
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

            //redirecionando para HomePage correta
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

    public int atualizaMeusDados(Usuario user) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String) session.getAttribute("usuario");

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            if (!user.getNome().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET nome = '" + user.getNome() + "' WHERE usuario = '" + login + "';");
            }

            if (!user.getSenha().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET senha = '" + user.getSenha() + "' WHERE usuario = '" + login + "';");
            }

            if (!user.getRg().equals("")) {
                //corrigindo RG
                user.setRg(user.getRg().replace(".", ""));
                user.setRg(user.getRg().replace("-", ""));

                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET rg = '" + user.getRg() + "' WHERE usuario = '" + login + "';");
            }

            if (!user.getCpf().equals("")) {
                //corrigindo CPF
                user.setCpf(user.getCpf().replace(".", ""));
                user.setCpf(user.getCpf().replace("-", ""));

                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cpf = '" + user.getCpf() + "' WHERE usuario = '" + login + "';");
            }

            if (!user.getEmail().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET email = '" + user.getEmail() + "' WHERE usuario = '" + login + "';");
            }

            if (!user.getEndereco().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET endereco = '" + user.getEndereco() + "' WHERE usuario = '" + login + "';");
            }

            if (!user.getCep().equals("")) {
                //corrigindo Cep
                user.setCep(user.getCep().replace("-", ""));

                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cep = '" + user.getCep() + "' WHERE usuario = '" + login + "';");
            }

            if (!user.getCidade().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET cidade = '" + user.getCidade() + "' WHERE usuario = '" + login + "';");
            }

            if (!user.getEstado().equals("")) {
                //setando sigla dos estados com letras maiusculas
                user.setEstado(user.getEstado().toUpperCase());

                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET estado = '" + user.getEstado() + "' WHERE usuario = '" + login + "';");
            }

            if (!user.getDatanasc().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET datanasc = '" + dtFormat.formatadorDatasMySQL(user.getDatanasc()) + "' WHERE usuario = '" + login + "';");
            }

            //se houver ao menos 1 alteração atualiza dataalt e manda e-mail
            if (!user.getNome().equals("") || !user.getUsuario().equals("") || !user.getSenha().equals("") || !user.getRg().equals("") || !user.getCpf().equals("") || !user.getEmail().equals("") || !user.getEndereco().equals("") || !user.getCep().equals("") || !user.getCidade().equals("") || !user.getEstado().equals("") || !user.getDatanasc().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET dataalt = current_date() WHERE usuario = '" + login + "';");

                //Enviando e-mail de alteração cadastral
                st.execute("SELECT nome, email, usuario, senha FROM `bibliotec`.`usuarios` WHERE usuario = '" + login + "';");
                ResultSet rs = st.getResultSet();

                String usuario = "";
                while (rs.next()) {
                    user.setNome(rs.getString("nome").trim());
                    user.setEmail(rs.getString("email").trim());
                    usuario = rs.getString("usuario").trim();
                    user.setSenha(rs.getString("senha").trim());
                }

                //dependendo se foi alterado o username, é enviado um email diferente antes que a sessão do sistema seja invalidada
                if(!user.getUsuario().equals("")){
                    //Enviando e-mail de confirmação de atualização cadastral
                    email.setAssunto("Atualização Cadastral - Biblioteca X");
                    email.setEmailDestinatario(user.getEmail().trim());
                    email.setMsg("Olá " + user.getNome() + ", <br><br>Seus dados foram atualizados com sucesso!<br><br>Username: <i>" + user.getUsuario().trim() + "</i>.<br>Senha: <i>" + user.getSenha().trim() + "</i>.");
                    email.enviarGmail();
                }else{
                    //Enviando e-mail de confirmação de atualização cadastral
                    email.setAssunto("Atualização Cadastral - Biblioteca X");
                    email.setEmailDestinatario(user.getEmail().trim());
                    email.setMsg("Olá " + user.getNome() + ", <br><br>Seus dados foram atualizados com sucesso!<br><br>Username: <i>" + usuario.trim() + "</i>.<br>Senha: <i>" + user.getSenha().trim() + "</i>.");
                    email.enviarGmail();
                }
            } else {
                return 0;
            }

            //Se alterar o usuário logo a sessão será quebrada e será necessário refazer o login
            if (!user.getUsuario().equals("")) {
                st.executeUpdate("UPDATE `bibliotec`.`usuarios` SET usuario = '" + user.getUsuario() + "' WHERE usuario = '" + login + "';");
            }

            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            con.conexao.close();
        } catch (SQLException e) {
            return -1;
        }
        return 1;
    }
}
