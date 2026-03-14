package service;

import dao.ProductChatDAO;
import dao.ProductChatDAOImpl;
import dto.ChatProductResult;
import dto.ChatQuery;
import dto.ChatResponse;
import dto.VariantStockResult;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;

public class ChatbotService {

    private static final String CONTEXT_PRODUCT = "PRODUCT";
    private static final String CONTEXT_CATEGORY = "CATEGORY";

    private final AIIntentService aiIntentService;
    private final AIResponseService aiResponseService;
    private final ProductChatDAO productChatDAO;

    public ChatbotService() {
        this.aiIntentService = new AIIntentService();
        this.aiResponseService = new AIResponseService();
        this.productChatDAO = new ProductChatDAOImpl();
    }

    public ChatResponse handleMessage(String message, HttpSession session) {
        ChatResponse response = new ChatResponse();

       try {
    ChatQuery query = aiIntentService.parseUserMessage(message);
    query.setRawMessage(message);

    String rawDetect = message.toLowerCase().trim();

    // detect small talk
    if (rawDetect.equals("xin chào") 
            || rawDetect.equals("hello") 
            || rawDetect.equals("hi")
            || rawDetect.contains("bạn là ai")
            || rawDetect.contains("bạn làm được gì")) {
        query.setIntent("SMALL_TALK");
    }

    // detect outfit
    else if (rawDetect.contains("outfit") 
            || rawDetect.contains("combo") 
            || rawDetect.contains("mặc gì")) {
        query.setIntent("OUTFIT_SUGGESTION");
    }
            // ===== END ADD =====

            applySessionContext(query, session);

            if (containsUnsupportedColor(message)) {
                response.setSuccess(true);
                response.setMessage("Xin lỗi, cửa hàng hiện chỉ hỗ trợ các màu Black, Red và Blue. Bạn có thể thử lại với màu phù hợp.");
                response.setProducts(new ArrayList<>());
                saveSessionContext(query, response, session);
                return response;
            }

            switch (query.getIntent()) {

                // ===== ADD NEW INTENT =====
                case "OUTFIT_SUGGESTION":
                    response = handleOutfitSuggestion(message);
                    break;
                // ===== END ADD =====

                case "SMALL_TALK":
    response.setSuccess(true);
    response.setMessage(
        "Xin chào! Tôi là trợ lý AI của SUMMIT SPIRIT.\n\n"
        + "Tôi có thể giúp bạn:\n"
        + "• Tìm sản phẩm\n"
        + "• Kiểm tra giá\n"
        + "• Kiểm tra màu và size\n"
        + "• Kiểm tra tình trạng còn hàng\n"
        + "• Gợi ý combo outfit theo mùa\n\n"
        + "Bạn có thể thử hỏi:\n"
        + "• quần size L\n"
        + "• balo trekking\n"
        + "• Ktom K116 còn hàng không\n"
        + "• gợi ý outfit mùa hè"
    );
    response.setProducts(new ArrayList<>());
    break;

                case ChatIntent.CHECK_STOCK:
                    if (query.getProductKeyword() == null || query.getProductKeyword().trim().isEmpty()) {
                        response = handleSearchProduct(query);
                    } else {
                        response = handleCheckStock(query);
                    }
                    break;

                case "SEARCH_PRODUCT":
                    response = handleSearchProduct(query);
                    break;

                case "UNSUPPORTED_PRODUCT":
                    response.setSuccess(true);
                    response.setMessage("Xin lỗi, cửa hàng hiện không có sản phẩm này.");
                    response.setProducts(new ArrayList<>());
                    break;

                default:
                    response.setSuccess(true);
                    response.setMessage("Xin lỗi, tôi chỉ hỗ trợ tư vấn sản phẩm và thông tin liên quan trong cửa hàng.");
                    response.setProducts(new ArrayList<>());
                    break;
            }

            saveSessionContext(query, response, session);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage("Hệ thống chatbot đang gặp lỗi. Vui lòng thử lại sau.");
            response.setProducts(new ArrayList<>());
            return response;
        }
    }

    // ===== ADD NEW METHOD =====
    private ChatResponse handleOutfitSuggestion(String message) throws Exception {

    ChatResponse response = new ChatResponse();
    response.setSuccess(true);

    String raw = message.toLowerCase();

    List<ChatProductResult> comboProducts = new ArrayList<>();

    if (raw.contains("hè") || raw.contains("summer")) {

        comboProducts.addAll(productChatDAO.findProductsByName("K109"));
        comboProducts.addAll(productChatDAO.findProductsByName("K129"));
        comboProducts.addAll(productChatDAO.findProductsByName("K52"));
        comboProducts.addAll(productChatDAO.findProductsByName("Matador"));

        response.setProducts(comboProducts);

        response.setMessage(
    "Gợi ý combo outfit mùa hè.\n\n"
    + "• Áo: KTOM K129 quick-dry.\n"
    + "• Quần: Ktom K109 shorts.\n"
    + "• Mũ: KTOM K52 cap.\n"
    + "• Balo: Matador Beast 28.\n\n"
    + "Bạn có thể xem hình ảnh các sản phẩm bên dưới."
);

        return response;
    }

    if (raw.contains("đông") || raw.contains("winter")) {

        comboProducts.addAll(productChatDAO.findProductsByName("K151"));
        comboProducts.addAll(productChatDAO.findProductsByName("K116"));
        comboProducts.addAll(productChatDAO.findProductsByName("Flag Bearer"));
        comboProducts.addAll(productChatDAO.findProductsByName("65L"));

        response.setProducts(comboProducts);

        response.setMessage(
                "Gợi ý combo outfit mùa đông:\n"
                + "• Jacket: Ktom K151\n"
                + "• Pants: Ktom K116 waterproof\n"
                + "• Hat: Tactical Flag Bearer\n"
                + "• Backpack: Naturehike 65L\n\n"
                + "Bạn có thể xem hình ảnh các sản phẩm bên dưới."
        );

        return response;
    }

    if (raw.contains("xuân") || raw.contains("spring")) {

        comboProducts.addAll(productChatDAO.findProductsByName("K128"));
        comboProducts.addAll(productChatDAO.findProductsByName("K155"));
        comboProducts.addAll(productChatDAO.findProductsByName("Ranger"));
        comboProducts.addAll(productChatDAO.findProductsByName("Moab"));

        response.setProducts(comboProducts);

        response.setMessage(
                "Gợi ý combo outfit mùa xuân:\n"
                + "• Shirt: K128 long sleeve\n"
                + "• Pants: K155 trekking pants\n"
                + "• Hat: Ranger Tactical Cap\n"
                + "• Backpack: Jack Wolfskin Moab Jam\n\n"
                + "Bạn có thể xem hình ảnh các sản phẩm bên dưới."
        );

        return response;
    }

    if (raw.contains("thu") || raw.contains("autumn")) {

    List<ChatProductResult> shirt = productChatDAO.findProductsByName("K128");
    if (shirt != null && !shirt.isEmpty()) {
        comboProducts.add(shirt.get(0));
    }

    List<ChatProductResult> pants = productChatDAO.findProductsByName("K154");
    if (pants != null && !pants.isEmpty()) {
        comboProducts.add(pants.get(0));
    }

    List<ChatProductResult> hat = productChatDAO.findProductsByName("Bucket");
    if (hat != null && !hat.isEmpty()) {
        comboProducts.add(hat.get(0));
    }

    List<ChatProductResult> backpack = productChatDAO.findProductsByName("Naturehike");
    if (backpack != null && !backpack.isEmpty()) {
        comboProducts.add(backpack.get(0));
    }

    response.setProducts(comboProducts);

    response.setMessage(
        "Gợi ý combo outfit mùa thu.\n\n"
        + "• Shirt: K128 long sleeve.\n"
        + "• Pants: K154 trekking pants.\n"
        + "• Hat: Bucket hat.\n"
        + "• Backpack: Naturehike backpack.\n\n"
        + "Bạn có thể xem hình ảnh các sản phẩm bên dưới."
    );

    return response;
}

    response.setProducts(new ArrayList<>());

    response.setMessage(
            "Bạn muốn gợi ý combo outfit theo mùa nào?\n"
            + "• Xuân\n"
            + "• Hè\n"
            + "• Thu\n"
            + "• Đông"
    );

    return response;
}
    // ===== END ADD =====

    private ChatResponse handleSearchProduct(ChatQuery query) throws Exception {
        List<ChatProductResult> products;

        if (query.getProductKeyword() != null && !query.getProductKeyword().trim().isEmpty()) {
            products = productChatDAO.findProductsByName(query.getProductKeyword());
        } else {
            products = productChatDAO.findProductsByFilters(query);
        }

        ChatResponse response = new ChatResponse();
        response.setSuccess(true);
        response.setProducts(products != null ? products : new ArrayList<>());

        if (products == null || products.isEmpty()) {
            response.setMessage("Tôi chưa tìm thấy sản phẩm phù hợp với yêu cầu của bạn.");
            return response;
        }

        boolean hasStrictFilter = query.getColor() != null
                || query.getSize() != null
                || query.isInStockOnly();

        if (hasStrictFilter) {
            response.setMessage(buildDeterministicSearchMessage(query, products));
        } else {
            response.setMessage(generateNaturalReply(
                    query.getRawMessage(),
                    products,
                    "Tôi tìm thấy " + products.size() + " sản phẩm phù hợp với yêu cầu của bạn."
            ));
        }

        return response;
    }


    private String buildDeterministicSearchMessage(ChatQuery query, List<ChatProductResult> products) {
        StringBuilder sb = new StringBuilder("Tôi tìm thấy ");
        sb.append(products.size()).append(" sản phẩm phù hợp");

        List<String> conditions = new ArrayList<>();

        if (query.getCategory() != null) {
            conditions.add("loại " + query.getCategory());
        }
        if (query.getColor() != null) {
            conditions.add("màu " + query.getColor());
        }
        if (query.getSize() != null) {
            conditions.add("size " + query.getSize());
        }
        if (query.getMinPrice() != null) {
            conditions.add("giá từ " + formatPrice(query.getMinPrice()));
        }
        if (query.getMaxPrice() != null) {
            conditions.add("giá đến " + formatPrice(query.getMaxPrice()));
        }
        if (query.getDescriptionKeyword() != null) {
            conditions.add("đặc tính " + query.getDescriptionKeyword());
        }
        if (query.isInStockOnly()) {
            conditions.add("còn hàng");
        }

        if (!conditions.isEmpty()) {
            sb.append(" với điều kiện ");
            sb.append(String.join(", ", conditions));
        }

        sb.append(".");
        return sb.toString();
    }

    private ChatResponse handleCheckStock(ChatQuery query) throws Exception {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);

        String keyword = query.getProductKeyword();

        String raw = query.getRawMessage() != null ? query.getRawMessage().toLowerCase() : "";
        boolean userMentionedColor = raw.contains("màu")
                || raw.contains("đen")
                || raw.contains("đỏ")
                || raw.contains("xanh");
        boolean userMentionedSize = raw.contains("size")
                || raw.matches(".*\\b(s|m|l|small|big)\\b.*");

        if (!userMentionedColor) {
            query.setColor(null);
        }
        if (!userMentionedSize) {
            query.setSize(null);
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            response.setProducts(new ArrayList<>());
            response.setMessage("Bạn hãy cho tôi tên sản phẩm cần kiểm tra tồn kho.");
            return response;
        }

        Integer productId = productChatDAO.findProductIdByKeyword(keyword);
        if (productId == null) {
            response.setProducts(new ArrayList<>());
            response.setMessage("Tôi không tìm thấy sản phẩm để kiểm tra tồn kho.");
            return response;
        }

        List<VariantStockResult> variants = productChatDAO.getVariantStock(productId);
        if (variants == null || variants.isEmpty()) {
            response.setProducts(new ArrayList<>());
            response.setMessage("Sản phẩm này hiện chưa có dữ liệu tồn kho trong hệ thống.");
            return response;
        }

        List<ChatProductResult> productInfo = productChatDAO.findProductsByName(keyword);
        response.setProducts(productInfo != null ? productInfo : new ArrayList<>());

        int totalQuantity = 0;
        boolean matched = false;

        for (VariantStockResult item : variants) {
            boolean colorMatch = (query.getColor() == null)
                    || (item.getColor() != null && query.getColor().equalsIgnoreCase(item.getColor()));

            boolean sizeMatch = (query.getSize() == null)
                    || (item.getSize() != null && query.getSize().equalsIgnoreCase(item.getSize()));

            if (colorMatch && sizeMatch) {
                totalQuantity += item.getQuantity();
                if (item.getQuantity() > 0) {
                    matched = true;
                }
            }
        }

        if (query.getColor() != null || query.getSize() != null) {
            if (matched) {
                StringBuilder msg = new StringBuilder("Sản phẩm vẫn còn hàng");
                if (query.getColor() != null) {
                    msg.append(" màu ").append(query.getColor());
                }
                if (query.getSize() != null) {
                    msg.append(" size ").append(query.getSize());
                }
                msg.append(" với số lượng hiện có là ").append(totalQuantity).append(".");
                response.setMessage(msg.toString());
            } else {
                response.setMessage("Biến thể bạn hỏi hiện đã hết hàng hoặc không có trong hệ thống.");
            }
        } else {
            int sumAll = 0;
            for (VariantStockResult item : variants) {
                sumAll += item.getQuantity();
            }
            response.setMessage("Sản phẩm hiện còn tổng cộng " + sumAll + " sản phẩm trong kho.");
        }

        return response;
    }

    private String generateNaturalReply(String userMessage, List<ChatProductResult> products, String fallback) {
        try {
            return aiResponseService.generateReply(userMessage, products);
        } catch (Exception e) {
            e.printStackTrace();
            return fallback;
        }
    }

    private void saveSessionContext(ChatQuery query, ChatResponse response, HttpSession session) {
        if (session == null) {
            return;
        }

        String raw = query.getRawMessage() != null ? query.getRawMessage().toLowerCase().trim() : "";

        boolean userMentionedColor = raw.contains("màu")
                || raw.contains("đen")
                || raw.contains("đỏ")
                || raw.contains("xanh");

        boolean userMentionedSize = raw.contains("size")
                || raw.matches(".*\\b(s|m|l|small|big)\\b.*");

        List<ChatProductResult> products = response.getProducts() != null ? response.getProducts() : new ArrayList<>();
        int resultCount = products.size();

        session.setAttribute("chat_last_result_count", resultCount);

        if (resultCount == 1) {
            session.setAttribute("chat_context_type", CONTEXT_PRODUCT);
            ChatProductResult first = products.get(0);
            session.setAttribute("chat_last_product_keyword", first.getProductName());

            if (first.getCategoryName() != null) {
                session.setAttribute("chat_last_category", first.getCategoryName());
            }
        } else if (resultCount > 1) {
            session.setAttribute("chat_context_type", CONTEXT_CATEGORY);
            session.removeAttribute("chat_last_product_keyword");
        } else {
            session.removeAttribute("chat_context_type");
            session.removeAttribute("chat_last_product_keyword");
        }

        if (query.getCategory() != null) {
            session.setAttribute("chat_last_category", query.getCategory());
        } else if (resultCount == 0) {
            session.removeAttribute("chat_last_category");
        }

        if (userMentionedColor && query.getColor() != null) {
            session.setAttribute("chat_last_color", query.getColor());
        } else {
            session.removeAttribute("chat_last_color");
        }

        if (userMentionedSize && query.getSize() != null) {
            session.setAttribute("chat_last_size", query.getSize());
        } else {
            session.removeAttribute("chat_last_size");
        }

        if (query.getMinPrice() != null) {
            session.setAttribute("chat_last_min_price", query.getMinPrice());
        } else {
            session.removeAttribute("chat_last_min_price");
        }

        if (query.getMaxPrice() != null) {
            session.setAttribute("chat_last_max_price", query.getMaxPrice());
        } else {
            session.removeAttribute("chat_last_max_price");
        }

        if (query.getDescriptionKeyword() != null && !query.getDescriptionKeyword().trim().isEmpty()) {
            session.setAttribute("chat_last_description_keyword", query.getDescriptionKeyword());
        } else {
            session.removeAttribute("chat_last_description_keyword");
        }
    }

    private void applySessionContext(ChatQuery query, HttpSession session) {
        if (session == null) {
            return;
        }

        String raw = query.getRawMessage() != null ? query.getRawMessage().toLowerCase().trim() : "";

        boolean isFollowUp = raw.startsWith("còn")
                || raw.contains("thì sao")
                || raw.contains("còn hàng")
                || raw.contains("còn màu")
                || raw.contains("còn size")
                || raw.contains("mẫu nào")
                || raw.contains("loại nào")
                || raw.contains("rẻ hơn")
                || raw.contains("đắt hơn");

        boolean userMentionedColor = raw.contains("màu")
                || raw.contains("đen")
                || raw.contains("đỏ")
                || raw.contains("xanh");

        boolean userMentionedSize = raw.contains("size")
                || raw.matches(".*\\b(s|m|l|small|big)\\b.*");

        if (!isFollowUp) {
            return;
        }

        Object contextTypeObj = session.getAttribute("chat_context_type");
        String contextType = contextTypeObj != null ? contextTypeObj.toString() : "";

        if (CONTEXT_PRODUCT.equals(contextType)) {
            if (query.getProductKeyword() == null) {
                Object value = session.getAttribute("chat_last_product_keyword");
                if (value != null) {
                    query.setProductKeyword(value.toString());
                }
            }

            if (!userMentionedColor) {
                query.setColor(null);
            } else if (query.getColor() == null) {
                Object value = session.getAttribute("chat_last_color");
                if (value != null) {
                    query.setColor(value.toString());
                }
            }

            if (!userMentionedSize) {
                query.setSize(null);
            } else if (query.getSize() == null) {
                Object value = session.getAttribute("chat_last_size");
                if (value != null) {
                    query.setSize(value.toString());
                }
            }

            return;
        }

        if (query.getCategory() == null) {
            Object value = session.getAttribute("chat_last_category");
            if (value != null) {
                query.setCategory(value.toString());
            }
        }

        if (query.getColor() == null) {
            Object value = session.getAttribute("chat_last_color");
            if (value != null) {
                query.setColor(value.toString());
            }
        }

        if (query.getSize() == null) {
            Object value = session.getAttribute("chat_last_size");
            if (value != null) {
                query.setSize(value.toString());
            }
        }

        if (query.getDescriptionKeyword() == null) {
            Object value = session.getAttribute("chat_last_description_keyword");
            if (value != null) {
                query.setDescriptionKeyword(value.toString());
            }
        }

        if (query.getMinPrice() == null) {
            Object value = session.getAttribute("chat_last_min_price");
            if (value instanceof Double) {
                query.setMinPrice((Double) value);
            }
        }

        if (query.getMaxPrice() == null) {
            Object value = session.getAttribute("chat_last_max_price");
            if (value instanceof Double) {
                query.setMaxPrice((Double) value);
            }
        }
    }

    private boolean containsUnsupportedColor(String message) {

    if (message == null) {
        return false;
    }

    String raw = message.toLowerCase().trim();

    // chỉ kiểm tra màu nếu user nói tới màu
    if (!raw.contains("màu")) {
        return false;
    }

    return containsAny(raw,
            "xanh lá", "xanh la", "green",
            "vàng", "vang", "yellow",
            "hồng", "hong", "pink",
            "trắng", "trang", "white",
            "tím", "tim", "purple",
            "cam", "orange",
            "nâu", "nau", "brown",
            "xám", "xam", "gray", "grey");
}

    private boolean containsAny(String raw, String... keywords) {
        if (raw == null) {
            return false;
        }

        for (String keyword : keywords) {
            if (raw.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String formatPrice(Double price) {
        if (price == null) {
            return "không xác định";
        }
        return String.format("%,.0f VNĐ", price);
    }
}