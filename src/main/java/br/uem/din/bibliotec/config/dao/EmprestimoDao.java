package br.uem.din.bibliotec.config.dao;

import br.uem.din.bibliotec.config.conexao.Conexao;
import br.uem.din.bibliotec.config.model.Emprestimo;
import br.uem.din.bibliotec.config.model.Livro;
import br.uem.din.bibliotec.config.model.Usuario;
import br.uem.din.bibliotec.config.services.DataFormat;
import br.uem.din.bibliotec.config.services.Email;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmprestimoDao {
    private Email email = new Email();
    private DataFormat dtFormat = new DataFormat();

    public List<Usuario> consultaUsuariosEmp() throws SQLException {
        List<Usuario> usuarios_emp = new ArrayList<Usuario>();

        //realiza conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        //busca todas as informações de acordo com os dados fornecidos
        st.execute("SELECT * FROM `bibliotec`.`usuarios` u WHERE u.ativo = '1';");
        ResultSet rs = st.getResultSet();

        //obtendo os valores carregados no result set e carregado no arrayList
        while (rs.next()) {
            Usuario usuario_temp = new Usuario(
                    rs.getString("nome"),
                    rs.getInt("codusuario"),
                    rs.getString("cpf"),
                    rs.getString("email"),
                    rs.getString("rg"),
                    dtFormat.formatadorDatasBrasil(rs.getString("datanasc"))
            );
            usuarios_emp.add(usuario_temp);
        }
        //fechando as conexões em aberto para evitar locks infinitos no banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return usuarios_emp;
    }

    public List<Livro> consultaLivrosEmp() throws SQLException {
        List<Livro> livros_emp = new ArrayList<Livro>();

        //realiza conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        //busca todas as informações de acordo com os dados fornecidos
        st.execute("SELECT * FROM `bibliotec`.`livro` l WHERE l.ativo = '1' and l.disponibilidade = '1';");
        ResultSet rs = st.getResultSet();

        //obtendo os valores carregados no result set e carregado no arrayList
        while (rs.next()) {
            Livro livros_temp = new Livro(
                    rs.getInt("codlivro"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("editora"),
                    rs.getString("anolancamento"),
                    rs.getString("codcatalogacao")
            );
            livros_emp.add(livros_temp);
        }
        //fechando as conexões em aberto para evitar locks infinitos no banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return livros_emp;
    }

    public int cadastrarEmprestimo(Emprestimo emp) throws SQLException {
        String datadev = "", dataemp = "", email_user_emp = "", titulo_book_emp = "", nome_user_emp = "";

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            int atrasado = consultarEmpAtrasados(emp.getCodusuario());
            if (atrasado != 0) {
                return -2;
            }

            //obtendo dados para imprimir na mensagem de retorno
            st.execute("SELECT u.nome, u.email FROM `bibliotec`.`usuarios` u WHERE u.codusuario = '"+emp.getCodusuario()+"'");
            rs = st.getResultSet();
            while (rs.next()){
                nome_user_emp = rs.getString("nome").trim();
                email_user_emp = rs.getString("email").trim();
            }

            st.execute("SELECT l.titulo FROM `bibliotec`.`livro` l WHERE l.codlivro = '"+emp.getCodlivro()+"'");
            rs = st.getResultSet();
            while (rs.next()){
                titulo_book_emp = rs.getString("titulo");
            }

            //obter usuário da reserva
            st.execute("select coalesce(l.usuariores,0) as usuariores from `bibliotec`.`livro` l where l.codlivro = '"+emp.getCodlivro()+"';");
            int usuariores = 0;
            rs = st.getResultSet();
            while(rs.next()){
                usuariores = rs.getInt("usuariores");
            }

            // se não tiver reserva ou se usuario_reserva == usuario_emprestimo
            if(usuariores == 0 || usuariores == emp.getCodusuario()) {
                //limpando data reserva do novo livro do emprestimo
                if (usuariores == emp.getCodusuario()) {
                    st.executeUpdate("UPDATE `bibliotec`.`livro` l set l.datares = null, l.usuariores = null where l.codlivro = '" + emp.getCodlivro() + "';");
                }

                //Inserindo um novo emprestimo e auterando a disponibilidade do livro
                st.executeUpdate("INSERT INTO `bibliotec`.`emprestimo` (`codusuario`, `codlivro`, `dataemp`, `datadev`, `ativo`) VALUES ('"+emp.getCodusuario()+"', '"+emp.getCodlivro()+"', current_date(), DATE_ADD(current_date(), INTERVAL 7 DAY) , '1');");
                st.executeUpdate("UPDATE `bibliotec`.`livro` l set l.disponibilidade = '0' where l.codlivro = '"+emp.getCodlivro()+"';");

                //Envio de e-mail ao cadastrar empréstimo
                st.execute("SELECT max(datadev) as datadev, current_date() as dataemp from `bibliotec`.`emprestimo` e where e.codusuario = '"+emp.getCodusuario()+"';");
                rs = st.getResultSet();
                while(rs.next()){
                    datadev = dtFormat.formatadorDatasBrasil(rs.getString("datadev"));
                    dataemp = dtFormat.formatadorDatasBrasil(rs.getString("dataemp"));
                }

                //Enviando e-mail ao finalizar o cadastro de emprestimo
                email.setAssunto("Empréstimo de Livro - Biblioteca X");
                email.setEmailDestinatario(email_user_emp);
                email.setMsg("Olá "+nome_user_emp+", <br><br>O empréstimo do livro <b>'"+titulo_book_emp+"'</b> foi realizado com sucesso! <br><br> Data do Empréstimo: <b>"+dataemp+"</b>. <br> Data da Devolução: <b>"+datadev+"</b>. <br><br>Fique atento à data de devolução.");
                email.enviarGmail();
            }else{
                return 0;
            }

            //fechando as conexões
            st.close();
            con.conexao.close();

            return 1;
        } catch (SQLException e) {
            return -1;
        }
    }

    public List<Emprestimo> meusEmprestimos() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String)session.getAttribute("usuario");

        List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            //busca todas as informações de acordo com os dados de acesso do usuário
            st.execute("SELECT \n" +
                    "    l.titulo, \n" +
                    "    l.autor, \n" +
                    "    l.editora, \n" +
                    "    e.dataemp, \n" +
                    "    e.datadev,\n" +
                    "    CASE\n" +
                    "\t\tWHEN e.datadev < current_date() THEN '1'\n" +
                    "        ELSE '0'\n" +
                    "    END AS atrasado\n" +
                    "FROM\n" +
                    "    emprestimo e\n" +
                    "        LEFT JOIN\n" +
                    "    livro l ON l.codlivro = e.codlivro\n" +
                    "        LEFT JOIN\n" +
                    "    usuarios u ON u.codusuario = e.codusuario\n" +
                    "WHERE\n" +
                    "    u.usuario LIKE '" + login + "' AND e.ativo = '1';");

            rs = st.getResultSet();

            while(rs.next()){
                Emprestimo emp_temp = new Emprestimo(
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editora"),
                        dtFormat.formatadorDatasBrasil(rs.getString("dataemp")),
                        dtFormat.formatadorDatasBrasil(rs.getString("datadev")),
                        rs.getInt("atrasado")

                );
                emprestimos.add(emp_temp);
            }

            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
        }
        return emprestimos;
    }

    public List<Emprestimo> consultarEmprestimos(Emprestimo emp) {
        List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            emp.setTitulo_book(emp.getNome_user());

            //busca todas as informações de acordo com os dados fornecidos
            st.execute("select\n" +
                    "\te.codemprestimo,\n" +
                    "    u.codusuario,\n" +
                    "    u.nome,\n" +
                    "    u.email,\n" +
                    "    l.codlivro,\n" +
                    "    l.titulo,\n" +
                    "    l.autor,\n" +
                    "    l.editora,\n" +
                    "    l.anolancamento,\n" +
                    "    e.dataemp,\n" +
                    "    e.datadev,\n" +
                    "    e.dataalt,\n" +
                    "  case\n" +
                    "\t\twhen e.ativo = 1 then 'Em vigor'\n" +
                    "    when e.ativo = 0 then 'Finalizado'\n" +
                    "\tend as status\n" +
                    "from\n" +
                    "\temprestimo e\n" +
                    "left join\n" +
                    "\tlivro l on l.codlivro = e.codlivro\n" +
                    "left join\n" +
                    "\tusuarios u on u.codusuario = e.codusuario\n" +
                    "where\n" +
                    "\tu.nome like '%"+emp.getNome_user()+"%' or\n" +
                    "\tl.titulo like '%"+emp.getTitulo_book()+"%' order by 1;");

            rs = st.getResultSet();
            while(rs.next()){
                Emprestimo emp_temp = new Emprestimo(
                        rs.getInt("codemprestimo"),
                        rs.getInt("codusuario"),
                        rs.getInt("codlivro"),
                        dtFormat.formatadorDatasBrasil(rs.getString("dataemp")),
                        dtFormat.formatadorDatasBrasil(rs.getString("datadev")),
                        dtFormat.formatadorDatasBrasil(rs.getString("dataalt")),
                        0,
                        "",
                        "",
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editora"),
                        rs.getString("anolancamento"),
                        rs.getString("status")
                );
                emprestimos.add(emp_temp);
            }

            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
        }

        return emprestimos;
    }

    public List<Emprestimo> consultarEmprestimosEmVigor(Emprestimo emp) {
        List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();
        try {
            //realiza conexão com banco de dados bibliotec
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            emp.setTitulo_book(emp.getNome_user());

            //busca todas as informações de acordo com os dados fornecidos
            st.execute("SELECT \n" +
                    "    e.codemprestimo,\n" +
                    "    u.codusuario,\n" +
                    "    u.nome,\n" +
                    "    u.email,\n" +
                    "    u.cpf,\n" +
                    "    u.rg,\n" +
                    "    l.codlivro,\n" +
                    "    l.titulo,\n" +
                    "    l.autor,\n" +
                    "    l.editora,\n" +
                    "    l.anolancamento,\n" +
                    "    e.dataemp,\n" +
                    "    e.datadev,\n" +
                    "    e.dataalt,\n" +
                    "    CASE\n" +
                    "        WHEN e.ativo = 1 THEN 'Em vigor'\n" +
                    "        WHEN e.ativo = 0 THEN 'Finalizado'\n" +
                    "    END AS status\n" +
                    "FROM\n" +
                    "    emprestimo e\n" +
                    "        LEFT JOIN\n" +
                    "    livro l ON l.codlivro = e.codlivro\n" +
                    "        LEFT JOIN\n" +
                    "    usuarios u ON u.codusuario = e.codusuario\n" +
                    "WHERE\n" +
                    "    (u.nome LIKE '%"+emp.getNome_user()+"%'\n" +
                    "\tOR l.titulo LIKE '%"+emp.getTitulo_book()+"%')\n" +
                    "\tAND e.ativo = '1'\n" +
                    "ORDER BY 1;");

            ResultSet rs = st.getResultSet();

            while(rs.next()){
                Emprestimo empr = new Emprestimo(
                        rs.getInt("codemprestimo"),
                        rs.getInt("codusuario"),
                        rs.getInt("codlivro"),
                        dtFormat.formatadorDatasBrasil(rs.getString("dataemp")),
                        dtFormat.formatadorDatasBrasil(rs.getString("datadev")),
                        dtFormat.formatadorDatasBrasil(rs.getString("dataalt")),
                        1,
                        "",
                        "",
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editora"),
                        rs.getString("anolancamento"),
                        rs.getString("status"),
                        rs.getString("rg"),
                        rs.getString("cpf")
                );
                emprestimos.add(empr);
            }

            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
        }

        return emprestimos;
    }

    public int finalizarEmprestimo(Emprestimo emp) {
        String data_res = "";
        try{
            //realiza conexão com banco de dados bibliotec
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //obtendo codlivro para torná-lo disponível de novo
            st.execute( "select\n" +
                            "\te.codlivro, \n" +
                            "    u.email,\n" +
                            "    u.nome,\n" +
                            "    l.titulo,\n" +
                            "    coalesce(l.datares,'') as datares\n" +
                            "from\n" +
                            "\temprestimo e\n" +
                            "left join\n" +
                            "\tusuarios   u on u.codusuario = e.codusuario\n" +
                            "left join\n" +
                            "\tlivro      l on l.codlivro = e.codlivro\t\n" +
                            "where\n" +
                            "\te.codemprestimo\t= '" +
                            ""+emp.getCodemprestimo()+"';");

            ResultSet rs = st.getResultSet();

            while(rs.next()){
                emp.setCodlivro(rs.getInt("codlivro"));
                emp.setEmail_user(rs.getString("email").trim());
                emp.setNome_user(rs.getString("nome").trim());
                emp.setTitulo_book(rs.getString("titulo").trim());
                data_res = rs.getString("datares").trim();
            }

            //realizando updates de modo a deixar livro disponível para emprestimo e marcar o emprestimo em questão como finalizado
            st.executeUpdate("UPDATE `bibliotec`.`livro` l SET l.disponibilidade = '1' WHERE l.codlivro = '"+emp.getCodlivro()+"';");
            st.executeUpdate("UPDATE `bibliotec`.`emprestimo` e SET e.ativo = '0', e.dataalt = current_date() WHERE e.codemprestimo = '"+emp.getCodemprestimo()+"';");

            if(!data_res.trim().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` l SET l.datares = DATE_ADD(current_date(), INTERVAL 1 DAY) WHERE l.codlivro = '"+emp.getCodlivro()+"';");
                String nome_res = "", email_res = "", datares = "";

                st.execute( "SELECT \n" +
                                "    u.nome as nome, " +
                                "    u.email as email, " +
                                "    l.datares as datares\n" +
                                "FROM\n" +
                                "    livro l\n" +
                                "        LEFT JOIN\n" +
                                "    usuarios u ON u.codusuario = l.usuariores\n" +
                                "WHERE\n" +
                                "    l.codlivro = '"+emp.getCodlivro()+"';");

                rs = st.getResultSet();

                while(rs.next()){
                    nome_res = rs.getString("nome");
                    email_res = rs.getString("email");
                    datares = dtFormat.formatadorDatasBrasil(rs.getString("datares"));
                }

                //Enviando e-mail de confirmação de alteração na data de reserva criada por outro usuário
                //Se o livro for devolvido antes da data de devolução, enntão a reserva do próximo usuário é adiantada também e é disparado e-mail
                email.setAssunto("Adiantamento de Reserva - Biblioteca X");
                email.setEmailDestinatario(email_res);
                email.setMsg("Olá "+nome_res+", <br><br>A sua reserva para o livro <b>'"+emp.getTitulo_book()+"'</b> foi adiantada para <b>"+datares+"</b>.");
                email.enviarGmail();
            }

            //Enviando e-mail de confirmação ao devolver livro à biblioteca
            email.setAssunto("Devolução de Livro - Biblioteca X");
            email.setEmailDestinatario(emp.getEmail_user());
            email.setMsg("Olá "+emp.getNome_user()+", <br><br>A devolução do livro <b>'"+emp.getTitulo_book()+"'</b> foi realizado com sucesso.");
            email.enviarGmail();

            //fechando conexões
            st.close();
            rs.close();
        }catch (SQLException e){
            return 0;
        }
        return 1;
    }

    public int editarEmprestimo(Emprestimo emp) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String)session.getAttribute("usuario");

        try{
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            //TRATANDO A ALTERAÇÃO DO LIVRO
            if(emp.getCodlivro()!=0){
                //verificando se o livro está reservado para outra pessoa
                st.execute("select coalesce(l.usuariores,0) as usuariores from `bibliotec`.`livro` l where l.codlivro = '"+emp.getCodlivro()+"';");
                int usuariores = 0;
                rs = st.getResultSet();
                while(rs.next()){
                    usuariores = rs.getInt("usuariores");
                }

                st.execute("select coalesce(u.codusuario,0) as codusuario from `bibliotec`.`emprestimo` u where u.codemprestimo = '"+emp.getCodemprestimo()+"';");
                int cod_usuario = 0;
                rs = st.getResultSet();
                while(rs.next()){
                    cod_usuario = rs.getInt("codusuario");
                }

                // sem reserva     || usuario_reserva == novo_usuario   || usuario_reserva == usuario_emprestimo_atual/anterior
                if(usuariores == 0 || usuariores == emp.getCodusuario() || usuariores == cod_usuario){

                    //limpando data reserva do novo livro do emprestimo
                    //usuario_reserva == novo_usuario   || usuario_reserva == usuario_emprestimo_atual/anterior
                    if(usuariores == emp.getCodusuario() || usuariores == cod_usuario){
                        st.executeUpdate("UPDATE `bibliotec`.`livro` l set l.datares = null, l.usuariores = null where l.codlivro = '"+emp.getCodlivro()+"';") ;
                    }

                    //obtendo o codlivro anterior e a data reserva se tiver
                    st.execute("SELECT \n" +
                            "    e.codlivro, coalesce(l.datares,'') as datares\n" +
                            "FROM\n" +
                            "    emprestimo e\n" +
                            "        LEFT JOIN\n" +
                            "    livro l ON l.codlivro = e.codlivro\n" +
                            "WHERE\n" +
                            "    e.codemprestimo = '"+emp.getCodemprestimo()+"';");

                    int livro_anterior = 0;
                    String datares_anterior = "";
                    rs = st.getResultSet();
                    while(rs.next()){
                        livro_anterior = rs.getInt("codlivro");
                        datares_anterior = rs.getString("datares").trim();
                    }

                    //se data reserva diferente de vazio, então deverá ser atualizado...
                    if(!datares_anterior.equals("")){
                        st.executeUpdate("UPDATE `bibliotec`.`livro` l set l.disponibilidade = '1', l.datares = DATE_ADD(current_date(), INTERVAL 1 DAY) where l.codlivro = '"+livro_anterior+"';");
                    }else{
                        st.executeUpdate("UPDATE `bibliotec`.`livro` l set l.disponibilidade = '1' where l.codlivro = '"+livro_anterior+"';");
                    }

                    //atualizando disponibilidade do novo livro e as informações do emprestimo
                    st.executeUpdate("update `bibliotec`.`livro` l set l.disponibilidade = '0' where l.codlivro = '"+emp.getCodlivro()+"';");
                    st.executeUpdate("UPDATE `bibliotec`.`emprestimo` e SET e.codlivro = '"+emp.getCodlivro()+"', e.dataalt = current_date() WHERE e.codemprestimo = '"+emp.getCodemprestimo()+"';");
                }else{
                    return -3;
                }
            }

            //TRATANDO A ALTERAÇÃO DO CODUSUARIO
            if(emp.getCodusuario()!=0){
                st.executeUpdate("UPDATE `bibliotec`.`emprestimo` e SET e.codusuario = '"+emp.getCodusuario()+"', e.dataalt = current_date() WHERE e.codemprestimo = '"+emp.getCodemprestimo()+"';");
            }

            //TRATANDO A ALTERAÇÃO DA DATA DE DEVOLUÇÃO
            if (!emp.getDatadev().equals("".trim())) {
                //convertendo nova data de devolução

                emp.setDatadev(dtFormat.formatadorDatasMySQL(emp.getDatadev()));

                String datares = "";
                if(emp.getCodlivro() == 0){
                    st.execute("SELECT \n" +
                            "    CASE\n" +
                            "        WHEN l.datares <= '"+emp.getDatadev()+"'\n" +
                            "\t\t\tTHEN 'Maior'\n" +
                            "\t\t\tELSE 'Menor'\n" +
                            "    END AS datares\n" +
                            "FROM\n" +
                            "    emprestimo e\n" +
                            "        LEFT JOIN\n" +
                            "    livro l ON l.codlivro = e.codlivro\n" +
                            "WHERE\n" +
                            "    e.codemprestimo = '"+emp.getCodemprestimo()+"';");
                    rs = st.getResultSet();
                    while(rs.next()){
                        datares = rs.getString("datares");
                    }

                    if(datares.equals("Maior")){
                        return -2;
                    }
                }else{
                    st.execute("SELECT \n" +
                            "    CASE\n" +
                            "        WHEN l.datares <= '"+emp.getDatadev()+"'\n" +
                            "\t\t\tTHEN 'Maior'\n" +
                            "\t\t\tELSE 'Menor'\n" +
                            "    END AS datares\n" +
                            "FROM\n" +
                            "    emprestimo e\n" +
                            "        LEFT JOIN\n" +
                            "    livro l ON l.codlivro = e.codlivro\n" +
                            "WHERE\n" +
                            "    e.codlivro ='"+emp.getCodlivro()+"' and e.ativo = '1';");
                    rs = st.getResultSet();
                    while(rs.next()){
                        datares = rs.getString("datares");
                    }

                    if(datares.equals("Maior")){
                        return 0;
                    }
                }
                st.executeUpdate("UPDATE `bibliotec`.`emprestimo` e SET e.datadev = '"+emp.getDatadev()+"', e.dataalt = current_date() WHERE e.codemprestimo = '"+emp.getCodemprestimo()+"';");
            }
        } catch (SQLException e) {
            return -1;
        }
        return 1;
    }

    public int consultarMeusEmpAtrasados() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String) session.getAttribute("usuario");
        int qtde_atrasado = 0;

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            st.execute("select\n" +
                    "\tcount(coalesce(e.codemprestimo,0)) as qtde\n" +
                    "from\n" +
                    "\temprestimo e\n" +
                    "inner join\n" +
                    "\tusuarios   u on u.codusuario = e.codusuario\t\n" +
                    "where\n" +
                    "\te.ativo = '1' and\n" +
                    "    e.datadev < current_date() and\n" +
                    "    u.usuario = '" + login.trim() + "';");

            rs = st.getResultSet();
            while (rs.next()) {
                qtde_atrasado = rs.getInt("qtde");
            }

            st.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println("Erro ao consultar empréstimos atrasados!");
        }
        return qtde_atrasado;
    }

    public int consultarEmpAtrasados(int codusuario) {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String) session.getAttribute("usuario");
        int qtde_atrasado = 0;

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            st.execute("select\n" +
                    "\tcount(coalesce(e.codemprestimo,0)) as qtde\n" +
                    "from\n" +
                    "\temprestimo e\n" +
                    "inner join\n" +
                    "\tusuarios   u on u.codusuario = e.codusuario\t\n" +
                    "where\n" +
                    "\te.ativo = '1' and\n" +
                    "    e.datadev < current_date() and\n" +
                    "    u.codusuario = '" + codusuario + "';");

            rs = st.getResultSet();
            while (rs.next()) {
                qtde_atrasado = rs.getInt("qtde");
            }

            st.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println("Erro ao consultar empréstimos atrasados!");
        }
        return qtde_atrasado;
    }
}