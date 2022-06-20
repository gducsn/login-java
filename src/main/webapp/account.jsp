<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css">
<link rel="icon" type="image/x-icon" href="https://img.icons8.com/ios/344/contacts.png">
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