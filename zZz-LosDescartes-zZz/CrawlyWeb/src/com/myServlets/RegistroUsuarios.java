package com.myServlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegistroUsuarios
 */
@WebServlet("/RegistroUsuarios")
public class RegistroUsuarios extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegistroUsuarios() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
//		Pintar un ejemplo muy tonto
//		PrintWriter salida = response.getWriter();
//		salida.println("<html><body>");
//		salida.println("Nombre introducido " + request.getParameter("nombre"));
//		salida.println("Apellido introducido " + request.getParameter("apellido"));
//		salida.println("</body></html>");
		
		String[] productos = {"Destornillador","Raton","Teclado"};
		request.setAttribute("lista_productos", productos);
		RequestDispatcher miDispatcher = request.getRequestDispatcher("/VistaJSP.jsp");
		miDispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
		
		//Si no pones esto, no va a funcionar
		response.setContentType("text/html");
	}

}
