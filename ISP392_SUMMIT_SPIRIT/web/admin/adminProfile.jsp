<%-- 
    Document   : adminProfile
    Created on : Jul 25, 2025, 01:01 AM
    Author     : Admin
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="dto.UserDTO"%>
<%@page import="user.UserError"%>

<%
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null || !"Admin".equalsIgnoreCase(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    UserError userError = (UserError) request.getAttribute("USER_ERROR");
    if (userError == null) userError = new UserError();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Admin Profile</title>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/adminProfile.css">
    </head>
    <body>
        <main class="main-content">
            <button class="back-btn" onclick="history.back()">&larr; Back</button>
            <div class="profile-form">
                <h2 class="title">Admin Profile</h2>
                <h4 class="username"><%= loginUser.getFullName().toUpperCase() %></h4>

                <form action="<%= request.getContextPath() %>/MainController" method="POST">
                    <input type="hidden" name="action" value="EditProfile">
                    <input type="hidden" name="userID" value="<%= loginUser.getUserID() %>">

                    <label class="form-label">Full Name</label>
                    <input type="text" name="fullName" class="form-input" value="<%= loginUser.getFullName() %>">

                    <div class="row">
                        <div class="col-md-6">
                            <label class="form-label">Email</label>
                            <div class="input-icon">
                                <i class="fas fa-envelope"></i>
                                <input type="text" name="email" class="form-input" value="<%= loginUser.getEmail() %>">
                            </div>
                            <div class="error"><%= userError.getEmail() %></div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Phone Number</label>
                            <div class="input-icon">
                                <i class="fas fa-mobile-alt"></i>
                                <input type="text" name="phone" class="form-input" value="<%= loginUser.getPhone() %>">
                            </div>
                            <div class="error"><%= userError.getPhone() %></div>
                        </div>
                    </div>

                    <label class="form-label">Address</label>
                    <div class="input-icon">
                        <i class="fas fa-map-marker-alt"></i>
                        <input type="text" name="address" class="form-input" value="<%= loginUser.getAddress() %>">
                    </div>

                    <div class="btn-group">
                        <a href="<%= request.getContextPath() %>/admin/admin.jsp" class="btn btn-outline">Cancel</a>
                        <a href="<%= request.getContextPath() %>/changePassword.jsp" class="btn btn-secondary">Change Password</a>
                        <button type="submit" class="btn btn-primary">Save Changes</button>
                    </div>
                </form>
            </div>
        </main>
    </body>
</html>