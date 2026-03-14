<%@page import="user.UserError"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Register</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/register.css">
    </head>
    <body>
        <div class="logo" >
            <a href="<%= request.getContextPath() %>/user/homepage.jsp">
                <img src="<%= request.getContextPath() %>/image/summit_logo.png" alt="Logo">
            </a>
        </div>

        <div class="container register-box">
            <h2>Welcome to Summit Spirit</h2>
            <h5>Register your account</h5>
            <%
                String message = (String) request.getAttribute("MESSAGE");
                if (message != null && !message.isEmpty()) {
            %>
            <p class="text-danger text-center"><%= message %></p>
            <%
                }
                UserError userError = (UserError) request.getAttribute("USER_ERROR");
                if (userError == null) {
                    userError = new UserError();
                }
            %>
            <%
                if (userError.getErrorMessage() != null && !userError.getErrorMessage().isEmpty()) {
            %>
            <p class="text-danger text-center"><%= userError.getErrorMessage() %></p>
            <%
                }
            %>
            <form id="create-form" action="<%= request.getContextPath() %>/MainController" method="POST">
                <div class="row g-3">
                    <div class="col-md-6">
                        <label for="fullName" class="form-label">Full Name</label>
                        <input type="text" class="form-control" id="fullName" name="fullName"
                               value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : "" %>" required>
                    </div>
                    <div class="col-md-6">
                        <label for="address" class="form-label">Address</label>
                        <input type="text" class="form-control" id="address" name="address"
                               value="<%= request.getAttribute("address") != null ? request.getAttribute("address") : "" %>" required>
                    </div>
                    <div class="col-md-6">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email"
                               value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>" required>
                        <div class="text-danger"><%= userError.getEmail() %></div>
                    </div>
                    <div class="col-md-6">
                        <label for="phone" class="form-label">Phone Number</label>
                        <input type="text" class="form-control" id="phone" name="phone" minlength="10" maxlength="11"
                               value="<%= request.getAttribute("phone") != null ? request.getAttribute("phone") : "" %>" required>
                        <div class="text-danger"><%= userError.getPhone() %></div>
                    </div>
                    <div class="col-md-6">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password"
                               value="<%= request.getAttribute("password") != null ? request.getAttribute("password") : "" %>" required>
                        <div class="text-danger"><%= userError.getPassword() %></div>
                    </div>
                    <div class="col-md-6">
                        <label for="confirm" class="form-label">Confirm Password</label>
                        <input type="password" class="form-control" id="confirm" name="confirm"
                               value="<%= request.getAttribute("confirm") != null ? request.getAttribute("confirm") : "" %>" required>
                        <div class="text-danger"><%= userError.getConfirm() %></div>
                    </div>
                    <input type="hidden" name="role" value="User">
                </div>

                <div class="mt-4 text-center">
                    <div class="g-recaptcha mb-2" data-sitekey="6LeToWArAAAAABMEYYO1tLmV18YTlfhk7C8Zt2GW"></div>
                    <div id="error" class="text-danger"></div>
                    <button type="submit" name="action" value="CreateUser" class="btn btn-register mt-2">Create Account</button>
                </div>

                <div class="already">
                    Already have an account? <a href="<%= request.getContextPath() %>/login.jsp">Log In</a>
                </div>
            </form>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://www.google.com/recaptcha/api.js" async defer></script>
        <script type="text/javascript">
            document.getElementById("create-form").addEventListener("submit", function (e) {
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
