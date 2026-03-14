package controllers;

import dto.CartDTO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;

@WebServlet(name = "GoToShippingServlet", urlPatterns = {"/GoToShippingServlet"})
public class GoToShippingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Kiểm tra đăng nhập
        if (request.getSession().getAttribute("LOGIN_USER") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // ✅ Cập nhật lại số lượng từ form
        CartDTO cart = (CartDTO) request.getSession().getAttribute("CART");

        if (cart != null) {
            Enumeration<String> paramNames = request.getParameterNames();

            while (paramNames.hasMoreElements()) {
                String param = paramNames.nextElement();

                if (param.startsWith("quantity_")) {
                    try {
                        String key = param.substring("quantity_".length()); // productID_size
                        int quantity = Integer.parseInt(request.getParameter(param));
                        cart.updateQuantity(key, quantity);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        }

        // ✅ Chuyển sang shipping.jsp
        request.getRequestDispatcher("user/shipping.jsp").forward(request, response);
    }
}
