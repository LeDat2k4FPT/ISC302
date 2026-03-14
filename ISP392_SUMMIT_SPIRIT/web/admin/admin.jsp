<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="dto.UserDTO"%>

<%
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null || !"Admin".equalsIgnoreCase(loginUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    String currentPage = (String) request.getAttribute("page");
    if (currentPage == null) {
        currentPage = request.getParameter("page") != null ? request.getParameter("page") : "adminHome.jsp";
    }
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Admin Panel</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

        <style>
            .sidebar-custom {
                background: #fff;
                min-height: 100vh;
                color: #234C45;
                border-right: 1px solid #ddd;
                padding-top: 20px;
            }
            .sidebar-custom .nav-link {
                color: #234C45;
                font-weight: 500;
                padding: 10px 15px;
            }
            .sidebar-custom .nav-link.active, .sidebar-custom .nav-link:hover {
                background: #f0f0f0;
                color: #234C45;
            }
            .top-bar-custom {
                background: #39504A;
                color: #fff;
                padding: 1rem 2rem;
                font-size: 18px;
                font-weight: bold;
                position: relative;
            }
            .user-dropdown {
                position: relative;
            }
            .dropdown-menu {
                position: absolute;
                right: 0;
                top: 100%;
                background-color: white;
                border-radius: 4px;
                box-shadow: 0 4px 8px rgba(0,0,0,0.1);
                z-index: 1000;
                min-width: 160px;
            }
            .dropdown-item {
                padding: 10px 16px;
                display: block;
                text-decoration: none;
                color: black;
            }
            .dropdown-item:hover {
                background-color: #f1f1f1;
            }
            .dropdown-header {
                font-weight: bold;
                padding: 10px 16px;
                border-bottom: 1px solid #ddd;
            }
            .user-icon {
                cursor: pointer;
                font-size: 22px;
            }
        </style>
    </head>
    <body>

        <!-- TOP BAR with user dropdown -->
        <div class="top-bar-custom d-flex justify-content-between align-items-center">
            <div>Hello Admin </div>

            <div class="position-relative user-dropdown">
                <i class="bi bi-person-fill user-icon" onclick="toggleDropdown()"></i>
                <div id="adminDropdown" class="dropdown-menu" style="display: none;">
                    <div class="dropdown-header"><%= loginUser.getFullName() %></div>
                    <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/admin.jsp?page=adminProfile.jsp">
                        <i class="bi bi-person"></i> My Profile
                    </a>

                    <form action="${pageContext.request.contextPath}/LogoutController" method="post" style="margin: 0;">
                        <button type="submit" class="dropdown-item text-danger">
                            <i class="bi bi-box-arrow-right"></i> Logout
                        </button>
                    </form>
                </div>
            </div>
        </div>

        <!-- MAIN LAYOUT -->
        <div class="container-fluid">
            <div class="row">
                <!-- SIDEBAR -->
                <nav class="col-md-2 sidebar-custom">
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link <%= "adminHome.jsp".equals(currentPage) ? "active" : "" %>" 
                               href="${pageContext.request.contextPath}/admin/admin.jsp?page=adminHome.jsp">
                                <i class="bi bi-house"></i> Home
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link <%= "viewRevenue.jsp".equals(currentPage) ? "active" : "" %>" 
                               href="${pageContext.request.contextPath}/ViewRevenueController">
                                <i class="bi bi-bar-chart"></i> View Revenue
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link <%= "adminManageUser.jsp".equals(currentPage) ? "active" : "" %>"
                               href="${pageContext.request.contextPath}/ManageUserAccountController">
                                <i class="bi bi-people"></i> Manage Users
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link <%= "manageVoucher.jsp".equals(currentPage) ? "active" : "" %>"
                               href="${pageContext.request.contextPath}/ManageVoucherController">
                                <i class="bi bi-ticket"></i> Manage Vouchers
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link <%= "potentialCustomer.jsp".equals(currentPage) ? "active" : "" %>"
                               href="${pageContext.request.contextPath}/ManagePotentialCustomerController">
                                <i class="bi bi-star"></i> Potential Customers
                            </a>
                        </li>
                    </ul>
                </nav>

                <!-- MAIN CONTENT -->
                <main class="col-md-10 p-4">
                    <jsp:include page="<%= currentPage %>"/>
                </main>
            </div>
        </div>

        <!-- JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                    function toggleDropdown() {
                        const dropdown = document.getElementById("adminDropdown");
                        dropdown.style.display = (dropdown.style.display === "block") ? "none" : "block";
                    }

                    document.addEventListener("click", function (event) {
                        const icon = document.querySelector(".user-icon");
                        const dropdown = document.getElementById("adminDropdown");
                        if (!dropdown.contains(event.target) && !icon.contains(event.target)) {
                            dropdown.style.display = "none";
                        }
                    });
        </script>
    </body>
</html>
