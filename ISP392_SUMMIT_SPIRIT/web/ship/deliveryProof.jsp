<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="dto.OrderDTO" %>
<%@ page import="dto.UserDTO" %>
<%@ page import="dto.UserAddressDTO" %>
<%@ page import="java.util.Map" %>

<%
    UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
    if (loginUser == null || !"Shipper".equals(loginUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<OrderDTO> shippedOrders = (List<OrderDTO>) request.getAttribute("shippedOrders");
    Map<Integer, UserAddressDTO> addressMap = (Map<Integer, UserAddressDTO>) request.getAttribute("addressMap");

%>
<link href="https://fonts.googleapis.com/css2?family=Kumbh+Sans&display=swap" rel="stylesheet">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/css/deliveryProof.css" />

<div class="proof-container">
    <h2>📦 Delivery Confirmation Panel</h2>

    <% if (shippedOrders != null && !shippedOrders.isEmpty()) { %>
    <div class="grid-wrapper">
        <% for (OrderDTO order : shippedOrders) { 
        UserAddressDTO info = addressMap.get(order.getOrderID());
    %>
    <div class="proof-card">
        <div class="order-meta">
            <h3>Order #<%= order.getOrderID() %></h3>
            <p>User ID: <%= order.getUserID() %></p>
            <p>Customer: <%= info != null ? info.getFullName() : "N/A" %></p>
            <p>Phone: <%= info != null ? info.getPhone() : "N/A" %></p>
            <p>Address:
                <% if (info != null) { %>
                    <%= info.getAddress() %>, <%= info.getDistrict() %>, <%= info.getCity() %>, <%= info.getCountry() %>
                <% } else { %>
                    N/A
                <% } %>
            </p>
            <p>Date: <%= order.getOrderDate() %></p>
            <p>Total: <%= String.format("%,.0f", order.getTotalAmount()) %> VND</p>
        </div>

            <div class="capture-zone">
                <video id="video-<%= order.getOrderID() %>" autoplay muted playsinline></video>
                <canvas id="canvas-<%= order.getOrderID() %>" style="display: none;"></canvas>
                <img id="preview-<%= order.getOrderID() %>" class="preview-img" style="display: none;" />
                <button type="button" onclick="capture(<%= order.getOrderID() %>)" class="capture-btn">📷 Take Photo</button>
            </div>

            <form action="<%= request.getContextPath() %>/MarkDeliveredController" method="post" class="proof-form">
                <input type="hidden" name="orderID" value="<%= order.getOrderID() %>">
                <input type="hidden" name="imageData" id="imageData-<%= order.getOrderID() %>">
                <input type="text" name="note" placeholder="e.g. House with red door" required />
                <button type="submit" class="submit-btn">
                    <i class="fas fa-truck"></i> Confirm Delivery
                </button>
            </form>
        </div>
        <% } %>
    </div>
    <% } else { %>
    <div class="no-data">🚫 No orders here.</div>
    <% } %>
</div>

<script>
    window.addEventListener("DOMContentLoaded", () => {
    <% for (OrderDTO order : shippedOrders) { %>
        const video<%= order.getOrderID() %> = document.getElementById("video-<%= order.getOrderID() %>");
        navigator.mediaDevices.getUserMedia({video: true})
                .then(stream => {
                    video<%= order.getOrderID() %>.srcObject = stream;
                })
                .catch(err => {
                    alert("📷 Camera not available: " + err);
                });
    <% } %>
    });

    function capture(orderID) {
        const video = document.getElementById("video-" + orderID);
        const canvas = document.getElementById("canvas-" + orderID);
        const preview = document.getElementById("preview-" + orderID);
        const imageDataField = document.getElementById("imageData-" + orderID);

        const context = canvas.getContext("2d");
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        context.drawImage(video, 0, 0, canvas.width, canvas.height);

        const imageData = canvas.toDataURL("image/png");
        preview.src = imageData;
        preview.style.display = "block";
        imageDataField.value = imageData;
    }
</script>
<script>
    const activeStreams = new Map();

    function startCamera(orderID) {
        const video = document.getElementById("video-" + orderID);
        if (activeStreams.has(orderID)) {
            // Đã mở cam cho order này rồi
            return;
        }

        navigator.mediaDevices.getUserMedia({video: true})
                .then(stream => {
                    video.srcObject = stream;
                    activeStreams.set(orderID, stream);
                    video.style.display = "block"; // Hiện video
                })
                .catch(err => {
                    alert("📷 Cannot access camera: " + err);
                });
    }

    function capture(orderID) {
        const video = document.getElementById("video-" + orderID);
        const canvas = document.getElementById("canvas-" + orderID);
        const preview = document.getElementById("preview-" + orderID);
        const imageDataField = document.getElementById("imageData-" + orderID);

        // Mở camera nếu chưa mở
        if (!activeStreams.has(orderID)) {
            startCamera(orderID);
            return; // Đợi camera load xong, sau đó mới bấm lại lần 2 để chụp
        }

        const context = canvas.getContext("2d");
        canvas.width = video.videoWidth;
        canvas.height = video.videoHeight;
        context.drawImage(video, 0, 0, canvas.width, canvas.height);

        const imageData = canvas.toDataURL("image/png");
        preview.src = imageData;
        preview.style.display = "block";
        imageDataField.value = imageData;
    }

    // 👉 Nếu muốn stop cam sau khi chụp:
    activeStreams.get(orderID)?.getTracks()?.forEach(track => track.stop());
</script>