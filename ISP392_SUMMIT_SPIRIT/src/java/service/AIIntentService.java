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

public class AIIntentService {

    private static final String API_KEY = "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";
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private final Gson gson = new Gson();

    public ChatQuery parseUserMessage(String userMessage) throws IOException, InterruptedException {
        String prompt = buildPrompt(userMessage);

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
                + "Return JSON only. No explanation. "
                + "Supported high-level intents are: "
                + "SMALL_TALK, SEARCH_PRODUCT, CHECK_STOCK, UNSUPPORTED_PRODUCT, OUT_OF_SCOPE. "
                + "Use this exact schema: "
                + "{"
                + "\"intent\":\"SMALL_TALK|SEARCH_PRODUCT|CHECK_STOCK|UNSUPPORTED_PRODUCT|OUT_OF_SCOPE\","
                + "\"productKeyword\":string|null,"
                + "\"category\":\"Pants|Shirts|Backpacks|Camping Tools|Tents|Hats|Cooking Equipment\"|null,"
                + "\"color\":\"Black|Red|Blue\"|null,"
                + "\"size\":\"S|M|L|small|big\"|null,"
                + "\"minPrice\":number|null,"
                + "\"maxPrice\":number|null,"
                + "\"descriptionKeyword\":string|null,"
                + "\"inStockOnly\":boolean"
                + "}. "
                + "Rules: "
                + "1. If user is greeting, thanking, opening a conversation, or chatting casually without asking for product lookup, use SMALL_TALK. "
                + "2. If user wants product search, recommendation, comparison, or asks for products by category/color/size/price/feature, use SEARCH_PRODUCT. "
                + "3. If user asks availability, remaining stock, whether an item still exists, or whether a size/color is available, use CHECK_STOCK and set inStockOnly=true. "
                + "4. If user asks for a product type not sold by the store, use UNSUPPORTED_PRODUCT. Do not map unsupported products to existing categories. "
                + "5. If message is unrelated to store/products, use OUT_OF_SCOPE. "
                + "6. If a specific product/model name is mentioned, fill productKeyword. "
                + "7. Normalize Vietnamese meaning into supported database values only. "
                + "8. Do not invent unsupported colors, categories, or sizes. "
                + "9. If the user asks by usage scenario or feature, map to descriptionKeyword using the normalized tags below. "
                + "Normalized feature tags: "
                + "'chong nang', 'chong uv', 'uv', 'uv protection', 'uv resistant', 'sun protection', 'sun-proof', 'omni-shade' => 'UV'; "
                + "'nhanh kho', 'quick dry', 'quick-dry', 'dry quickly', 'omni-wick' => 'quick-dry'; "
                + "'chong nuoc', 'waterproof', 'water resistant', 'rain proof' => 'waterproof'; "
                + "'trekking', 'leo nui', 'hiking', 'mountaineering', 'climbing', 'outdoor', 'picnic', 'camping' => 'trekking'; "
                + "'nhe', 'sieu nhe', 'lightweight', 'ultralight', 'minimalist' => 'lightweight'. "
                + "Words like jacket still belong to Shirts in this store. "
                + "Words like cap belong to Hats. "
                + "Words like tent belong to Tents. "
                + "Words like backpack belong to Backpacks. "
                + "Words like stove or grill belong to Cooking Equipment. "
                + "Return valid JSON only."
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

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Groq API error: " + response.statusCode() + " - " + response.body());
        }

        JsonObject root = gson.fromJson(response.body(), JsonObject.class);
        String content = root.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();

        System.out.println("AI JSON = " + content);

        ChatQuery query = mapJsonToChatQuery(content);
        applyKeywordFallback(userMessage, query);
        return query;
    }

    private String buildPrompt(String userMessage) {
    return "User message: " + userMessage + "\n"
            + "This store sells only: Pants, Shirts, Backpacks, Camping Tools, Tents, Hats, Cooking Equipment.\n"
            + "Interpret Vietnamese naturally.\n"
            + "Use only these normalized values:\n"
            + "Category: Pants, Shirts, Backpacks, Camping Tools, Tents, Hats, Cooking Equipment\n"
            + "Color: Black, Red, Blue\n"
            + "Size: S, M, L, small, big\n"
            + "Feature tags: UV, quick-dry, waterproof, trekking, lightweight\n"
            + "Important color rule:\n"
            + "- Only map supported colors: Black, Red, Blue\n"
            + "- Do not map unsupported colors to the nearest supported color\n"
            + "- For example, xanh la / xanh lá / green is unsupported, so color must be null\n"
            + "- Only map xanh duong / xanh dương / blue to Blue\n"
            + "Category mapping:\n"
            + "- quan => Pants\n"
            + "- ao => Shirts\n"
            + "- ao khoac => Shirts\n"
            + "- jacket / ao jacket => Shirts\n"
            + "- balo / ba lo / backpack => Backpacks\n"
            + "- dao / dung cu / do camping => Camping Tools\n"
            + "- leu / trai / tent => Tents\n"
            + "- mu / non / hat / cap => Hats\n"
            + "- bep / stove / grill => Cooking Equipment\n"
            + "Color mapping:\n"
            + "- den / black => Black\n"
            + "- do / red => Red\n"
            + "- xanh duong / xanh dương / blue => Blue\n"
            + "- xanh la / xanh lá / green => unsupported, set color to null\n"
            + "- mau khong ho tro nhu green, pink, yellow, white => set color to null\n"
            + "Size mapping:\n"
            + "- s => S\n"
            + "- m => M\n"
            + "- l => L\n"
            + "- small => small\n"
            + "- big => big\n"
            + "Feature mapping:\n"
            + "- chong nang / uv / chong uv / sun protection => UV\n"
            + "- nhanh kho / quick dry / quick-dry => quick-dry\n"
            + "- chong nuoc / waterproof / water resistant => waterproof\n"
            + "- trekking / hiking / leo nui / outdoor / camping => trekking\n"
            + "- sieu nhe / lightweight / ultralight => lightweight\n"
            + "Examples:\n"
            + "1) hi => "
            + "{\"intent\":\"SMALL_TALK\",\"productKeyword\":null,\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false}\n"
            + "2) ao den => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Shirts\",\"color\":\"Black\",\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false}\n"
            + "3) quan duoi 500k => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Pants\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":500000,\"descriptionKeyword\":null,\"inStockOnly\":false}\n"
            + "4) balo sieu nhe => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Backpacks\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":\"lightweight\",\"inStockOnly\":false}\n"
            + "5) leu trekking => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Tents\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":\"trekking\",\"inStockOnly\":false}\n"
            + "6) mu chong nang => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Hats\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":\"UV\",\"inStockOnly\":false}\n"
            + "7) ao size m => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Shirts\",\"color\":null,\"size\":\"M\",\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false}\n"
            + "8) bep size big => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Cooking Equipment\",\"color\":null,\"size\":\"big\",\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false}\n"
            + "9) ktom k116 => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":\"Ktom K116\",\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false}\n"
            + "10) ktom k116 con hang khong => "
            + "{\"intent\":\"CHECK_STOCK\",\"productKeyword\":\"Ktom K116\",\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":true}\n"
            + "11) ktom k116 con size m khong => "
            + "{\"intent\":\"CHECK_STOCK\",\"productKeyword\":\"Ktom K116\",\"category\":null,\"color\":null,\"size\":\"M\",\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":true}\n"
            + "12) ao den con size m khong => "
            + "{\"intent\":\"CHECK_STOCK\",\"productKeyword\":null,\"category\":\"Shirts\",\"color\":\"Black\",\"size\":\"M\",\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":true}\n"
            + "13) ao jacket => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":\"jacket\",\"category\":\"Shirts\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false}\n"
            + "14) co may loai jacket => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":\"jacket\",\"category\":\"Shirts\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false}\n"
            + "15) cap chong nang => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Hats\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":\"UV\",\"inStockOnly\":false}\n"
            + "16) tent trekking => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Tents\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":\"trekking\",\"inStockOnly\":false}\n"
            + "17) backpack lightweight => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Backpacks\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":\"lightweight\",\"inStockOnly\":false}\n"
            + "18) grill outdoor => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Cooking Equipment\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":\"trekking\",\"inStockOnly\":false}\n"
            + "19) shop co ban dong ho khong => "
            + "{\"intent\":\"UNSUPPORTED_PRODUCT\",\"productKeyword\":null,\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false}\n"
            + "20) thoi tiet hom nay the nao => "
            + "{\"intent\":\"OUT_OF_SCOPE\",\"productKeyword\":null,\"category\":null,\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":false}\n"
            + "21) co ao mau xanh la khong => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Shirts\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":true}\n"
            + "22) co ao mau xanh duong khong => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Shirts\",\"color\":\"Blue\",\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":true}\n"
            + "23) co ao mau xanh khong => "
            + "{\"intent\":\"SEARCH_PRODUCT\",\"productKeyword\":null,\"category\":\"Shirts\",\"color\":null,\"size\":null,\"minPrice\":null,\"maxPrice\":null,\"descriptionKeyword\":null,\"inStockOnly\":true}\n"
            + "Return JSON only.";
}

    private void applyKeywordFallback(String userMessage, ChatQuery query) {
        if (userMessage == null || query == null) {
            return;
        }

        String raw = userMessage.toLowerCase().trim();

        // Category fallback
        if (query.getCategory() == null) {
            if (containsAny(raw, "jacket", "áo jacket", "ao jacket")) {
                query.setCategory("Shirts");
            } else if (containsAny(raw, "backpack")) {
                query.setCategory("Backpacks");
            } else if (containsAny(raw, "tent")) {
                query.setCategory("Tents");
            } else if (containsAny(raw, "cap")) {
                query.setCategory("Hats");
            } else if (containsAny(raw, "stove", "grill")) {
                query.setCategory("Cooking Equipment");
            }
        }

        // Product keyword fallback for name-like queries
        if (query.getProductKeyword() == null || query.getProductKeyword().trim().isEmpty()) {
            if (containsAny(raw, "jacket")) {
                query.setProductKeyword("jacket");
            }
        }

        // Feature fallback
        if (query.getDescriptionKeyword() == null) {
            if (containsAny(raw, "chống nắng", "chong nang", "uv", "sun protection", "omni-shade")) {
                query.setDescriptionKeyword("UV");
            } else if (containsAny(raw, "nhanh khô", "nhanh kho", "quick dry", "quick-dry", "omni-wick")) {
                query.setDescriptionKeyword("quick-dry");
            } else if (containsAny(raw, "chống nước", "chong nuoc", "waterproof", "water resistant")) {
                query.setDescriptionKeyword("waterproof");
            } else if (containsAny(raw, "trekking", "leo núi", "leo nui", "hiking", "outdoor", "camping", "mountaineering")) {
                query.setDescriptionKeyword("trekking");
            } else if (containsAny(raw, "siêu nhẹ", "sieu nhe", "lightweight", "ultralight")) {
                query.setDescriptionKeyword("lightweight");
            }
        }

        // Intent fallback
        if ((query.getIntent() == null || query.getIntent().equals(ChatIntent.OUT_OF_SCOPE))
                && (query.getCategory() != null || query.getProductKeyword() != null)) {
            query.setIntent("SEARCH_PRODUCT");
        }

        // Stock intent fallback
        if (containsAny(raw, "còn hàng", "con hang", "còn size", "con size", "còn màu", "con mau")) {
            query.setIntent(ChatIntent.CHECK_STOCK);
            query.setInStockOnly(true);
        }
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

        return query;
    }

    private String mapIntent(String aiIntent) {
        if (aiIntent == null) {
            return ChatIntent.OUT_OF_SCOPE;
        }

        switch (aiIntent) {
            case "SMALL_TALK":
                return "SMALL_TALK";
            case "CHECK_STOCK":
                return ChatIntent.CHECK_STOCK;
            case "SEARCH_PRODUCT":
                return "SEARCH_PRODUCT";
            case "UNSUPPORTED_PRODUCT":
                return "UNSUPPORTED_PRODUCT";
            default:
                return ChatIntent.OUT_OF_SCOPE;
        }
    }

    private String getString(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return null;
        }
        String value = obj.get(key).getAsString();
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private Double getDouble(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return null;
        }
        try {
            return obj.get(key).getAsDouble();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean getBoolean(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) {
            return false;
        }
        try {
            return obj.get(key).getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }
}