![output](https://user-images.githubusercontent.com/94108883/174332149-0dbbe5df-c952-44ca-955e-6fb8180ee27b.gif)

# Login Form

Pagina per verificare l’accesso. Se l’utente è registrato, quindi presente 
nel DB, potrà accedere. Altrimenti verrà lanciato un errore. 

Per simulare la registrazione ho aggiunto manualmente un utente nel 
database.

Sono presenti tre classi: User, UserDAO, UserServlet.

La classe User ci serve come modello per istanziare nuovi utenti.

La seconda classe, UserDAO, ci serve per gestire le interazioni con il 
database. La classe contiene il metodo per la connessione e il metodo per 
il controllo del login.

La terza classe è il servlet con il quale gestiamo la richiesta 
dell’utente e inviamo i dati alla classe DAO.

Infine le due pagine JSP, la prima per effettuare il login, la seconda è 
la pagina di avvenuto login.

---

User

```java
package model;
import java.io.Serializable;

public class User {

	private String username;
	private String password;

	public User() {
	};

	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
```

Questa classe ci permette di istanziare un nuovo utente. Questo tipo di 
classe è detto “JavaBean” e per poter esserlo deve avere determinate 
caratteristiche:

- Avere un costruttore senza argomenti.
- Avere metodi get/set
- Permettere la serializzazione.

---

 User DAO

```java
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

	private String queryString = "SELECT * FROM login.user";

	protected Connection getConnection() throws NamingException, 
SQLException {
		Context initContext = new InitialContext();
		Context envContext = (Context) 
initContext.lookup(CONTEXT);
		DataSource dsconnection = (DataSource) 
envContext.lookup(DATASOURCE);
		return dsconnection.getConnection();
	}

	public User checkUser() {
		User checkuser = null;

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = 
connection.prepareStatement(queryString);) {
			ResultSet resulset = 
preparedStatement.executeQuery();

			while (resulset.next()) {

				String username = resulset.getString(2);
				String password = resulset.getString(3);

				checkuser = new User(username, password);

			}
			;

		} catch (Exception e) {
			e.printStackTrace();

		}

		return checkuser;
	};

}
```

Il metodo getConnection ci permette la connessione al DB.

```java
	private static final String CONTEXT = "java:/comp/env";
	private static final String DATASOURCE = "jdbc/login";

	private String queryString = "SELECT * FROM login.user";

	protected Connection getConnection() throws NamingException, 
SQLException {
		Context initContext = new InitialContext();
		Context envContext = (Context) 
initContext.lookup(CONTEXT);
		DataSource dsconnection = (DataSource) 
envContext.lookup(DATASOURCE);
		return dsconnection.getConnection();
	}
```

La prima cosa è stata definire le stringhe ci permettono di accedere al 
datasource nel file context.xml di tomcat. Successivamente definiamo una 
stringa che contiene al query per il database. In questo caso ci serve 
prelevare ogni utente per poi fare una comparazione. 

La connessione effettiva è nel metodo getConnection();.

Questo tipo di approccio è molto comune, quindi in ogni progetto 
utilizzerò questo metodo per connettermi al database. Ho argomentato il 
suo funzionamento [qui](https://github.com/gducsn/db-java-crud).

Una volta stabilita la connessione abbiamo il metodo che ritorna un valore 
boolean.

```java
public boolean checkDatabase(User user) {

		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = 
connection.prepareStatement(queryString);) {

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
```

Il metodo ha come argomento un utente di tipo User dal quale preleveremo 
le informazioni scritte nel form di login dall’utente. Come recupereremo 
queste info sarà spiegato dopo, quando saremo nel servlet.

Una volta recuperate le informazioni e inserite in nuove stringhe possiamo 
passarle nella query del database tramite il metodo .setString(). Il 
metodo prende due parametri, il primo è la colonna e il secondo è il 
valore.

Quando eseguiamo determinate query, soprattutto la SELECT, il valore di 
ritorno dal database deve essere gestito dall’interfaccia 
[ResultSet](https://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html). 
L’oggetto ResultSet mantiene un cursore che punta alla riga di dati 
corrente.

Inizialmente il cursore è posizionato prima della prima riga. Possiamo 
usare quindi il metodo .next() che ci offre varie opportunità, come 
iterare sugli oggetti presenti in tabella. In questo caso non ci serve 
iterare, ma sfruttare il metodo next() per ritornare un valore boolean. 
Infatti, se non sono presenti righe ritorna FALSE, altrimenti TRUE.

```java
return rs.next();
```

Questo si può tradurre in: “ritorna TRUE se è presente una riga”.

Il funzionamento è semplice: abbiamo i dati dell’utente, interroghiamo il 
database con questi dati, se torna TRUE vuol dire che username e password 
corrispondono, altrimenti no.

La query risulta essere fondamentale:

```java
"SELECT * FROM login.user WHERE username=? and password=?"

```

La query dice di voler selezionare tutto dal database se corrisponde 
all’username e alla password. Se uno dei due non corrisponde ritorna 
‘null’, quindi false.

Il mio primo approccio è stato molto diverso. Inizialmente pensavo fosse 
corretto inviare una query al database che mi ritornasse tutti gli utenti, 
poi iterarli e avere una comparazione tra quello scritto dall’utente e 
quello presente nel database.

Purtroppo è un approccio sbagliato per tanti motivi.

Ho avuto le idee più chiare leggendo il capitolo sul database del libro 
[Murach's Java Servlets and JSP 3rd 
Edition](https://www.amazon.it/Murachs-Java-Servlets-JSP-Reference/dp/1890774782). 
Ci sono tanti modi per interrogare il database e molti tra questi 
facilitano il lavoro.

---

User Servlet 

```java
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

	protected void doGet(HttpServletRequest request, 
HttpServletResponse response)
			throws ServletException, IOException {
}

	protected void doPost(HttpServletRequest request, 
HttpServletResponse response)
			throws ServletException, IOException {

		if (checkUser(request, response) == true) {
			RequestDispatcher dispatcher = 
request.getRequestDispatcher("account.jsp");
			dispatcher.forward(request, response);
		} else {
			request.setAttribute("valueON", (Boolean) true);
			RequestDispatcher dispatcher = 
request.getRequestDispatcher("");
			dispatcher.forward(request, response);
		}

	}

	private Boolean checkUser(HttpServletRequest request, 
HttpServletResponse response) {
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
```

La classe servlet ci serve per gestire le chiamate http.

Il metodo che gestisce i dati dell’utente è il ‘checkUser’, il quale 
ritorna un valore boolean.

Definiamo due stringhe nelle quali raccogliamo i valori di input espressi 
dall’utente nel form.

Il modo per raccoglierli è semplice:

nel campo input del form aggiungiamo un attributo ‘name’ nel quale 
scriviamo il nome, ad esempio ‘username’.

Con il metodo ‘getParameter’ inseriamo la stringa ‘username’ con la quale 
raccogliamo il valore emesso dall’utente.

Questi due valori ci servono per creare un nuovo utente da inviare al 
metodo ‘checkDatabase’ nella classe DAO.

Una volta creato l’utente istanziamo anche l’oggetto userDAO il quale ci 
dà accesso al metodo al suo interno. Al metodo passiamo l’utente creato.

Ora, nel blocco try/catch, inseriamo il costrutto if per verificare il 
giusto ritorno di valore.

Se è true allora definiamo la proprietà di ritorno del metodo con ‘TRUE’, 
altrimenti ‘FALSE’.

La chiamata che genera l’utente in fase di submit del form è di tipo 
‘post’, quindi sfruttiamo il metodo ‘doPost’ per inserire la chiamata al 
nostro metodo.

Con il costrutto if verifichiamo se è true oppure false.

 Nel caso fosse true allora saremo inviati alla pagina di successo, 
altrimenti sarà gestito l’errato inserimento di credenziali dell’utente.

Cosa succede quanto l’utente digita le credenziali in modo sbagliato? 
Tutti i metodi riportano il valore FALSE. La chiamata al metodo ‘doPost’ 
avviene sempre, solo che invece di essere indirizzati ad un’altra pagina 
siamo riportarti sempre sulla stessa pagina, viene creato un nuovo 
attributo “valueON” e compare la scritta:

“Your username or password is incorrect.”

---

 Login Page

```java
<%@page import="servlet.UserServlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" 
href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css">
<title>Login</title>
<link rel="icon" type="image/x-icon" 
href="https://img.icons8.com/ios/344/contacts.png">
</head>
<body>

<% request.getAttribute("valueON"); %>

<div class="d-flex justify-content-center p-5 ">
<form action="login" method="post" class="w-50 p-3">
  <div class="form-group">
    <label for="exampleInputEmail1">username</label>
        <input required name="username" type="text" class="form-control" 
id="inlineFormInputGroupUsername" placeholder="Username">
  </div>
  <div class="form-group">
    <label for="exampleInputPassword1">password</label>
    <input required name="password" type="password" class="form-control" 
id="exampleInputPassword1" placeholder="Password">
  </div>
  <button type="submit" class="btn btn-primary btn-dark ">Submit</button>

<c:if test="${ valueON }">
<div class="mt-2" role="alert">
   <p class="lead" style="color: red; font-size: 12px">Your username or 
password is incorrect.<p>
</div>
</c:if>
  
</form>
</div>
</body>
</html>
```

La pagina JSP gestisce la view. Al suo interno c’è un form e un piccolo 
uso di codice.

Quello che ci serve recuperare è l’attributo che abbiamo creato nel 
servlet con il quale si attiva o meno la presenza dell’errore.

Per il resto risulta essere una semplice pagina html con un form da 
compilare.

---

Success 

```java
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" 
href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css">
<link rel="icon" type="image/x-icon" 
href="https://img.icons8.com/ios/344/contacts.png">
<% String name = request.getParameter("username"); %>
<title>Welcome <%= name %></title>
</head>
<body>
<div>

<div class="d-flex justify-content-center p-5">
<h1 class="lead">hi <%= name %>, welcome back!</h1>
</div>

</div>
</body>
</html>
```

La semplice pagina di successo prende il valore ‘username’ associato e lo 
rende visibile. Cambia dinamicamente anche il valore all’interno del 
titolo della pagina.

