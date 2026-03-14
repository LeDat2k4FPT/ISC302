<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="dto.UserDTO, dao.CategoryDAO, dto.CategoryDTO" %>
<%
    UserDTO user = (UserDTO) session.getAttribute("LOGIN_USER");
    if (user == null || !"Staff".equals(user.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    CategoryDAO categoryDAO = new CategoryDAO();
    java.util.List<CategoryDTO> categories = categoryDAO.getAllCategories();
%>

<link rel="stylesheet" href="<%= request.getContextPath() %>/css/addProduct.css">

<div class="product-form-wrapper">
    <div class="product-header">
        <h2><i class="bi bi-plus-circle"></i> Add New Product</h2>
        <a href="<%= request.getContextPath() %>/ProductListController" class="btn-add">← Back to Product List</a>
    </div>

    <% String error = (String) request.getAttribute("error");
       if (error != null) { %>
    <div class="alert alert-danger"><%= error %></div>
    <% } %>

    <form action="<%= request.getContextPath() %>/AddProductController" method="post" enctype="multipart/form-data">
        <label>Product Name</label>
        <input type="text" name="productName" required>

        <label>Category</label>
        <select name="cateID" required>
            <% for (CategoryDTO c : categories) { %>
            <option value="<%= c.getCateID() %>"><%= c.getCateName() %></option>
            <% } %>
        </select>

        <label>Description</label>
        <textarea name="description" rows="3" required></textarea>

        <div class="form-row">
            <div>
                <label>Color</label>
                <select name="color">
                    <option value="">-- No Color --</option>
                    <option>Red</option>
                    <option>Black</option>
                    <option>Blue</option>
                </select>
            </div>

            <div>
                <label>Size</label>
                <select name="size">
                    <option value="">-- No Size --</option>
                    <option>S</option>
                    <option>M</option>
                    <option>L</option>
                    <option>Small</option>
                    <option>Big</option>
                </select>
            </div>
        </div>

        <div class="form-row">
            <div>
                <label>Price</label>
                <input type="text" name="price" required oninput="this.value = this.value.replace(/[^0-9.]/g, '')">
            </div>

            <div>
                <label>Stock</label>
                <input type="number" name="stock" required>
            </div>
        </div>

        <input type="hidden" name="status" value="Active">

        <label>Product Image (URL)</label>
        <input type="text" name="imageURL" placeholder="Or enter image URL...">

        <button type="submit" class="submit-btn">
            <i class="bi bi-plus-circle"></i> Add Product
        </button>
    </form>
</div>
