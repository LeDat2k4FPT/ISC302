package staff.management.product;

import dao.ProductDAO;
import dao.ProductVariantDAO;
import dto.ProductDTO;
import dto.ProductVariantDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ProductListController", urlPatterns = {"/ProductListController"})
public class ProductListController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        try {
            HttpSession session = request.getSession();

            ProductDAO productDAO = new ProductDAO();
            ProductVariantDAO variantDAO = new ProductVariantDAO();

            String keyword = request.getParameter("keyword");
            List<ProductDTO> productList;

            if (keyword != null && !keyword.trim().isEmpty()) {
                productList = productDAO.getProductsByName(keyword.trim());
            } else {
                productList = productDAO.getAllProducts();
            }

            Map<Integer, List<ProductVariantDTO>> variantMap = variantDAO.getAllVariantsGroupedByProduct();

            // Sau khi xử lý dữ liệu:
            session.setAttribute("productList", productList);
            session.setAttribute("variantMap", variantMap);
            session.setAttribute("keyword", keyword);

            // Redirect sang giao diện hiển thị layout
            String msg = request.getParameter("msg");
            String type = request.getParameter("type");

            String redirectURL = request.getContextPath() + "/staff/staffDashboard.jsp?page=productlist.jsp";

            if (msg != null && type != null) {
                redirectURL += "&msg=" + java.net.URLEncoder.encode(msg, "UTF-8")
                        + "&type=" + java.net.URLEncoder.encode(type, "UTF-8");
            }

            response.sendRedirect(redirectURL);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading product list: " + e.getMessage());
            request.getRequestDispatcher("/staff/error.jsp").forward(request, response);
        }
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
