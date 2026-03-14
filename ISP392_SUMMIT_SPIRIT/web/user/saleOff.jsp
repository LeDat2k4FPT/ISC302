<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, dto.ProductDTO, dto.CartDTO, dto.UserDTO"%>
<%
    List<ProductDTO> saleProducts = (List<ProductDTO>) request.getAttribute("SALE_PRODUCTS");
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    CartDTO cart = (CartDTO) session.getAttribute("CART");
    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Sale Off - Summit Spirit</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/saleOff.css">
    </head>
    <body>
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


        <!-- Main -->
        <div class="promotion-wrapper">
            <div class="promotion-banner">
                <img src="image/km_banner_1.png" alt="Banner khuyến mãi">
            </div>
        </div>

        <h1>🎉 20% Discounted Products 🎉</h1>

        <% if (saleProducts != null && !saleProducts.isEmpty()) { %>
        <div class="product-grid">
            <% for (ProductDTO p : saleProducts) {
                double originalPrice = p.getPrice(); // giá từ DB
                double finalPrice = Math.round(originalPrice * 0.8); // giá đã giảm 20%
            %>
            <div class="product">
                <a href="<%= request.getContextPath() %>/user/productDetail.jsp?id=<%= p.getProductID() %>&fromSaleOff=true">
                    <img src="<%= p.getProductImage() %>" alt="Product Image">
                </a>
                <h4><a href="<%= request.getContextPath() %>/user/productDetail.jsp?id=<%= p.getProductID() %>&fromSaleOff=true"><%= p.getProductName() %></a></h4>
                <p class="original-price"><%= String.format("%,.0f", originalPrice) %> VND</p>
                <p class="discount-price"><%= String.format("%,.0f", finalPrice) %> VND (−20%)</p>

            </div>
            <% } %>
        </div>
        <% } else { %>
        <p style="text-align:center;">No discounted products available.</p>
        <% } %>
    </body>
</html>
