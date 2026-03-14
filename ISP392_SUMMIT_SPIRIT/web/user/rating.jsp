<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="dao.ReviewDAO, dto.ReviewDTO, dto.UserDTO , dto.CartDTO" %>
<%@ page import="dao.ProductDAO" %>
<%@ page import="dto.ProductDTO" %>
<%@ page import="java.util.List" %>
<%
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    CartDTO cart = (CartDTO) session.getAttribute("CART");
    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;
    String email = request.getParameter("email");
    String phone = request.getParameter("phone");
    String address = request.getParameter("address");
    String orderDate = request.getParameter("orderDate");
    String status = request.getParameter("status");
    String orderID = request.getParameter("orderID");

    String productID = request.getParameter("productID");
    int productIdInt = Integer.parseInt(productID);
    if (address != null) address = java.net.URLDecoder.decode(address, "UTF-8");
    if (orderDate != null) orderDate = java.net.URLDecoder.decode(orderDate, "UTF-8");
    if (status != null) status = java.net.URLDecoder.decode(status, "UTF-8");

    ProductDAO productDAO = new ProductDAO();
    ProductDTO product = productDAO.getFullProductByID(productIdInt);

    ReviewDAO reviewDAO = new ReviewDAO();
    List<ReviewDTO> reviews = reviewDAO.getReviewsByProductID(productIdInt);
    int totalReviews = reviewDAO.countReviewsByProduct(productIdInt);
    double avgRating = reviewDAO.averageRatingByProduct(productIdInt);
%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Submit Review</title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/rating.css">
        <style>
            body {
                font-family: 'Kumbh Sans', sans-serif;
                margin: 0;
                padding: 0;
                background-color: #f9f9f9;
            }

            .header {
                padding: 10px 40px;
                background-color: #ffffff;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }

            .header img {
                height: 60px;
                cursor: pointer;
            }

            .nav-links {
                display: flex;
                align-items: center;
                gap: 20px;
            }

            .nav-links a {
                text-decoration: none;
                color: #000;
                font-weight: 500;
            }
            .cart-icon {
                position: relative;
                display: inline-block;
                font-size: 20px;
                color: #000;
                margin-left: 15px;
            }

            .cart-badge {
                position: absolute;
                top: -8px;
                right: -10px;
                background-color: #e53935;
                color: white;
                border-radius: 50%;
                padding: 2px 6px;
                font-size: 12px;
                font-weight: bold;
            }

            .user-dropdown {
                position: relative;
            }

            .user-name {
                cursor: pointer;
                font-weight: bold;
                padding: 10px;
                color: #000;
            }

            .dropdown-menu {
                display: none;
                position: absolute;
                top: 100%;
                right: 0;
                background-color: white;
                box-shadow: 0 8px 16px rgba(0,0,0,0.2);
                border-radius: 4px;
                z-index: 1000;
                min-width: 150px;
            }

            .dropdown-menu a {
                display: block;
                padding: 10px 16px;
                text-decoration: none;
                color: black;
            }

            .dropdown-menu a:hover {
                background-color: #f1f1f1;
            }
            .back-btn{
                display: inline-block;
                margin: 20px 20px;
                padding: 8px 16px;
                background-color: #234C45;
                color: white;
                font-weight: bold;
                border: none;
                border-radius: 6px;
                text-decoration: none;
                transition: background-color 0.3s ease;
            }
            .back-btn:hover {
                background-color: #28a745;
            }
            .rating-stars i {
                font-size: 24px;
                color: gold;
                cursor: pointer;
                transition: color 0.2s ease;
            }
            .form-buttons {
                display: flex;
                gap: 10px;
                margin-top: 10px;

            }
        </style>
    </head>
    <body>
        <!-- Header -->
        <div class="header">
            <a href="<%= request.getContextPath() %>/user/homepage.jsp">
                <img src="<%= request.getContextPath() %>/image/summit_logo.png" alt="Logo">
            </a>
            <div class="nav-links">
                <a href="<%= request.getContextPath() %>/user/homepage.jsp"><i class="fas fa-home"></i></a>
                <a href="<%= request.getContextPath() %>/user/cart.jsp" class="cart-icon">
                    <i class="fas fa-shopping-cart"></i>
                    <% if (cartItemCount > 0) { %>
                    <span class="cart-badge"><%= cartItemCount %></span>
                    <% } %>
                </a>
                <div class="user-dropdown">
                    <div class="user-name" onclick="toggleMenu()"><i class="fas fa-user"></i></div>
                    <div id="dropdown" class="dropdown-menu">
                        <a href="<%= request.getContextPath() %>/user/profile.jsp"><%= loginUser.getFullName() %></a>
                        <a href="<%= request.getContextPath() %>/MainController?action=Logout">Logout</a>
                    </div>
                </div>
            </div>
        </div>
        <script>
            function toggleMenu() {
                const menu = document.getElementById("dropdown");
                menu.style.display = menu.style.display === "block" ? "none" : "block";
            }
            document.addEventListener("click", function (event) {
                const dropdown = document.getElementById("dropdown");
                const userBtn = document.querySelector(".user-name");
                if (!dropdown.contains(event.target) && !userBtn.contains(event.target)) {
                    dropdown.style.display = "none";
                }
            });
        </script>
        <button class="back-btn" type="button" onclick="history.back();">Back</button>
        <div class="container">
            <!-- LEFT: Review Form -->
            <div class="review-left">
                <h2>📝 Submit Your Review</h2>
                <%
                    String reviewStatus = request.getParameter("review");
                    if ("success".equals(reviewStatus)) {
                %>
                <div style="color: green; font-weight: bold; margin: 10px 0;">
                    ✅ Cảm ơn bạn đã đánh giá sản phẩm!
                </div>
                <% } %>

                <form action="<%= request.getContextPath() %>/SubmitReview" method="post" onsubmit="return validateForm()">
                    <!-- Hidden values to preserve after submit -->
                    <input type="hidden" name="productId" value="<%= productID %>">
                    <input type="hidden" name="orderId" value="<%= orderID %>">
                    <input type="hidden" name="email" value="<%= email %>">
                    <input type="hidden" name="phone" value="<%= phone %>">
                    <input type="hidden" name="address" value="<%= address %>">
                    <input type="hidden" name="orderDate" value="<%= orderDate %>">
                    <input type="hidden" name="status" value="<%= status %>">
                    <input type="hidden" name="rating" id="ratingInput" value="0">

                    <label>Email:</label>
                    <input type="text" readonly value="<%= email %>">

                    <label>Phone:</label>
                    <input type="text" readonly value="<%= phone %>">

                    <label>Address:</label>
                    <input type="text" readonly value="<%= address %>">



                    <label>Rating:</label>
                    <div class="rating-stars" id="starContainer">
                        <% for (int i = 1; i <= 5; i++) { %>
                        <i class="fa-regular fa-star" data-index="<%= i %>"></i>
                        <% } %>
                    </div>

                    <label>Review Summary:</label>
                    <textarea name="comment" rows="5" required placeholder="Write your thoughts..."></textarea>

                    <!-- ✅ Nút Submit + Back -->
                    <div class="form-buttons">
                        <button type="submit">Submit Review</button>

                    </div>
                </form>
            </div>

            <!-- RIGHT: Product image + Existing reviews -->
            <div class="review-right">
                <% if (product != null) { %>
                <img src="<%= product.getProductImage() %>" alt="Product Image">
                <% } %>

                <h2>
                    ⭐ <%= String.format("%.1f", avgRating) %> / 5
                    (<%= totalReviews %> reviews)
                </h2>

                <% if (reviews != null && !reviews.isEmpty()) {
            for (ReviewDTO review : reviews) { %>
                <div style="margin-bottom: 15px; text-align: left; padding: 10px; border-bottom: 1px solid #ccc;">
                    <strong>Rating:</strong>
                    <% for (int i = 1; i <= 5; i++) { %>
                    <i class="fa<%= i <= review.getRating() ? "s" : "r" %> fa-star" style="color: gold;"></i>
                    <% } %><br>
                    <strong>Comment:</strong> <%= review.getComment() %><br>
                    <strong>User:</strong> <%= review.getUserFullName() %><br>
                    <small><%= review.getReviewDate() %></small>
                </div>
                <% } } else { %>
                <p>No reviews yet.</p>
                <% } %>
            </div>
        </div>

        <script>
            const stars = document.querySelectorAll("#starContainer i");
            const ratingInput = document.getElementById("ratingInput");
            let selectedRating = 0;

            stars.forEach((star, index) => {
                star.addEventListener("mouseover", () => {
                    for (let i = 0; i < stars.length; i++) {
                        stars[i].classList.remove("fa-solid");
                        stars[i].classList.add("fa-regular");
                        if (i <= index)
                            stars[i].classList.add("fa-solid");
                    }
                });

                star.addEventListener("click", () => {
                    selectedRating = index + 1;
                    ratingInput.value = selectedRating;
                });

                star.addEventListener("mouseleave", () => {
                    for (let i = 0; i < stars.length; i++) {
                        stars[i].classList.remove("fa-solid");
                        stars[i].classList.add("fa-regular");
                        if (i < selectedRating)
                            stars[i].classList.add("fa-solid");
                    }
                });
            });

            window.addEventListener("DOMContentLoaded", () => {
                for (let i = 0; i < selectedRating; i++) {
                    stars[i].classList.add("fa-solid");
                    stars[i].classList.remove("fa-regular");
                }
            });

            function validateForm() {
                if (ratingInput.value === "0") {
                    alert("Please select a rating before submitting.");
                    return false;
                }
                return true;
            }
        </script>
    </body>
</html>
