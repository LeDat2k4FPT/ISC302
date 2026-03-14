package controllers;

import dao.ProductDAO;
import dto.ProductDTO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ViewProductController", urlPatterns = {"/ViewProductController"})
public class ViewProductController extends HttpServlet {

    // Ánh xạ tên danh mục sang categoryID
    private static final Map<String, Integer> CATEGORY_MAP = new HashMap<>();

    static {
        CATEGORY_MAP.put("ao", 2);
        CATEGORY_MAP.put("quan", 1);
        CATEGORY_MAP.put("balo", 3);
        CATEGORY_MAP.put("dungcu", 4);
        CATEGORY_MAP.put("trai", 5);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String url = "user/homepage.jsp";

        try {
            // Nhận giá trị từ request
            String category = request.getParameter("category");
            String sort = request.getParameter("sort");
            String sortOrder = (sort != null && sort.equalsIgnoreCase("desc")) ? "desc" : "asc";

            ProductDAO dao = new ProductDAO();
            List<ProductDTO> products;

            if (category == null || !CATEGORY_MAP.containsKey(category.toLowerCase())) {
                // Trường hợp không có danh mục hợp lệ → hiển thị 3 sản phẩm mặc định
                int[] defaultIDs = {1, 2, 3};
                products = dao.getTopNProductsByIDs(defaultIDs);
            } else {
                // Trường hợp có danh mục hợp lệ → gọi hàm đã fix lặp để lấy sản phẩm theo danh mục
                int cateID = CATEGORY_MAP.get(category.toLowerCase());

                // Gọi hàm đã được chỉnh sửa tránh trùng sản phẩm (dùng MIN(...) + GROUP BY)
                products = dao.getProductsByCategorySorted(cateID, sortOrder);

                // Gửi thêm thông tin để JSP biết đang lọc theo danh mục và sắp xếp gì
                request.setAttribute("CURRENT_CATEGORY", category);
                request.setAttribute("SORT_ORDER", sortOrder);
            }

            // Đưa danh sách sản phẩm ra homepage.jsp
            request.setAttribute("PRODUCT_LIST", products);

        } catch (Exception e) {
            log("Error at ViewProductController: " + e.getMessage());
        } finally {
            // Chuyển đến trang giao diện
            request.getRequestDispatcher(url).forward(request, response);
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

    @Override
    public String getServletInfo() {
        return "Hiển thị danh sách sản phẩm theo danh mục hoặc mặc định";
    }
}
