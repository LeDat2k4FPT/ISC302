<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Login</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/login.css">
    </head>
    <body>
        <img src="<%= request.getContextPath() %>/image/summit_logo.png" alt="Summit Spirit Logo" class="logo">
        <div class="login-box">
            <h2 class="login-title">LOGIN</h2>
            <%
                String message = (String) request.getAttribute("MESSAGE");
                if (message != null && !message.isEmpty()) {
            %>
            <p style="color:red;"><%= message %></p>
            <%
                }
            %>
            <form id="login-form" action="<%= request.getContextPath() %>/MainController" method="POST">
                <input type="email" class="form-control mb-3" name="email" placeholder="email" required>
                <input type="password" class="form-control mb-3" name="password" placeholder="password" required>

                <div class="g-recaptcha mb-3" data-sitekey="6LeToWArAAAAABMEYYO1tLmV18YTlfhk7C8Zt2GW"></div>
                <div id="error"></div>

                <button type="submit" name="action" value="Login" class="btn btn-login">Login</button>
                <div class="register-link">
                    Don’t have an account yet? <a href="<%= request.getContextPath() %>/user/createUser.jsp">Register here</a>
                </div>
                <div class="forgot-link">
                    <a href="<%= request.getContextPath() %>/forgotPassword.jsp">Forgot password</a>
                </div>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://www.google.com/recaptcha/api.js" async defer></script>
        <script type="text/javascript">
            document.getElementById("login-form").addEventListener("submit", function (e) {
                var error = document.getElementById("error");
                var response = grecaptcha.getResponse();
                if (response.length === 0) {
                    e.preventDefault();
                    error.textContent = "Please verify that you are not a robot.";
                } else {
                    error.textContent = "";
                }
            });
        </script>
    </body>
</html>
