<%@page import="dto.UserDTO, dto.CartDTO" %>
<%@page import="user.UserError" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>User Profile</title>
        <meta charset="UTF-8">
        <!-- CSS & Icons -->
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" >
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/profile.css">
    </head>
    <body>
        <%
            UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
            if (loginUser == null || !"User".equals(loginUser.getRole())) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }
            UserError userError = (UserError) request.getAttribute("USER_ERROR");
            if (userError == null) userError = new UserError();
            CartDTO cart = (CartDTO) session.getAttribute("CART");
            int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;
        %>


        <!-- Header -->
        <div class="header">
            <a href="<%= request.getContextPath() %>/user/homepage.jsp">
                <img src="<%= request.getContextPath() %>/image/summit_logo.png" alt="Logo">
            </a>
            <div class="nav-links">
                <a href="<%= request.getContextPath() %>/user/homepage.jsp"><i class="fas fa-home"></i></a>
                <a href="<%= request.getContextPath() %>/user/cart.jsp" class="cart-icon">
                    <i class="fas fa-shopping-cart"></i>
                    <% if (cartItemCount > 0) { %>
                    <span class="cart-badge"><%= cartItemCount %></span>
                    <% } %>
                </a>
                <div class="user-dropdown">
                    <div class="user-name" onclick="toggleMenu()"><i class="fas fa-user"></i></div>
                    <div id="dropdown" class="dropdown-menu">
                        <a href="<%= request.getContextPath() %>/user/profile.jsp"><%= loginUser.getFullName() %></a>
                        <a href="<%= request.getContextPath() %>/MainController?action=Logout">Logout</a>
                    </div>
                </div>
            </div>
        </div>
        <script>
            function toggleMenu() {
                const menu = document.getElementById("dropdown");
                menu.style.display = menu.style.display === "block" ? "none" : "block";
            }
            document.addEventListener("click", function (event) {
                const dropdown = document.getElementById("dropdown");
                const userBtn = document.querySelector(".user-name");
                if (!dropdown.contains(event.target) && !userBtn.contains(event.target)) {
                    dropdown.style.display = "none";
                }
            });
        </script>


        <!-- Main content -->
        <div class="wrapper d-flex">
            <!-- Sidebar -->
            <aside class="sidebar">
                <nav class="menu">
                    <a href="<%= request.getContextPath() %>/user/profile.jsp" class="menu-link active">My Profile</a>
                    <a href="<%= request.getContextPath() %>/UserOrderHistoryController" class="menu-link">Orders History</a>
                </nav>
            </aside>

            <!-- Profile form -->
            <main class="main-content">
                <div class="profile-form">
                    <h2 class="title">User Profile</h2>
                    <h4 class="username"><%= loginUser.getFullName().toUpperCase() %></h4>

                    <form action="<%= request.getContextPath() %>/MainController" method="POST">
                        <input type="hidden" name="action" value="EditProfile">
                        <input type="hidden" name="userID" value="<%= loginUser.getUserID()%>">

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
                            <a href="<%= request.getContextPath() %>/user/homepage.jsp" class="btn btn-outline">Cancel</a>
                            <a href="<%= request.getContextPath() %>/changePassword.jsp" class="btn btn-secondary">Change Password</a>
                            <button type="submit" class="btn btn-primary">Save Changes</button>
                        </div>
                    </form>
                </div>
            </main>
        </div>
    </body>
