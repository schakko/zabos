<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<%@page isErrorPage="true"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="UTF-8"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Fehler</title>
</head>
<body>
  <h1>W&auml;hrend der Verwendung von Zabos trat folgender Fehler auf:</h1>
  <%= exception.getMessage() %>
  <br /><hr /><br />
  <%= exception.getStackTrace() %>
</body>
</html>