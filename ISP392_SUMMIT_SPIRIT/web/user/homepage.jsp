<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, dao.ProductDAO, dto.ProductDTO, dto.UserDTO , dto.CartDTO" %>

<%
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null || !"User".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<ProductDTO> products = (List<ProductDTO>) request.getAttribute("PRODUCT_LIST");
    if (products == null || products.isEmpty()) {
        try {
            ProductDAO dao = new ProductDAO();
            products = dao.getTopNProductsByIDs(new int[]{1, 2, 3});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    List<ProductDTO> topSales = null;
    try {
        ProductDAO dao = new ProductDAO();
        List<ProductDTO> allProducts = dao.getTop3Products();
        Collections.shuffle(allProducts);
        topSales = allProducts.size() > 4 ? allProducts.subList(0, 4) : allProducts;
    } catch (Exception e) {
        e.printStackTrace();
    }

    List<ProductDTO> selectedProducts = new ArrayList<>();
    try {
        ProductDAO dao = new ProductDAO();
        int[] categoryIDs = {1, 2, 3}; // Shirts, Pants, Backpacks
        for (int cateID : categoryIDs) {
            List<ProductDTO> list = dao.getProductsByCategorySorted(cateID, "asc");
            if (!list.isEmpty()) {
                selectedProducts.add(list.get(0));
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    CartDTO cart = (CartDTO) session.getAttribute("CART");
    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Homepage - Summit Spirit</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/homepage.css">
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

        <!-- Hero Banner -->
        <div class="hero">
            <img src="<%= request.getContextPath() %>/image/hero_banner.png" alt="Hero Banner">
            <div class="hero-text">
                <h1>SUMMIT SPIRIT</h1>
                <p>Climb Higher, Live Brighter</p>
            </div>
        </div>

        <!-- Categories -->
        <div class="categories">
            <a href="<%= request.getContextPath() %>/user/category.jsp?category=ao">
                <img src="<%= request.getContextPath() %>/image/logo_ao.png" alt="Shirts">
                Shirts
            </a>
            <a href="<%= request.getContextPath() %>/user/category.jsp?category=quan">
                <img src="<%= request.getContextPath() %>/image/logo_quan.png" alt="Pants">
                Pants
            </a>
            <a href="<%= request.getContextPath() %>/user/category.jsp?category=balo">
                <img src="<%= request.getContextPath() %>/image/logo_balo.png" alt="Backpacks">
                Backpacks
            </a>
            <a href="<%= request.getContextPath() %>/user/category.jsp?category=dungcu">
                <img src="<%= request.getContextPath() %>/image/logo_tool.png" alt="Camping Tools">
                Camping Tools
            </a>
            <a href="<%= request.getContextPath() %>/user/category.jsp?category=trai">
                <img src="<%= request.getContextPath() %>/image/logo_leu.png" alt="Tents">
                Tents
            </a>
            <a href="<%= request.getContextPath() %>/user/category.jsp?category=mu">
                <i class="fa-solid fa-hat-cowboy"></i>
                Hats
            </a>
            <a href="<%= request.getContextPath() %>/user/category.jsp?category=camping">
                <i class="fa-solid fa-fire"></i>
                Camping Stove
            </a>
        </div>

        <!-- Main Content -->
        <div class="main">
            <h2>🔥 Top Sales</h2>
            <div class="product-list">
                <% if (topSales != null && !topSales.isEmpty()) {
            for (ProductDTO p : topSales) { %>
                <div class="product">
                    <a href="<%= request.getContextPath() %>/user/productDetail.jsp?id=<%= p.getProductID() %>">
                        <img src="<%= p.getProductImage() %>" alt="Product Image">
                    </a>
                    <h4><a href="<%= request.getContextPath() %>/user/productDetail.jsp?id=<%= p.getProductID() %>"><%= p.getProductName() %></a></h4>
                    <p><%= p.getDescription() %></p>
                    <strong><%= String.format("%,.0f", p.getPrice()) %> VND</strong>
                </div>
                <% }} else { %>
                <p>No products found in Top Sales.</p>
                <% } %>
            </div>
            <!-- Divider between sections -->
            <div class="thin-divider"></div>

            <h2>Featured Picks</h2>
            <div class="product-list">
                <% for (ProductDTO p : selectedProducts) { %>
                <div class="product">
                    <a href="<%= request.getContextPath() %>/user/productDetail.jsp?id=<%= p.getProductID() %>">
                        <img src="<%= p.getProductImage() %>" alt="Product Image">
                    </a>
                    <h4><a href="<%= request.getContextPath() %>/user/productDetail.jsp?id=<%= p.getProductID() %>"><%= p.getProductName() %></a></h4>
                    <p><%= p.getDescription() %></p>
                    <strong><%= String.format("%,.0f", p.getPrice()) %> VND</strong>
                </div>
                <% } %>
            </div>

            <!-- Sales Off -->
            <h2>Sales Off</h2>
            <a href="<%= request.getContextPath() %>/MainController?action=viewSaleOff">
                <div class="promotion-program">
                    <img src="<%= request.getContextPath() %>/image/km_banner_1.png">
                </div>
            </a>
        </div>
                <%
            String message = (String) request.getAttribute("MESSAGE");
            if (message == null) message = "";
        %>
        <%= message %>

        <jsp:include page="/user/chatbot.jsp" />
       
    </body>
</html>
