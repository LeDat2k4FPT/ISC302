<%@page import="java.util.List"%>
<%@page import="dto.OrderDTO"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*, dto.UserDTO , dto.CartDTO" %>

<%
     UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null || !"User".equals(loginUser.getRole())) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    List<OrderDTO> orders = (List<OrderDTO>) request.getAttribute("orders");
    CartDTO cart = (CartDTO) session.getAttribute("CART");
    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Order History</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                font-family: 'Kumbh Sans', sans-serif;
                margin: 0;
                padding: 0;
                background-color: #f9f9f9;
            }
            .header {
                padding: 10px 40px;
                background-color: #ffffff;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }

            .header img {
                height: 60px;
                cursor: pointer;
            }

            .nav-links {
                display: flex;
                align-items: center;
                gap: 20px;
            }

            .nav-links a {
                text-decoration: none;
                color: #000;
                font-weight: 500;
            }
            .cart-icon {
                position: relative;
                display: inline-block;
                font-size: 20px;
                color: #000;
                margin-left: 15px;
            }

            .cart-badge {
                position: absolute;
                top: -8px;
                right: -10px;
                background-color: #e53935;
                color: white;
                border-radius: 50%;
                padding: 2px 6px;
                font-size: 12px;
                font-weight: bold;
            }

            .user-dropdown {
                position: relative;
            }

            .user-name {
                cursor: pointer;
                font-weight: bold;
                padding: 10px;
                color: #000;
            }

            .dropdown-menu {
                display: none;
                position: absolute;
                top: 100%;
                right: 0;
                background-color: white;
                box-shadow: 0 8px 16px rgba(0,0,0,0.2);
                border-radius: 4px;
                z-index: 1000;
                min-width: 150px;
            }

            .dropdown-menu a {
                display: block;
                padding: 10px 16px;
                text-decoration: none;
                color: black;
            }

            .dropdown-menu a:hover {
                background-color: #f1f1f1;
            }
            .wrapper {
                display: flex;
                min-height: calc(100vh - 80px); /* trừ topbar */
            }

            .sidebar {
                width: 240px;
                background-color: #e6ebe7;
                padding: 30px 20px;
            }
            .menu-link {
                display: block;
                padding: 12px 18px;
                margin-bottom: 10px;
                border-radius: 5px;
                color: #333;
                text-decoration: none;
                font-weight: 500;
            }
            .menu-link.active {
                background-color: #3c5946;
                color: white;
            }
            #cancelModal, #successModal {
                display: none;
                position: fixed;
                top: 30%;
                left: 35%;
                background: #fff;
                border: 1px solid #333;
                padding: 20px;
                z-index: 1000;
                box-shadow: 0 0 10px rgba(0,0,0,0.3);
                width: 30%;
                border-radius: 8px;
                text-align: center;
            }

            #modalOverlay {
                display: none;
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                background: rgba(0,0,0,0.5);
                z-index: 900;
            }

            button {
                margin-top: 10px;
                padding: 6px 12px;
            }

            .orders-container {
                padding: 40px;
                width: 100%;
            }

            .table-orders {
                border-collapse: separate;
                border-spacing: 0;
                width: 100%;
                background: white;
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 4px 12px rgba(0,0,0,0.1);
                margin-top: 20px;
            }

            .table-orders thead {
                background-color: #3c5946;
                color: white;
            }

            .table-orders th, .table-orders td {
                padding: 14px 16px;
                text-align: center;
                font-size: 15px;
            }

            .table-orders tbody tr:nth-child(even) {
                background-color: #f4f7f5;
            }

            .table-orders tbody tr:hover {
                background-color: #e2ede7;
            }

            .table-orders a {
                color: #3c5946;
                font-weight: bold;
                text-decoration: none;
            }

            .table-orders button {
                background-color: #c62828;
                color: white;
                border: none;
                padding: 6px 12px;
                border-radius: 5px;
                cursor: pointer;
            }

            .table-orders button:hover {
                background-color: #b71c1c;
            }


            .status-cancelled {
                color: #e53935;
                font-weight: bold;
            }
            .status-cancelling {
                color: #fb8c00;
                font-weight: bold;
            }
            .status-processing {
                color: #1e88e5;
            }
            .status-shipped {
                color: #43a047;
            }
            .status-pending {
                color: #757575;
            }
        </style>
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
                        <a href="MainController?action=Logout">Logout</a>
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
        <div class="wrapper d-flex">
            <aside class="sidebar">
                <nav class="menu">
                    <a href="<%= request.getContextPath() %>/user/profile.jsp" class="menu-link ">My Profile</a>
                    <a href="UserOrderHistoryController" class="menu-link active">Orders History</a>

                </nav>
            </aside>

            <div class="orders-container">
                <table class="table-orders">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Date</th>
                            <th>Status</th>
                            <th>Total</th>
                            <th>Detail</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <%
       if (orders != null && !orders.isEmpty()) {
           for (OrderDTO o : orders) {
               String status = o.getStatus() != null ? o.getStatus() : "";
               String statusClass = "";
               String displayStatus = status;

               if ("Cancelled".equalsIgnoreCase(status)) {
                   statusClass = "status-cancelled";
               } else if ("Cancelling".equalsIgnoreCase(status)) {
                   statusClass = "status-cancelling";
               } else if ("Processing".equalsIgnoreCase(status)) {
                   statusClass = "status-processing";
               } else if ("Shipped".equalsIgnoreCase(status)) {
                   statusClass = "status-shipped";
                   displayStatus = "Shipping";
               } else if ("Packed".equalsIgnoreCase(status)) {
                   statusClass = "status-packed";
               } else if ("Pending".equalsIgnoreCase(status)) {
                   statusClass = "status-pending";
               } else if ("Delivered".equalsIgnoreCase(status)) {
                   statusClass = "status-delivered";
               }
                        %>
                        <tr>
                            <td><%= o.getOrderID() %></td>
                            <td><%= o.getOrderDate() %></td>
                            <td class="<%= statusClass %>"><%= displayStatus %></td>
                            <td><%= String.format("%,.0f", o.getTotalAmount()) %></td>
                            <td><a href="OrderDetailController?orderID=<%= o.getOrderID() %>">VIEW</a></td>
                            <td>
                                <% if (!"Cancelled".equalsIgnoreCase(status)
                                       && !"Delivered".equalsIgnoreCase(status)
                                       && !"Cancelling".equalsIgnoreCase(status)
                                       && !"Packed".equalsIgnoreCase(status)
                                       && !"Shipped".equalsIgnoreCase(status)) { %>
                                <button onclick="openCancelModal(<%= o.getOrderID() %>)">Cancel</button>
                                <% } else { %>&nbsp;<% } %>

                            </td>
                        </tr>
                        <%
                                }
                            } else {
                        %>
                        <tr>
                            <td colspan="6">No orders found.</td>
                        </tr>
                        <% } %>

                    </tbody>
                </table>
            </div>

            <div id="modalOverlay"></div>

            <div id="cancelModal">
                <h3>Cancel Order</h3>
                <form action="CancelOrderController" method="post">
                    <input type="hidden" name="orderID" id="modalOrderID" />
                    <label>Reason for cancellation:</label><br/>
                    <textarea name="cancelReason" rows="4" cols="30" required></textarea><br/><br/>
                    <button type="submit">Confirm Cancel</button>
                    <button type="button" onclick="closeCancelModal()">Close</button>
                </form>
            </div>

            <div id="successModal">
                <p>We have received your cancellation.<br/>
                    Please wait for our email or phone number to confirm and receive your payment back.</p>
                <button onclick="closeSuccessModal()">Close</button>
            </div>

            <script>
                function openCancelModal(orderID, status) {
                    // Hiển thị modal yêu cầu nhập lý do
                    document.getElementById("modalOrderID").value = orderID;
                    document.getElementById("modalOverlay").style.display = "block";
                    document.getElementById("cancelModal").style.display = "block";
                }

                function closeCancelModal() {
                    document.getElementById("modalOverlay").style.display = "none";
                    document.getElementById("cancelModal").style.display = "none";
                }

                function closeSuccessModal() {
                    document.getElementById("modalOverlay").style.display = "none";
                    document.getElementById("successModal").style.display = "none";
                }
            </script>
            <script>
                function sortTable(sortBy) {
                    const currentUrl = new URL(window.location.href);
                    let currentSort = currentUrl.searchParams.get("sortBy");
                    let order = currentUrl.searchParams.get("order");

                    if (currentSort === sortBy) {
                        order = (order === "asc") ? "desc" : "asc";
                    } else {
                        order = "asc";
                    }

                    currentUrl.searchParams.set("sortBy", sortBy);
                    currentUrl.searchParams.set("order", order);
                    window.location.href = currentUrl.toString();
                }
            </script>

            <%-- Thông báo khi huỷ đơn hàng loại cần xác nhận --%>
            <%
                Boolean cancelSuccess = (Boolean) session.getAttribute("cancelSuccess");
                if (cancelSuccess != null && cancelSuccess) {
                    session.removeAttribute("cancelSuccess");
            %>
            <script>
                document.getElementById("modalOverlay").style.display = "block";
                document.getElementById("successModal").style.display = "block";
            </script>
            <%
                }
            %>
        </div>
    </body>
</html>
