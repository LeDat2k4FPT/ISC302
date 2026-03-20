package service;

import com.google.gson.Gson;
import dto.ChatRequest;
import dto.ChatResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ChatServlet", urlPatterns = {"/chat-endpoint"})
public class ChatServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final ChatbotService chatbotService = new ChatbotService();

    private static final String[][] SUGGESTION_GROUPS = {
        {
            "áo đen",
            "balo chống nước",
            "áo size M còn không?",
            "sản phẩm dưới 500k"
        },
        {
            "gợi ý lều đi camping",
            "mũ chống nắng",
            "balo màu xanh còn hàng không?",
            "áo giá bao nhiêu?"
        },
        {
            "quần size L",
            "bếp outdoor",
            "có sản phẩm nào chống nước không?",
            "Ktom K116 còn hàng không?"
        },
        {
            "áo chống UV",
            "balo nhẹ để đi trekking",
            "lều trekking",
            "có mẫu nào dưới 1 triệu không?"
        }
    };

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        ChatResponse chatResponse = new ChatResponse();

        try {
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("LOGIN_USER") == null) {
                chatResponse.setSuccess(false);
                chatResponse.setMessage("Bạn cần đăng nhập để sử dụng chatbot.");
                chatResponse.setProducts(new ArrayList<>());
                writeJson(response, chatResponse);
                return;
            }

            StringBuilder jsonBuilder = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            ChatRequest chatRequest = gson.fromJson(jsonBuilder.toString(), ChatRequest.class);

            if (chatRequest == null || chatRequest.getMessage() == null) {
                chatResponse.setSuccess(false);
                chatResponse.setMessage("Nội dung câu hỏi không hợp lệ.");
                chatResponse.setProducts(new ArrayList<>());
                writeJson(response, chatResponse);
                return;
            }

            String userMessage = chatRequest.getMessage().trim();

            if (userMessage.isEmpty()) {
                chatResponse.setSuccess(false);
                chatResponse.setMessage("Nội dung câu hỏi không hợp lệ.");
                chatResponse.setProducts(new ArrayList<>());
                writeJson(response, chatResponse);
                return;
            }

            if (isGreetingMessage(userMessage)) {
                chatResponse.setSuccess(true);
                chatResponse.setMessage(getWelcomeMessage(session));
                chatResponse.setProducts(new ArrayList<>());
                writeJson(response, chatResponse);
                return;
            }

            chatResponse = chatbotService.handleMessage(userMessage, session);

        } catch (Exception e) {
            e.printStackTrace();
            chatResponse.setSuccess(false);
            chatResponse.setMessage("Hệ thống chatbot đang gặp lỗi. Vui lòng thử lại sau.");
            chatResponse.setProducts(new ArrayList<>());
        }

        writeJson(response, chatResponse);
    }

    private void writeJson(HttpServletResponse response, ChatResponse chatResponse) throws IOException {
        response.getWriter().write(gson.toJson(chatResponse));
    }

    private String getWelcomeMessage(HttpSession session) {
        String username = extractUsername(session);
        String[] suggestions = getRandomSuggestionGroup();

        StringBuilder sb = new StringBuilder();
        sb.append("Chào mừng ").append(username).append(" đến với SUMMIT SPIRIT! ");
        sb.append("Tôi là trợ lý AI, sẵn sàng giúp bạn tìm kiếm sản phẩm, kiểm tra giá, size, màu sắc và tình trạng hàng hoá.\n\n");
        sb.append("Bạn có thể thử hỏi:\n");

        for (String suggestion : suggestions) {
            sb.append("- ").append(suggestion).append("\n");
        }

        return sb.toString().trim();
    }

    private String[] getRandomSuggestionGroup() {
        int index = ThreadLocalRandom.current().nextInt(SUGGESTION_GROUPS.length);
        return SUGGESTION_GROUPS[index];
    }

    private String extractUsername(HttpSession session) {
        if (session == null) {
            return "bạn";
        }

        Object loginUser = session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            return "bạn";
        }

        if (loginUser instanceof String) {
            String name = ((String) loginUser).trim();
            return name.isEmpty() ? "bạn" : name;
        }

        String[] methodNames = {"getFullName", "getName", "getUsername"};

        for (String methodName : methodNames) {
            try {
                Method method = loginUser.getClass().getMethod(methodName);
                Object value = method.invoke(loginUser);

                if (value != null) {
                    String name = value.toString().trim();
                    if (!name.isEmpty()) {
                        return name;
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }

        return "bạn";
    }

    private boolean isGreetingMessage(String message) {
        if (message == null) {
            return false;
        }

        String text = message.trim().toLowerCase();

        return text.equals("hi")
                || text.equals("hello")
                || text.equals("hey")
                || text.equals("xin chào")
                || text.equals("xin chao")
                || text.equals("chào")
                || text.equals("chao");
    }
}