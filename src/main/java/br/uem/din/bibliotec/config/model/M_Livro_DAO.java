package br.uem.din.bibliotec.config.model;

import br.uem.din.bibliotec.config.conexao.Conexao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class M_Livro_DAO {

    public String cadastrarLivro(M_Livro livro) throws SQLException {
        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            livro.setAtivo(1);
            livro.setMsg_retorno("Retorno: O livro '"+ livro.getTitulo() +"' foi cadastrado com sucesso.");
            livro.setColor_msg_retorno("green");

            st.executeUpdate("INSERT INTO `bibliotec`.`livro` (`codcatalogacao`, `numchamada`, `titulo`, `autor`, `editora`, `anolancamento`, `cidade`, `volume`, `ativo`) VALUES ('"+ livro.getCodcatalogacao() +"', '"+ livro.getNumchamada() +"', '"+ livro.getTitulo() +"', '"+ livro.getAutor() +"', '"+ livro.getEditora() +"', '"+ livro.getAnolancamento() +"', '"+ livro.getCidade() +"', '"+ livro.getVolume() +"', '"+ livro.getAtivo() +"');");

            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
            livro.setMsg_retorno("Retorno: A operação de cadastramento do livro '"+ livro.getTitulo() +"' falhou.");
            livro.setColor_msg_retorno("red");
        }
        return "acessoBibliotecario";
    }

    public List<M_Livro> consultarLivro(M_Livro livro) throws SQLException {
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);
        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        st.execute("select * from `bibliotec`.`livro` where titulo like \"%"+ livro.getTitulo() +"%\" and ativo = '1' order by 2;");
        ResultSet rs = st.getResultSet();

        List<M_Livro> livros = new ArrayList<>();

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
                    rs.getInt("ativo"));

            livros.add(livro_temporario);
        }

        st.close();
        rs.close();
        con.conexao.close();

        return livros;
    }

    public List<M_Livro> consultarLivroBibliotecario(M_Livro livro) throws SQLException {
        Conexao con = new Conexao();
        con.conexao.setAutoCommit(true);

        Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        st.execute("select l.codlivro, l.codcatalogacao, l.numchamada, l.titulo, l.autor, l.editora, l.anolancamento, l.cidade, l.volume, l.ativo, case when l.ativo = 1 then \" Ativo \" else \" Inativo \" end as status from livro l where l.titulo like \"%"+ livro.getTitulo() +"%\" order by 1 ;");
        ResultSet rs = st.getResultSet();

        List<M_Livro> livros = new ArrayList<>();

        while (rs.next()) {
            M_Livro livro_temporario = new M_Livro
                   (rs.getInt("codlivro"),
                    rs.getString("codcatalogacao"),
                    rs.getString("numchamada"),
                    rs.getString("titulo"),
                    rs.getString("autor"),
                    rs.getString("editora"),
                    rs.getString("anolancamento"),
                    rs.getString("cidade"),
                    rs.getInt("volume"),
                    rs.getInt("ativo"),
                    rs.getString("status")
                   );

            livros.add(livro_temporario);
        }

        st.close();
        rs.close();
        con.conexao.close();

        return livros;
    }

    public String deletarLivro(M_Livro livro){
        String titulo = "";
        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.execute("select titulo from `bibliotec`.`livro` where codlivro = " + livro.getCodlivro() + ";");
            ResultSet rs = st.getResultSet();

            while (rs.next()){
                titulo = rs.getString("titulo");
            }

            st.executeUpdate("UPDATE `bibliotec`.`livro` SET `ativo` = '0' WHERE (`codlivro` =" + livro.getCodlivro() +  ");");

            livro.setMsg_retorno("Retorno: O livro '" + titulo + "' foi deletado com sucesso.");
            livro.setColor_msg_retorno("green");

            st.close();
            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
            livro.setMsg_retorno("Retorno: A deleção do livro '" + titulo + "' falhou.");
            livro.setColor_msg_retorno("red");
        }

        return "acessoBibliotecario";
    }

    public String editarLivro(M_Livro livro, boolean validaStatus){
        String titulo_anterior = "";

        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.execute("SELECT titulo FROM `bibliotec`.`livro` WHERE codlivro = " + livro.getCodlivro() + ";");
            ResultSet rs = st.getResultSet();

            while (rs.next()){
                titulo_anterior = rs.getString("titulo");
            }

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

            if(livro.getVolume() != 0 ){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET volume = '" + livro.getVolume() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }

            if(!livro.getCodcatalogacao().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET codcatalogacao = '" + livro.getCodcatalogacao() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }

            if(!livro.getNumchamada().equals("")){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET numchamada = '" + livro.getNumchamada() + "' WHERE codlivro = " + livro.getCodlivro() + ";");
            }

            if(validaStatus){
                st.executeUpdate("UPDATE `bibliotec`.`livro` SET ativo = 1 WHERE codlivro = " + livro.getCodlivro() + ";");
            }

            if(livro.getTitulo().equals("")){
                livro.setMsg_retorno("Retorno: As informações do livro '"+titulo_anterior+"' foram atualizadas com sucesso.");
            }else{
                livro.setMsg_retorno("Retorno: As informações do livro '"+livro.getTitulo()+"' foram atualizadas com sucesso.");
            }
            livro.setColor_msg_retorno("green");

            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
            livro.setMsg_retorno("Retorno: A operação de alteração do livro '"+ titulo_anterior +"' falhou.");
            livro.setColor_msg_retorno("red");
        }

        return "acessoBibliotecario";
    }
}
