package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import database.UserDAO;
import model.User;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/login")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (checkUser(request, response) == true) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("account.jsp");
			dispatcher.forward(request, response);
		} else {
			request.setAttribute("valueON", (Boolean) true);
			RequestDispatcher dispatcher = request.getRequestDispatcher("");
			dispatcher.forward(request, response);
		}

	}

	private Boolean checkUser(HttpServletRequest request, HttpServletResponse response) {
		boolean value = false;

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		User createUser = new User(username, password);
		UserDAO userDAO = new UserDAO();

		try {
			if (userDAO.checkDatabase(createUser) == true) {
				value = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;

	};

}
