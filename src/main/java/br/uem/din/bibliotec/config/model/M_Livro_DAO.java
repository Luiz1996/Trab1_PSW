package br.uem.din.bibliotec.config.model;

import br.uem.din.bibliotec.config.conexao.Conexao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class M_Livro_DAO {

    public String cadastrarLivro(M_Livro livro) throws SQLException {
        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            st.executeUpdate("INSERT INTO `bibliotec`.`livro` (`codcatalogacao`, `numchamada`, `titulo`, `autor`, `editora`, `anolancamento`, `cidade`, `volume`, `ativo`) VALUES ('"+ livro.getCodcatalogacao() +"', '"+ livro.getNumchamada() +"', '"+ livro.getTitulo() +"', '"+ livro.getAutor() +"', '"+ livro.getEditora() +"', '"+ livro.getAnolancamento() +"', '"+ livro.getCidade() +"', '"+ livro.getVolume() +"', '"+ livro.getAtivo() +"');");

            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
        }
        
        return "acessoBibliotecario";
    }

    public String consultarLivro(M_Livro livro){
        try {
            Conexao con = new Conexao();
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            con.conexao.setAutoCommit(true);

            M_Livro livro1 = new M_Livro("","","","","","","",8,9);

            st.execute("select * from `bibliotec`.`livro` where titulo like \""+ livro.getTitulo() +"\" or autor like \""+ livro.getAutor() +"\" or editora like \""+ livro.getEditora() +"\";");

            ResultSet rs = st.getResultSet();
            while (rs.next()) {
                livro1.setTitulo(rs.getString("titulo"));
            }

            System.out.println(livro.getTitulo());

            con.conexao.close();
        } catch (SQLException e) {
            System.out.println("Dados informados são inválidos!");
        }

        return "mostrarDadosConsultados";
    }

}
