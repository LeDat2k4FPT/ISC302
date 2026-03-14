/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllers;

import dto.ProductDTO;
import dao.ProductDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "CartServlet", urlPatterns = {"/CartServlet"})
public class CartServlet extends HttpServlet {

    // Cart stored in session as Map<ProductID, CartItem>
    public static class CartItem {

        private ProductDTO product;
        private int quantity;

        public CartItem(ProductDTO product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public ProductDTO getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("CART");
        if (cart == null) {
            cart = new HashMap<>();
        }

        String action = request.getParameter("action");
        String productIDStr = request.getParameter("productID");
        int productID = 0;
        if (productIDStr != null) {
            try {
                productID = Integer.parseInt(productIDStr);
            } catch (NumberFormatException e) {
                productID = 0;
            }
        }

        ProductDAO productDAO = new ProductDAO();

        try {
            if ("add".equals(action) && productID > 0) {
                ProductDTO product = productDAO.getProductByID(productID);
                if (product != null) {
                    CartItem item = cart.get(productID);
                    if (item == null) {
                        cart.put(productID, new CartItem(product, 1));
                    } else {
                        item.setQuantity(item.getQuantity() + 1);
                    }
                }
            } else if ("remove".equals(action) && productID > 0) {
                cart.remove(productID);
            } else if ("update".equals(action) && productID > 0) {
                String quantityStr = request.getParameter("quantity");
                int quantity = 1;
                try {
                    quantity = Integer.parseInt(quantityStr);
                } catch (NumberFormatException e) {
                    quantity = 1;
                }
                if (quantity > 0) {
                    CartItem item = cart.get(productID);
                    if (item != null) {
                        item.setQuantity(quantity);
                    }
                }
            } else if ("view".equals(action)) {
                // just forward to cart.jsp
            }
            // Cập nhật session
            session.setAttribute("CART", cart);
        } catch (Exception e) {
            log("Error in CartServlet: " + e);
        }

        request.getRequestDispatcher("user/cart.jsp").forward(request, response);
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
