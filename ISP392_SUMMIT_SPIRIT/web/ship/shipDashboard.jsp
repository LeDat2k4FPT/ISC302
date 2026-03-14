<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dto.UserDTO" %>
<%
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null || !"Shipper".equals(loginUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    String currentPage = request.getParameter("page");
    if (currentPage == null) {
        currentPage = (String) request.getAttribute("page");
    }
    if (currentPage == null || currentPage.trim().isEmpty()) {
        currentPage = "shipHome.jsp"; // Trang mặc định
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Shipper Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/ship.css">
    <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body>
    <div class="top-bar-custom d-flex justify-content-between align-items-center">
        <span>Hello, <%= loginUser.getFullName() %></span>

        <!-- ✅ Link đúng đến trang profile -->
        <a href="<%= request.getContextPath() %>/ship/shipDashboard.jsp?page=shipProfile.jsp" class="user-icon-link" title="Profile">
            <i class="bi bi-person-circle"></i>
        </a>
    </div>

    <div class="dashboard-container">
        <aside class="sidebar">
            <div class="sidebar-header">
                <i class="bi bi-truck"></i>
                <span class="brand-name">Shipper</span>
            </div>

            <nav class="sidebar-nav">
                <a href="<%=request.getContextPath()%>/ship/shipDashboard.jsp?page=shipHome.jsp"
                   class="nav-item <%= currentPage.contains("shipHome") ? "active" : "" %>">
                    <i class="bi bi-house"></i> Home
                </a>
                <a href="<%=request.getContextPath()%>/ShippingListController"
                   class="nav-item <%= currentPage.contains("shippingList") ? "active" : "" %>">
                    <i class="bi bi-box"></i> Shipping List
                </a>
                <a href="<%=request.getContextPath()%>/DeliveryListController"
                   class="nav-item <%= currentPage.contains("deliveryProof") ? "active" : "" %>">
                    <i class="bi bi-camera"></i> Delivery Proof
                </a>
                <a href="<%=request.getContextPath()%>/DeliveryHistoryController"
                   class="nav-item <%= currentPage.contains("historyShipping") ? "active" : "" %>">
                    <i class="bi bi-clock-history"></i> History
                </a>
            </nav>

            <form action="<%=request.getContextPath()%>/LogoutController" method="post" class="logout-form">
                <button type="submit" class="logout-btn">
                    <i class="bi bi-power"></i> Logout
                </button>
            </form>
        </aside>

        <main class="content">
            <%-- ✅ Load nội dung của từng subpage dựa trên ?page=... --%>
            <jsp:include page="<%= currentPage %>" />
        </main>
    </div>
</body>
</html>
