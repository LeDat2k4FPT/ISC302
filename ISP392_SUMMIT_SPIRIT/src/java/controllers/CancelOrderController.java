package controllers;

import dao.OrderDAO;
import dto.OrderDTO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author gmt
 */
@WebServlet(name = "CancelOrderController", urlPatterns = {"/CancelOrderController"})
public class CancelOrderController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CancelOrderController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CancelOrderController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } catch (Exception ex) {
            log(ex.getMessage());
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int orderID = Integer.parseInt(request.getParameter("orderID"));
        String reason = request.getParameter("cancelReason");

        OrderDAO dao = new OrderDAO();
        OrderDTO currentOrder = dao.getOrderByID(orderID);

        String currentStatus = currentOrder.getStatus();
        String newStatus = null;

        boolean showPopup = false;

        if ("Shipped".equalsIgnoreCase(currentStatus) || "Processing".equalsIgnoreCase(currentStatus)) {
            newStatus = "Cancelling";
            showPopup = true;
        } else if ("Pending".equalsIgnoreCase(currentStatus) || "Failed".equalsIgnoreCase(currentStatus)) {
            newStatus = "Cancelled";
        }

        if (newStatus != null) {
            currentOrder.setStatus(newStatus);
            currentOrder.setNote(reason);

            boolean updated = dao.updateOrderStatusAndNote(currentOrder);
            if (updated) {
                if (showPopup) {
                    request.getSession().setAttribute("cancelSuccess", true);
                }
                response.sendRedirect("UserOrderHistoryController");
                return;
            }
        }

        response.sendRedirect("UserOrderHistoryController?msg=error");
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
