<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, dao.ProductDAO, dto.ProductDTO, dto.CartDTO, dto.UserDTO" %>

<%
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null || !"User".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    String category = request.getParameter("category");
    String sort = request.getParameter("sort");
    String keyword = request.getParameter("keyword");
    if (sort == null) {
        sort = "asc";
    }

    List<ProductDTO> products = new ArrayList<>();
    int cateID = 0;
    String categoryName = "";

    switch (category) {
        case "quan":
            cateID = 1;
            categoryName = "Pants";
            break;
        case "ao":
            cateID = 2;
            categoryName = "Shirts";
            break;
        case "balo":
            cateID = 3;
            categoryName = "Backpacks";
            break;
        case "dungcu":
            cateID = 4;
            categoryName = "Camping Tools";
            break;
        case "trai":
            cateID = 5;
            categoryName = "Tents";
            break;
        case "mu":
            cateID = 6;
            categoryName = "Hats";
            break;
        case "camping":
            cateID = 7;
            categoryName = "Camping Stove";
            break;
        default:
            categoryName = "Unknown";
            break;
    }

    try {
        ProductDAO dao = new ProductDAO();
        List<ProductDTO> all = dao.getProductsByCategorySorted(cateID, sort);
        if (keyword != null && !keyword.trim().isEmpty()) {
            for (ProductDTO p : all) {
                if (p.getProductName().toLowerCase().contains(keyword.toLowerCase())) {
                    products.add(p);
                }
            }
        } else {
            products = all;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    CartDTO cart = (CartDTO) session.getAttribute("CART");
    int totalQuantity = (cart != null) ? cart.getTotalQuantity() : 0;
    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Category - <%= categoryName%></title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath()%>/css/category.css">

        <style>
            .sort-options {
                margin: 20px 0;
                display: flex;
                align-items: center;
                gap: 12px;
            }

            .sort-options form {
                margin: 0;
            }

            .sort-label {
                font-size: 16px;
                color: #333;
                white-space: nowrap;
            }

            .sort-select {
                padding: 10px 16px;
                border: 1px solid #ccc;
                border-radius: 24px;
                outline: none;
                min-width: 180px;
                font-size: 15px;
                background: #fff;
                cursor: pointer;
            }
        </style>
    </head>
    <body>

        <!-- Header -->
        <div class="header">
            <a href="<%= request.getContextPath()%>/user/homepage.jsp">
                <img src="<%= request.getContextPath()%>/image/summit_logo.png" alt="Logo">
            </a>
            <div class="nav-links">
                <a href="<%= request.getContextPath()%>/user/homepage.jsp"><i class="fas fa-home"></i></a>
                <a href="<%= request.getContextPath()%>/user/cart.jsp" class="cart-icon">
                    <i class="fas fa-shopping-cart"></i>
                    <% if (cartItemCount > 0) {%>
                    <span class="cart-badge"><%= cartItemCount%></span>
                    <% }%>
                </a>
                <div class="user-dropdown">
                    <div class="user-name" onclick="toggleMenu()"><i class="fas fa-user"></i>
                        <div id="dropdown" class="dropdown-menu">
                            <a href="<%= request.getContextPath()%>/user/profile.jsp"><%= loginUser.getFullName()%></a>
                            <a href="<%= request.getContextPath()%>/MainController?action=Logout">Logout</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Toggle Dropdown Script -->
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

        <!-- Layout with sidebar and main content -->
        <div class="layout">
            <div class="categories">
                <h3 class="menu-title">Category</h3>
                <a href="<%= request.getContextPath()%>/user/category.jsp?category=ao" class="<%= "ao".equals(category) ? "active" : ""%>">Shirts</a>
                <a href="<%= request.getContextPath()%>/user/category.jsp?category=quan" class="<%= "quan".equals(category) ? "active" : ""%>">Pants</a>
                <a href="<%= request.getContextPath()%>/user/category.jsp?category=balo" class="<%= "balo".equals(category) ? "active" : ""%>">Backpacks</a>
                <a href="<%= request.getContextPath()%>/user/category.jsp?category=dungcu" class="<%= "dungcu".equals(category) ? "active" : ""%>">Camping Tools</a>
                <a href="<%= request.getContextPath()%>/user/category.jsp?category=trai" class="<%= "trai".equals(category) ? "active" : ""%>">Tents</a>
                <a href="<%= request.getContextPath()%>/user/category.jsp?category=mu" class="<%= "mu".equals(category) ? "active" : ""%>">Hats</a>
                <a href="<%= request.getContextPath()%>/user/category.jsp?category=camping" class="<%= "camping".equals(category) ? "active" : ""%>">Camping Stove</a>
            </div>

            <div class="main">
                <!-- Search -->
                <form class="search-bar" action="<%= request.getContextPath()%>/user/category.jsp" method="get">
                    <input type="hidden" name="category" value="<%= category%>" />
                    <input type="hidden" name="sort" value="<%= sort%>" />
                    <input type="text" name="keyword" placeholder="Search for products..." value="<%= keyword != null ? keyword : ""%>">
                    <button type="submit"><i class="fa fa-search"></i></button>
                </form>

                <!-- Sort Options -->
                <div class="sort-options">
                    <span class="sort-label">Sort by Price:</span>
                    <form method="get" action="<%= request.getContextPath()%>/user/category.jsp">
                        <input type="hidden" name="category" value="<%= category != null ? category : ""%>">
                        <input type="hidden" name="keyword" value="<%= keyword != null ? keyword : ""%>">

                        <select name="sort" onchange="this.form.submit()" class="sort-select">
                            <option value="asc" <%= "asc".equals(sort) ? "selected" : ""%>>Low to High</option>
                            <option value="desc" <%= "desc".equals(sort) ? "selected" : ""%>>High to Low</option>
                        </select>
                    </form>
                </div>

                <h2>Category: <%= categoryName%></h2>

                <div class="product-list">
                    <%
                        if (products != null && !products.isEmpty()) {
                            for (ProductDTO p : products) {
                    %>
                    <div class="product">
                        <a href="<%= request.getContextPath()%>/user/productDetail.jsp?id=<%= p.getProductID()%>">
                            <img src="<%= p.getProductImage()%>" alt="Product Image">
                        </a>
                        <h4><a href="<%= request.getContextPath()%>/user/productDetail.jsp?id=<%= p.getProductID()%>"><%= p.getProductName()%></a></h4>
                        <p><%= p.getDescription()%></p>
                        <strong><%= String.format("%,.0f", p.getPrice())%> VND</strong>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <p>No products found in this category.</p>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>
        <jsp:include page="/common/chatbot.jsp" />
    </body>
</html>