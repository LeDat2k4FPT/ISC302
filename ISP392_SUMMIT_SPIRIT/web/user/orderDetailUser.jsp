<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.*" %>
<%@ page import="dto.OrderDTO" %>
<%@ page import="dto.OrderDetailDTO" %>
<%@ page import="dto.UserAddressDTO" %>
<%@ page import="dao.OrderDAO, dto.UserDTO , dto.CartDTO" %>
<%@ page import="java.text.NumberFormat" %>

<%
     UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null || !"User".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    CartDTO cart = (CartDTO) session.getAttribute("CART");
    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;
    int orderID = 0;
    try {
        String paramOrderID = request.getParameter("orderID");
        if (paramOrderID != null) {
            orderID = Integer.parseInt(paramOrderID);
        }
    } catch (NumberFormatException e) {
        orderID = 0;
    }

    OrderDAO dao = new OrderDAO();
    OrderDTO order = dao.getOrderById(orderID);
    List<OrderDetailDTO> orderDetails = dao.getOrderDetails(orderID);
    UserAddressDTO userInfo = dao.getUserAddressInfoByOrderId(orderID);

    NumberFormat nf = NumberFormat.getInstance();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Order Details</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/orderDetail.css">
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
        <button class="btn" onclick="history.back()">← Back</button>
        <div class="container">
            <% if (order == null) { %>
            <div class="alert">
                <h2>🚫 Order Not Found!</h2>
                <p>No order exists with ID: <%= orderID %></p>
                <button class="btn" onclick="history.back()">← Back</button>
            </div>
            <% } else { %>
            <div class="order-card">
                <h2>Order #<%= order.getOrderID() %></h2>
                <div class="order-info">
                    <% if (userInfo != null) { %>
                    <div><strong>Customer:</strong> <%= userInfo.getFullName() %></div>
                    <div><strong>Email:</strong> <%= userInfo.getEmail() %></div>
                    <div><strong>Phone:</strong> <%= userInfo.getPhone() %></div>
                    <div><strong>Address:</strong> <%= userInfo.getAddress() %>, <%= userInfo.getDistrict() %>,
                        <%= userInfo.getCity() %>, <%= userInfo.getCountry() %></div>
                        <% } else { %>
                    <div><strong>Customer:</strong> <i>Not provided yet</i></div>
                    <div><strong>Email:</strong> <i>Not provided yet</i></div>
                    <div><strong>Phone:</strong> <i>Not provided yet</i></div>
                    <div><strong>Address:</strong> <i>Not provided yet</i></div>
                    <% } %>
                    <div><strong>Date:</strong> <%= order.getOrderDate() %></div>
                    <div>
                        <strong>Status:</strong>
                        <%
                            String status = order.getStatus();
                            String badgeClass = "status-badge";
                            if ("Shipped".equalsIgnoreCase(status)) badgeClass += " status-shipped";
                            else if ("Processing".equalsIgnoreCase(status)) badgeClass += " status-processing";
                            else if ("Cancelled".equalsIgnoreCase(status)) badgeClass += " status-canceled";
                            else if ("Delivered".equalsIgnoreCase(status)) badgeClass += " status-delivered";
                        %>
                        <span class="<%= badgeClass %>"><%= status %></span>
                    </div>
                    <div><strong>Total:</strong> <%= nf.format(order.getTotalAmount()) %> ₫</div>
                </div>
            </div>

            <h3>📦 Products</h3>
            <div class="products-list">
                <% for (OrderDetailDTO od : orderDetails) { %>
                <div class="product-box">
                    <p><strong>Product ID:</strong> <%= od.getProductID() %></p>
                    <p><strong>Name:</strong> <%= od.getProductName() %></p>
                    <p><strong>Size:</strong> <%= od.getSizeName() != null ? od.getSizeName() : "N/A" %></p>
                    <p><strong>Color:</strong> <%= od.getColorName() != null ? od.getColorName() : "N/A" %></p>
                    <p><strong>Quantity:</strong> <%= od.getQuantity() %></p>
                    <p><strong>Unit Price:</strong> <%= nf.format(od.getUnitPrice()) %> ₫</p>

                    <%-- ✅ Thêm nút Review nếu đơn hàng đã giao --%>
                    <% if ("Delivered".equalsIgnoreCase(order.getStatus())) {
                           String fullAddress = userInfo != null ? userInfo.getAddress() + ", " + userInfo.getDistrict() + ", " + userInfo.getCity() + ", " + userInfo.getCountry() : "";
                    %>
                    <form action="<%= request.getContextPath() %>/user/rating.jsp" method="get">
                        <input type="hidden" name="orderID" value="<%= order.getOrderID() %>">
                        <input type="hidden" name="productID" value="<%= od.getProductID() %>">
                        <input type="hidden" name="productName" value="<%= od.getProductName() %>">
                        <input type="hidden" name="size" value="<%= od.getSizeName() %>">
                        <input type="hidden" name="color" value="<%= od.getColorName() %>">
                        <input type="hidden" name="quantity" value="<%= od.getQuantity() %>">
                        <input type="hidden" name="unitPrice" value="<%= nf.format(od.getUnitPrice()) %>">

                        <%-- ✅ Thêm thông tin người dùng và đơn hàng --%>
                        <input type="hidden" name="fullName" value="<%= userInfo != null ? userInfo.getFullName() : "" %>">
                        <input type="hidden" name="email" value="<%= userInfo != null ? userInfo.getEmail() : "" %>">
                        <input type="hidden" name="phone" value="<%= userInfo != null ? userInfo.getPhone() : "" %>">
                        <input type="hidden" name="address" value="<%= fullAddress %>">
                        <input type="hidden" name="orderDate" value="<%= order.getOrderDate() %>">
                        <input type="hidden" name="status" value="<%= order.getStatus() %>">

                        <button type="submit" class="btn btn-review">Review</button>
                    </form>
                    <% } %>

                </div>
                <% } %>
            </div>

            <% } %>
        </div>
    </body>
