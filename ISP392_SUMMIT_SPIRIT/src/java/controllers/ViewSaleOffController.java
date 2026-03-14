package controllers;

import dao.ProductDAO;
import dto.ProductDTO;
import dto.UserDTO;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ViewSaleOffController", urlPatterns = {"/ViewSaleOffController"})
public class ViewSaleOffController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UserDTO loginUser = (session != null) ? (UserDTO) session.getAttribute("LOGIN_USER") : null;

        if (loginUser == null || !"User".equals(loginUser.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            ProductDAO dao = new ProductDAO();
            // ✅ Gọi đúng hàm lấy 3 sản phẩm cụ thể và set giảm giá tạm thời
            List<ProductDTO> saleProducts = dao.get3FixedShirtsForSaleOff();

            request.setAttribute("SALE_PRODUCTS", saleProducts);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error while fetching discounted products!");
        }

        request.getRequestDispatcher("user/saleOff.jsp").forward(request, response);
    }
}
