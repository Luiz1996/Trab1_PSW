package br.uem.din.bibliotec.config.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//classe responsável por fazer a conexão com banco
public class Conexao {
    String servername = "localhost";
    String mydb = "c_r_u_d";
    String usuario = "root";
    String senha = "";

    public Connection conexao;
    public Conexao() throws SQLException{
        conexao = DriverManager.getConnection("jdbc:mysql://localhost/bibliotec", usuario, senha);
    }
}
