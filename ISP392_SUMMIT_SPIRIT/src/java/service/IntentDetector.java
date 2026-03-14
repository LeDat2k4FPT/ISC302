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
            return query;
        }

        String rawMessage = message.trim();
        String normalized = normalize(rawMessage);

        query.setRawMessage(rawMessage);

        String productKeyword = detectProductKeyword(rawMessage);
        query.setProductKeyword(productKeyword);

        boolean inStockOnly = containsAny(normalized,
                "còn hàng", "còn không", "available", "in stock", "còn size", "còn màu");
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
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String detectCategory(String text) {
        if (containsAny(text, "quần", "pants", "short", "shorts")) {
            return "Pants";
        }
        if (containsAny(text, "áo", "shirt", "shirts", "jacket", "hoodie")) {
            return "Shirts";
        }
        if (containsAny(text, "balo", "ba lô", "backpack", "backpacks")) {
            return "Backpacks";
        }
        if (containsAny(text, "dao", "knife", "tool", "tools", "dụng cụ cắm trại", "camping tools")) {
            return "Camping Tools";
        }
        if (containsAny(text, "lều", "tent", "tents")) {
            return "Tents";
        }
        if (containsAny(text, "nón", "mũ", "hat", "hats", "cap")) {
            return "Hats";
        }
        if (containsAny(text, "bếp", "stove", "grill", "cooking", "nấu ăn")) {
            return "Cooking Equipment";
        }
        return null;
    }

    private String detectColor(String text) {
        if (containsAny(text, "đen", "black")) {
            return "Black";
        }
        if (containsAny(text, "đỏ", "red")) {
            return "Red";
        }
        if (containsAny(text, "xanh", "xanh dương", "blue")) {
            return "Blue";
        }
        return null;
    }

    private String detectSize(String text) {
        Pattern pattern = Pattern.compile("\\b(s|m|l|small|big)\\b", Pattern.CASE_INSENSITIVE);
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
            if ("small".equalsIgnoreCase(value)) {
                return "small";
            }
            if ("big".equalsIgnoreCase(value)) {
                return "big";
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

        Pattern maxPattern = Pattern.compile("(dưới|rẻ hơn|không quá)\\s*(\\d+(?:\\.\\d+)?)\\s*(k|triệu)?");
        Matcher maxMatcher = maxPattern.matcher(text);
        if (maxMatcher.find()) {
            double max = convertPrice(maxMatcher.group(2), maxMatcher.group(3));
            query.setMaxPrice(max);
            return;
        }

        Pattern minPattern = Pattern.compile("(trên|hơn|từ)\\s*(\\d+(?:\\.\\d+)?)\\s*(k|triệu)?");
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
        if (containsAny(text, "nhanh khô", "quick dry", "quick-dry")) {
            return "quick-dry";
        }
        if (containsAny(text, "chống nắng", "chống uv", "uv", "sun protection")) {
            return "UV";
        }
        if (containsAny(text, "chống nước", "waterproof")) {
            return "waterproof";
        }
        if (containsAny(text, "nhẹ", "siêu nhẹ", "lightweight", "ultra-light")) {
            return "lightweight";
        }
        if (containsAny(text, "trekking", "leo núi", "outdoor", "hiking", "climbing")) {
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
        return null;
    }

    private String detectIntent(ChatQuery query) {
        if (query.isInStockOnly()) {
            return ChatIntent.CHECK_STOCK;
        }
        if (query.getProductKeyword() != null && !query.getProductKeyword().isEmpty()) {
            return ChatIntent.SEARCH_BY_NAME;
        }
        if (query.getMinPrice() != null || query.getMaxPrice() != null) {
            return ChatIntent.SEARCH_BY_PRICE;
        }
        if (query.getColor() != null) {
            return ChatIntent.SEARCH_BY_COLOR;
        }
        if (query.getSize() != null) {
            return ChatIntent.SEARCH_BY_SIZE;
        }
        if (query.getCategory() != null) {
            return ChatIntent.SEARCH_BY_CATEGORY;
        }
        if (query.getDescriptionKeyword() != null) {
            return ChatIntent.RECOMMEND_BY_DESCRIPTION;
        }
        return ChatIntent.OUT_OF_SCOPE;
    }
}