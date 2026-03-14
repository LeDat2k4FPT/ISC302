package controllers;

import dao.CartDAO;
import dto.CartDTO;
import dto.CartItemDTO;
import dto.ProductDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "ApplyDiscountServlet", urlPatterns = {"/ApplyDiscountServlet"})
public class ApplyDiscountServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String discountCode = request.getParameter("discountCode");
        String sourcePage = request.getParameter("sourcePage");
        if (sourcePage == null || sourcePage.isEmpty()) {
            sourcePage = "user/shipping.jsp";
        }

        HttpSession session = request.getSession();
        Object loginUser = session.getAttribute("LOGIN_USER");
        if (loginUser == null) {
            request.setAttribute("DISCOUNT_ERROR", "Bạn cần đăng nhập để sử dụng mã giảm giá.");
            request.getRequestDispatcher(sourcePage).forward(request, response);
            return;
        }

        CartDTO cart = (CartDTO) session.getAttribute("CART");
        ProductDTO buyNowProduct = (ProductDTO) session.getAttribute("BUY_NOW_PRODUCT");

        boolean hasValidProduct = (cart != null && !cart.isEmpty()) || buyNowProduct != null;

        if (!hasValidProduct) {
            request.setAttribute("DISCOUNT_ERROR", "Không thể áp dụng mã khi không có sản phẩm nào.");
            request.getRequestDispatcher(sourcePage).forward(request, response);
            return;
        }

        // Kiểm tra sản phẩm giảm giá sẵn
        if (cart != null && !cart.isEmpty()) {
            for (CartItemDTO item : cart.getCartItems()) {
                if (item.getProduct().isFromSaleOff()) {
                    request.setAttribute("DISCOUNT_ERROR", "Không thể sử dụng mã giảm giá với sản phẩm đang được giảm giá sẵn.");
                    request.getRequestDispatcher(sourcePage).forward(request, response);
                    return;
                }
            }
        }

        if (buyNowProduct != null && buyNowProduct.isFromSaleOff()) {
            request.setAttribute("DISCOUNT_ERROR", "Không thể sử dụng mã giảm giá với sản phẩm đang được giảm giá sẵn.");
            request.getRequestDispatcher(sourcePage).forward(request, response);
            return;
        }

        // Tính tổng tiền
        double totalCartAmount = 0.0;
        if (cart != null && !cart.isEmpty()) {
            totalCartAmount = cart.getTotalPrice();
        } else if (buyNowProduct != null) {
            totalCartAmount = buyNowProduct.getPrice() * buyNowProduct.getQuantity();
        }

        CartDAO dao = new CartDAO();
        Optional<Double> discountOpt = dao.validateDiscountCode(discountCode, totalCartAmount);

        if (discountOpt.isPresent()) {
            double discountValue = discountOpt.get();
            double maxAllowedDiscount = Math.min(discountValue, 100);
            double discountAmount = totalCartAmount * maxAllowedDiscount / 100;

            if (discountAmount >= totalCartAmount) {
                request.setAttribute("DISCOUNT_ERROR", "Chiết khấu vượt quá tổng tiền.");
            } else {
                request.setAttribute("DISCOUNT_PERCENT", maxAllowedDiscount);
                request.setAttribute("DISCOUNT_CODE", discountCode);
            }
        } else {
            request.setAttribute("DISCOUNT_ERROR", "Mã giảm giá không hợp lệ, hết hạn hoặc không đủ điều kiện.");
        }

        // Lưu thông tin giao hàng vào session
        session.setAttribute("SHIPPING_COUNTRY", request.getParameter("country"));
        session.setAttribute("SHIPPING_FULLNAME", request.getParameter("fullname"));
        session.setAttribute("SHIPPING_PHONE", request.getParameter("phone"));
        session.setAttribute("SHIPPING_EMAIL", request.getParameter("email"));
        session.setAttribute("SHIPPING_ADDRESS", request.getParameter("address"));
        session.setAttribute("SHIPPING_DISTRICT", request.getParameter("district"));
        session.setAttribute("SHIPPING_CITY", request.getParameter("city"));

        request.getRequestDispatcher(sourcePage).forward(request, response);
    }
}
