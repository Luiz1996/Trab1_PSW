package br.uem.din.bibliotec.config.filter;

import br.uem.din.bibliotec.config.controller.C_Usuario;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "meusDados", urlPatterns = {"/meusDados.xhtml"})
public class meusDados implements Filter {

    public meusDados(){}

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException{

        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        HttpSession session = (HttpSession) req.getSession();

        String login = (String)session.getAttribute("usuario");

        C_Usuario user = new C_Usuario(login);

        if(login == null){
            res.sendRedirect(req.getContextPath() + "/gestaoBibliotecas.xhtml");
        }else{
            chain.doFilter(request, response);
        }
    }
}
