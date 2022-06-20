<%@page import="servlet.UserServlet"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css">
<title>Login</title>
<link rel="icon" type="image/x-icon" href="https://img.icons8.com/ios/344/contacts.png">
</head>
<body>

<% request.getAttribute("valueON"); %>


<div class="d-flex justify-content-center p-5 ">
<form action="login" method="post" class="w-50 p-3">
  <div class="form-group">
    <label for="exampleInputEmail1">username</label>
        <input required name="username" type="text" class="form-control" id="inlineFormInputGroupUsername" placeholder="Username">
  </div>
  <div class="form-group">
    <label for="exampleInputPassword1">password</label>
    <input required name="password" type="password" class="form-control" id="exampleInputPassword1" placeholder="Password">
  </div>
  <button type="submit" class="btn btn-primary btn-dark ">Submit</button>


<c:if test="${ valueON }">
<div class="mt-2" role="alert">
   <p class="lead" style="color: red; font-size: 12px">Your username or password is incorrect.<p>
</div>
</c:if>
  
</form>
</div>
</body>
</html>