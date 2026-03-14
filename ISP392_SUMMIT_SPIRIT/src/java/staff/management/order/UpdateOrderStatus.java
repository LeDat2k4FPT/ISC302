package staff.management.order;

import dao.OrderDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "UpdateOrderStatus", urlPatterns = {"/UpdateOrderStatus"})
public class UpdateOrderStatus extends HttpServlet {
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("LOGIN_USER") == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        try {
//            int orderID = Integer.parseInt(request.getParameter("orderID"));
//            String status = request.getParameter("status");
//
//            System.out.println("[SERVLET] orderID=" + orderID + ", status=" + status);
//
//            OrderDAO dao = new OrderDAO();
//            boolean updated = dao.updateOrderStatus(orderID, status);
//
//            if (updated) {
//                System.out.println("[SERVLET] Update successful.");
//            } else {
//                System.out.println("[SERVLET] Update failed.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        response.sendRedirect("staff/orderlist.jsp"); // quay lại list sau khi update
//    }

//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//
//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("LOGIN_USER") == null) {
//            response.sendRedirect("login.jsp");
//            return;
//        }
//
//        try {
//            int orderID = Integer.parseInt(request.getParameter("orderID"));
//            String status = request.getParameter("status");
//
//            System.out.println("[SERVLET] orderID=" + orderID + ", status=" + status);
//
//            OrderDAO dao = new OrderDAO();
//            boolean updated = dao.updateOrderStatus(orderID, status);
//
//            if (updated) {
//                System.out.println("[SERVLET] Update successful.");
//            } else {
//                System.out.println("[SERVLET] Update failed.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Quay lại trang chính để load lại
////request.getRequestDispatcher("staff/orderlist.jsp").forward(request, response);
////response.sendRedirect(request.getContextPath() + "/staffDashboard.jsp");
//        response.sendRedirect(request.getContextPath() + "/staffDashboard.jsp?page=staff/orderlist.jsp&msg=Update+successful&type=success");
//
//    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("LOGIN_USER") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String message;
        String type;
        try {
            int orderID = Integer.parseInt(request.getParameter("orderID"));
            String status = request.getParameter("status");

            System.out.println("[SERVLET] orderID=" + orderID + ", status=" + status);

            OrderDAO dao = new OrderDAO();
            boolean updated = dao.updateOrderStatus(orderID, status);

            if (updated) {
                System.out.println("[SERVLET] Update successful.");
                message = "Update successfully!!!";
                type = "success";
            } else {
                System.out.println("[SERVLET] Update failed.");
                message = "Update failed";
                type = "danger";
            }

        } catch (Exception e) {
            e.printStackTrace();
            message = "Error updating order";
            type = "danger";
        }

        response.sendRedirect(request.getContextPath()
                + "/staff/staffDashboard.jsp?page=orderlist.jsp&msg="
                + java.net.URLEncoder.encode(message, "UTF-8")
                + "&type=" + type);
    }

}
