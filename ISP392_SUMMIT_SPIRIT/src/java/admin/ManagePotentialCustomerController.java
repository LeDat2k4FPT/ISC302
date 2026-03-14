package admin;

import dao.UserDAO;
import dao.OrderDAO;
import dao.ReviewDAO;
import dto.UserDTO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ManagePotentialCustomerController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserDAO userDAO = new UserDAO();
            OrderDAO orderDAO = new OrderDAO();
            ReviewDAO reviewDAO = new ReviewDAO();
            List<UserDTO> users = userDAO.getAllUsers();
            List<Map<String, Object>> potentialList = new ArrayList<>();

            for (UserDTO user : users) {
                if (user.getRole().equalsIgnoreCase("User")) {
                    int userID = user.getUserID();
                    double totalSpent = orderDAO.getTotalSpentByUser(userID); // chỉ tính đơn Delivered
                    int orderCount = orderDAO.getOrderCountByUser(userID);    // chỉ tính đơn Delivered
                    int reviewCount = reviewDAO.getReviewCountByUser(userID);

                    // ⚠ Chỉ lấy user có >=1 đơn đã giao và tổng tiền đã chi > 0
                    if (orderCount >= 1 && totalSpent > 0) {
                        Map<String, Object> info = new HashMap<>();
                        info.put("user", user);
                        info.put("totalSpent", totalSpent);
                        info.put("orderCount", orderCount);
                        info.put("reviewCount", reviewCount);
                        potentialList.add(info);
                    }
                }
            }

            // Sắp xếp giảm dần theo tổng chi tiêu
            potentialList.sort((a, b) -> Double.compare((double) b.get("totalSpent"), (double) a.get("totalSpent")));

            request.setAttribute("potentialList", potentialList);
            request.setAttribute("page", "potentialCustomer.jsp");
            request.getRequestDispatcher("admin/admin.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException(e);
        }
    }
}
