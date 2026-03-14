<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="dto.CartDTO, dto.UserDTO" %>
<!DOCTYPE html>
<%
    CartDTO cart = (CartDTO) session.getAttribute("CART");
    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>VNPay Payment Result</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css"
              integrity="sha512-z3gLpd7yknf1YoNbCzqRKc4qyor8gaKU1qmn+CShxbuBusANI9QpRohGBreCFkKxLhei6S9CQXFEbbKuqLg0DA=="
              crossorigin="anonymous" referrerpolicy="no-referrer" />
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/paymentResult.css">
    </head>
    <body>
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
                    <div class="user-name" onclick="toggleMenu()"><i class="fas fa-user"></i></div>
                    <div id="dropdown" class="dropdown-menu">
                        <a href="<%= request.getContextPath()%>/user/profile.jsp"><%= loginUser != null ? loginUser.getFullName() : "Account"%></a>
                        <a href="<%= request.getContextPath()%>/MainController?action=Logout">Logout</a>
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
        <section>
            <div>
                <img src="https://cdn2.cellphones.com.vn/insecure/rs:fill:150:0/q:90/plain/https://cellphones.com.vn/media/wysiwyg/Review-empty.png"
                     alt="Transaction Status">
            </div>

            <%
                String paymentResult = (String) request.getAttribute("paymentResult");
                String message = (String) request.getAttribute("message");
                String transactionId = (String) request.getAttribute("transactionId");
                String amount = (String) request.getAttribute("amount");
                String orderInfo = (String) request.getAttribute("orderInfo");
                String retryLink = (String) request.getAttribute("retryLink");
            %>

            <% if ("success".equals(paymentResult)) { %>
            <div>
                <div class="success-text text-center">
                    <p class="fs-5 fw-semibold">
                        Your transaction has been successful!
                        <i class="fas fa-check-circle"></i>
                    </p>
                    <p class="text-success">
                        🌄 Thank you for shopping at Summit Spirit!
                    </p>
                </div>
                <% if (transactionId != null) {%>
                <p>Transaction code: <b><%= transactionId%></b></p>
                <% } %>
                <% if (amount != null) {%>
                <p>Amount: <b><%= amount%></b></p>
                <% } %>
                <% if (orderInfo != null) {%>
                <p>Order information: <b><%= orderInfo%></b></p>
                <% } %>

                <div class="text-center mt-3">
                    <a href="UserOrderHistoryController" class="back-link">View Order History</a>
                </div>
            </div>
            <% } else if ("failed".equals(paymentResult)) { %>
            <div>
                <h3 class="failed">
                    Transaction order failed!
                </h3>
                <%
                    String fallbackLink = request.getContextPath() + "/user/checkout.jsp";
                    String finalLink = (retryLink != null) ? retryLink : fallbackLink;
                %>
                <a href="<%= finalLink%>" class="btn-retry">🔁 Back to payment</a>
            </div>
            <% } else if ("invalid".equals(paymentResult)) { %>
            <div>
                <h3 class="invalid">
                    Invalid signature! Data may have been modified.
                </h3>
                <%
                    String fallbackLink = request.getContextPath() + "/user/checkout.jsp";
                    String finalLink = (retryLink != null) ? retryLink : fallbackLink;
                %>
                <a href="<%= finalLink%>" class="btn-retry">🔁 Back to payment</a>
            </div>
            <% } else if ("error".equals(paymentResult)) { %>
            <div>
                <h3 class="error">
                    An error occurred while processing the transaction!
                </h3>
                <%
                    String fallbackLink = request.getContextPath() + "/user/checkout.jsp";
                    String finalLink = (retryLink != null) ? retryLink : fallbackLink;
                %>
                <a href="<%= request.getContextPath()%>/MainController?action=Checkout"
                   class="btn-retry">
                    🔁 Back to payment
                </a>            </div>
                <% } else { %>
            <div>
                <h3 class="invalid">
                    We have received your order, please wait for processing!
                </h3>
            </div>
            <% }%>
        </section>
    </body>
</html>
