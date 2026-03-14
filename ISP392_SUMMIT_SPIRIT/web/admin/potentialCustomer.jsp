<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="dto.UserDTO"%>
<%
    List<Map<String, Object>> potentialList = (List<Map<String, Object>>) request.getAttribute("potentialList");
%>
<div class="container mt-4">
    <h2 style="color:#234C45;font-weight:600;">Potential Customers</h2>
    <% if (potentialList != null && !potentialList.isEmpty()) { %>
    <p class="text-muted">Total potential customers: <%= potentialList.size() %></p>
    <% } %>
    
    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle bg-white mt-3">
            <thead class="table-light">
            <tr>
                <th>#</th>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Total Orders</th>
                <th>Total Spent</th>
                <th>Reviews</th>
            </tr>
            </thead>
            <tbody>
            <% 
                if (potentialList != null && !potentialList.isEmpty()) {
                    int idx = 1;
                    for (Map<String, Object> info : potentialList) {
                        UserDTO user = (UserDTO) info.get("user");
                        double totalSpent = info.get("totalSpent") != null ? (double) info.get("totalSpent") : 0;
                        int orderCount = info.get("orderCount") != null ? (int) info.get("orderCount") : 0;
                        int reviewCount = info.get("reviewCount") != null ? (int) info.get("reviewCount") : 0;
            %>
                <tr style="<%= idx == 1 ? "background:#e6ffed;" : "" %>">
                    <td><%= idx++ %></td>
                    <td><%= user.getFullName() %></td>
                    <td><%= user.getEmail() %></td>
                    <td><%= user.getPhone() %></td>
                    <td><%= orderCount %></td>
                    <td>$<%= String.format("%,.2f", totalSpent) %></td>
                    <td><%= reviewCount %></td>
                </tr>
            <% 
                    }
                } else { 
            %>
                <tr>
                    <td colspan="7" class="text-center text-muted">No potential customers found.</td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>
