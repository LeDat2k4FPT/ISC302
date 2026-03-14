/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
/**
 *
 * @author Hanne
 */
package ship;

import dao.OrderDAO;
import dao.ShippingDAO;
import dto.UserDTO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.Base64;
import java.io.File;

@WebServlet(name = "MarkDeliveredController", urlPatterns = {"/MarkDeliveredController"})
@MultipartConfig
public class MarkDeliveredController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Nhận dữ liệu từ form
            int orderID = Integer.parseInt(request.getParameter("orderID"));
            String note = request.getParameter("note");
            String imageData = request.getParameter("imageData"); // base64 image

            String imageUrl = null;

            if (imageData != null && imageData.startsWith("data:image")) {
                // Lấy phần base64 sau "data:image/png;base64,"
                String base64Image = imageData.split(",")[1];
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                // Tạo tên file duy nhất
                String fileName = "delivery_" + orderID + "_" + System.currentTimeMillis() + ".png";

                // Tạo thư mục uploads nếu chưa có
                String uploadPath = getServletContext().getRealPath("/uploads");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Lưu ảnh vào thư mục
                File imageFile = new File(uploadDir, fileName);
                try ( FileOutputStream fos = new FileOutputStream(imageFile)) {
                    fos.write(imageBytes);
                }

                // URL lưu vào DB (tương đối từ context path)
                imageUrl = "uploads/" + fileName;
            }

            // Cập nhật trạng thái đơn và lưu ảnh
            OrderDAO orderDAO = new OrderDAO();
            ShippingDAO shippingDAO = new ShippingDAO();
            UserDTO shipper = (UserDTO) request.getSession().getAttribute("LOGIN_USER");
            boolean orderUpdated = orderDAO.updateOrderStatus(orderID, "Delivered");
            if (shipper == null) {
                response.sendRedirect("login.jsp");
                return;
            }
            int userID = shipper.getUserID();

            boolean shippingUpdated = shippingDAO.markAsDelivered(orderID, imageUrl, note, userID);

            if (orderUpdated && shippingUpdated) {
                response.sendRedirect("DeliveryListController");
            } else {
                response.sendError(500, "Không thể cập nhật đơn hàng.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Lỗi xử lý giao hàng.");
        }
    }
}
