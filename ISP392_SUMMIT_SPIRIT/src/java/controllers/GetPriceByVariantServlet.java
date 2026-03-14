package controllers;

import dao.ProductVariantDAO;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;

@WebServlet("/GetPriceByVariantServlet")
public class GetPriceByVariantServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8"); // ✅ THÊM DÒNG NÀY

        String size = request.getParameter("size");
        String color = request.getParameter("color");
        String productIdStr = request.getParameter("productId");

        try (PrintWriter out = response.getWriter()) {
            int productId = Integer.parseInt(productIdStr);
            ProductVariantDAO dao = new ProductVariantDAO();
            double price = dao.getPriceByVariant(productId, size, color);

            // ✅ Trả JSON chuẩn
            String json = "{\"price\": " + price + "}";
            out.write(json);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching price");
        }
    }
}
