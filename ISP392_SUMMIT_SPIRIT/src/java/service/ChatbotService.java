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
    private static final String INTENT_OUTFIT_SUGGESTION = "OUTFIT_SUGGESTION";

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

            String rawDetect = message == null ? "" : message.toLowerCase().trim();

            if (containsAny(rawDetect,
                    "bảng size", "bang size",
                    "bảng quy đổi size", "bang quy doi size",
                    "size chart", "bảng kích cỡ", "bang kich co")) {
                query.setIntent(ChatIntent.SIZE_GUIDE);
            } else if (containsAny(rawDetect,
                    "ngoại cỡ", "ngoai co",
                    "size ngoại cỡ", "size ngoai co",
                    "size lớn", "size lon",
                    "người ngoại cỡ", "nguoi ngoai co")) {
                query.setIntent(ChatIntent.OVERSIZE_SUPPORT);
            } else if (containsAny(rawDetect,
                    "chọn size", "chon size",
                    "size phù hợp", "size phu hop",
                    "làm sao chọn size", "lam sao chon size",
                    "tư vấn size", "tu van size")) {
                query.setIntent(ChatIntent.SIZE_SELECTION_HELP);
            }

            if (rawDetect.equals("xin chào")
                    || rawDetect.equals("xin chao")
                    || rawDetect.equals("hello")
                    || rawDetect.equals("hi")
                    || rawDetect.contains("bạn là ai")
                    || rawDetect.contains("ban la ai")
                    || rawDetect.contains("bạn làm được gì")
                    || rawDetect.contains("ban lam duoc gi")) {
                query.setIntent(ChatIntent.SMALL_TALK);
            } else if (rawDetect.contains("outfit")
                    || rawDetect.contains("combo")
                    || rawDetect.contains("mặc gì")
                    || rawDetect.contains("mac gi")
                    || rawDetect.contains("mùa xuân")
                    || rawDetect.contains("mua xuan")
                    || rawDetect.contains("mùa hè")
                    || rawDetect.contains("mua he")
                    || rawDetect.contains("mùa thu")
                    || rawDetect.contains("mua thu")
                    || rawDetect.contains("mùa đông")
                    || rawDetect.contains("mua dong")
                    || rawDetect.equals("xuân")
                    || rawDetect.equals("xuan")
                    || rawDetect.equals("hè")
                    || rawDetect.equals("he")
                    || rawDetect.equals("thu")
                    || rawDetect.equals("đông")
                    || rawDetect.equals("dong")
                    || rawDetect.contains("gợi ý đồ")
                    || rawDetect.contains("goi y do")
                    || rawDetect.contains("phù hợp mùa")
                    || rawDetect.contains("phu hop mua")
                    || rawDetect.contains("đồ leo núi mùa")
                    || rawDetect.contains("do leo nui mua")
                    || rawDetect.contains("sản phẩm mùa")
                    || rawDetect.contains("san pham mua")) {
                query.setIntent(INTENT_OUTFIT_SUGGESTION);
            }

            applySessionContext(query, session);

            if (containsAmbiguousBlue(message)) {
                response.setSuccess(true);
                response.setMessage("Màu 'xanh' còn hơi mơ hồ. Cửa hàng hiện chỉ hỗ trợ các màu Black, Red và Blue. Nếu bạn muốn xanh dương, hãy thử hỏi như: áo màu xanh dương hoặc áo màu Blue.");
                response.setProducts(new ArrayList<ChatProductResult>());
                saveSessionContext(query, response, session);
                return response;
            }

            if (containsUnsupportedColor(message)) {
                response.setSuccess(true);
                response.setMessage("Xin lỗi, cửa hàng hiện chỉ hỗ trợ các màu Black, Red và Blue. Bạn có thể thử lại với màu phù hợp.");
                response.setProducts(new ArrayList<ChatProductResult>());
                saveSessionContext(query, response, session);
                return response;
            }

            String intent = query.getIntent();

            if (INTENT_OUTFIT_SUGGESTION.equals(intent)) {
                response = handleOutfitSuggestion(message);
            } else if (ChatIntent.SMALL_TALK.equals(intent)) {
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
                response.setProducts(new ArrayList<ChatProductResult>());
            } else if (ChatIntent.CHECK_STOCK.equals(intent)) {
                if (query.getProductKeyword() == null || query.getProductKeyword().trim().isEmpty()) {
                    response = handleSearchProduct(query);
                } else {
                    response = handleCheckStock(query);
                }
            } else if (ChatIntent.FIND_CHEAPEST_PRODUCT.equals(intent)) {
                response = handleFindCheapestProduct();
            } else if (ChatIntent.FIND_MOST_EXPENSIVE_PRODUCT.equals(intent)) {
                response = handleFindMostExpensiveProduct();
            } else if (ChatIntent.FIND_BEST_SELLING_PRODUCT.equals(intent)) {
                response = handleFindBestSellingProduct();
            } else if (ChatIntent.FIND_CHEAPEST_BY_CATEGORY.equals(intent)) {
                response = handleFindCheapestByCategory(query);
            } else if (ChatIntent.FIND_MOST_EXPENSIVE_BY_CATEGORY.equals(intent)) {
                response = handleFindMostExpensiveByCategory(query);
            } else if (ChatIntent.SIZE_GUIDE.equals(intent)) {
                response = handleSizeGuide();
            } else if (ChatIntent.OVERSIZE_SUPPORT.equals(intent)) {
                response = handleOversizeSupport();
            } else if (ChatIntent.SIZE_SELECTION_HELP.equals(intent)) {
                response = handleSizeSelectionHelp();
            } else if (ChatIntent.SIZE_RECOMMENDATION.equals(intent)) {
                response = handleSizeRecommendation(query);
            } else if (ChatIntent.SEARCH_PRODUCT.equals(intent)) {
                response = handleSearchProduct(query);
            } else if (ChatIntent.UNSUPPORTED_PRODUCT.equals(intent)) {
                response.setSuccess(true);
                response.setMessage("Xin lỗi, cửa hàng hiện không có sản phẩm này.");
                response.setProducts(new ArrayList<ChatProductResult>());
            } else {
                response.setSuccess(true);
                response.setMessage("Xin lỗi, tôi chỉ hỗ trợ tư vấn sản phẩm và thông tin liên quan trong cửa hàng.");
                response.setProducts(new ArrayList<ChatProductResult>());
            }

            saveSessionContext(query, response, session);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage("Hệ thống chatbot đang gặp lỗi. Vui lòng thử lại sau.");
            response.setProducts(new ArrayList<ChatProductResult>());
            return response;
        }
    }

    private ChatResponse handleOutfitSuggestion(String message) throws Exception {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);

        String raw = message == null ? "" : message.toLowerCase();
        List<ChatProductResult> comboProducts = new ArrayList<ChatProductResult>();

        if (raw.contains("hè") || raw.contains("he") || raw.contains("summer")) {
            comboProducts.addAll(safeFindProductsByName("K109"));
            comboProducts.addAll(safeFindProductsByName("K129"));
            comboProducts.addAll(safeFindProductsByName("K52"));
            comboProducts.addAll(safeFindProductsByName("Matador"));

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

        if (raw.contains("đông") || raw.contains("dong") || raw.contains("winter")) {
            comboProducts.addAll(safeFindProductsByName("K151"));
            comboProducts.addAll(safeFindProductsByName("K116"));
            comboProducts.addAll(safeFindProductsByName("Flag Bearer"));
            comboProducts.addAll(safeFindProductsByName("65L"));

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

        if (raw.contains("xuân") || raw.contains("xuan") || raw.contains("spring")) {
            comboProducts.addAll(safeFindProductsByName("K128"));
            comboProducts.addAll(safeFindProductsByName("K155"));
            comboProducts.addAll(safeFindProductsByName("Ranger"));
            comboProducts.addAll(safeFindProductsByName("Moab"));

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

        if (raw.contains("thu") || raw.contains("autumn") || raw.contains("fall")) {
            addFirstIfPresent(comboProducts, safeFindProductsByName("K128"));
            addFirstIfPresent(comboProducts, safeFindProductsByName("K154"));
            addFirstIfPresent(comboProducts, safeFindProductsByName("Bucket"));
            addFirstIfPresent(comboProducts, safeFindProductsByName("Naturehike"));

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

        response.setProducts(new ArrayList<ChatProductResult>());
        response.setMessage(
                "Bạn muốn gợi ý combo outfit theo mùa nào?\n"
                + "• Xuân\n"
                + "• Hè\n"
                + "• Thu\n"
                + "• Đông"
        );

        return response;
    }

    private ChatResponse handleFindCheapestProduct() throws Exception {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);

        ChatProductResult product = productChatDAO.findCheapestProduct();
        List<ChatProductResult> products = new ArrayList<ChatProductResult>();

        if (product == null) {
            response.setProducts(products);
            response.setMessage("Tôi chưa tìm thấy sản phẩm phù hợp với yêu cầu của bạn.");
            return response;
        }

        products.add(product);
        response.setProducts(products);
        response.setMessage("Sản phẩm rẻ nhất hiện tại là " + product.getProductName()
                + " với giá " + formatPrice(product.getMinPrice()) + ".");
        return response;
    }

    private ChatResponse handleFindMostExpensiveProduct() throws Exception {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);

        ChatProductResult product = productChatDAO.findMostExpensiveProduct();
        List<ChatProductResult> products = new ArrayList<ChatProductResult>();

        if (product == null) {
            response.setProducts(products);
            response.setMessage("Tôi chưa tìm thấy sản phẩm phù hợp với yêu cầu của bạn.");
            return response;
        }

        products.add(product);
        response.setProducts(products);
        response.setMessage("Sản phẩm mắc nhất hiện tại là " + product.getProductName()
                + " với giá " + formatPrice(product.getMaxPrice()) + ".");
        return response;
    }

    private ChatResponse handleFindBestSellingProduct() throws Exception {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);

        ChatProductResult product = productChatDAO.findBestSellingProduct();
        List<ChatProductResult> products = new ArrayList<ChatProductResult>();

        if (product == null) {
            response.setProducts(products);
            response.setMessage("Hiện tôi chưa xác định được sản phẩm bán chạy nhất.");
            return response;
        }

        products.add(product);
        response.setProducts(products);
        response.setMessage("Sản phẩm đang bán chạy nhất là " + product.getProductName()
                + " với tổng số lượng đã bán là " + product.getTotalQuantity() + ".");
        return response;
    }

    private ChatResponse handleFindCheapestByCategory(ChatQuery query) throws Exception {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);

        String category = query.getCategory();
        String raw = query.getRawMessage() == null ? "" : query.getRawMessage().toLowerCase().trim();
        List<ChatProductResult> products = new ArrayList<ChatProductResult>();

        if (containsAny(raw, "dao", "knife")) {
            ChatProductResult product = productChatDAO.findCheapestProductByKeyword("dao");

            if (product == null) {
                response.setProducts(products);
                response.setMessage("Hiện không có sản phẩm dao nào trong hệ thống.");
                return response;
            }

            products.add(product);
            response.setProducts(products);
            response.setMessage("Sản phẩm dao rẻ nhất hiện tại là "
                    + product.getProductName() + " với giá " + formatPrice(product.getMinPrice()) + ".");
            return response;
        }

        if (category == null || category.trim().isEmpty()) {
            response.setProducts(products);
            response.setMessage("Bạn hãy cho tôi biết nhóm sản phẩm cần tìm rẻ nhất.");
            return response;
        }

        ChatProductResult product = productChatDAO.findCheapestProductByCategory(category);

        if (product == null) {
            response.setProducts(products);
            response.setMessage("Tôi chưa tìm thấy sản phẩm phù hợp với yêu cầu của bạn.");
            return response;
        }

        products.add(product);
        response.setProducts(products);
        response.setMessage("Sản phẩm rẻ nhất của nhóm " + category + " là "
                + product.getProductName() + " với giá " + formatPrice(product.getMinPrice()) + ".");
        return response;
    }

    private ChatResponse handleFindMostExpensiveByCategory(ChatQuery query) throws Exception {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);

        String category = query.getCategory();
        String raw = query.getRawMessage() == null ? "" : query.getRawMessage().toLowerCase().trim();
        List<ChatProductResult> products = new ArrayList<ChatProductResult>();

        if (containsAny(raw, "dao", "knife")) {
            ChatProductResult product = productChatDAO.findMostExpensiveProductByKeyword("dao");

            if (product == null) {
                response.setProducts(products);
                response.setMessage("Hiện không có sản phẩm dao nào trong hệ thống.");
                return response;
            }

            products.add(product);
            response.setProducts(products);
            response.setMessage("Sản phẩm dao mắc nhất hiện tại là "
                    + product.getProductName() + " với giá " + formatPrice(product.getMaxPrice()) + ".");
            return response;
        }

        if (category == null || category.trim().isEmpty()) {
            response.setProducts(products);
            response.setMessage("Bạn hãy cho tôi biết nhóm sản phẩm cần tìm mắc nhất.");
            return response;
        }

        ChatProductResult product = productChatDAO.findMostExpensiveProductByCategory(category);

        if (product == null) {
            response.setProducts(products);
            response.setMessage("Tôi chưa tìm thấy sản phẩm phù hợp với yêu cầu của bạn.");
            return response;
        }

        products.add(product);
        response.setProducts(products);
        response.setMessage("Sản phẩm mắc nhất của nhóm " + category + " là "
                + product.getProductName() + " với giá " + formatPrice(product.getMaxPrice()) + ".");
        return response;
    }

    private List<ChatProductResult> safeFindProductsByName(String keyword) throws Exception {
        List<ChatProductResult> products = productChatDAO.findProductsByName(keyword);
        return products != null ? products : new ArrayList<ChatProductResult>();
    }

    private void addFirstIfPresent(List<ChatProductResult> target, List<ChatProductResult> source) {
        if (source != null && !source.isEmpty()) {
            target.add(source.get(0));
        }
    }

    private ChatResponse handleSearchProduct(ChatQuery query) throws Exception {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);

        List<ChatProductResult> products = new ArrayList<ChatProductResult>();

        try {
            if (query.getProductKeyword() != null && !query.getProductKeyword().trim().isEmpty()) {
                products = productChatDAO.findProductsByName(query.getProductKeyword());
            } else {
                products = productChatDAO.findProductsByFilters(query);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setProducts(new ArrayList<ChatProductResult>());
            response.setMessage("Lỗi khi truy vấn sản phẩm từ database.");
            return response;
        }

        response.setProducts(products != null ? products : new ArrayList<ChatProductResult>());

        if (products == null || products.isEmpty()) {
            response.setMessage("Tôi chưa tìm thấy sản phẩm phù hợp với yêu cầu của bạn.");
            return response;
        }

        boolean hasStrictFilter = query.getColor() != null
                || query.getSize() != null
                || query.getMinPrice() != null
                || query.getMaxPrice() != null
                || query.getDescriptionKeyword() != null
                || query.isInStockOnly();

        try {
            if (isGeneralCategoryPriceQuestion(query)) {
                List<ChatProductResult> allProductsForPrice = getAllProductsForPriceRange(query, products);
                response.setMessage(buildCategoryPriceMessage(query, allProductsForPrice));
            } else if (hasStrictFilter) {
                response.setMessage(buildDeterministicSearchMessage(query, products));
            } else {
                response.setMessage(generateNaturalReply(
                        query.getRawMessage(),
                        products,
                        "Tôi tìm thấy " + products.size() + " sản phẩm phù hợp với yêu cầu của bạn."
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setMessage("Tôi tìm thấy " + products.size() + " sản phẩm phù hợp với yêu cầu của bạn.");
        }

        return response;
    }

    private List<ChatProductResult> getAllProductsForPriceRange(ChatQuery query, List<ChatProductResult> fallbackProducts) {
        try {
            return productChatDAO.findAllProductsByFilters(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fallbackProducts != null ? fallbackProducts : new ArrayList<ChatProductResult>();
    }

    private boolean isGeneralCategoryPriceQuestion(ChatQuery query) {
        if (query == null || query.getRawMessage() == null) {
            return false;
        }

        String raw = query.getRawMessage().toLowerCase().trim();

        boolean askingPrice = raw.contains("giá")
                || raw.contains("gia")
                || raw.contains("bao nhiêu")
                || raw.contains("bao nhieu")
                || raw.contains("giá sao")
                || raw.contains("gia sao")
                || raw.contains("tầm giá")
                || raw.contains("tam gia")
                || raw.contains("giá cao")
                || raw.contains("gia cao");

        boolean hasCategoryOnly = query.getCategory() != null
                && (query.getProductKeyword() == null || query.getProductKeyword().trim().isEmpty())
                && query.getColor() == null
                && query.getSize() == null
                && query.getDescriptionKeyword() == null
                && query.getMinPrice() == null
                && query.getMaxPrice() == null
                && !query.isInStockOnly();

        return askingPrice && hasCategoryOnly;
    }

    private String buildCategoryPriceMessage(ChatQuery query, List<ChatProductResult> products) {
        if (products == null || products.isEmpty()) {
            return "Tôi chưa tìm thấy sản phẩm phù hợp với yêu cầu của bạn.";
        }

        Double min = null;
        Double max = null;

        for (ChatProductResult item : products) {
            if (item.getMinPrice() != null) {
                if (min == null || item.getMinPrice() < min) {
                    min = item.getMinPrice();
                }
            }
            if (item.getMaxPrice() != null) {
                if (max == null || item.getMaxPrice() > max) {
                    max = item.getMaxPrice();
                }
            }
        }

        String category = query.getCategory() != null ? query.getCategory() : "sản phẩm";

        if (min != null && max != null) {
            if (min.doubleValue() == max.doubleValue()) {
                return "Các sản phẩm thuộc nhóm " + category + " hiện có mức giá khoảng " + formatPrice(min) + ".";
            }
            return "Các sản phẩm thuộc nhóm " + category + " hiện có mức giá tham khảo từ "
                    + formatPrice(min) + " đến " + formatPrice(max) + ".";
        }

        return "Tôi tìm thấy " + products.size() + " sản phẩm thuộc nhóm " + category + ".";
    }

    private String buildDeterministicSearchMessage(ChatQuery query, List<ChatProductResult> products) {
        StringBuilder sb = new StringBuilder("Tôi tìm thấy ");
        sb.append(products.size()).append(" sản phẩm phù hợp");

        List<String> conditions = new ArrayList<String>();

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
                || raw.contains("mau")
                || raw.contains("đen")
                || raw.contains("den")
                || raw.contains("đỏ")
                || raw.contains("do")
                || raw.contains("xanh");

        boolean userMentionedSize = raw.contains("size")
                || raw.matches(".*\\b(s|m|l|xl|xxl|small|big)\\b.*");

        if (!userMentionedColor) {
            query.setColor(null);
        }
        if (!userMentionedSize) {
            query.setSize(null);
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            response.setProducts(new ArrayList<ChatProductResult>());
            response.setMessage("Bạn hãy cho tôi tên sản phẩm cần kiểm tra tồn kho.");
            return response;
        }

        Integer productId = productChatDAO.findProductIdByKeyword(keyword);
        if (productId == null) {
            response.setProducts(new ArrayList<ChatProductResult>());
            response.setMessage("Tôi không tìm thấy sản phẩm để kiểm tra tồn kho.");
            return response;
        }

        List<VariantStockResult> variants = productChatDAO.getVariantStock(productId);
        if (variants == null || variants.isEmpty()) {
            response.setProducts(new ArrayList<ChatProductResult>());
            response.setMessage("Sản phẩm này hiện chưa có dữ liệu tồn kho trong hệ thống.");
            return response;
        }

        List<ChatProductResult> productInfo = productChatDAO.findProductsByName(keyword);
        response.setProducts(productInfo != null ? productInfo : new ArrayList<ChatProductResult>());

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
                || raw.contains("mau")
                || raw.contains("đen")
                || raw.contains("den")
                || raw.contains("đỏ")
                || raw.contains("do")
                || raw.contains("xanh");

        boolean userMentionedSize = raw.contains("size")
                || raw.matches(".*\\b(s|m|l|xl|xxl|small|big)\\b.*");

        List<ChatProductResult> products = response.getProducts() != null
                ? response.getProducts() : new ArrayList<ChatProductResult>();

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
                || raw.startsWith("con")
                || raw.contains("thì sao")
                || raw.contains("thi sao")
                || raw.contains("còn hàng")
                || raw.contains("con hang")
                || raw.contains("còn màu")
                || raw.contains("con mau")
                || raw.contains("còn size")
                || raw.contains("con size")
                || raw.contains("mẫu nào")
                || raw.contains("mau nao")
                || raw.contains("loại nào")
                || raw.contains("loai nao")
                || raw.contains("rẻ hơn")
                || raw.contains("re hon")
                || raw.contains("đắt hơn")
                || raw.contains("dat hon");

        boolean userMentionedColor = raw.contains("màu")
                || raw.contains("mau")
                || raw.contains("đen")
                || raw.contains("den")
                || raw.contains("đỏ")
                || raw.contains("do")
                || raw.contains("xanh");

        boolean userMentionedSize = raw.contains("size")
                || raw.matches(".*\\b(s|m|l|xl|xxl|small|big)\\b.*");

        boolean userSpecifiedNewProduct = query.getProductKeyword() != null
                && !query.getProductKeyword().trim().isEmpty();

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
            } else if (query.getColor() == null && !userSpecifiedNewProduct) {
                Object value = session.getAttribute("chat_last_color");
                if (value != null) {
                    query.setColor(value.toString());
                }
            }

            if (!userMentionedSize) {
                query.setSize(null);
            } else if (query.getSize() == null && !userSpecifiedNewProduct) {
                Object value = session.getAttribute("chat_last_size");
                if (value != null) {
                    query.setSize(value.toString());
                }
            }

            return;
        }

        if (query.getCategory() == null && !userSpecifiedNewProduct) {
            Object value = session.getAttribute("chat_last_category");
            if (value != null) {
                query.setCategory(value.toString());
            }
        }

        if (query.getColor() == null && !userSpecifiedNewProduct) {
            Object value = session.getAttribute("chat_last_color");
            if (value != null) {
                query.setColor(value.toString());
            }
        }

        if (query.getSize() == null && !userSpecifiedNewProduct) {
            Object value = session.getAttribute("chat_last_size");
            if (value != null) {
                query.setSize(value.toString());
            }
        }

        if (query.getDescriptionKeyword() == null && !userSpecifiedNewProduct) {
            Object value = session.getAttribute("chat_last_description_keyword");
            if (value != null) {
                query.setDescriptionKeyword(value.toString());
            }
        }

        if (query.getMinPrice() == null && !userSpecifiedNewProduct) {
            Object value = session.getAttribute("chat_last_min_price");
            if (value instanceof Double) {
                query.setMinPrice((Double) value);
            }
        }

        if (query.getMaxPrice() == null && !userSpecifiedNewProduct) {
            Object value = session.getAttribute("chat_last_max_price");
            if (value instanceof Double) {
                query.setMaxPrice((Double) value);
            }
        }
    }

    private boolean containsAmbiguousBlue(String message) {
        if (message == null) {
            return false;
        }

        String raw = message.toLowerCase().trim();

        boolean hasXanh = raw.contains("xanh");
        boolean hasSpecificBlue = raw.contains("xanh dương")
                || raw.contains("xanh duong")
                || raw.contains("blue");
        boolean hasSpecificGreen = raw.contains("xanh lá")
                || raw.contains("xanh la")
                || raw.contains("green");

        return hasXanh && !hasSpecificBlue && !hasSpecificGreen;
    }

    private boolean containsUnsupportedColor(String message) {
        if (message == null) {
            return false;
        }

        String raw = message.toLowerCase().trim();
        String normalized = raw.replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        return containsWord(normalized,
                "xanh lá", "xanh la", "green",
                "vàng", "vang", "yellow",
                "hồng", "hong", "pink",
                "trắng", "trang", "white",
                "tím", "tim", "purple",
                "cam", "orange",
                "nâu", "nau", "brown",
                "xám", "xam", "gray", "grey");
    }

    private boolean containsWord(String text, String... keywords) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        String padded = " " + text + " ";

        for (String keyword : keywords) {
            String normalizedKeyword = " " + keyword.toLowerCase().trim() + " ";
            if (padded.contains(normalizedKeyword)) {
                return true;
            }
        }
        return false;
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

    private ChatResponse handleOversizeSupport() {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);
        response.setProducts(new ArrayList<ChatProductResult>());

        response.setMessage(
                "Có. Hiện shop có hỗ trợ size lớn cho một số sản phẩm, đặc biệt là áo với các size XL và XXL.\n\n"
                + "• Áo: hỗ trợ đến XL, XXL\n"
                + "• Quần: hiện bảng size chủ yếu đến XL\n\n"
                + "Nếu bạn muốn, hãy gửi chiều cao và cân nặng, tôi sẽ gợi ý size phù hợp cho bạn."
        );
        return response;
    }

    private ChatResponse handleSizeSelectionHelp() {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);
        response.setProducts(new ArrayList<ChatProductResult>());

        response.setMessage(
                "Để chọn size phù hợp, bạn hãy gửi cho tôi:\n"
                + "• Loại sản phẩm: áo hay quần\n"
                + "• Chiều cao (cm)\n"
                + "• Cân nặng (kg)\n\n"
                + "Ví dụ:\n"
                + "• áo, 172cm, 68kg\n"
                + "• quần, 165cm, 58kg\n\n"
                + "Tôi sẽ đối chiếu bảng size và gợi ý size phù hợp cho bạn."
        );
        return response;
    }

    private ChatResponse handleSizeGuide() {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);
        response.setProducts(new ArrayList<ChatProductResult>());

        response.setMessage(
                "Bảng quy đổi size hiện tại của shop như sau:\n\n"
                + "BẢNG SIZE ÁO\n"
                + "• S: 160-165cm | 60-65kg\n"
                + "• M: 165-170cm | 65-70kg\n"
                + "• L: 170-175cm | 70-75kg\n"
                + "• XL: 175-180cm | 75-80kg\n"
                + "• XXL: 175-180cm | 80-85kg\n\n"
                + "BẢNG SIZE QUẦN\n"
                + "• S: size số 26-27 | 145-150cm | 45-50kg\n"
                + "• M: size số 28 | 155-160cm | 50-56kg\n"
                + "• L: size số 29 | 160-165cm | 56-60kg\n"
                + "• XL: size số 30 | 165-170cm | 60-64kg\n\n"
                + "Bạn có thể gửi chiều cao + cân nặng để tôi tư vấn size cụ thể."
        );
        return response;
    }

    private ChatResponse handleSizeRecommendation(ChatQuery query) {
        ChatResponse response = new ChatResponse();
        response.setSuccess(true);
        response.setProducts(new ArrayList<ChatProductResult>());

        Integer height = query.getHeightCm();
        Integer weight = query.getWeightKg();
        String category = query.getCategory();

        if (height == null || weight == null) {
            response.setMessage("Bạn hãy gửi đủ chiều cao và cân nặng, ví dụ: áo 172cm 68kg hoặc quần 165cm 58kg.");
            return response;
        }

        if (category == null || category.trim().isEmpty()) {
            response.setMessage("Bạn muốn tư vấn size cho áo hay quần? Ví dụ: áo 172cm 68kg hoặc quần 165cm 58kg.");
            return response;
        }

        if ("Shirts".equalsIgnoreCase(category)) {
            String size = recommendShirtSize(height, weight);
            if (size == null) {
                response.setMessage(
                        "Tôi chưa thấy size áo nào khớp hoàn toàn với số đo của bạn.\n"
                        + "Bảng size áo hiện có:\n"
                        + "• S: 160-165cm | 60-65kg\n"
                        + "• M: 165-170cm | 65-70kg\n"
                        + "• L: 170-175cm | 70-75kg\n"
                        + "• XL: 175-180cm | 75-80kg\n"
                        + "• XXL: 175-180cm | 80-85kg"
                );
                return response;
            }

            response.setMessage(buildShirtRecommendationMessage(height, weight, size));
            return response;
        }

        if ("Pants".equalsIgnoreCase(category)) {
            String size = recommendPantsSize(height, weight);
            if (size == null) {
                response.setMessage(
                        "Tôi chưa thấy size quần nào khớp hoàn toàn với số đo của bạn.\n"
                        + "Bảng size quần hiện có:\n"
                        + "• S: size số 26-27 | 145-150cm | 45-50kg\n"
                        + "• M: size số 28 | 155-160cm | 50-56kg\n"
                        + "• L: size số 29 | 160-165cm | 56-60kg\n"
                        + "• XL: size số 30 | 165-170cm | 60-64kg"
                );
                return response;
            }

            response.setMessage(buildPantsRecommendationMessage(height, weight, size));
            return response;
        }

        response.setMessage("Hiện tôi mới hỗ trợ tư vấn size cho áo và quần.");
        return response;
    }

    private String recommendShirtSize(int height, int weight) {
    if (inRange(height, 160, 165) && inRange(weight, 60, 65)) return "S";
    if (inRange(height, 165, 170) && inRange(weight, 65, 70)) return "M";
    if (inRange(height, 170, 175) && inRange(weight, 70, 75)) return "L";
    if (inRange(height, 175, 180) && inRange(weight, 75, 80)) return "XL";
    if (inRange(height, 175, 180) && inRange(weight, 80, 85)) return "XXL";

    if (inRange(height, 160, 165)) return "S";
    if (inRange(height, 165, 170)) return "M";
    if (inRange(height, 170, 175)) return "L";
    if (inRange(height, 175, 180) && weight < 80) return "XL";
    if (inRange(height, 175, 180) && weight >= 80) return "XXL";

    if (inRange(weight, 60, 65)) return "S";
    if (inRange(weight, 65, 70)) return "M";
    if (inRange(weight, 70, 75)) return "L";
    if (inRange(weight, 75, 80)) return "XL";
    if (inRange(weight, 80, 85)) return "XXL";

    return null;
}

    private String recommendPantsSize(int height, int weight) {
    if (inRange(height, 145, 150) && inRange(weight, 45, 50)) return "S";
    if (inRange(height, 155, 160) && inRange(weight, 50, 56)) return "M";
    if (inRange(height, 160, 165) && inRange(weight, 56, 60)) return "L";
    if (inRange(height, 165, 170) && inRange(weight, 60, 64)) return "XL";

    if (inRange(height, 145, 150)) return "S";
    if (inRange(height, 155, 160)) return "M";
    if (inRange(height, 160, 165)) return "L";
    if (inRange(height, 165, 170)) return "XL";

    if (inRange(weight, 45, 50)) return "S";
    if (inRange(weight, 50, 56)) return "M";
    if (inRange(weight, 56, 60)) return "L";
    if (inRange(weight, 60, 64)) return "XL";

    return null;
}

    private String buildShirtRecommendationMessage(int height, int weight, String size) {
        String detail;
        if ("S".equals(size)) {
            detail = "• S: 160-165cm | 60-65kg";
        } else if ("M".equals(size)) {
            detail = "• M: 165-170cm | 65-70kg";
        } else if ("L".equals(size)) {
            detail = "• L: 170-175cm | 70-75kg";
        } else if ("XL".equals(size)) {
            detail = "• XL: 175-180cm | 75-80kg";
        } else {
            detail = "• XXL: 175-180cm | 80-85kg";
        }

        return "Với áo, chiều cao " + height + "cm và cân nặng " + weight + "kg, bạn nên chọn size " + size + ".\n\n"
                + "Theo bảng size áo:\n"
                + detail
                + "\n\nNếu bạn thích mặc rộng hơn, có thể cân nhắc tăng thêm 1 size.";
    }

    private String buildPantsRecommendationMessage(int height, int weight, String size) {
        String detail;
        if ("S".equals(size)) {
            detail = "• S: size số 26-27 | 145-150cm | 45-50kg";
        } else if ("M".equals(size)) {
            detail = "• M: size số 28 | 155-160cm | 50-56kg";
        } else if ("L".equals(size)) {
            detail = "• L: size số 29 | 160-165cm | 56-60kg";
        } else {
            detail = "• XL: size số 30 | 165-170cm | 60-64kg";
        }

        return "Với quần, chiều cao " + height + "cm và cân nặng " + weight + "kg, bạn nên chọn size " + size + ".\n\n"
                + "Theo bảng size quần:\n"
                + detail
                + "\n\nNếu bạn thích mặc rộng hơn, có thể cân nhắc tăng thêm 1 size.";
    }

    private boolean inRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
}