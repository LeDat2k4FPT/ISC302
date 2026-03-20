package service;

import dto.ChatQuery;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntentDetector {

    public ChatQuery parseUserMessage(String message) {
        ChatQuery query = new ChatQuery();

        if (message == null) {
            query.setRawMessage("");
            query.setIntent(ChatIntent.OUT_OF_SCOPE);
            query.setInStockOnly(false);
            return query;
        }

        String rawMessage = message.trim();
        String normalized = normalize(rawMessage);

        query.setRawMessage(rawMessage);

        if (isSmallTalk(normalized)) {
            query.setIntent(ChatIntent.SMALL_TALK);
            query.setInStockOnly(false);
            return query;
        }

        String productKeyword = detectProductKeyword(rawMessage);
        query.setProductKeyword(productKeyword);

        boolean inStockOnly = containsAny(normalized,
                "còn hàng", "con hang",
                "còn không", "con khong",
                "available", "in stock",
                "còn size", "con size",
                "còn màu", "con mau");
        query.setInStockOnly(inStockOnly);

        String category = detectCategory(normalized);
        query.setCategory(category);

        String color = detectColor(normalized);
        query.setColor(color);

        String size = detectSize(normalized);
        query.setSize(size);

        parsePrice(normalized, query);

        String descriptionKeyword = detectDescriptionKeyword(normalized);
        query.setDescriptionKeyword(descriptionKeyword);

        query.setIntent(detectIntent(query));

        return query;
    }

    private String normalize(String input) {
        String text = input.toLowerCase().trim();
        text = text.replaceAll("[?.,!]", " ");
        text = text.replaceAll("\\s+", " ");
        text = text.replaceAll("(\\d)\\s+k", "$1k");
        return text;
    }

    private boolean containsAny(String text, String... keywords) {
        if (text == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSmallTalk(String text) {
        return containsAny(text,
                "hi", "hello", "helo", "xin chào", "xin chao", "chào", "chao",
                "shop ơi", "shop oi", "ad ơi", "ad oi",
                "cảm ơn", "cam on", "thanks", "thank you");
    }

    private String detectCategory(String text) {
        if (containsAny(text, "quần", "quan", "pants", "short", "shorts")) {
            return "Pants";
        }
        if (containsAny(text, "áo", "ao", "shirt", "shirts", "jacket", "hoodie")) {
            return "Shirts";
        }
        if (containsAny(text, "balo", "ba lô", "ba lo", "backpack", "backpacks")) {
            return "Backpacks";
        }
        if (containsAny(text, "dao", "knife", "tool", "tools", "dụng cụ cắm trại", "dung cu cam trai", "camping tools")) {
            return "Camping Tools";
        }
        if (containsAny(text, "lều", "leu", "tent", "tents")) {
            return "Tents";
        }
        if (containsAny(text, "nón", "non", "mũ", "mu", "hat", "hats", "cap")) {
            return "Hats";
        }
        if (containsAny(text, "bếp", "bep", "stove", "grill", "cooking", "nấu ăn", "nau an")) {
            return "Cooking Equipment";
        }
        return null;
    }

    private String detectColor(String text) {
        if (containsAny(text, "đen", "den", "black")) {
            return "Black";
        }
        if (containsAny(text, "đỏ", "do", "red")) {
            return "Red";
        }
        if (containsAny(text, "xanh dương", "xanh duong", "blue")) {
            return "Blue";
        }
        return null;
    }

    private String detectSize(String text) {
    if (containsAny(text, "size lớn", "loại lớn", "lớn", "lon", "big")) {
        return "big";
    }

    if (containsAny(text, "size nhỏ", "loại nhỏ", "nhỏ", "nho", "small")) {
        return "small";
    }

    Pattern pattern = Pattern.compile("\\b(s|m|l)\\b", Pattern.CASE_INSENSITIVE);
    Matcher matcher = pattern.matcher(text);

    if (matcher.find()) {
        String value = matcher.group(1);
        if ("s".equalsIgnoreCase(value)) {
            return "S";
        }
        if ("m".equalsIgnoreCase(value)) {
            return "M";
        }
        if ("l".equalsIgnoreCase(value)) {
            return "L";
        }
    }
    return null;
}

    private void parsePrice(String text, ChatQuery query) {
        Pattern rangePattern = Pattern.compile("(?:từ\\s*)?(\\d+(?:\\.\\d+)?)\\s*(k|triệu)?\\s*(?:đến|-|to)\\s*(\\d+(?:\\.\\d+)?)\\s*(k|triệu)?");
        Matcher rangeMatcher = rangePattern.matcher(text);
        if (rangeMatcher.find()) {
            double min = convertPrice(rangeMatcher.group(1), rangeMatcher.group(2));
            double max = convertPrice(rangeMatcher.group(3), rangeMatcher.group(4));
            query.setMinPrice(min);
            query.setMaxPrice(max);
            return;
        }

        Pattern maxPattern = Pattern.compile("(dưới|duoi|rẻ hơn|re hon|không quá|khong qua)\\s*(\\d+(?:\\.\\d+)?)\\s*(k|triệu)?");
        Matcher maxMatcher = maxPattern.matcher(text);
        if (maxMatcher.find()) {
            double max = convertPrice(maxMatcher.group(2), maxMatcher.group(3));
            query.setMaxPrice(max);
            return;
        }

        Pattern minPattern = Pattern.compile("(trên|tren|hơn|hon|từ|tu)\\s*(\\d+(?:\\.\\d+)?)\\s*(k|triệu)?");
        Matcher minMatcher = minPattern.matcher(text);
        if (minMatcher.find()) {
            double min = convertPrice(minMatcher.group(2), minMatcher.group(3));
            query.setMinPrice(min);
        }
    }

    private double convertPrice(String numberText, String unit) {
        double value = Double.parseDouble(numberText);

        if (unit == null) {
            return value;
        }

        if ("k".equalsIgnoreCase(unit)) {
            return value * 1000;
        }

        if ("triệu".equalsIgnoreCase(unit)) {
            return value * 1000000;
        }

        return value;
    }

    private String detectDescriptionKeyword(String text) {
        if (containsAny(text, "nhanh khô", "nhanh kho", "quick dry", "quick-dry")) {
            return "quick-dry";
        }
        if (containsAny(text, "chống nắng", "chong nang", "chống uv", "chong uv", "uv", "sun protection")) {
            return "UV";
        }
        if (containsAny(text, "chống nước", "chong nuoc", "waterproof")) {
            return "waterproof";
        }
        if (containsAny(text, "nhẹ", "nhe", "siêu nhẹ", "sieu nhe", "lightweight", "ultra-light", "ultralight")) {
            return "lightweight";
        }
        if (containsAny(text, "trekking", "leo núi", "leo nui", "outdoor", "hiking", "climbing", "camping")) {
            return "trekking";
        }
        return null;
    }

    private String detectProductKeyword(String rawMessage) {
        Pattern codePattern = Pattern.compile("\\b([A-Za-z]{1,10}[\\- ]?[A-Za-z0-9]{1,10})\\b");
        Matcher matcher = codePattern.matcher(rawMessage);

        while (matcher.find()) {
            String token = matcher.group(1).trim();
            if (token.matches(".*\\d.*")) {
                return token;
            }
        }

        if (rawMessage.toLowerCase().contains("jacket")) {
            return "jacket";
        }

        return null;
    }

    private String detectIntent(ChatQuery query) {
        if (query.isInStockOnly()) {
            return ChatIntent.CHECK_STOCK;
        }

        if (query.getProductKeyword() != null
                || query.getCategory() != null
                || query.getColor() != null
                || query.getSize() != null
                || query.getMinPrice() != null
                || query.getMaxPrice() != null
                || query.getDescriptionKeyword() != null) {
            return ChatIntent.SEARCH_PRODUCT;
        }

        return ChatIntent.OUT_OF_SCOPE;
    }
}