<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<style>
    #chat-toggle-btn {
        position: fixed;
        bottom: 20px;
        right: 20px;
        width: 60px;
        height: 60px;
        border-radius: 50%;
        border: none;
        background: #198754;
        color: white;
        font-size: 24px;
        cursor: pointer;
        z-index: 9999;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
    }

    #chatbox-container {
        position: fixed;
        bottom: 90px;
        right: 20px;
        width: 360px;
        height: 500px;
        background: white;
        border-radius: 12px;
        box-shadow: 0 6px 20px rgba(0,0,0,0.2);
        display: none;
        flex-direction: column;
        overflow: hidden;
        z-index: 9999;
        border: 1px solid #ddd;
    }

    .chatbox-fullscreen{
        width:90vw !important;
        height:90vh !important;
        right:5vw !important;
        bottom:5vh !important;
    }

    #chatbox-header {
        background: #198754;
        color: white;
        padding: 12px;
        font-weight: bold;
        display:flex;
        justify-content:space-between;
        align-items:center;
    }

    #chat-zoom-btn{
        background:none;
        border:none;
        color:white;
        font-size:18px;
        cursor:pointer;
    }

    #chatbox-messages {
        flex: 1;
        padding: 10px;
        overflow-y: auto;
        background: #ffffff;
        min-height: 0;
    }

    /* FIX MESSAGE */
    .chat-message {
        margin-bottom: 12px;
        padding: 10px 14px;
        border-radius: 14px;
        max-width: 80%;
        width: fit-content;
        display: block;
        word-wrap: break-word;
        white-space: normal;
        clear: both;
    }

    .user-message {
        background: #198754;
        color: white;
        margin-left: auto;
    }

    .bot-message {
        background: #e9ecef;
        color: black;
        margin-right: auto;
    }

    #chatbox-input-area {
        display: flex;
        border-top: 1px solid #ddd;
    }

    #chat-input {
        flex: 1;
        border: none;
        padding: 12px;
        outline: none;
    }

    #chat-send-btn {
        border: none;
        background: #198754;
        color: white;
        padding: 0 18px;
        cursor: pointer;
    }

    /* PRODUCT GRID */

    .chat-product-row{
        display:grid;
        grid-template-columns: repeat(auto-fit, minmax(220px,1fr));
        gap:12px;
        margin-top:10px;
        margin-bottom:20px;
        width:100%;
    }

    .chat-product {
        background: white;
        border: 1px solid #ddd;
        border-radius: 10px;
        padding: 12px;
        display:flex;
        flex-direction:column;
        height:100%;
    }

    .chat-product img {
        width:100%;
        height:140px;
        object-fit:contain;
        border-radius:6px;
        margin-bottom:8px;
        background:#f8f9fa;
    }

    .chat-product-name{
        font-weight:bold;
        font-size:14px;
        margin-bottom:4px;
    }

    .chat-product-price{
        color:#dc3545;
        font-weight:bold;
        margin-top:auto;
    }

    .chat-product-category {
        color: #6c757d;
        font-size: 13px;
    }

    .chat-product-desc {
        font-size: 13px;
        margin-top: 4px;
    }
</style>

<button id="chat-toggle-btn">🤖</button>

<div id="chatbox-container">

    <div id="chatbox-header">
        Trợ lý ảo thông minh
        <button id="chat-zoom-btn">⛶</button>
    </div>

    <div id="chatbox-messages"></div>

    <div id="chatbox-input-area">
        <input type="text" id="chat-input" placeholder="Type your question...">
        <button id="chat-send-btn">Gửi</button>
    </div>

</div>

<script>

const chatToggleBtn = document.getElementById("chat-toggle-btn");
const chatboxContainer = document.getElementById("chatbox-container");
const chatboxMessages = document.getElementById("chatbox-messages");
const chatInput = document.getElementById("chat-input");
const chatSendBtn = document.getElementById("chat-send-btn");
const zoomBtn = document.getElementById("chat-zoom-btn");

window.onload = function() {
    appendMessage("Xin chào! Hãy hỏi tôi về sản phẩm, giá, màu, size hoặc tình trạng còn hàng.", "bot-message");
};

chatToggleBtn.addEventListener("click", function () {
    chatboxContainer.style.display =
        chatboxContainer.style.display === "flex" ? "none" : "flex";
});

zoomBtn.addEventListener("click", function(){
    if(chatboxContainer.classList.contains("chatbox-fullscreen")){
        chatboxContainer.classList.remove("chatbox-fullscreen");
    }else{
        chatboxContainer.classList.add("chatbox-fullscreen");
    }
});

chatSendBtn.addEventListener("click", sendMessage);

chatInput.addEventListener("keypress", function (e) {
    if (e.key === "Enter") {
        sendMessage();
    }
});

function appendMessage(text, className) {
    const div = document.createElement("div");
    div.className = "chat-message " + className;
    div.textContent = text;
    chatboxMessages.appendChild(div);
}

function appendProducts(products) {

    const row = document.createElement("div");
    row.className = "chat-product-row";

    products.forEach(function(product) {

        const div = document.createElement("div");
        div.className = "chat-product";

        let html = "";

        if (product.imageUrl) {
            html += '<img src="' + product.imageUrl + '" alt="' + product.productName + '">';
        }

        html += '<div class="chat-product-name">' + (product.productName || "") + '</div>';

        if (product.categoryName) {
            html += '<div class="chat-product-category">' + product.categoryName + '</div>';
        }

        if (product.minPrice !== null && product.minPrice !== undefined) {
            html += '<div class="chat-product-price">' + formatPrice(product.minPrice) + '</div>';
        }

        if (product.description) {
            html += '<div class="chat-product-desc">' + product.description + '</div>';
        }

        div.innerHTML = html;
        row.appendChild(div);

    });

    chatboxMessages.appendChild(row);

    /* FIX overlap */
    const spacer = document.createElement("div");
    spacer.style.height = "10px";
    chatboxMessages.appendChild(spacer);

    chatboxMessages.scrollTop = chatboxMessages.scrollHeight;
}

function formatPrice(price) {
    if (price === null || price === undefined) {
        return "Không rõ giá";
    }
    return Number(price).toLocaleString("vi-VN") + " VNĐ";
}

function sendMessage() {

    const message = chatInput.value.trim();
    if (!message) return;

    appendMessage(message, "user-message");
    chatInput.value = "";

    fetch("<%= request.getContextPath() %>/chat-endpoint", {
        method: "POST",
        headers: {
            "Content-Type": "application/json; charset=UTF-8"
        },
        body: JSON.stringify({message: message})
    })
    .then(res => res.json())
    .then(function(data) {

        appendMessage(data.message, "bot-message");

        if (data.products && data.products.length > 0) {
            appendProducts(data.products);
        }

    })
    .catch(function() {
        appendMessage("Không thể kết nối chatbot", "bot-message");
    });

}
</script>