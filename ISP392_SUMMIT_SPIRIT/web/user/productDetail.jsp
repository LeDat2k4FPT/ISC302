<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.ProductDAO, dto.ProductDTO, dto.CartDTO, dto.UserDTO" %>
<%@ page import="dao.ProductVariantDAO, dao.ReviewDAO" %>
<%@ page import="dto.ReviewDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.OrderDAO" %>
<%@ page import="java.util.Map, java.util.HashMap" %>
<%
    int productID = Integer.parseInt(request.getParameter("id"));
    boolean fromSaleOff = "true".equals(request.getParameter("fromSaleOff")); // ✅ Check if from sale page
    ProductDTO product = null;
    String categoryName = "";
    String categoryParam = "";

    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    boolean canReview = false;
if (loginUser != null) {
    OrderDAO orderDAO = new OrderDAO();
    canReview = orderDAO.hasUserPurchasedProduct(loginUser.getUserID(), productID);
}
    CartDTO cart = (CartDTO) session.getAttribute("CART");
    int totalQuantity = (cart != null) ? cart.getTotalQuantity() : 0;
    int cartItemCount = (cart != null) ? cart.getCartItems().size() : 0;
    int alreadyInCart = 0;
    if (cart != null && cart.getCartItem(productID) != null) {
        alreadyInCart = cart.getCartItem(productID).getQuantity();
    }

    List<String> sizeList = new java.util.ArrayList<>();
    List<String> colorList = new java.util.ArrayList<>();
    List<ReviewDTO> reviews = new java.util.ArrayList<>();

    int totalReviews = 0;
double avgRating = 0;
    Map<String, Integer> variantStockMap = new HashMap<>();


    try {
        ProductDAO dao = new ProductDAO();
        product = dao.getFullProductByID(productID);
        if (product != null) {
            int cateID = product.getCateID();
            switch (cateID) {
                case 1: categoryName = "Pants"; categoryParam = "quan"; break;
                case 2: categoryName = "Shirts"; categoryParam = "ao"; break;
                case 3: categoryName = "Backpacks"; categoryParam = "balo"; break;
                case 4: categoryName = "Gears"; categoryParam = "dungcu"; break;
                case 5: categoryName = "Tents"; categoryParam = "trai"; break;
                case 6: categoryName = "Hats"; categoryParam = "mu"; break;
                case 7: categoryName = "Camping Stove"; categoryParam = "camping"; break;
                default: categoryName = "Unknown"; categoryParam = "#"; break;
            }

            ProductVariantDAO variantDAO = new ProductVariantDAO();
            sizeList = variantDAO.getAvailableSizesByProductId(productID);
            colorList = variantDAO.getAvailableColorsByProductId(productID);

            ReviewDAO reviewDAO = new ReviewDAO();
            reviews = reviewDAO.getReviewsByProductID(productID);

            totalReviews = reviewDAO.countReviewsByProduct(productID);
            avgRating = reviewDAO.averageRatingByProduct(productID);

            // ✅ Load tồn kho từng biến thể size + color
            for (String size : sizeList) {
                for (String color : colorList) {
                    int stock = variantDAO.getAvailableQuantity(productID, size, color);
                    int inCart = (cart != null) ? cart.getQuantityByVariant(productID, size, color) : 0;
                    int available = stock - inCart;
                    if (available < 0) available = 0;
                    variantStockMap.put(size + "_" + color, available);
                }
            }

        }
    } catch (Exception e) {
        e.printStackTrace();
    }



%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title><%= product != null ? product.getProductName() : "Product Detail" %></title>
        <link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="<%= request.getContextPath() %>/css/productDetail.css">
    </head>
    <body>
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
                        <a href="<%= request.getContextPath() %>/user/profile.jsp"><%= loginUser != null ? loginUser.getFullName() : "Account" %></a>
                        <a href="MainController?action=Logout">Logout</a>
                    </div>
                </div>
            </div>
        </div>
        <script>
            window.maxAvailable = 0;
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

        <% if (product != null) {
            double originalPrice = product.getPrice();
            double discountedPrice = Math.round(originalPrice * 0.8);
        %>
        <div class="layout">
            <% if ("success".equals(request.getParameter("review"))) { %>
            <div style="padding: 10px; background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; margin-bottom: 20px;">
                Thank you for your review!
            </div>
            <% } %>

            <div class="breadcrumb">
                <a class="back-button styled-button" href="javascript:history.back()">← Back</a>
            </div>

            <div class="product-section">
                <div class="product-image">
                    <img src="<%= product.getProductImage() %>" alt="Product Image">
                </div>
                <div class="product-info">
                    <h1><%= product.getProductName() %></h1>

                    <% if (fromSaleOff) { %>
                    <div class="price">
                        <span style="text-decoration: line-through; color: gray;">
                            <%= String.format("%,.0f", originalPrice) %> VND
                        </span><br>
                        <span style="color: red; font-weight: bold;">
                            <%= String.format("%,.0f", discountedPrice) %> VND (-20%)
                        </span>
                    </div>
                    <% } else { %>
                    <div class="price" id="price-display" style="font-weight:bold;">
                        <%= String.format("%,.0f", originalPrice) %> VND
                    </div>
                    <% } %>

                    <script>
                        const variantStockMap = {
                        <%
    for (String size : sizeList.isEmpty() ? java.util.Arrays.asList("") : sizeList) {
        for (String color : colorList.isEmpty() ? java.util.Arrays.asList("") : colorList) {
            int stock = new ProductVariantDAO().getAvailableQuantity(productID, size, color);
            int inCart = (cart != null) ? cart.getQuantityByVariant(productID, size, color) : 0;
            int available = stock - inCart;
            if (available < 0) available = 0;
            String key = (size.isEmpty() && color.isEmpty()) ? "default" :
                         (!size.isEmpty() && !color.isEmpty()) ? size + "_" + color :
                         size.isEmpty() ? color : size;
                        %>
                            "<%= key %>": <%= available %>,
                        <%
        }
    }
                        %>

                        };
                    </script>

                    <form action="<%= request.getContextPath() %>/MainController" method="post" onsubmit="return validateBeforeSubmit(window.maxAvailable)">
                        <input type="hidden" name="productID" value="<%= product.getProductID() %>">
                        <input type="hidden" name="price" id="price-input" value="<%= fromSaleOff ? discountedPrice : originalPrice %>">
                        <input type="hidden" name="fromSaleOff" value="<%= fromSaleOff %>">

                        <% if (!sizeList.isEmpty()) { %>
                        <div class="select-group">
                            <label>Size:</label>
                            <div class="option-buttons" id="size-options">
                                <% for (String size : sizeList) { %>
                                <button type="button" class="option-btn"
                                        onclick="selectSize('<%= size %>', this);
                                                updateStockInfo();
                                                fetchPriceByVariant();"><%= size %></button>
                                <% } %>
                            </div>
                            <input type="hidden" name="size" id="size" value="">
                            <small id="size-error">Please choose size!</small>
                        </div>
                        <% } else { %>
                        <input type="hidden" name="size" id="size" value="">
                        <% } %>

                        <% if (!colorList.isEmpty()) { %>
                        <div class="select-group">
                            <label>Color:</label>
                            <div class="option-buttons" id="color-options">
                                <% for (String color : colorList) { %>
                                <button type="button" class="option-btn"
                                        onclick="selectColor('<%= color %>', this);
                                                updateStockInfo();
                                                fetchPriceByVariant();"><%= color %></button>
                                <% } %>

                            </div>
                            <input type="hidden" name="color" id="color" value="">
                            <small id="color-error">Please choose color!</small>
                        </div>
                        <% } else { %>
                        <input type="hidden" name="color" id="color" value="">
                        <% } %>

                        <div class="select-group">
                            <label>Quantity:</label>
                            <div class="quantity-controls">
                                <button type="button" onclick="decreaseQuantity()">−</button>
                                <span class="quantity-number" id="quantity-display">1</span>
                                <button type="button" onclick="increaseQuantity(window.maxAvailable)">+</button>
                                <input type="hidden" name="quantity" id="quantity" value="1">
                            </div>
                        </div>

                        <div class="add-to-cart">
                            <button type="submit" class="checkout-btn" name="action" value="AddToCart">Add to Cart</button>
                            <button type="button" class="checkout-btn" onclick="submitBuyNow()">Buy Now</button>
                        </div>

                    </form>
                </div>
            </div>

            <div class="thin-divider"></div>
            <div class="section-box">
                <h3>Description</h3>
                <p><%= product.getDescription() %></p>
            </div>
            <div class="thin-divider"></div>
            <div class="section-box">
                <h3>
                    Customer Reviews
                    <% if (totalReviews > 0) { %>
                    <span style="font-size: 16px; font-weight: normal;">
                        <%
                            int roundedRating = (int) Math.round(avgRating);
                            for (int i = 1; i <= 5; i++) {
                                if (i <= roundedRating) {
                        %>
                        <i class="fa-solid fa-star" style="color: gold;"></i>
                        <%
                                } else {
                        %>
                        <i class="fa-regular fa-star" style="color: gold;"></i>
                        <%
                                }
                            }
                        %>
                        (<%= totalReviews %> reviews)
                    </span>
                    <% } %>
                </h3>


                <% if (canReview) { %>
                <form action="<%= request.getContextPath() %>/SubmitReview" method="post" style="margin-bottom: 20px;" onsubmit="return validateReviewForm();">
                    <input type="hidden" name="productId" value="<%= productID %>" />
                    <input type="hidden" name="fromPage" value="productDetail">
                    <input type="hidden" name="email" value="<%= loginUser.getEmail() %>">
                    <input type="hidden" name="phone" value="<%= loginUser.getPhone() %>">
                    <input type="hidden" name="address" value="<%= loginUser.getAddress() %>">
                    <input type="hidden" name="orderId" value="0">
                    <input type="hidden" name="orderDate" value="">
                    <input type="hidden" name="status" value="">

                    <label><strong>Rating:</strong></label><br>
                    <div class="star-rating" style="font-size: 24px;">
                        <% for (int i = 5; i >= 1; i--) { %>
                        <input type="radio" id="star<%= i %>" name="rating" value="<%= i %>">
                        <label for="star<%= i %>">&#9733;</label>
                        <% } %>
                    </div>

                    <style>
                        .star-rating {
                            direction: rtl;
                            unicode-bidi: bidi-override;
                            display: inline-block;
                        }
                        .star-rating input[type="radio"] {
                            display: none;
                        }
                        .star-rating label {
                            color: #ccc;
                            cursor: pointer;
                            display: inline-block;
                        }
                        .star-rating input[type="radio"]:checked ~ label,
                        .star-rating label:hover,
                        .star-rating label:hover ~ label {
                            color: gold;
                        }
                    </style>

                    <br/><br/>
                    <label><strong>Comment:</strong></label><br/>
                    <textarea name="comment" rows="4" cols="60" placeholder="Write your experience here..." required></textarea><br/><br/>

                    <input type="submit" value="Submit Review" style="padding: 8px 16px; background-color: #28a745; color: white; border: none;">
                </form>
                <% } else if (loginUser != null) { %>
                <p style="color: gray;"><i>Only customers who have purchased this product can write a review.</i></p>
                <% } else { %>
                <p><a href="<%= request.getContextPath() %>/login.jsp">Login</a> to write a review.</p>
                <% } %>

            </div>

            <script>
                document.querySelectorAll('.rating input').forEach(input => {
                    input.addEventListener('change', function () {
                        const rating = parseInt(this.value);
                        document.querySelectorAll('.rating label i').forEach((star, index) => {
                            star.className = index >= 5 - rating ? 'fa-solid fa-star' : 'fa-regular fa-star';
                        });
                    });
                });
            </script>
            <br/><br/>




            <% if (reviews != null && !reviews.isEmpty()) {
        for (ReviewDTO review : reviews) { %>
            <div style="margin-bottom: 15px; padding: 10px; border-bottom: 1px solid #ccc;">
                <strong>Rating:</strong>
                <% for (int i = 1; i <= 5; i++) { %>
                <i class="fa<%= i <= review.getRating() ? 's' : 'r' %> fa-star" style="color: gold;"></i>
                <% } %>
                <br>
                <strong>Comment:</strong> <%= review.getComment() %><br>
                <strong>User:</strong> <%= review.getUserFullName() %><br>
                <small><%= review.getReviewDate() %></small>
            </div>
            <% } } else { %>
            <p>No reviews yet.</p>
            <% } %>
        </div>

    </div>
    <% } else { %>
    <p>Product not found.</p>
    <% } %>

    <script>
        function selectSize(value, btn) {
            document.getElementById("size").value = value;
            document.querySelectorAll("#size-options .option-btn").forEach(b => b.classList.remove("active"));
            btn.classList.add("active");
            document.getElementById("size-error").style.display = "none";
        }

        function selectColor(value, btn) {
            document.getElementById("color").value = value;
            document.querySelectorAll("#color-options .option-btn").forEach(b => b.classList.remove("active"));
            btn.classList.add("active");
            document.getElementById("color-error").style.display = "none";
        }

        function decreaseQuantity() {
            const input = document.getElementById("quantity");
            const display = document.getElementById("quantity-display");
            let val = parseInt(input.value);
            if (val > 1) {
                input.value = val - 1;
                display.textContent = val - 1;
            }
        }

        function increaseQuantity(dummy) {
            const input = document.getElementById("quantity");
            const display = document.getElementById("quantity-display");
            const size = document.getElementById("size").value;
            const color = document.getElementById("color").value;
            let key = "default";
            if (size && color)
                key = size + "_" + color;
            else if (size)
                key = size;
            else if (color)
                key = color;

            const maxAvailable = variantStockMap[key] || 0;

            let val = parseInt(input.value);
            if (val < maxAvailable) {
                input.value = val + 1;
                display.textContent = val + 1;
            } else {
                alert("Số lượng vượt quá tồn kho.");
            }
        }


        function validateBeforeSubmit(maxAvailable) {
            const sizeInput = document.getElementById("size");
            const colorInput = document.getElementById("color");
            const quantity = parseInt(document.getElementById("quantity").value);

            let valid = true;
            if (sizeInput && sizeInput.value.trim() === "") {
                document.getElementById("size-error").style.display = "block";
                valid = false;
            }
            if (colorInput && colorInput.value.trim() === "") {
                document.getElementById("color-error").style.display = "block";
                valid = false;
            }
            if (quantity > maxAvailable) {
                alert("Số lượng vượt quá tồn kho.");
                valid = false;
            }

            return valid;
        }

        function submitBuyNow() {
            var size = document.getElementById('size').value;
            var color = document.getElementById('color').value;
            var quantity = document.getElementById('quantity').value;
            var productId = '<%= product.getProductID() %>';
            var valid = true;
        <% if (!sizeList.isEmpty()) { %>
            if (!size) {
                document.getElementById('size-error').style.display = 'block';
                valid = false;
            } else {
                document.getElementById('size-error').style.display = 'none';
            }
        <% } %>
        <% if (!colorList.isEmpty()) { %>
            if (!color) {
                document.getElementById('color-error').style.display = 'block';
                valid = false;
            } else {
                document.getElementById('color-error').style.display = 'none';
            }
        <% } %>
            if (!valid)
                return;

            var form = document.createElement('form');
            form.method = 'GET';
            form.action = '<%= request.getContextPath() %>/user/checkout.jsp';

            form.innerHTML =
                    '<input type="hidden" name="productId" value="' + productId + '">' +
                    '<input type="hidden" name="size" value="' + size + '">' +
                    '<input type="hidden" name="color" value="' + color + '">' +
                    '<input type="hidden" name="quantity" value="' + quantity + '">';

            document.body.appendChild(form);
            form.submit();
        }


// ✅ THÊM Ở ĐÂY:
        function updateStockInfo() {
            const size = document.getElementById("size").value;
            const color = document.getElementById("color").value;

            let key = "default";
            if (size && color) {
                key = size + "_" + color;
            } else if (size) {
                key = size;
            } else if (color) {
                key = color;
            }

            const available = variantStockMap[key] || 0;


            const quantity = document.getElementById("quantity");
            const display = document.getElementById("quantity-display");
            if (parseInt(quantity.value) > available) {
                quantity.value = available > 0 ? 1 : 0;
                display.textContent = quantity.value;
            }

            const addToCartBtn = document.querySelector("button[name='action'][value='AddToCart']");
            const buyNowBtn = document.querySelector("button[onclick='submitBuyNow()']");
            if (available > 0) {
                addToCartBtn.disabled = false;
                addToCartBtn.style.backgroundColor = '';
                addToCartBtn.textContent = 'Add to Cart';
                buyNowBtn.disabled = false;
                buyNowBtn.style.backgroundColor = '';
            } else {
                addToCartBtn.disabled = true;
                addToCartBtn.style.backgroundColor = '#ccc';
                addToCartBtn.textContent = 'Sold Out';
                buyNowBtn.disabled = true;
                buyNowBtn.style.backgroundColor = '#ccc';
            }

            window.maxAvailable = available;

            // ✅ GỌI FETCH GIÁ Ở ĐÂY
            fetchPriceByVariant();
        }


        <% if (!fromSaleOff) { %>
        document.querySelectorAll("#size-options .option-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                updateStockInfo();
                fetchPriceByVariant();
            });
        });
        document.querySelectorAll("#color-options .option-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                updateStockInfo();
                fetchPriceByVariant();
            });
        });
        <% } else { %>
        document.querySelectorAll("#size-options .option-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                updateStockInfo(); // 👈 chỉ update số lượng tồn kho
            });
        });
        document.querySelectorAll("#color-options .option-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                updateStockInfo();
            });
        });
        <% } %>




    </script>
    <script>
        function fetchPriceByVariant() {
            const size = document.getElementById("size").value;
            const color = document.getElementById("color").value;
            if (!size || !color)
                return;

            const productId = '<%= product.getProductID() %>';

            fetch("<%=request.getContextPath()%>/GetPriceByVariantServlet?productId=" + productId + "&size=" + size + "&color=" + color)

                    .then(res => res.json())
                    .then(data => {
                        if (data.price !== undefined) {
                            const priceElement = document.getElementById("price-display");
                            priceElement.textContent = Number(data.price).toLocaleString() + " VND";

                            // ✅ Cập nhật giá vào input ẩn để truyền đi khi submit
                            document.querySelector("input[name='price']").value = data.price;
                        }
                    })
                    .catch(err => console.error("Lỗi khi lấy giá: ", err));
        }
    </script>
    <script>
        window.onload = function () {
            const sizeInput = document.getElementById("size");
            const colorInput = document.getElementById("color");

            // Nếu chưa có size đã chọn, chọn size đầu tiên nếu có
            if (sizeInput && sizeInput.value === "" && <%= !sizeList.isEmpty() %>) {
                sizeInput.value = "<%= sizeList.isEmpty() ? "" : sizeList.get(0) %>";
                const firstSizeBtn = document.querySelector("#size-options .option-btn");
                if (firstSizeBtn)
                    firstSizeBtn.classList.add("active");
            }

            // Nếu chưa có color đã chọn, chọn color đầu tiên nếu có
            if (colorInput && colorInput.value === "" && <%= !colorList.isEmpty() %>) {
                colorInput.value = "<%= colorList.isEmpty() ? "" : colorList.get(0) %>";
                const firstColorBtn = document.querySelector("#color-options .option-btn");
                if (firstColorBtn)
                    firstColorBtn.classList.add("active");
            }

            updateStockInfo();
            fetchPriceByVariant();
        }
    </script>
    <script>
        function validateReviewForm() {
            const ratingSelected = document.querySelector('input[name="rating"]:checked');
            if (!ratingSelected) {
                alert("Please select a rating before submitting your review.");
                return false;
            }
            return true;
        }
    </script>

</body>
</html>