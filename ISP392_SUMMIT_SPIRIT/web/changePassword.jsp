<%@page import="dto.UserDTO" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Change Password</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/changePassword.css">
    </head>
    <body>
        <%
            UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
            if (loginUser == null) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            String message = (String) request.getAttribute("MESSAGE");
        %>

        <div class="card-box">
            <h2>Change Password</h2>
            <p>Update your current password</p>

            <% if (message != null && !message.isEmpty()) { %>
            <div class="error"><%= message %></div>
            <% } %>

            <form action="<%= request.getContextPath() %>/MainController" method="POST">
                <input type="hidden" name="action" value="ChangePassword" />

                <label class="form-label">Current Password</label>
                <input type="password" name="currentPassword" class="form-control" required />

                <label class="form-label">New Password</label>
                <input type="password" name="newPassword" class="form-control" required />

                <label class="form-label">Confirm New Password</label>
                <input type="password" name="confirmNewPassword" class="form-control" required />

                <button type="submit" name="action" value="ChangePassword" class="btn-submit">Update Password</button>
            </form>
            <div class="text-center mt-3">
                <button class="back-btn" onclick="history.back()">← Back</button>
            </div>
        </div>
    </body>
</html>
