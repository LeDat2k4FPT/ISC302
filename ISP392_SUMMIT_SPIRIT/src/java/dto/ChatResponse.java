package dto;

import java.util.ArrayList;
import java.util.List;

public class ChatResponse {
    private boolean success;
    private String message;
    private List<ChatProductResult> products;

    public ChatResponse() {
        this.products = new ArrayList<>();
    }

    public ChatResponse(boolean success, String message, List<ChatProductResult> products) {
        this.success = success;
        this.message = message;
        this.products = products;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ChatProductResult> getProducts() {
        return products;
    }

    public void setProducts(List<ChatProductResult> products) {
        this.products = products;
    }
}