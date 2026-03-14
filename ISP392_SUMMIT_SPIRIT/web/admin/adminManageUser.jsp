<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="dto.UserDTO" %>
<%
    List<UserDTO> users = (List<UserDTO>) request.getAttribute("users");
    String keyword = request.getAttribute("keyword") != null ? (String) request.getAttribute("keyword") : "";
    String role = request.getAttribute("role") != null ? (String) request.getAttribute("role") : "all";

    String message = (String) session.getAttribute("message");
    if (message != null) {
        session.removeAttribute("message"); // Xóa sau khi hiển thị
    }
%>

<div class="container-fluid px-0">
    <h2 class="mb-4" style="color:#234C45;font-weight:600;">Manage User Accounts</h2>
        <form method="get" action="${pageContext.request.contextPath}/ManageUserAccountController" class="row g-2 align-items-center mb-3">
        <div class="col-md-4">
            <input type="text" class="form-control" name="keyword" value="<%= keyword %>" placeholder="Search by name, email, address, or role...">
        </div>
        <div class="col-md-3">
            <select class="form-select" name="role">
                <option value="all" <%= "all".equals(role) ? "selected" : "" %>>All Roles</option>
                <option value="User" <%= "User".equals(role) ? "selected" : "" %>>User</option>
                <option value="Staff" <%= "Staff".equals(role) ? "selected" : "" %>>Staff</option>
                <option value="Shipper" <%= "Shipper".equals(role) ? "selected" : "" %>>Shipper</option>
            </select>
        </div>
        <div class="col-auto">
            <button type="submit" class="btn btn-success" style="background:#234C45;">Search</button>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/ManageUserAccountController" class="btn btn-secondary">Cancel</a>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/AdminCreateUserController" 
                class="btn btn-outline-success" 
                style="color:#234C45;border-color:#234C45;">
                Create New Account
             </a>
        </div>
    </form>

    <% if (message != null) { %>
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <%= message %>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    <% } %>

    <!-- Search form (giữ nguyên) -->

    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle bg-white">
            <thead class="table-light">
                <tr>
                    <th>UserID</th>
                    <th>Full Name</th>
                    <th>Phone</th>
                    <th>Email</th>
                    <th>Address</th>
                    <th>Role</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <% if (users != null) {
                    for (UserDTO u : users) { %>
                    <tr>
                        <td><%= u.getUserID() %></td>
                        <td><%= u.getFullName() %></td>
                        <td><%= u.getEmail() %></td>
                        <td><%= u.getPhone() %></td>
                        <td><%= u.getAddress() %></td>

                        <!-- Dropdown Role -->
                        <td>
                            <form action="${pageContext.request.contextPath}/ManageUserActionController" method="post" class="d-flex align-items-center gap-2">
                                <input type="hidden" name="userID" value="<%= u.getUserID() %>">
                                <input type="hidden" name="action" value="editRole">

                                <select name="newRole" class="form-select form-select-sm" style="width:auto;">
                                    <option value="User" <%= "User".equals(u.getRole()) ? "selected" : "" %>>User</option>
                                    <option value="Staff" <%= "Staff".equals(u.getRole()) ? "selected" : "" %>>Staff</option>
                                    <option value="Shipper" <%= "Shipper".equals(u.getRole()) ? "selected" : "" %>>Shipper</option>
                                </select>

                                <button type="submit" class="btn btn-sm btn-outline-success">Save</button>
                            </form>
                        </td>

                        <!-- Delete -->
                        <td>
                            <form action="${pageContext.request.contextPath}/ManageUserActionController" method="post" onsubmit="return confirm('Delete user <%= u.getFullName() %>?');">
                                <input type="hidden" name="userID" value="<%= u.getUserID() %>">
                                <input type="hidden" name="action" value="delete">
                                <button type="submit" class="btn btn-sm btn-outline-danger">Delete</button>
                            </form>
                        </td>
                    </tr>
                <% }} else { %>
                    <tr><td colspan="7" class="text-center">No users found</td></tr>
                <% } %>
            </tbody>
        </table>
    </div>
</div>
