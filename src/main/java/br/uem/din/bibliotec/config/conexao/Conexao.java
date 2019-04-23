package br.uem.din.bibliotec.config.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//classe responsável por fazer a conexão com banco
public class Conexao {
    //Ddeclaração das strings que vão compor a url da conexão
    String servername = "localhost";
    String mydb = "bibliotec";
    String usuario = "root";
    String senha = "";

    public Connection conexao;
    public Conexao() throws SQLException{
        conexao = DriverManager.getConnection("jdbc:mysql://localhost/" + mydb, usuario, senha);
    }
}
