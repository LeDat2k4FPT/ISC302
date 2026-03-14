package staff.management.product;

import dao.ProductDAO;
import dto.ProductDTO;
import dao.ProductVariantDAO;
import dao.ProductImageDAO;
import dto.ProductVariantDTO;
import dto.ProductImageDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "UpdateProductController", urlPatterns = {"/UpdateProductController"})
public class UpdateProductController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            System.out.println("===== UpdateProductController RUNNING =====");

            int productID = Integer.parseInt(request.getParameter("productID"));
            String productName = request.getParameter("productName");
            String description = request.getParameter("description");
            String status = request.getParameter("status");
            int cateID = Integer.parseInt(request.getParameter("cateID"));

            ProductDTO product = new ProductDTO();
            product.setProductID(productID);
            product.setProductName(productName);
            product.setDescription(description);
            product.setStatus(status);
            product.setCateID(cateID);

            ProductDAO dao = new ProductDAO();
            boolean updated = dao.updateProductByID(product);

            System.out.println("PRODUCT UPDATED = " + updated);

            /* =========================
               UPDATE VARIANTS
            ========================== */

            ProductVariantDAO variantDAO = new ProductVariantDAO();

            int variantCount = Integer.parseInt(request.getParameter("variantCount"));

            for (int i = 0; i < variantCount; i++) {

                String attrIdStr = request.getParameter("variantId_" + i);
                String qtyStr = request.getParameter("quantity_" + i);
                String prcStr = request.getParameter("price_" + i);

                if (attrIdStr == null || qtyStr == null || prcStr == null) {
                    continue;
                }

                int attributeID = Integer.parseInt(attrIdStr);
                int qty = Integer.parseInt(qtyStr);
                double price = Double.parseDouble(prcStr);

                ProductVariantDTO variant = new ProductVariantDTO();
                variant.setAttributeID(attributeID);
                variant.setQuantity(qty);
                variant.setPrice(price);

                variantDAO.updateVariant(variant);

                System.out.println("Updating variant ID: " + attributeID);
            }

            /* =========================
               UPDATE IMAGE (URL)
            ========================== */

            String imageUrl = request.getParameter("imageUrl");

            if (imageUrl != null && !imageUrl.trim().isEmpty()) {

                ProductImageDAO imageDAO = new ProductImageDAO();

                imageDAO.deleteByProductID(productID);
                imageDAO.insertImage(new ProductImageDTO(imageUrl.trim(), productID));
            }

            /* =========================
               REDIRECT
            ========================== */

            if (updated) {

                response.sendRedirect("ProductListController?msg=Update Product Success&type=success");

            } else {

                request.setAttribute("error", "Update product failed");
                request.getRequestDispatcher("staff/editproduct.jsp").forward(request, response);

            }

        } catch (Exception e) {

            e.printStackTrace();

            request.setAttribute("error", "Error: " + e.getMessage());
            request.getRequestDispatcher("staff/editproduct.jsp").forward(request, response);

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