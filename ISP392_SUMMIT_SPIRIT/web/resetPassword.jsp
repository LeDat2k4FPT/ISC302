<%--
    Document   : resetPassword
    Created on : Jun 15, 2025, 6:59:41 PM
    Author     : gmt
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Reset Password</title>
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/resetPassword.css">
    </head>
    <body>
        <div class="form-container">
            <h2>Reset Your Password</h2>
            <%
               String message = (String) request.getAttribute("MESSAGE");
               if (message != null && !message.isEmpty()) {
            %>
            <p class="error text-center"><%= message %></p>
            <%
                }
            %>
            <form action="<%= request.getContextPath() %>/MainController" method="POST">
                <input type="hidden" name="action" value="ResetPassword" />

                <label>New Password:</label>
                <input type="password" name="newPassword" required />

                <label>Confirm New Password:</label>
                <input type="password" name="confirmNewPassword" required />

                <input type="submit" value="Update Password" />

                <div class="text-center mt-3">
                    <a href="<%= request.getContextPath() %>/user/profile.jsp" class="back-link">Back to User Information Page</a>
                </div>
            </form>
        </div>
    </body>
</html>
