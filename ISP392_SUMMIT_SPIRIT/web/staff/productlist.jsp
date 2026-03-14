<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.Map, dto.ProductDTO, dto.ProductVariantDTO, java.util.Comparator" %>

<%
    String keyword = (String) session.getAttribute("keyword");
    if (keyword == null) keyword = "";

    List<ProductDTO> productList = (List<ProductDTO>) session.getAttribute("productList");
    if (productList == null) {
        response.sendRedirect(request.getContextPath() + "/ProductListController");
        return;
    }
    Map<Integer, List<ProductVariantDTO>> variantMap = (Map<Integer, List<ProductVariantDTO>>) session.getAttribute("variantMap");

    if (productList != null) {
        productList.sort(Comparator.comparingInt(ProductDTO::getProductID));
    }

    String msg = request.getParameter("msg");
    String type = request.getParameter("type");
%>

<head>
    <meta charset="UTF-8">
    <title>Product</title>
    <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/productList.css">

</head>

<% if (msg != null && type != null) { %>
<div class="alert alert-<%= type %>" role="alert" style="margin-top: 10px;">
    <%= msg %>
</div>
<script>
    setTimeout(() => {
        const alert = document.querySelector('.alert');
        if (alert)
            alert.style.display = 'none';
    }, 3000);
</script>
<% } %>
<div class="product-section">
    <div class="product-box shadow-sm rounded-4 p-4 bg-white">

        <!-- 👇 Đây là phần đã chỉnh đúng -->
        <div class="product-header">
            <h2>📦 Product</h2>
            <a href="<%=request.getContextPath()%>/staff/staffDashboard.jsp?page=addProduct.jsp" class="btn-add-product">
                <i class="bi bi-plus-circle"></i> Add Product
            </a>
        </div>

        <form class="search-form" action="<%=request.getContextPath()%>/ProductListController" method="get">
            <input type="text" name="keyword" value="<%= keyword %>" placeholder="Search product name..." />
            <button type="submit">Search</button>
        </form>

        <div class="product-table-wrapper">
            <table class="product-table">
                <thead>
                    <tr>
                        <th rowspan="2">ID</th>
                        <th rowspan="2">Product name</th>
                        <th rowspan="2">Category</th>
                        <th colspan="4" style="text-align:center;">Variant</th>
                        <th rowspan="2">Stock</th>
                        <th rowspan="2">Status</th>
                        <th rowspan="2">Actions</th>
                    </tr>
                    <tr>
                        <th>Size</th>
                        <th>Color</th>
                        <th>Quantity</th>
                        <th>Price</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        if (productList != null && !productList.isEmpty()) {
                            for (ProductDTO p : productList) {
                                if (p.getStatus() != null && !"active".equalsIgnoreCase(p.getStatus())) continue;
                                List<ProductVariantDTO> variants = variantMap != null ? variantMap.get(p.getProductID()) : null;
                                if (variants != null) {
                                    variants.sort(Comparator
                                        .comparing(ProductVariantDTO::getSizeName, Comparator.nullsLast(String::compareTo))
                                        .thenComparing(ProductVariantDTO::getColorName, Comparator.nullsLast(String::compareTo)));
                                }
                                int rowCount = (variants != null) ? variants.size() : 0;
                                boolean firstRow = true;

                                if (rowCount > 0) {
                                    for (ProductVariantDTO v : variants) {
                    %>
                    <tr>
                        <% if (firstRow) { %>
                        <td rowspan="<%= rowCount %>"><%= p.getProductID() %></td>
                        <td rowspan="<%= rowCount %>"><%= p.getProductName() %></td>
                        <td rowspan="<%= rowCount %>"><%= p.getCateName() %></td>
                        <% } %>
                        <td><%= v.getSizeName() != null ? v.getSizeName() : "" %></td>
                        <td><%= v.getColorName() != null ? v.getColorName() : "" %></td>
                        <td><%= v.getQuantity() != 0 ? v.getQuantity() : "" %></td>
                        <td><%= v.getPrice() != 0 ? String.format("%,.0f", v.getPrice()) : "" %></td>
                        <% if (firstRow) { %>
                        <td rowspan="<%= rowCount %>"><%= p.getStock() %></td>
                        <td rowspan="<%= rowCount %>"><%= p.getStatus() != null ? p.getStatus() : "active" %></td>
                        <td rowspan="<%= rowCount %>" class="actions">
                            <a href="<%= request.getContextPath() %>/EditProductController?productID=<%= p.getProductID() %>">🖋️</a>
                            <a href="<%= request.getContextPath() %>/DeleteProductController?productID=<%= p.getProductID() %>"
                               onclick="return confirm('Are you sure you want to deactivate this product?')">🗑️</a>
                        </td>
                        <% } %>
                    </tr>
                    <%
                                        firstRow = false;
                                    }
                                } else {
                    %>
                    <tr>
                        <td><%= p.getProductID() %></td>
                        <td><%= p.getProductName() %></td>
                        <td><%= p.getCateName() %></td>
                        <td colspan="4" style="text-align:center;">No variants</td>
                        <td><%= p.getStock() %></td>
                        <td><%= p.getStatus() != null ? p.getStatus() : "active" %></td>
                        <td class="actions">
                            <a href="<%= request.getContextPath() %>/EditProductController?productID=<%= p.getProductID() %>">🖋️</a>
                            <a href="<%= request.getContextPath() %>/DeleteProductController?productID=<%= p.getProductID() %>"
                               onclick="return confirm('Are you sure you want to deactivate this product?')">🗑️</a>
                        </td>
                    </tr>
                    <%
                                }
                            }
                        } else {
                    %>
                    <tr><td colspan="10" class="no-data">No products found.</td></tr>
                    <% } %>
                </tbody>
            </table>
        </div> <!-- đóng product-box -->
    </div>
</div>


<%
    session.removeAttribute("productList");
    session.removeAttribute("variantMap");
    session.removeAttribute("keyword");
%>