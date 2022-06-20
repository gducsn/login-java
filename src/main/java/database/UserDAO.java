package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import model.User;

public class UserDAO {

	private static final String CONTEXT = "java:/comp/env";
	private static final String DATASOURCE = "jdbc/login";

	private String queryString = "SELECT * FROM login.user WHERE username=? and password=?";

	protected Connection getConnection() throws NamingException, SQLException {
		Context initContext = new InitialContext();
		Context envContext = (Context) initContext.lookup(CONTEXT);
		DataSource dsconnection = (DataSource) envContext.lookup(DATASOURCE);
		return dsconnection.getConnection();
	}

	public boolean checkDatabase(User user) {

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(queryString);) {

			String username = user.getUsername();
			String password = user.getPassword();

			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);

			ResultSet rs = preparedStatement.executeQuery();
			return rs.next();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	};

}
