<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="dto.OrderDTO" %>
<%@ page import="dto.UserAddressDTO" %>
<%@ page import="dao.OrderDAO" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="dto.UserDTO" %>

<%
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null || !"Shipper".equals(loginUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<OrderDTO> packedOrders = (List<OrderDTO>) request.getAttribute("packedOrders");
    OrderDAO dao = new OrderDAO();
    NumberFormat nf = NumberFormat.getInstance();
%>

<link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/shippingList.css">



<div class="order-section">
    <div class="order-container">
        <h2>📦 Orders Waiting for Shipping</h2>

        <% if (packedOrders != null && !packedOrders.isEmpty()) { %>
        <div class="table-wrapper">
            <table class="order-table">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Customer Name</th>
                        <th>Phone</th>
                        <th>Shipping Address</th>
                        <th>Order Date</th>
                        <th>Total</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (OrderDTO order : packedOrders) {
                        UserAddressDTO info = dao.getUserAddressInfoByOrderId(order.getOrderID());
                    %>
                    <tr>
                        <td><%= order.getOrderID() %></td>
                        <td><%= info != null ? info.getFullName() : "None" %></td>
                        <td><%= info != null ? info.getPhone() : "None" %></td>
                        <td>
                            <% if (info != null) { %>
                            <%= info.getAddress() %>, <%= info.getDistrict() %>, <%= info.getCity() %>, <%= info.getCountry() %>
                            <% } else { %>
                            None
                            <% } %>
                        </td>
                        <td><%= order.getOrderDate() %></td>
                        <td><%= nf.format(order.getTotalAmount()) %> VND</td>
                        <td>
                            <form method="post" action="AcceptShippingController">
                                <input type="hidden" name="orderID" value="<%= order.getOrderID() %>" />
                                <button type="submit" class="accept-btn">Accept</button>
                            </form>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        <% } else { %>
        <p class="no-order-msg">🚫 No packed orders available.</p>
        <% } %>
    </div>
</div>