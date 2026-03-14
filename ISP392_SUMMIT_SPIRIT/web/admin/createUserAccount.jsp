<%-- 
    Document   : createUserAccount
    Created on : Jun 17, 2025, 3:02:08 PM
    Author     : Hanne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% String msg = (String) request.getAttribute("message"); %>
<div class="container-fluid px-0" style="max-width:600px;">
    <h2 class="mb-4" style="color:#234C45;font-weight:600;">Create New User Account</h2>
    <% if (msg != null && !msg.isEmpty()) { %>
    <div class="alert alert-danger"><%= msg %></div>
    <% } %>
    <form action="${pageContext.request.contextPath}/AdminCreateUserController" method="post" class="bg-white p-4 rounded shadow-sm">
        <div class="mb-3">
            <label class="form-label">Full Name</label>
            <input type="text" class="form-control" name="fullName" value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : "" %>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" class="form-control" name="email" value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Address</label>
            <input type="text" class="form-control" name="address" value="<%= request.getAttribute("address") != null ? request.getAttribute("address") : "" %>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Phone</label>
            <input type="text" class="form-control" name="phone" value="<%= request.getAttribute("phone") != null ? request.getAttribute("phone") : "" %>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Password</label>
            <input type="password" class="form-control" name="password" value="<%= request.getAttribute("password") != null ? request.getAttribute("password") : "" %>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Confirm Password</label>
            <input type="password" class="form-control" name="confirm" value="<%= request.getAttribute("confirm") != null ? request.getAttribute("confirm") : "" %>" required>
        </div>
        <div class="mb-3">
            <label class="form-label">Role</label>
            <select class="form-select" name="role">
                <option value="User" <%= "User".equals(request.getAttribute("role")) ? "selected" : "" %>>User</option>
                <option value="Staff" <%= "Staff".equals(request.getAttribute("role")) ? "selected" : "" %>>Staff</option>
                <option value="Shipper" <%= "Shipper".equals(request.getAttribute("role")) ? "selected" : "" %>>Shipper</option>
                <option value="Admin" <%= "Admin".equals(request.getAttribute("role")) ? "selected" : "" %>>Admin</option>
            </select>
        </div>

        <div class="d-flex justify-content-between">
            <button type="submit" class="btn btn-success px-4" style="background:#234C45;">Create</button>
            <button type="button" class="btn btn-outline-secondary px-4" onclick="location.href = '${pageContext.request.contextPath}/ManageUserAccountController'">Cancel</button>
        </div>
    </form>
</div>
