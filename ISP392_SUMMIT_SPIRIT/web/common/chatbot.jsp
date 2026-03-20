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

    .chatbox-fullscreen {
        width: 90vw !important;
        height: 90vh !important;
        right: 5vw !important;
        bottom: 5vh !important;
    }

    #chatbox-header {
        background: #198754;
        color: white;
        padding: 12px;
        font-weight: bold;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    #chat-zoom-btn {
        background: none;
        border: none;
        color: white;
        font-size: 18px;
        cursor: pointer;
    }

    #chatbox-messages {
        flex: 1;
        padding: 10px;
        overflow-y: auto;
        background: #ffffff;
        min-height: 0;
    }

    .chat-message {
        margin-bottom: 12px;
        padding: 10px 14px;
        border-radius: 14px;
        max-width: 80%;
        width: fit-content;
        display: block;
        word-wrap: break-word;
        white-space: pre-line;
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

    .chat-product-row {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
        gap: 12px;
        margin-top: 10px;
        margin-bottom: 20px;
        width: 100%;
    }

    .chat-product {
        background: white;
        border: 1px solid #ddd;
        border-radius: 10px;
        padding: 12px;
        display: flex;
        flex-direction: column;
        height: 100%;
    }

    .chat-product img {
        width: 100%;
        height: 140px;
        object-fit: contain;
        border-radius: 6px;
        margin-bottom: 8px;
        background: #f8f9fa;
    }

    .chat-product-name {
        font-weight: bold;
        font-size: 14px;
        margin-bottom: 4px;
    }

    .chat-product-price {
        color: #dc3545;
        font-weight: bold;
        margin-top: auto;
    }

    .chat-product-category {
        color: #6c757d;
        font-size: 13px;
    }

    .chat-product-desc {
        font-size: 13px;
        margin-top: 4px;
    }

    .chat-product-detail-btn {
        display: inline-block;
        margin-top: 10px;
        padding: 8px 12px;
        background: #198754;
        color: white;
        text-decoration: none;
        border-radius: 8px;
        text-align: center;
        font-size: 13px;
        font-weight: 600;
    }

    .chat-product-detail-btn:hover {
        background: #157347;
        color: white;
    }
</style>

<button id="chat-toggle-btn" type="button">🤖</button>

<div id="chatbox-container">
    <div id="chatbox-header">
        Trợ lý ảo thông minh
        <button id="chat-zoom-btn" type="button">⛶</button>
    </div>

    <div id="chatbox-messages"></div>

    <div id="chatbox-input-area">
        <input type="text" id="chat-input" placeholder="Nhập câu hỏi..." />
        <button id="chat-send-btn" type="button">Gửi</button>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function () {
    const chatToggleBtn = document.getElementById("chat-toggle-btn");
    const chatboxContainer = document.getElementById("chatbox-container");
    const chatboxMessages = document.getElementById("chatbox-messages");
    const chatInput = document.getElementById("chat-input");
    const chatSendBtn = document.getElementById("chat-send-btn");
    const zoomBtn = document.getElementById("chat-zoom-btn");

    const CHAT_HISTORY_KEY = "summit_spirit_chat_history";
    const CHATBOX_STATE_KEY = "summit_spirit_chatbox_open";

    if (!chatToggleBtn || !chatboxContainer || !chatboxMessages || !chatInput || !chatSendBtn || !zoomBtn) {
        return;
    }

    loadChatHistory();

    if (chatboxMessages.childElementCount === 0) {
        appendMessage("Xin chào! Hãy hỏi tôi về sản phẩm, giá, màu, size hoặc tình trạng còn hàng.", "bot-message", true);
    }

    if (sessionStorage.getItem(CHATBOX_STATE_KEY) === "open") {
        chatboxContainer.style.display = "flex";
    }

    chatToggleBtn.addEventListener("click", function () {
        const isOpen = chatboxContainer.style.display === "flex";
        chatboxContainer.style.display = isOpen ? "none" : "flex";
        sessionStorage.setItem(CHATBOX_STATE_KEY, isOpen ? "closed" : "open");
    });

    zoomBtn.addEventListener("click", function () {
        chatboxContainer.classList.toggle("chatbox-fullscreen");
    });

    chatSendBtn.addEventListener("click", sendMessage);

    chatInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
            sendMessage();
        }
    });

    function appendMessage(text, className, shouldSave) {
        const div = document.createElement("div");
        div.className = "chat-message " + className;
        div.textContent = text || "";
        chatboxMessages.appendChild(div);
        scrollToBottom();

        if (shouldSave) {
            saveHistoryItem({
                type: "message",
                text: text || "",
                className: className
            });
        }
    }

    function appendProducts(products, shouldSave) {
        const row = document.createElement("div");
        row.className = "chat-product-row";

        products.forEach(function (product) {
            const div = document.createElement("div");
            div.className = "chat-product";

            if (product.imageUrl) {
                const img = document.createElement("img");
                img.src = product.imageUrl;
                img.alt = product.productName || "product";
                div.appendChild(img);
            }

            const name = document.createElement("div");
            name.className = "chat-product-name";
            name.textContent = product.productName || "";
            div.appendChild(name);

            if (product.categoryName) {
                const category = document.createElement("div");
                category.className = "chat-product-category";
                category.textContent = product.categoryName;
                div.appendChild(category);
            }

            if (product.minPrice !== null && product.minPrice !== undefined) {
                const price = document.createElement("div");
                price.className = "chat-product-price";
                price.textContent = formatPrice(product.minPrice);
                div.appendChild(price);
            }

            if (product.description) {
                const desc = document.createElement("div");
                desc.className = "chat-product-desc";
                desc.textContent = product.description;
                div.appendChild(desc);
            }

            if (product.productId) {
                const detailBtn = document.createElement("a");
                detailBtn.href = "<%= request.getContextPath() %>/user/productDetail.jsp?id=" + product.productId;
                detailBtn.className = "chat-product-detail-btn";
                detailBtn.textContent = "Xem chi tiết";
                div.appendChild(detailBtn);
            }

            row.appendChild(div);
        });

        chatboxMessages.appendChild(row);

        const spacer = document.createElement("div");
        spacer.style.height = "10px";
        chatboxMessages.appendChild(spacer);

        scrollToBottom();

        if (shouldSave) {
            saveHistoryItem({
                type: "products",
                products: products
            });
        }
    }

    function scrollToBottom() {
        chatboxMessages.scrollTop = chatboxMessages.scrollHeight;
    }

    function formatPrice(price) {
        if (price === null || price === undefined) {
            return "Không rõ giá";
        }
        return Number(price).toLocaleString("vi-VN") + " VNĐ";
    }

    function getChatHistory() {
        try {
            const raw = sessionStorage.getItem(CHAT_HISTORY_KEY);
            return raw ? JSON.parse(raw) : [];
        } catch (e) {
            return [];
        }
    }

    function saveHistoryItem(item) {
        const history = getChatHistory();
        history.push(item);
        sessionStorage.setItem(CHAT_HISTORY_KEY, JSON.stringify(history));
    }

    function loadChatHistory() {
        const history = getChatHistory();

        history.forEach(function (item) {
            if (item.type === "message") {
                appendMessage(item.text, item.className, false);
            } else if (item.type === "products" && item.products && Array.isArray(item.products)) {
                appendProducts(item.products, false);
            }
        });
    }

    function sendMessage() {
        const message = chatInput.value.trim();
        if (!message) return;

        appendMessage(message, "user-message", true);
        chatInput.value = "";

        fetch("<%= request.getContextPath() %>/chat-endpoint", {
            method: "POST",
            headers: {
                "Content-Type": "application/json; charset=UTF-8"
            },
            body: JSON.stringify({ message: message })
        })
        .then(function (res) {
            if (!res.ok) {
                throw new Error("HTTP error");
            }
            return res.json();
        })
        .then(function (data) {
            appendMessage(data.message || "Không có phản hồi.", "bot-message", true);

            if (data.products && Array.isArray(data.products) && data.products.length > 0) {
                appendProducts(data.products, true);
            }
        })
        .catch(function () {
            appendMessage("Không thể kết nối chatbot.", "bot-message", true);
        });
    }
});
</script>