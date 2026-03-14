
<!-- Phần đầu trang giữ nguyên -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dto.CartDTO, dto.CartItemDTO, dto.ProductDTO, dto.UserDTO" %>
<%
    CartDTO cart = (CartDTO) session.getAttribute("CART");
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    double discountPercent = 0.0;
    if (session.getAttribute("DISCOUNT_PERCENT") != null) {
        discountPercent = (double) session.getAttribute("DISCOUNT_PERCENT");
    }
    String discountCode = (String) session.getAttribute("DISCOUNT_CODE");
    String discountError = (String) session.getAttribute("DISCOUNT_ERROR");
    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Cart</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/cart.css">
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

            function updateQuantityOnServer(productKey, quantity) {
                const parts = productKey.split('_');
                const productID = parts[0];
                const size = parts[1];
                const color = parts[2];

                fetch('UpdateQuantityServlet', {
                    method: 'POST',
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    body: 'productID=' + encodeURIComponent(productID) +
                            '&size=' + encodeURIComponent(size) +
                            '&color=' + encodeURIComponent(color) +
                            '&quantity=' + encodeURIComponent(quantity)
                }).catch(err => console.error("Error updating quantity:", err));
            }

            function increase(id, stock) {
                const input = document.getElementById("qty_" + id);
                let value = parseInt(input.value);
                if (value >= stock) {
                    alert("Product quantity exceeds stock");
                    input.value = stock;
                    return;
                }
                input.value = value + 1;
                updateQuantityOnServer(id, input.value);
                updateTotal();
            }

            function decrease(id) {
                const input = document.getElementById("qty_" + id);
                let value = parseInt(input.value);
                if (value <= 1) {
                    if (confirm("Quantity is 0. Remove this item from cart?")) {
                        const parts = id.split('_');
                        const productID = parts[0];
                        const size = parts[1];
                        const color = parts[2];
                        window.location.href = "<%= request.getContextPath() %>/RemoveFromCartServlet?productID=" + productID + "&size=" + size + "&color=" + color;
                        return;
                    } else {
                        input.value = 1;
                    }
                } else {
                    input.value = value - 1;
                    updateQuantityOnServer(id, input.value);
                    updateTotal();
                }
            }

            function handleManualInput(id, stock) {
                const input = document.getElementById("qty_" + id);
                let value = parseInt(input.value);
                if (value > stock) {
                    alert("Product quantity exceeds stock");
                    input.value = stock;
                } else if (value < 1 || isNaN(value)) {
                    if (confirm("Quantity is 0. Remove this item from cart?")) {
                        const parts = id.split('_');
                        const productID = parts[0];
                        const size = parts[1];
                        const color = parts[2];
                        window.location.href = "<%= request.getContextPath() %>/RemoveFromCartServlet?productID=" + productID + "&size=" + size + "&color=" + color;
                        return;
                    } else {
                        input.value = 1;
                    }
                }
                updateQuantityOnServer(id, input.value);
                updateTotal();
            }

            function updateTotal() {
                let cartItems = document.querySelectorAll(".cart-item");
                let subTotal = 0;
                cartItems.forEach(item => {
                    const pid = item.getAttribute("data-id");
                    const price = parseFloat(item.getAttribute("data-price"));
                    const qty = parseInt(document.getElementById("qty_" + pid).value);
                    subTotal += price * qty;
                });

                const discountPercent = <%= discountPercent %>;
                const discountAmount = subTotal * discountPercent / 100;
                const ship = 30000;
                const grandTotal = subTotal + ship - discountAmount;

                document.getElementById("subtotal").innerText = subTotal.toLocaleString();
                document.getElementById("ship").innerText = ship.toLocaleString();
                if (discountPercent > 0) {
                    document.getElementById("discount").innerText = "-" + discountAmount.toLocaleString();
                }
                document.getElementById("grandTotal").innerText = grandTotal.toLocaleString() + " VND";
            }

            document.addEventListener("DOMContentLoaded", () => {
                document.querySelector('.btn-continue').addEventListener('click', function () {
                    document.querySelectorAll('.cart-item').forEach(item => {
                        const pid = item.getAttribute("data-id");
                        const currentQty = document.getElementById("qty_" + pid).value;
                        const hiddenInput = document.getElementById("hidden_qty_" + pid);
                        if (hiddenInput) {
                            hiddenInput.value = currentQty;
                        }
                    });
                });
            });
        </script>

        <% if (cart == null || cart.isEmpty()) { %>
        <div class="empty-cart">Your cart is empty.</div>
        <% } else { double total = 0; %>


        <div class="main-wrapper">
            <div class="cart-section">
                <h2>Shopping cart</h2>
                <p>You have <%= cart.getTotalQuantity() %> item<%= cart.getTotalQuantity() > 1 ? "s" : "" %> in your cart</p>
                <% for (CartItemDTO item : cart.getCartItems()) {
                    ProductDTO p = item.getProduct();
                    String sizeStr = (p.getSize() != null) ? p.getSize().trim() : "";
                    String colorStr = (p.getColor() != null) ? p.getColor().trim() : "";
                    String uniqueKey = p.getProductID() + "_" + sizeStr + "_" + colorStr;
                    int quantity = item.getQuantity();
                    int stock = p.getStock();

                    double originalPrice = p.getPrice();
        double price = originalPrice;
        boolean isDiscounted = p.isFromSaleOff() ;

        if (isDiscounted) {
            price = Math.round(originalPrice * 0.8);
        }


                    double lineTotal = price * quantity;
                    total += lineTotal;
                %>
                <div class="cart-item" data-id="<%= uniqueKey %>" data-price="<%= price %>" data-stock="<%= stock %>">
                    <img src="<%= p.getProductImage() %>" alt="">
                    <div class="cart-info">
                        <h3><%= p.getProductName() %></h3>
                        <% if (!sizeStr.isEmpty()) { %>
                        <p>Size: <%= sizeStr %></p>
                        <% } %>
                        <% if (!colorStr.isEmpty()) { %>
                        <p>Color: <%= colorStr %></p>
                        <% } %>
                        <div class="quantity-box">
                            <button type="button" onclick="decrease('<%= uniqueKey %>')">−</button>
                            <input type="number" name="quantity_<%= uniqueKey %>" id="qty_<%= uniqueKey %>" value="<%= quantity %>" min="1" max="<%= stock %>" readonly>
                            <button type="button" onclick="increase('<%= uniqueKey %>', <%= stock %>)">+</button>
                            <%
                                String safeSize = (p.getSize() != null && !p.getSize().trim().isEmpty()) ? p.getSize().trim() : "N/A";
                                String safeColor = (p.getColor() != null && !p.getColor().trim().isEmpty()) ? p.getColor().trim() : "N/A";
                            %>
                            <a href="<%= request.getContextPath() %>/RemoveFromCartServlet?productID=<%= p.getProductID() %>&size=<%= safeSize %>&color=<%= safeColor %>" class="delete-link">
                                <i class="fa fa-trash"></i>
                            </a>


                        </div>
                    </div>
                    <div class="price">
                        <% if (isDiscounted) {

                            double displayedOriginal = Math.round(originalPrice * 1.25);
                        %>
                        <span style="text-decoration: line-through; color: gray;">
                            <%= String.format("%,.0f", displayedOriginal * quantity) %> VND
                        </span><br>
                        <span style="color: red; font-weight: bold;">
                            <%= String.format("%,.0f", originalPrice * quantity) %> VND
                        </span>
                        <span style="color: red;"> (-20%)</span>
                        <% } else { %>
                        <span style="font-weight: bold;">
                            <%= String.format("%,.0f", originalPrice * quantity) %> VND
                        </span>
                        <% } %>
                    </div>
                </div>
                <% } %>
            </div>

            <div class="summary-section">
                <h3>ORDER SUMMARY</h3>
                <% double shipFee = 30000;
                   double discountAmount = total * discountPercent / 100;
                   double grandTotal = total + shipFee - discountAmount; %>
                <div class="summary-line">Subtotal: <span id="subtotal"><%= String.format("%,.0f", total) %></span></div>
                <div class="summary-line">Shipping: <span id="ship"><%= String.format("%,.0f", shipFee) %></span></div>
                    <% if (discountPercent > 0) { %>
                <div class="summary-line" id="discount-line">Discount (<%= discountPercent %>%): <span id="discount">-<%= String.format("%,.0f", discountAmount) %></span></div>
                <% } %>
                <div class="summary-line total-line">TOTAL <span id="grandTotal"><%= String.format("%,.0f", grandTotal) %> VND</span></div>

                <form action="<%= request.getContextPath() %>/GoToShippingServlet" method="POST">
                    <% for (CartItemDTO item : cart.getCartItems()) {
                        ProductDTO p = item.getProduct();
                        String uniqueKey = p.getProductID() + "_" + p.getSize() + "_" + p.getColor();
                        int quantity = item.getQuantity();
                    %>
                    <input type="hidden" name="quantity_<%= uniqueKey %>" value="<%= quantity %>" id="hidden_qty_<%= uniqueKey %>">
                    <% } %>
                    <button type="submit" class="btn-continue">CONTINUE</button>
                </form>
            </div>
        </div>

        <% } %>
    </body>
</html>
