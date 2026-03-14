package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.ChatProductResult;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AIResponseService {

    private static final String API_KEY = "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";
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private final Gson gson = new Gson();

    public String generateReply(String userMessage, List<ChatProductResult> products)
            throws IOException, InterruptedException {

        JsonObject payload = new JsonObject();
        payload.addProperty("model", MODEL);
        payload.addProperty("temperature", 0.2);

        JsonArray messages = new JsonArray();

        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content",
                "You are a helpful shopping assistant for an outdoor fashion and camping store. "
                + "You must answer ONLY from the database results provided by the developer. "
                + "Do not invent products, prices, stock, colors, sizes, descriptions, or features that are not present in the database data. "
                + "If the database results are empty, clearly say no suitable product was found. "
                + "Answer in Vietnamese. "
                + "Keep the answer concise, natural, and helpful. "
                + "Prefer 2 to 4 short sentences. "
                + "Mention only the most relevant 1 to 3 products unless the user explicitly asks for more. "
                + "When possible, mention product name and price. "
                + "Do not repeat the full product list in the text because the UI already shows the products below. "
                + "Do not mention any information that is missing from the database results.");

        messages.add(systemMsg);

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content",
                "Câu hỏi người dùng: " + userMessage + "\n\n"
                + "Dữ liệu sản phẩm từ database:\n"
                + gson.toJson(products) + "\n\n"
                + "Hãy trả lời tự nhiên bằng tiếng Việt, ngắn gọn, chỉ dựa trên dữ liệu database ở trên.");

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
        return root.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString()
                .trim();
    }
}