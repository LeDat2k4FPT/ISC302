package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.ChatQuery;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AIIntentService {

    private static final String API_KEY = "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";
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private final Gson gson = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();

    public ChatQuery parseUserMessage(String userMessage) throws IOException, InterruptedException {
        validateApiKey();

        String prompt = buildFullPrompt(userMessage);

        JsonObject payload = new JsonObject();
        payload.addProperty("model", MODEL);
        payload.addProperty("temperature", 0);

        JsonObject responseFormat = new JsonObject();
        responseFormat.addProperty("type", "json_object");
        payload.add("response_format", responseFormat);

        JsonArray messages = new JsonArray();

        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty(
                "content",
                "You are an AI that converts a user's Vietnamese shopping/store message into a strict JSON object for database filtering. "
                + "Return JSON only. No explanation."
        );
        messages.add(systemMsg);

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", prompt);
        messages.add(userMsg);

        payload.add("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(payload)))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Groq API error: " + response.statusCode() + " - " + response.body());
        }

        JsonObject root = gson.fromJson(response.body(), JsonObject.class);
        String contentText = root.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();

        String cleanedJson = cleanJsonText(contentText);

        System.out.println("AI JSON = " + cleanedJson);

        ChatQuery query = mapJsonToChatQuery(cleanedJson);
        applyKeywordFallback(userMessage, query);
        normalizeGeneralPriceQuery(userMessage, query);
        normalizeGlobalPriceExtremes(userMessage, query);
        normalizeCategoryPriceExtremes(userMessage, query);
        normalizeBestSellingIntent(userMessage, query);
        normalizeSizeRecommendationIntent(userMessage, query);

        return query;
    }

    private void validateApiKey() {
        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            throw new IllegalStateException("Missing GROQ_API_KEY.");
        }
    }

    private String cleanJsonText(String raw) {
        if (raw == null) {
            return "{}";
        }

        String cleaned = raw.trim();

        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }

        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }

        int firstBrace = cleaned.indexOf('{');
        int lastBrace = cleaned.lastIndexOf('}');
        if (firstBrace >= 0 && lastBrace > firstBrace) {
            cleaned = cleaned.substring(firstBrace, lastBrace + 1);
        }

        return cleaned;
    }

    private String buildFullPrompt(String userMessage) {
        String safeMessage = userMessage == null ? "" : userMessage.trim();

        return "You are an AI that converts a user's Vietnamese shopping/store message into a strict JSON object for database filtering.\n"
                + "Return JSON only. No explanation.\n"
                + "\n"
                + "Supported high-level intents are:\n"
                + "SMALL_TALK, SEARCH_PRODUCT, CHECK_STOCK, UNSUPPORTED_PRODUCT, OUT_OF_SCOPE, FIND_CHEAPEST_PRODUCT, FIND_MOST_EXPENSIVE_PRODUCT, FIND_CHEAPEST_BY_CATEGORY, FIND_MOST_EXPENSIVE_BY_CATEGORY, FIND_BEST_SELLING_PRODUCT, SIZE_RECOMMENDATION.\n"
                + "\n"
                + "Use this exact schema:\n"
                + "{\n"
                + "  \"intent\":\"SMALL_TALK|SEARCH_PRODUCT|CHECK_STOCK|UNSUPPORTED_PRODUCT|OUT_OF_SCOPE|FIND_CHEAPEST_PRODUCT|FIND_MOST_EXPENSIVE_PRODUCT|FIND_CHEAPEST_BY_CATEGORY|FIND_MOST_EXPENSIVE_BY_CATEGORY|FIND_BEST_SELLING_PRODUCT|SIZE_RECOMMENDATION\",\n"
                + "  \"productKeyword\":string|null,\n"
                + "  \"category\":\"Pants|Shirts|Backpacks|Camping Tools|Tents|Hats|Cooking Equipment\"|null,\n"
                + "  \"color\":\"Black|Red|Blue\"|null,\n"
                + "  \"size\":\"S|M|L|XL|XXL|small|big\"|null,\n"
                + "  \"minPrice\":number|null,\n"
                + "  \"maxPrice\":number|null,\n"
                + "  \"descriptionKeyword\":string|null,\n"
                + "  \"inStockOnly\":boolean,\n"
                + "  \"heightCm\":number|null,\n"
                + "  \"weightKg\":number|null\n"
                + "}\n"
                + "\n"
                + "Rules:\n"
                + "1. If user is greeting, thanking, opening a conversation, or chatting casually without asking for product lookup, use SMALL_TALK.\n"
                + "2. If user wants product search, recommendation, comparison, or asks for products by category/color/size/price/feature, use SEARCH_PRODUCT.\n"
                + "3. If user asks availability, remaining stock, whether an item still exists, or whether a size/color is available, use CHECK_STOCK and set inStockOnly=true.\n"
                + "4. If user asks for a product type not sold by the store, use UNSUPPORTED_PRODUCT.\n"
                + "5. If message is unrelated to store/products, use OUT_OF_SCOPE.\n"
                + "6. If a specific product/model name is mentioned, fill productKeyword.\n"
                + "7. Normalize Vietnamese meaning into supported database values only.\n"
                + "8. Do not invent unsupported colors, categories, or sizes.\n"
                + "9. If the user asks by usage scenario or feature, map to descriptionKeyword using the normalized tags below.\n"
                + "10. If the user asks general price questions for a product group like 'dao giá bao nhiêu', 'dao camping giá sao', 'dụng cụ camping giá bao nhiêu', prioritize category mapping over feature mapping.\n"
                + "11. If the user asks for the cheapest product in the whole shop, use FIND_CHEAPEST_PRODUCT.\n"
                + "12. If the user asks for the most expensive product in the whole shop, use FIND_MOST_EXPENSIVE_PRODUCT.\n"
                + "13. If the user asks for the cheapest product inside a category such as 'quần rẻ nhất', use FIND_CHEAPEST_BY_CATEGORY and fill category.\n"
                + "14. If the user asks for the most expensive product inside a category such as 'áo mắc nhất', use FIND_MOST_EXPENSIVE_BY_CATEGORY and fill category.\n"
                + "15. If the user asks for the best-selling product in the shop, use FIND_BEST_SELLING_PRODUCT.\n"
                + "16. If the user gives height and weight and asks what size to choose, use SIZE_RECOMMENDATION and fill heightCm, weightKg, and category if possible.\n"
                + "\n"
                + "Normalized feature tags:\n"
                + "- chống nắng / chong nang / chong uv / uv / uv protection / sun protection / omni-shade => UV\n"
                + "- chống gió / chong gio / windproof / wind resistant / áo gió / ao gio => windproof\n"
                + "- nhanh khô / nhanh kho / quick dry / quick-dry / omni-wick => quick-dry\n"
                + "- chống nước / chong nuoc / waterproof / water resistant / rain proof => waterproof\n"
                + "- trekking / leo núi / leo nui / hiking / mountaineering / climbing / outdoor / picnic / camping => trekking\n"
                + "- nhẹ / nhe / siêu nhẹ / sieu nhe / lightweight / ultralight / minimalist => lightweight\n"
                + "\n"
                + "Store sells only:\n"
                + "Pants, Shirts, Backpacks, Camping Tools, Tents, Hats, Cooking Equipment.\n"
                + "\n"
                + "Category mapping:\n"
                + "- quần / quan => Pants\n"
                + "- áo / ao => Shirts\n"
                + "- áo khoác / ao khoac => Shirts\n"
                + "- jacket / áo jacket / ao jacket => Shirts\n"
                + "- balo / ba lô / ba lo / backpack => Backpacks\n"
                + "- dao / knife / dụng cụ / dung cu / camping tools / dụng cụ camping / dung cu camping / dao camping / đồ camping / do camping => Camping Tools\n"
                + "- lều / leu / trại / trai / tent => Tents\n"
                + "- mũ / mu / nón / non / hat / cap => Hats\n"
                + "- bếp / bep / stove / grill => Cooking Equipment\n"
                + "\n"
                + "Color mapping:\n"
                + "- đen / den / black => Black\n"
                + "- đỏ / do / red => Red\n"
                + "- xanh dương / xanh duong / blue => Blue\n"
                + "- xanh lá / xanh la / green => unsupported, set color to null\n"
                + "- green, pink, yellow, white => set color to null\n"
                + "\n"
                + "Size mapping:\n"
                + "- s => S\n"
                + "- m => M\n"
                + "- l => L\n"
                + "- xl => XL\n"
                + "- xxl => XXL\n"
                + "- small / nhỏ / nho / size nhỏ / loại nhỏ => small\n"
                + "- big / lớn / lon / size lớn / loại lớn => big\n"
                + "\n"
                + "Examples:\n"
                + "1) hi =>\n"
                + "{\"intent\":\"SMALL_TALK\",\"productKeyword\":null,\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "2) áo đen =>\n"
                + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Shirts\",\"color\":\"Black\",\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "3) quần dưới 500k =>\n"
                + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Pants\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":500000,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "4) balo siêu nhẹ =>\n"
                + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Backpacks\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":\"lightweight\",\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "5) ktom k116 còn hàng không =>\n"
                + "{\"intent\":\"CHECK_STOCK\",\"productKeyword\":\"Ktom K116\",\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":true,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "6) shop có bán đồng hồ không =>\n"
                + "{\"intent\":\"UNSUPPORTED_PRODUCT\",\"productKeyword\":null,\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "7) thời tiết hôm nay thế nào =>\n"
                + "{\"intent\":\"OUT_OF_SCOPE\",\"productKeyword\":null,\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "8) dao giá bao nhiêu =>\n"
                + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Camping Tools\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "9) sản phẩm rẻ nhất là gì =>\n"
                + "{\"intent\":\"FIND_CHEAPEST_PRODUCT\",\"productKeyword\":null,\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "10) sản phẩm mắc nhất là gì =>\n"
                + "{\"intent\":\"FIND_MOST_EXPENSIVE_PRODUCT\",\"productKeyword\":null,\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "11) quần rẻ nhất là gì =>\n"
                + "{\"intent\":\"FIND_CHEAPEST_BY_CATEGORY\",\"productKeyword\":null,\"category\":\"Pants\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "12) áo mắc nhất là gì =>\n"
                + "{\"intent\":\"FIND_MOST_EXPENSIVE_BY_CATEGORY\",\"productKeyword\":null,\"category\":\"Shirts\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "13) áo size xl =>\n"
                + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Shirts\",\"color\":null,\"size\":\"XL\",\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "14) quần size xxl còn hàng không =>\n"
                + "{\"intent\":\"CHECK_STOCK\",\"productKeyword\":null,\"category\":\"Pants\",\"color\":null,\"size\":\"XXL\",\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":true,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "15) sản phẩm bán chạy nhất là gì =>\n"
                + "{\"intent\":\"FIND_BEST_SELLING_PRODUCT\",\"productKeyword\":null,\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":null,\"weightKg\":null}\n"
                + "\n"
                + "16) áo 172cm 68kg =>\n"
                + "{\"intent\":\"SIZE_RECOMMENDATION\",\"productKeyword\":null,\"category\":\"Shirts\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":172,\"weightKg\":68}\n"
                + "\n"
                + "17) tôi cao 165cm nặng 58kg chọn quần size gì =>\n"
                + "{\"intent\":\"SIZE_RECOMMENDATION\",\"productKeyword\":null,\"category\":\"Pants\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false,\"heightCm\":165,\"weightKg\":58}\n"
                + "\n"
                + "User message: " + safeMessage + "\n"
                + "\n"
                + "Return valid JSON only.";
    }

    private void applyKeywordFallback(String userMessage, ChatQuery query) {
        if (userMessage == null || query == null) {
            return;
        }

        String raw = userMessage.toLowerCase().trim();

        if (query.getCategory() == null) {
            if (containsAny(raw, "jacket", "áo jacket", "ao jacket")) {
                query.setCategory("Shirts");
            } else if (containsAny(raw, "backpack", "balo", "ba lô", "ba lo")) {
                query.setCategory("Backpacks");
            } else if (containsAny(raw, "tent", "lều", "leu")) {
                query.setCategory("Tents");
            } else if (containsAny(raw, "cap", "hat", "mũ", "mu", "nón", "non")) {
                query.setCategory("Hats");
            } else if (containsAny(raw, "stove", "grill", "bếp", "bep")) {
                query.setCategory("Cooking Equipment");
            } else if (containsAny(raw,
                    "dao", "knife",
                    "dao camping",
                    "dụng cụ camping", "dung cu camping",
                    "camping tools")) {
                query.setCategory("Camping Tools");
            } else if (containsAny(raw, "quần", "quan", "pants")) {
                query.setCategory("Pants");
            } else if (containsAny(raw, "áo", "ao", "shirt")) {
                query.setCategory("Shirts");
            }
        }

        if (query.getProductKeyword() == null || query.getProductKeyword().trim().isEmpty()) {
            if (containsAny(raw, "jacket")) {
                query.setProductKeyword("jacket");
            }
        }

        if (query.getDescriptionKeyword() == null) {
            if (containsAny(raw, "chống nắng", "chong nang", "uv", "sun protection", "omni-shade")) {
                query.setDescriptionKeyword("UV");
            } else if (containsAny(raw, "nhanh khô", "nhanh kho", "quick dry", "quick-dry", "omni-wick")) {
                query.setDescriptionKeyword("quick-dry");
            } else if (containsAny(raw, "chống nước", "chong nuoc", "waterproof", "water resistant", "rain proof")) {
                query.setDescriptionKeyword("waterproof");
            } else if (containsAny(raw, "chống gió", "chong gio", "windproof", "wind resistant", "áo gió", "ao gio")) {
                query.setDescriptionKeyword("windproof");
            } else if (containsAny(raw, "trekking", "leo núi", "leo nui", "hiking", "outdoor", "mountaineering")) {
                query.setDescriptionKeyword("trekking");
            } else if (containsAny(raw, "siêu nhẹ", "sieu nhe", "lightweight", "ultralight")) {
                query.setDescriptionKeyword("lightweight");
            }
        }

        if ((query.getIntent() == null || query.getIntent().equals(ChatIntent.OUT_OF_SCOPE))
                && (query.getCategory() != null || query.getProductKeyword() != null)) {
            query.setIntent(ChatIntent.SEARCH_PRODUCT);
        }

        if (containsAny(raw, "còn hàng", "con hang", "còn size", "con size", "còn màu", "con mau")) {
            query.setIntent(ChatIntent.CHECK_STOCK);
            query.setInStockOnly(true);
        }
    }

    private void normalizeGeneralPriceQuery(String userMessage, ChatQuery query) {
        if (userMessage == null || query == null) {
            return;
        }

        String raw = userMessage.toLowerCase().trim();

        if (!isGeneralPriceQuestion(raw)) {
            return;
        }

        if (query.getCategory() == null) {
            if (containsAny(raw,
                    "dao", "knife",
                    "dao camping",
                    "dụng cụ camping", "dung cu camping",
                    "camping tools")) {
                query.setCategory("Camping Tools");
            } else if (containsAny(raw, "balo", "ba lô", "ba lo", "backpack")) {
                query.setCategory("Backpacks");
            } else if (containsAny(raw, "lều", "leu", "tent")) {
                query.setCategory("Tents");
            } else if (containsAny(raw, "mũ", "mu", "nón", "non", "hat", "cap")) {
                query.setCategory("Hats");
            } else if (containsAny(raw, "bếp", "bep", "stove", "grill")) {
                query.setCategory("Cooking Equipment");
            } else if (containsAny(raw, "quần", "quan", "pants")) {
                query.setCategory("Pants");
            } else if (containsAny(raw, "áo", "ao", "shirt", "jacket")) {
                query.setCategory("Shirts");
            }
        }

        boolean isGeneralCategoryPrice = query.getCategory() != null
                && (query.getProductKeyword() == null || query.getProductKeyword().trim().isEmpty());

        if (isGeneralCategoryPrice) {
            query.setDescriptionKeyword(null);
            query.setColor(null);
            query.setSize(null);
            query.setInStockOnly(false);

            if (query.getIntent() == null || ChatIntent.OUT_OF_SCOPE.equals(query.getIntent())) {
                query.setIntent(ChatIntent.SEARCH_PRODUCT);
            }
        }
    }

    private void normalizeGlobalPriceExtremes(String userMessage, ChatQuery query) {
        if (userMessage == null || query == null) {
            return;
        }

        String raw = userMessage.toLowerCase().trim();

        boolean cheapest = containsAny(raw,
                "rẻ nhất shop", "re nhat shop",
                "đồ rẻ nhất", "do re nhat",
                "món rẻ nhất", "mon re nhat",
                "sản phẩm rẻ nhất", "san pham re nhat");

        boolean mostExpensive = containsAny(raw,
                "mắc nhất shop", "mac nhat shop",
                "đắt nhất shop", "dat nhat shop",
                "món đắt nhất", "mon dat nhat",
                "sản phẩm mắc nhất", "san pham mac nhat",
                "sản phẩm đắt nhất", "san pham dat nhat");

        if (cheapest) {
            query.setIntent(ChatIntent.FIND_CHEAPEST_PRODUCT);
            query.setCategory(null);
            query.setProductKeyword(null);
            query.setDescriptionKeyword(null);
            query.setColor(null);
            query.setSize(null);
            query.setMinPrice(null);
            query.setMaxPrice(null);
            query.setInStockOnly(false);
            return;
        }

        if (mostExpensive) {
            query.setIntent(ChatIntent.FIND_MOST_EXPENSIVE_PRODUCT);
            query.setCategory(null);
            query.setProductKeyword(null);
            query.setDescriptionKeyword(null);
            query.setColor(null);
            query.setSize(null);
            query.setMinPrice(null);
            query.setMaxPrice(null);
            query.setInStockOnly(false);
        }
    }

    private void normalizeCategoryPriceExtremes(String userMessage, ChatQuery query) {
        if (userMessage == null || query == null) {
            return;
        }

        String raw = userMessage.toLowerCase().trim();
        boolean hasCategory = query.getCategory() != null && !query.getCategory().trim().isEmpty();

        boolean cheapestByCategory = hasCategory && containsAny(raw,
                "rẻ nhất", "re nhat",
                "thấp nhất", "thap nhat",
                "giá thấp nhất", "gia thap nhat");

        boolean mostExpensiveByCategory = hasCategory && containsAny(raw,
                "mắc nhất", "mac nhat",
                "đắt nhất", "dat nhat",
                "cao nhất", "gia cao nhat",
                "giá cao nhất");

        if (cheapestByCategory) {
            query.setIntent(ChatIntent.FIND_CHEAPEST_BY_CATEGORY);
            query.setProductKeyword(null);
            query.setDescriptionKeyword(null);
            query.setColor(null);
            query.setSize(null);
            query.setMinPrice(null);
            query.setMaxPrice(null);
            query.setInStockOnly(false);
            return;
        }

        if (mostExpensiveByCategory) {
            query.setIntent(ChatIntent.FIND_MOST_EXPENSIVE_BY_CATEGORY);
            query.setProductKeyword(null);
            query.setDescriptionKeyword(null);
            query.setColor(null);
            query.setSize(null);
            query.setMinPrice(null);
            query.setMaxPrice(null);
            query.setInStockOnly(false);
        }
    }

    private void normalizeBestSellingIntent(String userMessage, ChatQuery query) {
        if (userMessage == null || query == null) {
            return;
        }

        String raw = userMessage.toLowerCase().trim();

        boolean bestSelling = containsAny(raw,
                "bán chạy nhất", "ban chay nhat",
                "mua nhiều nhất", "mua nhieu nhat",
                "best seller", "best-selling",
                "phổ biến nhất", "pho bien nhat");

        if (bestSelling) {
            query.setIntent(ChatIntent.FIND_BEST_SELLING_PRODUCT);
            query.setCategory(null);
            query.setProductKeyword(null);
            query.setDescriptionKeyword(null);
            query.setColor(null);
            query.setSize(null);
            query.setMinPrice(null);
            query.setMaxPrice(null);
            query.setInStockOnly(false);
        }
    }

    private void normalizeSizeRecommendationIntent(String userMessage, ChatQuery query) {
        if (userMessage == null || query == null) {
            return;
        }

        String raw = userMessage.toLowerCase().trim();

        Integer height = extractHeightCm(raw);
        Integer weight = extractWeightKg(raw);

        if (height != null) {
            query.setHeightCm(height);
        }
        if (weight != null) {
            query.setWeightKg(weight);
        }

        boolean askingSizeRecommendation = containsAny(raw,
                "size gì", "size gi",
                "mặc size gì", "mac size gi",
                "chọn size gì", "chon size gi",
                "size nào", "size nao",
                "tư vấn size", "tu van size",
                "size phù hợp", "size phu hop");

        boolean hasBodyInfo = query.getHeightCm() != null && query.getWeightKg() != null;

        if (hasBodyInfo && (askingSizeRecommendation || query.getCategory() != null)) {
            query.setIntent(ChatIntent.SIZE_RECOMMENDATION);
            query.setProductKeyword(null);
            query.setColor(null);
            query.setSize(null);
            query.setMinPrice(null);
            query.setMaxPrice(null);
            query.setDescriptionKeyword(null);
            query.setInStockOnly(false);
        }
    }

    private Integer extractHeightCm(String raw) {
        if (raw == null) {
            return null;
        }

        Matcher m1 = Pattern.compile("(\\d{3})\\s*cm").matcher(raw);
        if (m1.find()) {
            return safeParseInt(m1.group(1));
        }

        Matcher m2 = Pattern.compile("cao\\s*(\\d{3})").matcher(raw);
        if (m2.find()) {
            return safeParseInt(m2.group(1));
        }

        return null;
    }

    private Integer extractWeightKg(String raw) {
        if (raw == null) {
            return null;
        }

        Matcher m1 = Pattern.compile("(\\d{2,3})\\s*kg").matcher(raw);
        if (m1.find()) {
            return safeParseInt(m1.group(1));
        }

        Matcher m2 = Pattern.compile("nặng\\s*(\\d{2,3})").matcher(raw);
        if (m2.find()) {
            return safeParseInt(m2.group(1));
        }

        return null;
    }

    private Integer safeParseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isGeneralPriceQuestion(String raw) {
        if (raw == null) {
            return false;
        }

        return containsAny(raw,
                "giá", "gia",
                "bao nhiêu", "bao nhieu",
                "bao nhiêu tiền", "bao nhieu tien",
                "giá sao", "gia sao",
                "tầm giá", "tam gia",
                "giá cao", "gia cao",
                "giá mắc", "gia mac",
                "đắt không", "dat khong",
                "rẻ không", "re khong");
    }

    private boolean containsAny(String raw, String... keywords) {
        if (raw == null) {
            return false;
        }
        for (String k : keywords) {
            if (raw.contains(k)) {
                return true;
            }
        }
        return false;
    }

    private ChatQuery mapJsonToChatQuery(String jsonText) {
        ChatQuery query = new ChatQuery();
        JsonObject obj = gson.fromJson(jsonText.trim(), JsonObject.class);

        String aiIntent = getString(obj, "intent");
        query.setIntent(mapIntent(aiIntent));
        query.setProductKeyword(getString(obj, "productKeyword"));
        query.setCategory(getString(obj, "category"));
        query.setColor(getString(obj, "color"));
        query.setSize(getString(obj, "size"));
        query.setDescriptionKeyword(getString(obj, "descriptionKeyword"));
        query.setInStockOnly(getBoolean(obj, "inStockOnly"));
        query.setMinPrice(getDouble(obj, "minPrice"));
        query.setMaxPrice(getDouble(obj, "maxPrice"));
        query.setHeightCm(getInteger(obj, "heightCm"));
        query.setWeightKg(getInteger(obj, "weightKg"));

        return query;
    }

    private String mapIntent(String aiIntent) {
        if (aiIntent == null) {
            return ChatIntent.OUT_OF_SCOPE;
        }

        switch (aiIntent) {
            case "SMALL_TALK":
                return ChatIntent.SMALL_TALK;
            case "CHECK_STOCK":
                return ChatIntent.CHECK_STOCK;
            case "SEARCH_PRODUCT":
                return ChatIntent.SEARCH_PRODUCT;
            case "UNSUPPORTED_PRODUCT":
                return ChatIntent.UNSUPPORTED_PRODUCT;
            case "FIND_CHEAPEST_PRODUCT":
                return ChatIntent.FIND_CHEAPEST_PRODUCT;
            case "FIND_MOST_EXPENSIVE_PRODUCT":
                return ChatIntent.FIND_MOST_EXPENSIVE_PRODUCT;
            case "FIND_CHEAPEST_BY_CATEGORY":
                return ChatIntent.FIND_CHEAPEST_BY_CATEGORY;
            case "FIND_MOST_EXPENSIVE_BY_CATEGORY":
                return ChatIntent.FIND_MOST_EXPENSIVE_BY_CATEGORY;
            case "FIND_BEST_SELLING_PRODUCT":
                return ChatIntent.FIND_BEST_SELLING_PRODUCT;
            case "SIZE_RECOMMENDATION":
                return ChatIntent.SIZE_RECOMMENDATION;
            default:
                return ChatIntent.OUT_OF_SCOPE;
        }
    }

    private String getString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return null;
        }
        String value = obj.get(key).getAsString();
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private Double getDouble(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return null;
        }
        try {
            return obj.get(key).getAsDouble();
        } catch (Exception e) {
            return null;
        }
    }

    private Integer getInteger(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return null;
        }
        try {
            return obj.get(key).getAsInt();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean getBoolean(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return false;
        }
        try {
            return obj.get(key).getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }
}