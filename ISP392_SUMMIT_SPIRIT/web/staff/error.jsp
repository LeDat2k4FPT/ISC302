<%--
    Document   : error
    Created on : Jun 18, 2025, 12:58:19 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>Error Page</title>
    </head>
    <body>
        <h2 style="color: red;">Oops! An error occurred.</h2>
        <p><%= request.getAttribute("error") %></p>
        <a href="staff/staffDashboard.jsp">⬅ Back to Dashboard</a>
    </body>
</html>
