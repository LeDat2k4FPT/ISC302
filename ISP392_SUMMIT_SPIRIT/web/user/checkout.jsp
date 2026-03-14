<%--
    Document   : checkout
    Created on : Jul 3, 2025, 9:11:24 PM
    Author     : gmt
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="dto.UserDTO, dao.ProductDAO, dto.ProductDTO, dto.CartDTO"%>
<%@page import="java.util.List, java.util.ArrayList, java.util.Map" %>
<%@page import="dao.ProductVariantDAO" %>
<%@page import="dto.VoucherDTO, dao.VoucherDAO" %>
<%--<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>--%>

<%
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");

    double discountPercent = 0.0;
    String discountCode = "";
    String discountError = null;

    List<String> sizeList = new ArrayList<>();
    List<String> colorList = new ArrayList<>();

    String paramProductId = request.getParameter("productId");
    String paramSize = request.getParameter("size");
    String paramColor = request.getParameter("color");
    String paramQuantity = request.getParameter("quantity");

    boolean hasSaleOffProduct = false;
    boolean isRetry = request.getParameter("retry") != null;

    ProductDTO product = null;
    int quantity = 1;

    CartDTO cart = (CartDTO) session.getAttribute("CART");
    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;

    // Nếu là retry từ giỏ hàng, không xử lý buy now
    if (session.getAttribute("PENDING_ORDER") != null && isRetry) {
        Map<String, Object> orderSession = (Map<String, Object>) session.getAttribute("PENDING_ORDER");

        paramProductId = String.valueOf(orderSession.get("productId"));
        paramSize = (String) orderSession.get("size");
        paramColor = (String) orderSession.get("color");
        paramQuantity = String.valueOf(orderSession.get("quantity"));
        hasSaleOffProduct = Boolean.parseBoolean(String.valueOf(orderSession.get("fromSaleOff")));
        discountCode = (String) orderSession.get("discountCode");
        discountPercent = Double.parseDouble(String.valueOf(orderSession.getOrDefault("discountPercent", "0")));

        // Không set BUY_NOW_PRODUCT nếu là từ giỏ hàng
        session.removeAttribute("BUY_NOW_PRODUCT");
        session.removeAttribute("FROM_BUY_NOW");
    } else {
        String code = request.getParameter("discountCode");
        if (code != null && !code.trim().isEmpty()) {
            VoucherDAO dao = new VoucherDAO();
            VoucherDTO voucher = dao.getVoucherByCode(code);
            if (voucher != null) {
                discountCode = voucher.getVoucherCode();
                discountPercent = voucher.getDiscountValue();
                request.setAttribute("VOUCHER_ID", voucher.getVoucherID());
            } else {
                discountError = "Invalid or expired code!";
            }
        }
    }

    if (paramProductId != null && !paramProductId.isEmpty()) {
        try {
            int productId = Integer.parseInt(paramProductId);
            ProductVariantDAO variantDAO = new ProductVariantDAO();
            sizeList = variantDAO.getAvailableSizesByProductId(productId);
            colorList = variantDAO.getAvailableColorsByProductId(productId);

            ProductDAO productDAO = new ProductDAO();
            product = productDAO.getProductByID(productId);

            try {
                quantity = Integer.parseInt(paramQuantity);
            } catch (NumberFormatException e) {
                quantity = 1;
            }

            if (product != null) {
                if (paramSize != null) product.setSize(paramSize);
                if (paramColor != null) product.setColor(paramColor);
                product.setQuantity(quantity);
                boolean fromSaleOff = hasSaleOffProduct || "true".equals(request.getParameter("fromSaleOff"));
                product.setFromSaleOff(fromSaleOff);

                // Nếu không phải retry từ giỏ hàng thì set BUY_NOW_PRODUCT
                if (!isRetry && request.getParameter("fromCart") == null) {
                    session.setAttribute("BUY_NOW_PRODUCT", product);
                    session.setAttribute("FROM_BUY_NOW", true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String country = request.getParameter("country") != null ? request.getParameter("country") : "";
    String fullname = request.getParameter("fullname") != null ? request.getParameter("fullname") : "";
    String phone = request.getParameter("phone") != null ? request.getParameter("phone") : "";
    String email = request.getParameter("email") != null ? request.getParameter("email") : "";
    String address = request.getParameter("address") != null ? request.getParameter("address") : "";
    String district = request.getParameter("district") != null ? request.getParameter("district") : "";
    String city = request.getParameter("city") != null ? request.getParameter("city") : "";
    Integer voucherID = (Integer) request.getAttribute("VOUCHER_ID");

    if (session.getAttribute("PENDING_ORDER") != null && isRetry) {
        Map<String, Object> orderSession = (Map<String, Object>) session.getAttribute("PENDING_ORDER");
        country = (String) orderSession.get("country");
        fullname = (String) orderSession.get("fullName");
        phone = (String) orderSession.get("phone");
        email = (String) orderSession.get("email");
        address = (String) orderSession.get("address");
        district = (String) orderSession.get("district");
        city = (String) orderSession.get("city");
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Checkout Page</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/checkout.css">
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

        <% if (request.getParameter("retry") != null) { %>
        <div style="padding: 10px; background-color: #ffdddd; border: 1px solid red; color: red; margin: 20px;">
            ⚠️ Payment transaction failed. Please check the information and pay again.
        </div>
        <% } %>

        <form id="checkoutForm" action="payment" method="POST" onsubmit="return validateForm()">
            <input type="hidden" name="checkoutType" value="<%= session.getAttribute("BUY_NOW_PRODUCT") != null ? "BUY_NOW" : "CART" %>">
            <div class="container">
                <div class="form-section">
                    <h3>Shipping Address</h3>
                    <input type="text" name="country" placeholder="Country/Region" value="<%= country %>" required>
                    <div class="row">
                        <input type="text" name="fullname" placeholder="Full Name" value="<%= fullname %>" required>
                        <input type="text" name="phone" placeholder="Phone" value="<%= phone %>" required>
                        <div class="error-message" id="phoneError"></div>
                    </div>
                    <input type="email" name="email" placeholder="Email" value="<%= email %>" required>
                    <div class="error-message" id="emailError"></div>
                    <input type="text" name="address" placeholder="Address" value="<%= address %>" required>
                    <div class="row">
                        <input type="text" name="district" placeholder="District" value="<%= district %>" required>
                        <input type="text" name="city" placeholder="City" value="<%= city %>" required>
                    </div>

                    <% if (!hasSaleOffProduct) { %>
                    <div class="row">
                        <input type="hidden" name="sourcePage" value="user/checkout.jsp">
                        <input type="hidden" name="productId" value="<%= paramProductId %>">
                        <input type="hidden" name="quantity" value="<%= paramQuantity %>">
                        <input type="text" name="discountCode" placeholder="Discount code" value="<%= discountCode %>">
                        <button type="submit" class="apply-btn" name="action" value="ApplyDiscount" onclick="setFormAction('<%= request.getContextPath() %>/MainController')">Apply</button>
                    </div>
                    <% if (discountError != null) { %>
                    <div class="error-message"><%= discountError %></div>
                    <% } else if (!discountCode.trim().isEmpty() && discountPercent > 0) { %>
                    <div class="success-message">Discount code has been applied!</div>
                    <% } %>
                    <% } else { %>
                    <div class="error-message">You cannot apply the discount code because the product is already discounted.</div>
                    <% } %>

                    <div class="footer-buttons">
                        <a href="javascript:history.back()" class="back-btn">← Back</a>
                        <% if (product != null) {
                            double total = product.getPrice() * quantity;
                            double shipFee = 30000;
                            double discountAmount = total * discountPercent / 100;
                            double grandTotal = total + shipFee - discountAmount;
                        %>
                        <input type="hidden" name="amount" value="<%= (int) grandTotal %>">
                        <input type="hidden" name="productName" value="<%= product.getProductName() %>">
                        <input type="hidden" name="size" value="<%= product.getSize() != null ? product.getSize() : "N/A" %>">
                        <input type="hidden" name="color" value="<%= product.getColor() != null ? product.getColor() : "N/A" %>">
                        <input type="hidden" name="productId" value="<%= paramProductId %>">
                        <input type="hidden" name="quantity" value="<%= paramQuantity %>">
                        <input type="hidden" name="fromSaleOff" value="<%= hasSaleOffProduct %>">
                        <input type="hidden" name="discountCode" value="<%= discountCode != null ? discountCode : "" %>">
                        <input type="hidden" name="discountPercent" value="<%= discountPercent %>">
                        <% if (voucherID != null) { %>
                        <input type="hidden" name="voucherID" value="<%= voucherID %>">
                        <% } %>
                        <% } %>
                        <button type="submit" name="action" value="CheckoutVNPay" class="pay-btn" onclick="setFormAction('<%= request.getContextPath() %>/payment')">Continue To Pay</button>
                    </div>
                </div>

                <div class="cart-preview">
                    <h3>Your Order</h3>
                    <% if (product != null) {
                            double total = product.getPrice() * quantity;
                            double shipFee = 30000;
                            double discountAmount = total * discountPercent / 100;
                            double grandTotal = total + shipFee - discountAmount;
                    %>
                    <div class="cart-item">
                        <img src="<%= product.getProductImage() %>" alt="">
                        <div class="cart-info">
                            <h4><%= product.getProductName() %></h4>
                            <% if (product.getSize() != null && !product.getSize().isEmpty()) { %>
                            <p>Size: <%= product.getSize() %></p>
                            <% } %>
                            <% if (product.getColor() != null && !product.getColor().isEmpty()) { %>
                            <p>Color: <%= product.getColor() %></p>
                            <% } %>
                            <div class="quantity-box"><span><%= quantity %></span></div>
                        </div>
                        <div><%= String.format("%,.0f", product.getPrice()) %></div>
                    </div>
                    <div class="summary-line">Subtotal: <span><%= String.format("%,.0f", total) %></span></div>
                    <div class="summary-line">Ship: <span><%= String.format("%,.0f", shipFee) %></span></div>
                    <div class="summary-line">Voucher: <span><%= discountPercent > 0 ? "-" + String.format("%,.0f", discountAmount) : "0" %></span></div>
                    <div class="summary-line total-line">Total: <span><%= String.format("%,.0f", grandTotal) %></span></div>
                    <% } %>
                </div>
            </div>
        </form>

        <script>
            function setFormAction(actionUrl) {
                document.getElementById('checkoutForm').action = actionUrl;
            }

            function validateForm() {
                let valid = true;
                document.getElementById('phoneError').innerText = "";
                document.getElementById('emailError').innerText = "";

                const phone = document.querySelector('input[name="phone"]').value.trim();
                const phonePattern = /^0\d{9}$/;
                const email = document.querySelector('input[name="email"]').value.trim();
                const emailPattern = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$/;
                if (!phonePattern.test(phone)) {
                    document.getElementById('phoneError').innerText = "Invalid phone number format!";
                    valid = false;
                }
                if (!emailPattern.test(email)) {
                    document.getElementById('emailError').innerText = "Invalid email format!";
                    valid = false;
                }
                return valid;
            }
        </script>
    </body>
</html>

