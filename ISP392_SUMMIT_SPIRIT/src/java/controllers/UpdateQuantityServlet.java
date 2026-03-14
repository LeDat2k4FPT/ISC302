package controllers;

import dao.ProductDAO;
import dto.CartDTO;
import dto.UserDTO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;

@WebServlet("/UpdateQuantityServlet")
public class UpdateQuantityServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession();

            // ✅ Check login
            UserDTO user = (UserDTO) session.getAttribute("LOGIN_USER");
            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // ✅ Lấy tham số productID, size, color, quantity từ AJAX
            String productIDStr = request.getParameter("productID");
            String size = request.getParameter("size");
            String color = request.getParameter("color"); // ✅ thêm color
            String quantityStr = request.getParameter("quantity");

            if (productIDStr == null || size == null || color == null || quantityStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            int productID = Integer.parseInt(productIDStr);
            int newQuantity = Integer.parseInt(quantityStr);

            CartDTO cart = (CartDTO) session.getAttribute("CART");

            if (cart == null || cart.getCartItem(productID, size, color) == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // ✅ Nếu số lượng <= 0 → xóa khỏi giỏ hàng
            if (newQuantity <= 0) {
                cart.removeFromCart(productID, size, color); // ✅ truyền thêm color
                session.setAttribute("CART", cart);
                return;
            }

            // ✅ Kiểm tra tồn kho biến thể đúng size-color
            ProductDAO dao = new ProductDAO();
            int stock = dao.getStockByVariant(productID, size, color);
            if (newQuantity > stock) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Quantity exceeds stock");
                return;
            }

            // ✅ Cập nhật số lượng theo productID + size + color
            cart.updateQuantity(productID, size, color, newQuantity); // ✅ truyền thêm color
            session.setAttribute("CART", cart);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid number format");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
