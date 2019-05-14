package br.uem.din.bibliotec.config.model;

import br.uem.din.bibliotec.config.conexao.Conexao;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class M_Livro_DAO {
    //declaração de variáveis constantes que auxiliarão na cor dos retornos
    final String SUCESSO = "green";
    final String FALHA = "red";

    //como o dado é informado como AAAA-MM-DD precisamos convertê-la para o formato do brasileiro ao imprimir no front-end ao usuário
    public String formatadorDatasBrasil(String data){
        if(data == null){
            return " ";
        }else{
            String[] dataSeparada = data.split("-");
            String dataPadraoBrasil = dataSeparada[2]+"/"+dataSeparada[1]+"/"+dataSeparada[0];
            return dataPadraoBrasil.trim();
        }
    }

    //método de cadastramento de livro
    public String cadastrarLivro(M_Livro livro) throws SQLException {
        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //realiza insert no banco e retorna mensagem de sucesso na cor verde
            st.executeUpdate("INSERT INTO `bibliotec`.`livro` (`codcatalogacao`, `numchamada`, `titulo`, `autor`, `editora`, `anolancamento`, `cidade`, `volume`, `ativo`, `datacad`, `disponibilidade`) VALUES ('"+livro.getCodcatalogacao()+"', '"+livro.getNumchamada()+"', '"+livro.getTitulo()+"', '"+livro.getAutor()+"', '"+livro.getEditora()+"', '"+livro.getAnolancamento()+"', '"+livro.getCidade()+"', "+livro.getVolume()+", '1', current_date(), '1');");

            livro.setMsg_retorno("Retorno: O livro '"+ livro.getTitulo() +"' foi cadastrado com sucesso.");
            livro.setColor_msg_retorno(SUCESSO);

            st.close();
            con.conexao.close();
        } catch (SQLException e) {
            //em caso de erro no insert é retornado mensagem de falha na cor vermelha
            System.out.println("Dados informados são inválidos!");
            livro.setMsg_retorno("Retorno: A operação de cadastramento do livro '"+ livro.getTitulo() +"' falhou.");
            livro.setColor_msg_retorno(FALHA);
        }
        return "acessoBibliotecario";
    }


    public List<M_Livro> consultarLivro(M_Livro livro, int soDisponiveis) throws SQLException {
        //realiza conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        //setando os valores obtidos no front-end para realizar busca no banco de dados
        livro.setEditora(livro.getTitulo());
        livro.setAutor(livro.getTitulo());

        //busca todas as informações de acordo com os dados fornecidos
        if(soDisponiveis == 0){
            st.execute("select * from `bibliotec`.`livro` where (titulo like \"%"+ livro.getTitulo() +"%\" or autor like \"%" + livro.getAutor() + "%\" or editora like \"%" + livro.getEditora() + "%\") and ativo = '1' order by 2;");
        }else{
            st.execute("select * from `bibliotec`.`livro` where (titulo like \"%"+ livro.getTitulo() +"%\" or autor like \"%" + livro.getAutor() + "%\" or editora like \"%" + livro.getEditora() + "%\") and ativo = '1' and disponibilidade = '1' order by 2;");
        }

        ResultSet rs = st.getResultSet();

        //declaração do arrayList para auxiliar na impressão da dataTable do consultar acervo do Visitante
        List<M_Livro> livros = new ArrayList<>();

        //obtendo os valores carregados no result set e carregado no arrayList
        while (rs.next()) {
            M_Livro livro_temporario = new M_Livro(rs.getInt("codlivro"),
                    rs.getString("codcatalogacao"),
                    rs.getString("numchamada"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("editora"),
                    rs.getString("anolancamento"),
                    rs.getString("cidade"),
                    rs.getInt("volume"),
                    rs.getInt("ativo"),
                    "",
                    "",
                    "",
                    rs.getString("datacad"),
                    rs.getString("dataalt"),
                    rs.getString("disponibilidade")
                    );

            livros.add(livro_temporario);
        }

        //fechando as conexões em aberto para evitar locks infinitos no banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return livros;
    }

    public List<M_Livro> consultarLivroBibliotecario(M_Livro livro, int ativo) throws SQLException {
        //realizando a conexão com banco de dados
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        //setando os valores obtidos no front-end para realizar busca no banco de dados
        livro.setEditora(livro.getTitulo());
        livro.setAutor(livro.getTitulo());

        //buscando as informações no banco de dados de acordo com o titulo do livro informado pelo usupario, essa busca possui diferencial da coluna Status (Ativo/Inativo)
        if(ativo == 0){
            st.execute("select l.codlivro, l.codcatalogacao, l.numchamada, l.titulo, l.autor, l.editora, l.anolancamento, l.cidade, l.volume, l.ativo, case when l.ativo = 1 then \" Ativo \" else \" Inativo \" end as status, datacad, dataalt, case when disponibilidade = 1 then \" S \" when disponibilidade = 0 then \" N \" end as disponibilidade  from livro l where (l.titulo like \"%"+ livro.getTitulo() +"%\" or l.autor like \"%" + livro.getAutor() + "%\" or l.editora like \"%" + livro.getEditora() + "%\") order by 1 ;");
        }else{
            st.execute("select l.codlivro, l.codcatalogacao, l.numchamada, l.titulo, l.autor, l.editora, l.anolancamento, l.cidade, l.volume, l.ativo, case when l.ativo = 1 then \" Ativo \" else \" Inativo \" end as status, datacad, dataalt, case when disponibilidade = 1 then \" S \" when disponibilidade = 0 then \" N \" end as disponibilidade  from livro l where ativo = '1' order by 4 ;");
        }

        ResultSet rs = st.getResultSet();

        //declara o arrayList que será usado no dataTable do Bibliotecário
        List<M_Livro> livros = new ArrayList<>();

        //carregando o arrayList com os valores obtidos no resultSet
        while (rs.next()) {
            M_Livro livro_temporario = new M_Livro
                (   rs.getInt("codlivro"),
                    rs.getString("codcatalogacao"),
                    rs.getString("numchamada"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("editora"),
                    rs.getString("anolancamento"),
                    rs.getString("cidade"),
                    rs.getInt("volume"),
                    rs.getInt("ativo"),
                    rs.getString("status"),
                   "",
                   "",
                   formatadorDatasBrasil(rs.getString("datacad")),
                   formatadorDatasBrasil(rs.getString("dataalt")),
                   rs.getString("disponibilidade")
                );

            livros.add(livro_temporario);
        }

        //fechando todas as conexões para evitar lock nas tabelas do banco de dados
        st.close();
        rs.close();
        con.conexao.close();

        return livros;
    }

    public String deletarLivro(M_Livro livro){
        //declaração de variaveis locais que serão utilizadas na impressão dos retornos ao usuário
        String titulo = "";
        Integer codlivro_local = 0;

        //valida se o código do livro não foi fornecido e retorna mensagem de erro na cor vermelha
        if(livro.getCodlivro() == 0){
            livro.setMsg_retorno("Retorno: O código Id do livro é inválido, deleção falhou.");
            livro.setColor_msg_retorno(FALHA);
            return "acessoBibliotecario";
        }

        try {
            //abre conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //realiza a consulta que vai nos auxiliar na tratativa de erros em caso de deleção de livro que não existe no banco de dados
            //ou seja, codlivro informado pelo usuário é inválido
            st.execute("select titulo, codlivro from `bibliotec`.`livro` where codlivro = " + livro.getCodlivro() + ";");
            ResultSet rs = st.getResultSet();

            //carrega os dados do resultSet dentro das variáveis locais
            while (rs.next()){
                titulo = rs.getString("titulo");
                codlivro_local = rs.getInt("codlivro");
            }

            //valida de o código do livro foi fornecido incorretamente, caso contrário retorna msg de erro na cor vermelha
            if(codlivro_local == 0){
                livro.setMsg_retorno("Retorno: O livro com codlivro: "+ livro.getCodlivro() +" não existe, deleção falhou.");
                livro.setColor_msg_retorno(FALHA);
                return "acessoBibliotecario";
            }

            //executa a EXCLUSÃO LÓGICA do livro no banco de dados, ou seja, ativo recebe 0
            st.executeUpdate("UPDATE `bibliotec`.`livro` SET `ativo` = '0', dataalt = current_date(), disponibilidade = '0' WHERE (`codlivro` =" + livro.getCodlivro() + ");");

            //retorna msg de sucesso na cor verde
            livro.setMsg_retorno("Retorno: O livro '" + titulo + "' foi deletado com sucesso.");
            livro.setColor_msg_retorno(SUCESSO);

            //fecha conexões para evitar lock nas tabelas do banco de dados
            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            //em casos de erro no update acima, retorna mensagem de falha na cor vermelha
            System.out.println("Dados informados são inválidos!");
            livro.setMsg_retorno("Retorno: A deleção do livro '" + titulo + "' falhou.");
            livro.setColor_msg_retorno(FALHA);
        }

        return "acessoBibliotecario";
    }

    public String editarLivro(M_Livro livro){
        System.out.println("volume:"+livro.getVolume());


        //declaração de varáveis locais que nos ajudará nas tratativas de erros
        String titulo_anterior = "";
        Integer codlivro_local = 0;

        //valida se o código do livro não foi fornecido, caso contrário retorna msg de erro na cor vermelha
        if(livro.getCodlivro() == 0){
            livro.setMsg_retorno("Retorno: O código Id do livro é inválido, edição falhou.");
            livro.setColor_msg_retorno(FALHA);
            return "acessoBibliotecario";
        }

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            //realiza a consulta que vai nos auxiliar na tratativa de erros em caso de deleção de livro que não existe no banco de dados
            //ou seja, codlivro informado pelo usuário é inválido
            st.execute("SELECT titulo, codlivro FROM `bibliotec`.`livro` WHERE codlivro = " + livro.getCodlivro() + ";");
            ResultSet rs = st.getResultSet();

            //carrega as variáveis locais com os valores do resultSet
            while (rs.next()){
                titulo_anterior = rs.getString("titulo");
                codlivro_local = rs.getInt("codlivro");
            }

            //valida se o código do livro foi fornecido de forma incorreta, ou seja, livro inexistente na base de dados
            if(codlivro_local == 0){
                livro.setMsg_retorno("Retorno: O livro com codlivro: "+ livro.getCodlivro() +" não existe, edição falhou.");
                livro.setColor_msg_retorno(FALHA);
                return "acessoBibliotecario";
            }

            //este bloco realiza os updates apenas nos campos que foram preenchidos pelo usuário
            st.executeUpdate("UPDATE `bibliotec`.`livro` SET dataalt = current_date() WHERE codlivro ="+ livro.getCodlivro() +";");

            if(!livro.getTitulo().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET titulo = '" + livro.getTitulo() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }

            if(!livro.getAutor().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET autor = '" + livro.getAutor() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }

            if(!livro.getEditora().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET editora = '" + livro.getEditora() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }

            if(!livro.getAnolancamento().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET anolancamento = '" + livro.getAnolancamento() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }

            if(!livro.getCidade().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET cidade = '" + livro.getCidade() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }

            if(livro.getVolume() != null ){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET volume = '" + livro.getVolume() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }
            System.out.println("aqui");
            if(!livro.getCodcatalogacao().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET codcatalogacao = '" + livro.getCodcatalogacao() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }
            System.out.println("aqui");
            if(!livro.getNumchamada().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET numchamada = '" + livro.getNumchamada() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }
            System.out.println("aqui");
            if(livro.getAtivo() == 0){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET ativo = '0' WHERE codlivro = " + livro.getCodlivro() + ";");
            }else{
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET ativo = '1' WHERE codlivro = " + livro.getCodlivro() + ";");
            }
            System.out.println("aqui");
            if(livro.getDisponibilidade().equals("0")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET disponibilidade = '0' WHERE codlivro = " + livro.getCodlivro() + ";");
            }else{
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET disponibilidade = '1' WHERE codlivro = " + livro.getCodlivro() + ";");
            }
            System.out.println("aqui");
            //dependendo se o titulo informado na tela for vazio/nulo ele imprime na mensagem de retorno com base no titulo buscado no banco de dados
            //caso contrário, ele imprimirá o novo título
            if(livro.getTitulo().equals("")){
                livro.setMsg_retorno("Retorno: As informações do livro '"+titulo_anterior+"' foram atualizadas com sucesso.");
            }else{
                livro.setMsg_retorno("Retorno: As informações do livro '"+livro.getTitulo()+"' foram atualizadas com sucesso.");
            }
            livro.setColor_msg_retorno(SUCESSO);

            //fechando as conexões para evitar lock
            st.close();
            rs.close();
            con.conexao.close();
        } catch (SQLException e) {
            //caso algum update ou buscas em base de dados falhe, ele retornará mensagem de erro na cor vermelha
            System.out.println("Dados informados são inválidos!");
            livro.setMsg_retorno("Retorno: A operação de alteração do livro '"+ titulo_anterior +"' falhou.");
            livro.setColor_msg_retorno(FALHA);
        }

        return "acessoBibliotecario";
    }

    public String cadastrarReserva(M_Livro livro){
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String)session.getAttribute("usuario");
        M_Usuario_DAO user = new M_Usuario_DAO();
        String statusLivro = "";
        int codUsuarioLocal = 0;

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);
            ResultSet rs = null;

            st.execute("select codusuario from `bibliotec`.`usuarios` where usuario = '"+login+"';");
            rs = st.getResultSet();

            while(rs.next()){
                codUsuarioLocal = rs.getInt("codusuario");
            }

            //consultar se livro já possui reserva
            st.execute( "SELECT \n" +
                            "    l.datares,\n" +
                            "    l.usuariores,\n" +
                            "    l.titulo,\n" +
                            "    CASE\n" +
                            "        WHEN current_date() <= l.datares \n" +
                            "\t\t\tTHEN 'Reservado'\n" +
                            "\t\t\tELSE 'Não Reservado'\n" +
                            "    END AS status\n" +
                            "FROM\n" +
                            "    `bibliotec`.`livro` l\n" +
                            "WHERE\n" +
                            "    l.codlivro = '"+livro.getCodlivro()+"';");

            rs = st.getResultSet();

            while(rs.next()){
                livro.setDatares(rs.getString("datares"));
                livro.setUsuariores(rs.getInt("usuariores"));
                livro.setTitulo(rs.getString("titulo"));
                statusLivro = rs.getString("status");
            }

            //verificando se ja possui reserva deste livro para ele mesmo
            if(livro.getUsuariores() == codUsuarioLocal){
                livro.setMsg_retorno("Retorno: O livro '"+ livro.getTitulo() +"' já possui reserva em seu nome para retirada em "+formatadorDatasBrasil(livro.getDatares())+".");
                livro.setColor_msg_retorno(FALHA);
                return user.minhaHomePage();
            }

            //verificando se há reservas para outros usuários
            if(statusLivro.equals("Reservado")){
                livro.setMsg_retorno("Retorno: O livro '"+ livro.getTitulo() +"' já possui reserva em nome de outra pessoa.");
                livro.setColor_msg_retorno(FALHA);
                return user.minhaHomePage();
            }

            //criando nova reserva
            if(livro.getDatares() == null && livro.getUsuariores() == 0){
                st.execute( "select\n" +
                                "    l.disponibilidade\n" +
                                "from\n" +
                                "\tlivro l\n" +
                                "where\n" +
                                "\tl.codlivro = '"+livro.getCodlivro()+"';");
                rs = st.getResultSet();
                while(rs.next()){
                    livro.setDisponibilidade(rs.getString("disponibilidade"));
                }

                if(livro.getDisponibilidade().equals("1")){
                    st.executeUpdate("update livro l set l.datares = current_date(), l.usuariores = '"+codUsuarioLocal+"' where l.codlivro = '"+livro.getCodlivro()+"';");
                    livro.setMsg_retorno("Retorno: Reserva efetuada com sucesso, o livro estará disponível para retirada na data atual até o fechamento da biblioteca.");
                    livro.setColor_msg_retorno(SUCESSO);
                }else{
                    //se o livro já estiver emprestado mas sem reserva, precisamos obter a data de devolção do emprestimo atual para poder criar nova reserva
                    //trazendo data de devolução do emprestimo atual
                    st.execute("SELECT \n" +
                                    "    MAX(e.codemprestimo) AS codemprestimo, DATE_ADD(e.datadev ,INTERVAL 1 DAY) as datares\n" +
                                    "FROM\n" +
                                    "    emprestimo e\n" +
                                    "WHERE\n" +
                                    "    e.ativo = '1' AND e.codlivro = '"+livro.getCodlivro()+"';");

                    rs = st.getResultSet();
                    while(rs.next()){
                        livro.setDatares(rs.getString("datares").trim());
                    }

                    System.out.println("AQUI");

                    st.executeUpdate(   "update livro l set l.datares = '"+livro.getDatares()+"', l.usuariores = '"+codUsuarioLocal+"' where l.codlivro = '"+livro.getCodlivro()+"';");

                    livro.setMsg_retorno("Retorno: Reserva efetuada com sucesso, o livro estará disponível para retirada no dia "+formatadorDatasBrasil(livro.getDatares())+" até o fechamento da biblioteca.");
                    livro.setColor_msg_retorno(SUCESSO);
                }
                return user.minhaHomePage();
            }

            st.close();
            con.conexao.close();
        } catch (SQLException e) {
            //em caso de erro no insert é retornado mensagem de falha na cor vermelha
            System.out.println("Dados informados são inválidos!");
            livro.setMsg_retorno("Retorno: A operação de cadastramento de reserva falhou.");
            livro.setColor_msg_retorno(FALHA);
        }
        return user.minhaHomePage();
    }

    public List<M_Livro> consultaMinhasReservas(M_Livro livro){
        List<M_Livro> livros = new ArrayList<M_Livro>();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        String login = (String)session.getAttribute("usuario");

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);
            ResultSet rs = null;

            st.execute(  "select\n" +
                            "\tl.titulo,\n" +
                            "    l.autor,\n" +
                            "    l.editora,\n" +
                            "    l.anolancamento,\n" +
                            "    l.volume,\n" +
                            "    l.datares,\n" +
                            "    l.codlivro\n" +
                            "from\n" +
                            "\tlivro    l\n" +
                            "left join\n" +
                            "\tusuarios u on u.codusuario = l.usuariores\n" +
                            "where\n" +
                            "\t  u.usuario = '"+login+"' and\n" +
                            "    l.datares >= current_date();");

            rs = st.getResultSet();

            //carregando o arrayList com os valores obtidos no resultSet
            while (rs.next()) {
                M_Livro livro_temporario = new M_Livro(
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("editora"),
                        rs.getString("anolancamento"),
                        rs.getInt("volume"),
                        formatadorDatasBrasil(rs.getString("datares")),
                        rs.getInt("codlivro")
                        );
                livros.add(livro_temporario);
            }
        }catch (Exception e){
            System.out.println("Dados informados são invalidos, falha na consulta!");
        }

        return livros;
    }

    public String cancelarReserva(M_Livro livro){
        M_Usuario_DAO user = new M_Usuario_DAO();

        try {
            //realiza conexão com banco de dados
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.executeUpdate("update `bibliotec`.`livro` l set l.datares = null, l.usuariores = null where l.codlivro = '"+livro.getCodlivro()+"';");

            livro.setMsg_retorno("Retorno: Reserva cancelada com sucesso.");
            livro.setColor_msg_retorno(SUCESSO);
        }catch (Exception e){
            System.out.println("Dados informados são invalidos, falha no cancelamento!");
            livro.setMsg_retorno("Retorno: A operação de cancelamento de reserva falhou.");
            livro.setColor_msg_retorno(FALHA);
        }
        return user.minhaHomePage();
    }
}
