<%-- 
    Document   : updateOrder
    Created on : Jul 3, 2025, 10:14:55 AM
    Author     : Admin
--%>
<%@page import="dao.OrderDAO"%>
<%@page import="dto.OrderDTO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    int orderID = Integer.parseInt(request.getParameter("orderID"));
    OrderDAO dao = new OrderDAO();
    OrderDTO order = dao.getOrderById(orderID);
%>
<html>
    <head><title>Update Order Status</title></head>
    <body>
        <h2>Update Order #<%= order.getOrderID() %></h2>
        <form action="<%=request.getContextPath()%>/UpdateOrderStatus" method="post">
            <input type="hidden" name="orderID" value="<%= order.getOrderID() %>"/>
            <input type="hidden" name="orderID" value="<%= order.getOrderID() %>"/>
            <select name="status">
                <option value="Processing" <%= "Processing".equals(order.getStatus()) ? "selected":"" %>>Processing</option>
                <option value="Shipped" <%= "Shipped".equals(order.getStatus()) ? "selected":"" %>>Shipped</option>
                <option value="Delivered" <%= "Delivered".equals(order.getStatus()) ? "selected":"" %>>Delivered</option>
                <option value="Cancelled" <%= "Cancelled".equals(order.getStatus()) ? "selected":"" %>>Cancelled</option>
            </select>

            <button type="submit">Update</button>
        </form>

    </body>
</html>
