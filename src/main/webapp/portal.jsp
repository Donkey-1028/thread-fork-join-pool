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
	if(result.indexOf("200") == -1){
%> 
<strong style="color: #FF0000">API Request URL : <%= result %></strong>	
<%
	} else {
%>
API Request URL : <%= result %>	
<%
	}
%>
<br>
<% 
} 
%>
</body>
</html>