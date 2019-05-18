package br.uem.din.bibliotec.config.model;

import br.uem.din.bibliotec.config.conexao.Conexao;
import br.uem.din.bibliotec.config.services.SendEmail;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class M_Emprestimo_DAO {
    private final String FALHA = "red";
    private final String SUCESSO = "green";

    //como o dado é informado como AAAA-MM-DD precisamos convertê-la para o formato do brasileiro ao imprimir no front-end ao usuário
    public String formatadorDatasBrasil(String data){
        if(data == null){
            return "";
        }else{
            String[] dataSeparada = data.split("-");
            String dataPadraoBrasil = dataSeparada[2]+"/"+dataSeparada[1]+"/"+dataSeparada[0];
            return dataPadraoBrasil.trim();
        }
    }

    //como o dado é informado como DD/MM/AAAA precisamos convertê-la para o formato do banco de dados
    public String formatadorDatasMySQL(String data){
        String[] dataSeparada = data.split("/");
        LocalDate data_formatada = LocalDate.of(Integer.parseInt(dataSeparada[2]), Integer.parseInt(dataSeparada[1]), Integer.parseInt(dataSeparada[0]));

        return data_formatada.toString().trim();
    }

    public List<M_Usuario> consultaUsuariosEmp() throws SQLException {
        List<M_Usuario> usuarios_emp = new ArrayList<M_Usuario>();

        //realiza conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        //busca todas as informações de acordo com os dados fornecidos
        st.execute("SELECT * FROM `bibliotec`.`usuarios` u WHERE u.ativo = '1';");
        ResultSet rs = st.getResultSet();

        //obtendo os valores carregados no result set e carregado no arrayList
        while (rs.next()) {
            M_Usuario usuario_temp = new M_Usuario(
                    rs.getString("nome"),
                    rs.getInt("codusuario"),
                    rs.getString("cpf"),
                    rs.getString("email"),
                    rs.getString("rg"),
                    formatadorDatasBrasil(rs.getString("datanasc"))
            );
            usuarios_emp.add(usuario_temp);
        }
        //fechando as conexões em aberto para evitar locks infinitos no banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return usuarios_emp;
    }

    public List<M_Livro> consultaLivrosEmp() throws SQLException {
        List<M_Livro> livros_emp = new ArrayList<M_Livro>();

        //realiza conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        //busca todas as informações de acordo com os dados fornecidos
        st.execute("SELECT * FROM `bibliotec`.`livro` l WHERE l.ativo = '1' and l.disponibilidade = '1';");
        ResultSet rs = st.getResultSet();

        //obtendo os valores carregados no result set e carregado no arrayList
        while (rs.next()) {
            M_Livro livros_temp = new M_Livro(
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

    public String cadastrarEmprestimo(M_Emprestimo emp) throws SQLException {
        String datadev = "", dataemp = "", email_user_emp = "", titulo_book_emp = "", nome_user_emp = "";

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

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
                    datadev = formatadorDatasBrasil(rs.getString("datadev"));
                    dataemp = formatadorDatasBrasil(rs.getString("dataemp"));
                }

                SendEmail email = new SendEmail();
                email.setAssunto("Empréstimo de Livro - Biblioteca X");
                email.setEmailDestinatario(email_user_emp);
                email.setMsg("Olá "+nome_user_emp+", <br><br>O empréstimo do livro <b>'"+titulo_book_emp+"'</b> foi realizado com sucesso! <br><br> Data do Empréstimo: <b>"+dataemp+"</b>. <br> Data da Devolução: <b>"+datadev+"</b>. <br><br>Fique atento à data de devolução.");
                email.enviarGmail();
            }else{
                //atualizando mensageria de retorno
                emp.setColor_msg_retorno(FALHA);
                emp.setMsg_retorno("Retorno: O livro encontra-se reservado para outra pessoa. Falhar ao criar novo empréstimo!");
                return "/acessoBalconista?faces-redirect=true";
            }

            //atualizando mensageria de retorno
            emp.setColor_msg_retorno(SUCESSO);
            emp.setMsg_retorno("Retorno: O empréstimo do livro '"+titulo_book_emp+"' para '"+nome_user_emp+"' foi realizado com sucesso.");

            //fechando as conexões em aberto para evitar locks infinitos no banco de dados
            st.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
            emp.setColor_msg_retorno(FALHA);
            emp.setMsg_retorno("Retorno: O empréstimo para "+nome_user_emp+" falhou, contacte o administrador.");
        }
        return "/acessoBalconista?faces-redirect=true";
    }

    public List<M_Emprestimo> meusEmprestimos() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String)session.getAttribute("usuario");

        List<M_Emprestimo> emprestimos = new ArrayList<M_Emprestimo>();

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            //busca todas as informações de acordo com os dados de acesso do usuário
            st.execute( "select\n" +
                            "\tl.titulo,\n" +
                            "    l.autor,\n" +
                            "    l.editora,\n" +
                            "    e.dataemp,\n" +
                            "    e.datadev\n" +
                            "from\n" +
                            "    emprestimo e\n" +
                            "left join\n" +
                            "    livro l on l.codlivro = e.codlivro\n" +
                            "left join\n" +
                            "    usuarios u on u.codusuario = e.codusuario\n" +
                            "where\n" +
                            "    u.usuario like '%" + login + "%' and\n" +
                            "    e.ativo = '1';");

            rs = st.getResultSet();

            while(rs.next()){
                M_Emprestimo emp_temp = new M_Emprestimo(
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editora"),
                        formatadorDatasBrasil(rs.getString("dataemp")),
                        formatadorDatasBrasil(rs.getString("datadev"))
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

    public List<M_Emprestimo> consultarEmprestimos(M_Emprestimo emp){
        List<M_Emprestimo> emprestimos = new ArrayList<M_Emprestimo>();

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
                M_Emprestimo emp_temp = new M_Emprestimo(
                        rs.getInt("codemprestimo"),
                        rs.getInt("codusuario"),
                        rs.getInt("codlivro"),
                        formatadorDatasBrasil(rs.getString("dataemp")),
                        formatadorDatasBrasil(rs.getString("datadev")),
                        formatadorDatasBrasil(rs.getString("dataalt")),
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

    public List<M_Emprestimo> consultarEmprestimosEmVigor(M_Emprestimo emp){
        List<M_Emprestimo> emprestimos = new ArrayList<M_Emprestimo>();
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
                M_Emprestimo empr = new M_Emprestimo(
                        rs.getInt("codemprestimo"),
                        rs.getInt("codusuario"),
                        rs.getInt("codlivro"),
                        formatadorDatasBrasil(rs.getString("dataemp")),
                        formatadorDatasBrasil(rs.getString("datadev")),
                        formatadorDatasBrasil(rs.getString("dataalt")),
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

    public String finalizarEmprestimo(M_Emprestimo emp){
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
                    datares = formatadorDatasBrasil(rs.getString("datares"));
                }

                //Enviando e-mail de confirmação de alteração na data de reserva criada por outro usuário
                //Se o livro for devolvido antes da data de devolução, enntão a reserva do próximo usuário é adiantada também e é disparado e-mail
                SendEmail email = new SendEmail();
                email.setAssunto("Adiantamento de Reserva - Biblioteca X");
                email.setEmailDestinatario(email_res);
                email.setMsg("Olá "+nome_res+", <br><br>A sua reserva para o livro <b>'"+emp.getTitulo_book()+"'</b> foi adiantada para <b>"+datares+"</b>.");
                email.enviarGmail();
            }

            //Enviando e-mail de confirmação ao devolver livro à biblioteca
            SendEmail email = new SendEmail();
            email.setAssunto("Devolução de Livro - Biblioteca X");
            email.setEmailDestinatario(emp.getEmail_user());
            email.setMsg("Olá "+emp.getNome_user()+", <br><br>A devolução do livro <b>'"+emp.getTitulo_book()+"'</b> foi realizado com sucesso.");
            email.enviarGmail();

            //atualizando mensageria de retorno
            emp.setColor_msg_retorno(SUCESSO);
            emp.setMsg_retorno("Retorno: O empréstimo foi finalizado com sucesso. O livro encontra-se disponível para outros empréstimos.");
        }catch (SQLException e){
            System.out.println("Finalização de empréstimos falhou!");
            //atualizando mensageria de retorno
            emp.setColor_msg_retorno(FALHA);
            emp.setMsg_retorno("Retorno: Não foi possível finalizar o empréstimo, contacte o administrador.");
        }
        return "/acessoBalconista?faces-redirect=true";
    }

    public String editarEmprestimo(M_Emprestimo emp){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String)session.getAttribute("usuario");

        try{
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;


            //realizando as alterações no empréstimo selecionado
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
                    //atualizando mensageria de retorno
                    emp.setColor_msg_retorno(FALHA);
                    emp.setMsg_retorno("Retorno: O livro já está reservado em nome de outra pessoa.");
                    return "/acessoBalconista?faces-redirect=true";
                }
            }

            //TRATANDO A ALTERAÇÃO DO CODUSUARIO
            if(emp.getCodusuario()!=0){
                st.executeUpdate("UPDATE `bibliotec`.`emprestimo` e SET e.codusuario = '"+emp.getCodusuario()+"', e.dataalt = current_date() WHERE e.codemprestimo = '"+emp.getCodemprestimo()+"';");
            }

            //TRATANDO A ALTERAÇÃO DA DATA DE DEVOLUÇÃO
            if(!emp.getDatadev().equals("")){
                //convertendo nova data de devolução
                emp.setDatadev(formatadorDatasMySQL(emp.getDatadev()));

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

                    System.out.println("AQUI 1");

                    if(datares.equals("Maior")){
                        //atualizando mensageria de retorno
                        emp.setColor_msg_retorno(FALHA);
                        emp.setMsg_retorno("Retorno: O livro já possui reserva, não foi possível alterar data de devolução.");
                        return "/acessoBalconista?faces-redirect=true";
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

                    System.out.println("AQUI 2");

                    if(datares.equals("Maior")){
                        //atualizando mensageria de retorno
                        emp.setColor_msg_retorno(FALHA);
                        emp.setMsg_retorno("Retorno: O livro já possui reserva, não foi possível alterar data de devolução.");
                        return "/acessoBalconista?faces-redirect=true";
                    }
                }
                st.executeUpdate("UPDATE `bibliotec`.`emprestimo` e SET e.datadev = '"+emp.getDatadev()+"', e.dataalt = current_date() WHERE e.codemprestimo = '"+emp.getCodemprestimo()+"';");
            }

            //atualizando mensageria de retorno
            emp.setColor_msg_retorno(SUCESSO);
            emp.setMsg_retorno("Retorno: O empréstimo foi alterado com sucesso.");
        }catch (SQLException e){
            System.out.println("Alteração de empréstimos falhou!");
            //atualizando mensageria de retorno
            emp.setColor_msg_retorno(FALHA);
            emp.setMsg_retorno("Retorno: As alterações falharam, contacte o administrador.");
        }

        return "/acessoBalconista?faces-redirect=true";
    }
}