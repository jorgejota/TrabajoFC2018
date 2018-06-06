package com.hijadeputa;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.tfg.ckan.OperacionesMatematicasRandom;

/**
 * Servlet implementation class MiPrueba
 */
public class MiPrueba extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MiPrueba() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void voyAHacerUnMetodo() {
    	
    	
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		int primerNumero = request.getParameter("number1");
		int segundoNumero = request.getParameter("number1");
		OperacionesMatematicasRandom nueva = new OperacionesMatematicasRandom(primerNumero, segundoNumero);
		int resultado = nueva.hacerOperacion();
		response.sendRedirect("regirigir.jsp?resultado="+resultado);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		//response.sendRedirect(arg0);
	}

}
