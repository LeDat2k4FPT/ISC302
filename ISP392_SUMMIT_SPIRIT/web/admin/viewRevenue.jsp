<%-- 
    Document   : viewRevenue
    Created on : Jun 24, 2025, 6:08:49 PM
    Author     : Hanne
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, com.google.gson.Gson"%>
<%@page import="dto.RevenueDTO, dto.ProductSoldDTO"%>

<%
    List<RevenueDTO> revenueList = (List<RevenueDTO>) request.getAttribute("revenueList");
    List<RevenueDTO> topProducts = (List<RevenueDTO>) request.getAttribute("topProducts");
    List<RevenueDTO> colorStats = (List<RevenueDTO>) request.getAttribute("colorStats");

    String selectedMonth = (String) request.getAttribute("selectedMonth");
    String selectedYear = (String) request.getAttribute("selectedYear");
    String selectedCategory = (String) request.getAttribute("selectedCategory");

    if (selectedMonth == null) selectedMonth = "all";
    if (selectedYear == null) selectedYear = "all";
    if (selectedCategory == null) selectedCategory = "all";

    Gson gson = new Gson();
    String revenueJson = gson.toJson(revenueList);
    String topProductsJson = gson.toJson(topProducts);
    String colorStatsJson = gson.toJson(colorStats);

    boolean isAllCategory = "all".equals(selectedCategory);
%>

<h2 class="text-center mb-4">Revenue Analytics</h2>

<!-- Filter Form -->
<form action="${pageContext.request.contextPath}/ViewRevenueController" method="get" class="row g-3 mb-4">
    <div class="col-md-3">
        <label>Month:</label>
        <select name="monthValue" class="form-select">
            <option value="all" <%= "all".equals(selectedMonth) ? "selected" : "" %>>All</option>
            <% for (int i = 1; i <= 12; i++) { %>
                <option value="<%= i %>" <%= String.valueOf(i).equals(selectedMonth) ? "selected" : "" %>>Month <%= i %></option>
            <% } %>
        </select>
    </div>
    <div class="col-md-3">
    <label>Year:</label>
    <select name="yearValue" class="form-select">
        <option value="all" <%= "all".equals(selectedYear) ? "selected" : "" %>>All</option>
        <% for (int y = 2023; y <= 2026; y++) { %>
            <option value="<%= y %>" <%= String.valueOf(y).equals(selectedYear) ? "selected" : "" %>><%= y %></option>
        <% } %>
    </select>
</div>
    <div class="col-md-3">
        <label>Category:</label>
        <select name="category" class="form-select">
            <option value="all" <%= "all".equals(selectedCategory) ? "selected" : "" %>>All</option>
            <% 
                List<dto.CategoryDTO> categoryList = (List<dto.CategoryDTO>) request.getAttribute("categoryList");
                if (categoryList != null) {
                    for (dto.CategoryDTO cate : categoryList) {
            %>
            <option value="<%= cate.getCateName() %>" <%= cate.getCateName().equals(selectedCategory) ? "selected" : "" %>><%= cate.getCateName() %></option>
            <%      }
                }
            %>
        </select>

    </div>
    <div class="col-md-3 d-flex align-items-end">
        <button class="btn btn-dark w-100">Filter</button>
    </div>
</form>

<!-- Chart Section -->
<div class="row">
    <div class="col-md-6 mb-4 d-flex align-items-stretch">
        <div class="w-100" style="background: #fff; border-radius: 10px; box-shadow: 0 2px 8px #eee; padding: 20px; height: 400px; display: flex; flex-direction: column; justify-content: center;">
            <h5 class="text-center">Line Chart: Revenue Over Time</h5>
            <div style="flex:1; display:flex; align-items:center; justify-content:center;">
                <canvas id="lineChart" style="max-width:100%; max-height:320px;"></canvas>
            </div>
        </div>
    </div>
    <div class="col-md-6 mb-4 d-flex align-items-stretch">
        <div class="w-100" style="background: #fff; border-radius: 10px; box-shadow: 0 2px 8px #eee; padding: 20px; height: 400px; display: flex; flex-direction: column; justify-content: center;">
            <h5 class="text-center">Pie Chart: Sold by Color</h5>
            <div style="flex:1; display:flex; align-items:center; justify-content:center;">
                <canvas id="pieChart" style="max-width:100%; max-height:320px;"></canvas>
            </div>
        </div>
    </div>
    <div class="col-md-12 mb-4">
        <h5 class="text-center">Bar Chart: Top Selling Products</h5>
        <canvas id="barChart"></canvas>
    </div>
</div>

<!-- Revenue Table -->
<table class="table table-bordered table-hover">
    <thead class="table-dark">
        <tr>
            <% if (isAllCategory) { %>
                <th>Product Name</th>
                <th>Total Quantity Sold</th>
                <th>Total Revenue (VND)</th>
            <% } %>
        </tr>
    </thead>
    <tbody>
        <% if (isAllCategory) {
            if (topProducts != null && !topProducts.isEmpty()) {
                for (dto.RevenueDTO p : topProducts) { %>
                    <tr>
                        <td><%= p.getProductName() %></td>
                        <td><%= p.getTotalQuantity() %></td>
                        <td><%= String.format("%,.0f", p.getTotalRevenue()) %></td>
                    </tr>
        <%      }
            } else { %>
                <tr><td colspan="3">No data available.</td></tr>
        <%  }
        } else { %>
            <c:forEach var="r" items="${revenueList}">
                <tr>
                    <td>${r.categoryName}</td>
                    <td>${r.totalQuantity}</td>
                    <td><fmt:formatNumber value="${r.totalRevenue}" type="number" groupingUsed="true"/></td>
                </tr>
            </c:forEach>
        <% } %>
    </tbody>
</table>

<% if (!isAllCategory) { %>
    <h5 class="mt-4">Top 5 Best-Selling Products in Category: <%= selectedCategory %></h5>
    <table class="table table-bordered table-hover">
        <thead class="table-dark">
            <tr>
                <th>Product Name</th>
                <th>Total Quantity Sold</th>
                <th>Total Revenue (VND)</th>
            </tr>
        </thead>
        <tbody>
            <% if (topProducts != null && !topProducts.isEmpty()) {
                for (dto.RevenueDTO p : topProducts) { %>
                    <tr>
                        <td><%= p.getProductName() %></td>
                        <td><%= p.getTotalQuantity() %></td>
                        <td><%= String.format("%,.0f", p.getTotalRevenue()) %></td>
                    </tr>
            <%  }
            } else { %>
                <tr><td colspan="3">No data available.</td></tr>
            <% } %>
        </tbody>
    </table>
<% } %>

<!-- Chart.js CDN -->
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<!-- Render Charts -->
<script>
    const revenueData = <%= revenueJson %>;
    const topProducts = <%= topProductsJson %>;
    const colorStats = <%= colorStatsJson %>;

    // LINE CHART: Revenue over time
const lineLabels = revenueData.map(item => item.month); // chỉ hiện số tháng
    const lineValues = revenueData.map(item => item.totalRevenue);

    new Chart(document.getElementById('lineChart'), {
        type: 'line',
        data: {
            labels: lineLabels,
            datasets: [{
                label: 'Revenue (VND)',
                data: lineValues,
                fill: true,
                tension: 0.4,
                borderColor: '#39504A',
                backgroundColor: 'rgba(57, 80, 74, 0.1)'
            }]
        }
    });

    // PIE CHART: Quantity by Color
    const pieLabels = colorStats.map(item => item.productName + (item.colorName ? ` (${item.colorName})` : ''));
    const pieValues = colorStats.map(item => item.totalQuantity);

    new Chart(document.getElementById('pieChart'), {
        type: 'pie',
        data: {
            labels: pieLabels,
            datasets: [{
                data: pieValues,
                backgroundColor: pieLabels.map(() => `hsl(${Math.random() * 360}, 60%, 60%)`)
            }]
        }
    });

    // BAR CHART: Top Selling Products
    const barLabels = topProducts.map(item => item.productName);
    const barValues = topProducts.map(item => item.totalQuantity);

    new Chart(document.getElementById('barChart'), {
        type: 'bar',
        data: {
            labels: barLabels,
            datasets: [{
                label: 'Quantity Sold',
                data: barValues,
                backgroundColor: '#234C45'
            }]
        }
    });
</script>