package controllers;

import dao.ProductDAO;
import dto.CartDTO;
import dto.CartItemDTO;
import dto.ProductDTO;
import dto.UserDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/AddToCartServlet")
public class AddToCartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int productID = Integer.parseInt(request.getParameter("productID"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            double price = Double.parseDouble(request.getParameter("price"));
            String size = request.getParameter("size");
            String color = request.getParameter("color");
            boolean fromSaleOff = "true".equals(request.getParameter("fromSaleOff"));

            // Làm sạch dữ liệu size và color
            if (size != null) {
                size = size.trim();
            }
            if (color != null) {
                color = color.trim();
            }

            if (quantity <= 0) {
                response.sendRedirect("user/productDetail.jsp?id=" + productID + "&error=invalid_quantity");
                return;
            }

            ProductDAO dao = new ProductDAO();
            ProductDTO product = dao.getFullProductByID(productID);
            if (product == null) {
                response.sendRedirect("user/error.jsp");
                return;
            }

            product.setSize(size);
            product.setColor(color);
            product.setPrice(price);
            product.setFromSaleOff(fromSaleOff);

            int stock = dao.getStockByVariant(productID, size, color);

            HttpSession session = request.getSession();
            UserDTO user = (UserDTO) session.getAttribute("LOGIN_USER");
            if (user == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            int userID = user.getUserID();
            CartDTO cart = (CartDTO) session.getAttribute("CART");
            if (cart == null || cart.getUserID() != userID) {
                cart = new CartDTO(userID);
            }

            // Gọn hơn khi lấy existingQuantity
            CartItemDTO existingItem = cart.getCartItem(productID, size, color);
            int existingQuantity = (existingItem != null) ? existingItem.getQuantity() : 0;

            if (quantity + existingQuantity > stock) {
                int available = stock - existingQuantity;
                response.sendRedirect("user/productDetail.jsp?id=" + productID + "&error=stock&available=" + available);
                return;
            }

            cart.addToCart(product, quantity, size, color);
            session.setAttribute("CART", cart);

            // Trả về lại trang sản phẩm kèm theo fromSaleOff nếu có
            response.sendRedirect("user/productDetail.jsp?id=" + productID + (fromSaleOff ? "&fromSaleOff=true" : ""));

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("user/error.jsp");
        }
    }
}
