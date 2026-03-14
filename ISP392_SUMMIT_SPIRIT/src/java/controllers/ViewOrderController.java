package controllers;

import dao.OrderDAO;
import dto.OrderDTO;
import dto.UserDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ViewOrderController", urlPatterns = {"/ViewOrderController"})
public class ViewOrderController extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");

        if (loginUser == null) {

            response.sendRedirect("login.jsp");
            return;
        }

        int userID = loginUser.getUserID();
        OrderDAO orderDAO = new OrderDAO();

        try {

            List<OrderDTO> orderList = orderDAO.getOrdersByUser(userID);

            if (orderList != null && !orderList.isEmpty()) {
                request.setAttribute("ORDER_LIST", orderList);
            } else {
                request.setAttribute("MESSAGE", "Bạn chưa có đơn hàng nào.");
            }
        } catch (Exception e) {

            e.printStackTrace();
            request.setAttribute("ERROR", "Đã xảy ra lỗi khi lấy thông tin đơn hàng. Vui lòng thử lại sau.");
        }

        request.getRequestDispatcher("user/orderHistory.jsp").forward(request, response);
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
