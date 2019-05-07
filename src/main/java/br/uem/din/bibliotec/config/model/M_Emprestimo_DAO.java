package br.uem.din.bibliotec.config.model;

import br.uem.din.bibliotec.config.conexao.Conexao;

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
                    rs.getString("email")
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
        String nome_user_emp = "";
        String titulo_book_emp = "";

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = null;

            //Inserindo um novo emprestimo e auterando a disponibilidade do livro
            st.executeUpdate("INSERT INTO `bibliotec`.`emprestimo` (`codusuario`, `codlivro`, `dataemp`, `datadev`, `ativo`) VALUES ('"+emp.getCodusuario()+"', '"+emp.getCodlivro()+"', current_date(), DATE_ADD(current_date(), INTERVAL 7 DAY) , '1');");
            st.executeUpdate("UPDATE `bibliotec`.`livro` l set l.disponibilidade = '0' where l.codlivro = '"+emp.getCodlivro()+"';");

            //obtendo dados para imprimir na mensagem de retorno
            st.execute("SELECT u.nome FROM `bibliotec`.`usuarios` u WHERE u.codusuario = '"+emp.getCodusuario()+"'");
            rs = st.getResultSet();
            while (rs.next()){
                nome_user_emp = rs.getString("nome");
            }

            st.execute("SELECT l.titulo FROM `bibliotec`.`livro` l WHERE l.codlivro = '"+emp.getCodlivro()+"'");
            rs = st.getResultSet();
            while (rs.next()){
                titulo_book_emp = rs.getString("titulo");
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
        return "acessoBalconista";
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
                    "\t(u.nome like '%"+emp.getNome_user()+"%' or\n" +
                    "\tl.titulo like '%"+emp.getTitulo_book()+"%') and\n" +
                    "\te.ativo = '1' order by 1;");

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
                        rs.getString("status")
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
        try{
            //realiza conexão com banco de dados bibliotec
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //obtendo codlivro para torná-lo disponível de novo
            st.execute("SELECT codlivro FROM `bibliotec`.`emprestimo` e WHERE e.codemprestimo = '"+emp.getCodemprestimo()+"';");
            ResultSet rs = st.getResultSet();

            while(rs.next()){
                emp.setCodlivro(rs.getInt("codlivro"));
            }

            //realizando updates de modo a deixar livro disponível para emprestimo e marcar o emprestimo em questão como finalizado
            st.executeUpdate("UPDATE `bibliotec`.`livro` l SET l.disponibilidade = '1' WHERE l.codlivro = '"+emp.getCodlivro()+"';");
            st.executeUpdate("UPDATE `bibliotec`.`emprestimo` e SET e.ativo = '0', e.dataalt = current_date() WHERE e.codemprestimo = '"+emp.getCodemprestimo()+"';");

            //atualizando mensageria de retorno
            emp.setColor_msg_retorno(SUCESSO);
            emp.setMsg_retorno("Retorno: O empréstimo foi finalizado com sucesso. O livro encontra-se disponível para outros empréstimos.");
        }catch (SQLException e){
            System.out.println("Finalização de empréstimos falhou!");
            //atualizando mensageria de retorno
            emp.setColor_msg_retorno(FALHA);
            emp.setMsg_retorno("Retorno: Não foi possível finalizar o empréstimo, contacte o administrador.");
        }
        return "acessoBalconista";
    }

    public String editarEmprestimo(M_Emprestimo emp){
        try{
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //realizando as alterações no empréstimo selecionado
            if(emp.getCodlivro()!=0){
                st.executeUpdate("UPDATE `bibliotec`.`emprestimo` e SET e.codlivro = '"+emp.getCodlivro()+"', e.dataalt = current_date() WHERE e.codemprestimo = '"+emp.getCodemprestimo()+"';");
            }

            if(emp.getCodusuario()!=0){
                st.executeUpdate("UPDATE `bibliotec`.`emprestimo` e SET e.codusuario = '"+emp.getCodusuario()+"', e.dataalt = current_date() WHERE e.codemprestimo = '"+emp.getCodemprestimo()+"';");
            }

            if(!emp.getDatadev().equals("")){
                //convertendo nova data de devolução
                emp.setDatadev(formatadorDatasMySQL(emp.getDatadev()));
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

        return "acessoBalconista";
    }
}