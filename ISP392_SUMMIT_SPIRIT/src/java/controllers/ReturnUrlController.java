package controllers;

import com.vnpay.common.Config;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import dao.OrderDAO;
import dto.OrderDTO;

/**
 *
 * @author gmt
 */
@WebServlet(name = "ReturnUrlController", urlPatterns = {"/ReturnUrlController"})
public class ReturnUrlController extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy tất cả parameter từ VNPay
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<?> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = (String) params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");

        // Xóa các field không cần thiết để tạo chữ ký
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        // Tạo lại chữ ký
        String signValue = Config.hashAllFields(fields);

        if (signValue.equals(vnp_SecureHash)) {
            // Xác thực hợp lệ
            String responseCode = request.getParameter("vnp_ResponseCode");
            String orderId = request.getParameter("vnp_TxnRef");
            if (orderId != null) {
                try {
                    OrderDAO orderDAO = new OrderDAO();
                    OrderDTO order = new OrderDTO();
                    order.setOrderID(Integer.parseInt(orderId));
                    if ("00".equals(responseCode)) {
                        order.setStatus("Completed");
                    } else {
                        order.setStatus("Failed");
                    }
                    orderDAO.updateOrderStatus(order);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if ("00".equals(responseCode)) {
                // ✅ Thanh toán thành công
                request.setAttribute("paymentResult", "success");
                request.setAttribute("message", "Thanh toán thành công!");
                // Trích thông tin đơn hàng từ VNPay
                request.setAttribute("transactionId", request.getParameter("vnp_TransactionNo"));
                request.setAttribute("amount", request.getParameter("vnp_Amount")); // VND * 100
                request.setAttribute("orderInfo", request.getParameter("vnp_OrderInfo"));
            } else {
                // ❌ Thanh toán thất bại
                request.setAttribute("paymentResult", "failed");
                request.setAttribute("message", "Thanh toán thất bại. Mã lỗi: " + responseCode);
            }
        } else {
            // ❗ Chữ ký không hợp lệ
            request.setAttribute("paymentResult", "invalid");
            request.setAttribute("message", "Chữ ký không hợp lệ! Dữ liệu có thể đã bị thay đổi.");
        }

        // Chuyển tới trang hiển thị kết quả
        request.getRequestDispatcher("user/paymentResult.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
