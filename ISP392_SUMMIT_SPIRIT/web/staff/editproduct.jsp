<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="dto.ProductDTO"%>
<%@ page import="dao.ProductVariantDAO, dao.ColorDAO, dao.SizeDAO, dao.CategoryDAO" %>
<%@ page import="dto.ProductVariantDTO, dto.CategoryDTO" %>

<%
    ProductDTO product = (ProductDTO) session.getAttribute("PRODUCT");
    if (product == null) {
        response.sendRedirect("ProductListController");
        return;
    }

    ProductVariantDAO variantDAO = new ProductVariantDAO();
    ColorDAO colorDAO = new ColorDAO();
    SizeDAO sizeDAO = new SizeDAO();
    CategoryDAO categoryDAO = new CategoryDAO();

    java.util.List<ProductVariantDTO> variants = variantDAO.getVariantsByProductId(product.getProductID());
    java.util.List<CategoryDTO> categories = categoryDAO.getAllCategories();
    java.util.List<String> allSizes = sizeDAO.getAllSizes();
    java.util.List<String> allColors = colorDAO.getAllColors();
%>

<!DOCTYPE html>
<html>
    <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
    %>

    <div style="color:red;font-size:18px">
        <%=error%>
    </div>

    <%
        }
    %>
    <head>
        <meta charset="UTF-8">
        <title>Edit Product</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath()%>/css/editProduct.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">

    </head>
    <body>

        <div class="product-form-wrapper">
            <div class="product-header">
                <h2>Edit Product - ID: <%= product.getProductID()%></h2>
                <a href="<%= request.getContextPath()%>/ProductListController" class="btn-add">← Back</a>
            </div>

            <form action="<%= request.getContextPath()%>/UpdateProductController" method="post">
                <input type="hidden" name="productID" value="<%= product.getProductID()%>"/>

                <label>Product Name</label>
                <input type="text" name="productName" value="<%= product.getProductName()%>" required/>

                <label>Description</label>
                <textarea name="description" required><%= product.getDescription()%></textarea>

                <div class="form-row">
                    <div>
                        <label>Status</label>
                        <input type="text" name="status" value="<%= product.getStatus() != null ? product.getStatus() : "Active"%>" readonly/>
                    </div>
                    <div>
                        <label>Category</label>
                        <select name="cateID">
                            <% for (CategoryDTO c : categories) {%>
                            <option value="<%= c.getCateID()%>" <%= product.getCateID() == c.getCateID() ? "selected" : ""%>>
                                <%= c.getCateName()%>
                            </option>
                            <% }%>
                        </select>
                    </div>
                </div>

                <label>Image (URL)</label>
                <input type="text" name="imageUrl" placeholder="Hoặc nhập URL ảnh..." value="<%= product.getProductImage() != null ? product.getProductImage() : ""%>"/>

                <label>Variants</label>

                <table class="variant-table">
                    <thead>
                        <tr>

                            <%
                                boolean showSize = false;
                                boolean showColor = false;

                                if (!variants.isEmpty()) {
                                    ProductVariantDTO sample = variants.get(0);
                                    showSize = sample.getSizeID() != 0;
                                    showColor = sample.getColorID() != 0;
                                }
                            %>

                            <% if (showSize) { %><th>Size</th><% } %>
                            <% if (showColor) { %><th>Color</th><% } %>

                            <th>Quantity</th>
                            <th>Price</th>

                        </tr>
                    </thead>

                    <tbody>

                        <%
                            int idx = 0;

                            for (ProductVariantDTO v : variants) {

                                String sizeName = (v.getSizeID() != 0) ? sizeDAO.getSizeNameById(v.getSizeID()) : null;
                                String colorName = (v.getColorID() != 0) ? colorDAO.getColorNameById(v.getColorID()) : null;
                        %>

                        <tr>

                            <td style="display:none;">
                                <input type="hidden" name="variantId_<%= idx%>" value="<%= v.getAttributeID()%>"/>
                            </td>

                            <% if (showSize) {%>
                            <td>
                                <select name="size_<%= idx%>">
                                    <% for (String s : allSizes) {%>
                                    <option value="<%= s%>" <%= s.equals(sizeName) ? "selected" : ""%>><%= s%></option>
                                    <% } %>
                                </select>
                            </td>
                            <% } %>

                            <% if (showColor) {%>
                            <td>
                                <select name="color_<%= idx%>">
                                    <% for (String c : allColors) {%>
                                    <option value="<%= c%>" <%= c.equals(colorName) ? "selected" : ""%>><%= c%></option>
                                    <% } %>
                                </select>
                            </td>
                            <% }%>

                            <td>
                                <input type="number" name="quantity_<%= idx%>" value="<%= v.getQuantity()%>" required>
                            </td>

                            <td>
                                <input type="number" step="0.01" name="price_<%= idx%>" value="<%= v.getPrice()%>" required>
                            </td>

                        </tr>

                        <%
                                idx++;
                            }
                        %>

                    </tbody>
                </table>

                <input type="hidden" name="variantCount" value="<%= variants.size()%>"/>
                <button type="submit" class="submit-btn">Update Product</button>          
            </form>
        </div>

    </body>
</html>
