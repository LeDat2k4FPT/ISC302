<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.text.DecimalFormat, dto.CartDTO, dto.CartItemDTO, dto.ProductDTO, dto.UserDTO" %>
<%@ page import="dto.VoucherDTO, dao.VoucherDAO" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>

<%
    CartDTO cart = (CartDTO) session.getAttribute("CART");
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");

    // Khai báo 1 lần duy nhất
    String country = "", fullname = "", phone = "", email = "", address = "", district = "", city = "", discountCode = "";
    double discountPercent = 0.0;
    String discountError = null;

    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;

    // Nhận từ request
    Double appliedDiscountPercent = (Double) request.getAttribute("DISCOUNT_PERCENT");
    String appliedDiscountCode = (String) request.getAttribute("DISCOUNT_CODE");
    String discountErrorMsg = (String) request.getAttribute("DISCOUNT_ERROR");

    // Nhận từ session nếu có đơn đang chờ
    Map<String, Object> pendingOrder = (Map<String, Object>) session.getAttribute("PENDING_ORDER");
    if (pendingOrder != null) {
        country = (String) pendingOrder.getOrDefault("country", country);
        fullname = (String) pendingOrder.getOrDefault("fullName", fullname);
        phone = (String) pendingOrder.getOrDefault("phone", phone);
        email = (String) pendingOrder.getOrDefault("email", email);
        address = (String) pendingOrder.getOrDefault("address", address);
        district = (String) pendingOrder.getOrDefault("district", district);
        city = (String) pendingOrder.getOrDefault("city", city);
        discountCode = (String) pendingOrder.getOrDefault("discountCode", discountCode);
        discountPercent = Double.parseDouble(String.valueOf(pendingOrder.getOrDefault("discountPercent", discountPercent)));
    }

    // Ghi đè nếu có giá trị mới từ request
    if (appliedDiscountPercent != null) discountPercent = appliedDiscountPercent;
    if (appliedDiscountCode != null) discountCode = appliedDiscountCode;
    if (discountErrorMsg != null) discountError = discountErrorMsg;

    // Kiểm tra sp giảm giá
    boolean hasSaleOffProduct = false;
    if (cart != null) {
        for (CartItemDTO item : cart.getCartItems()) {
            if (item.getProduct().isFromSaleOff()) {
                hasSaleOffProduct = true;
                break;
            }
        }
    }

    // Lấy voucher nếu có mã trong request
    String code = request.getParameter("discountCode");
    VoucherDAO dao = new VoucherDAO();
    VoucherDTO voucher = dao.getVoucherByCode(code);

    if (voucher != null) {
        request.setAttribute("DISCOUNT_CODE", voucher.getVoucherCode());
        request.setAttribute("DISCOUNT_PERCENT", voucher.getDiscountValue());
        request.setAttribute("VOUCHER_ID", voucher.getVoucherID());
    } else {
        request.setAttribute("DISCOUNT_ERROR", "Invalid or expired code!");
    }

    // Gán giá trị từ request nếu có
    if (request.getParameter("country") != null) country = request.getParameter("country");
    if (request.getParameter("fullname") != null) fullname = request.getParameter("fullname");
    if (request.getParameter("phone") != null) phone = request.getParameter("phone");
    if (request.getParameter("email") != null) email = request.getParameter("email");
    if (request.getParameter("address") != null) address = request.getParameter("address");
    if (request.getParameter("district") != null) district = request.getParameter("district");
    if (request.getParameter("city") != null) city = request.getParameter("city");

    Integer voucherID = (Integer) request.getAttribute("VOUCHER_ID");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Shipping Address - Summit Spirit</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/shipping.css">
    </head>
    <body>

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
                        <a href="<%= request.getContextPath() %>/user/profile.jsp"><%= loginUser != null ? loginUser.getFullName() : "Account" %></a>
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
                        <input type="hidden" name="sourcePage" value="user/shipping.jsp">
                        <input type="text" name="discountCode" placeholder="Discount code" value="<%= discountCode %>">
                        <button type="submit" class ="apply-btn" name="action" value="ApplyDiscount" onclick="setFormAction('<%= request.getContextPath() %>/MainController')">Apply</button>
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
                        <a href="<%= request.getContextPath() %>/user/cart.jsp" class="back-btn">← Return To Cart</a>
                        <%-- Hidden inputs for VNPay checkout --%>
                        <% if (cart != null && !cart.isEmpty()) {
                            double total = 0;
                            StringBuilder productNames = new StringBuilder();
                            StringBuilder sizes = new StringBuilder();
                            StringBuilder colors = new StringBuilder();
                            for (CartItemDTO item : cart.getCartItems()) {
                                ProductDTO p = item.getProduct();
                                int quantity = item.getQuantity();
                                double lineTotal = p.getPrice() * quantity;
                                total += lineTotal;
                                if (productNames.length() > 0) productNames.append(", ");
                                productNames.append(p.getProductName());
                                if (sizes.length() > 0) sizes.append(", ");
                                sizes.append(p.getSize() != null ? p.getSize() : "N/A");
                                if (colors.length() > 0) colors.append(", ");
                                colors.append(p.getColor() != null ? p.getColor() : "N/A");
                            }
                            double shipFee = 30000;
                            double discountAmount = total * discountPercent / 100;
                            double grandTotal = total + shipFee - discountAmount;
                        %>
                        <input type="hidden" name="amount" value="<%= (int) grandTotal %>">
                        <input type="hidden" name="productName" value="<%= productNames.toString() %>">
                        <input type="hidden" name="size" value="<%= sizes.toString() %>">
                        <input type="hidden" name="color" value="<%= colors.toString() %>">
                        <input type="hidden" name="discountCode" value="<%= discountCode != null ? discountCode : "" %>">
                        <input type="hidden" name="discountPercent" value="<%= discountPercent %>">
                        <% if (voucherID != null) { %>
                        <input type="hidden" name="voucherID" value="<%= voucherID %>">
                        <% } %>
                        <% } %>
                        <button type="submit" name="action" value="CheckoutVNPay" class="pay-btn" onclick="setFormAction('<%= request.getContextPath() %>/payment')">Continue To Pay</button>
                    </div>
                </div>

                <% if (cart != null && !cart.isEmpty()) { %>
                <div class="cart-preview">
                    <h3>Your Cart</h3>
                    <% double total = 0;
                       for (CartItemDTO item : cart.getCartItems()) {
                           ProductDTO p = item.getProduct();
                           int quantity = item.getQuantity();
                           double lineTotal = p.getPrice() * quantity;
                           total += lineTotal;
                    %>
                    <div class="cart-item">
                        <img src="<%= p.getProductImage() %>" alt="">
                        <div class="cart-info">
                            <h4><%= p.getProductName() %></h4>
                            <% if (p.getSize() != null && !p.getSize().isEmpty()) { %>
                            <p>Size: <%= p.getSize() %></p>
                            <% } %>
                            <% if (p.getColor() != null && !p.getColor().isEmpty()) { %>
                            <p>Color: <%= p.getColor() %></p>
                            <% } %>
                            <div class="quantity-box"><span><%= quantity %></span></div>
                        </div>
                        <div><%= String.format("%,.0f", p.getPrice()) %></div>
                    </div>
                    <% }
                       double shipFee = 30000;
                       double discountAmount = total * discountPercent / 100;
                       double grandTotal = total + shipFee - discountAmount;
                    %>
                    <div class="summary-line">Subtotal: <span><%= String.format("%,.0f", total) %></span></div>
                    <div class="summary-line">Ship: <span><%= String.format("%,.0f", shipFee) %></span></div>
                    <div class="summary-line">Voucher: <span><%= discountPercent > 0 ? "-" + String.format("%,.0f", discountAmount) : "0" %></span></div>
                    <div class="summary-line total-line">Total: <span><%= String.format("%,.0f", grandTotal) %></span></div>
                </div>
                <% } else { %>
                <p>No items in cart.</p>
                <% } %>
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
