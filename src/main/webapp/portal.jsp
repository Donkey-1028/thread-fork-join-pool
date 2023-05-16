<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

ArrayList<String> results = (ArrayList)request.getAttribute("result");

%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Portal Page</title>
</head>
<body>
<% 
for(String result : results){
%>
API Request URL : <%= result %>	
<% 
} 
%>
</body>
</html>