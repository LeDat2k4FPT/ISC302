package admin;

import dao.UserDAO;
import dto.UserDTO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author Hanne
 */
@WebServlet(name = "AdminCreateUserController", urlPatterns = {"/AdminCreateUserController"})
public class AdminCreateUserController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String fullName = request.getParameter("fullName");
        String address = request.getParameter("address");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");
        String role = request.getParameter("role");

        boolean isValid = true;
        String message = "";

        try {
            UserDAO dao = new UserDAO();

            if (dao.checkEmailExists(email)) {
                message = "❌ Email already exists!";
                isValid = false;
            } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                message = "❌ Invalid email format.";
                isValid = false;
            } else if (dao.checkPhoneExists(phone)) {
                message = "❌ Phone number already exists!";
                isValid = false;
            } else if (!phone.matches("^0[0-9]{9}$")) {
                message = "❌ Invalid phone number format.";
                isValid = false;
            } else if (!password.equals(confirm)) {
                message = "❌ Password and confirm password do not match.";
                isValid = false;
            } else if (!password.matches("^[A-Z](?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{7,}$")) {
                message = "❌ Password must start with uppercase, include lowercase, number, special character, and be at least 8 characters.";
                isValid = false;
            }

            if (isValid) {
                UserDTO user = new UserDTO(0, fullName, address, password, phone, email, role);
                boolean success = dao.create(user);
                if (success) {
                    // Redirect để load lại user list
                    response.sendRedirect(request.getContextPath() + "/ManageUserAccountController");
                    return;
                } else {
                    message = "❌ Failed to create user. Please try again.";
                }
            }

            // Giữ lại dữ liệu nếu lỗi
            request.setAttribute("message", message);
            request.setAttribute("fullName", fullName);
            request.setAttribute("address", address);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("password", password);
            request.setAttribute("confirm", confirm);
            request.setAttribute("role", role);

            // Đưa vào layout admin với page là createUserAccount.jsp
            request.setAttribute("page", "createUserAccount.jsp");
            request.getRequestDispatcher("admin/admin.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("message", "❌ Error: " + e.getMessage());
            request.setAttribute("page", "createUserAccount.jsp");
            request.getRequestDispatcher("admin/admin.jsp").forward(request, response);
        }
    }


    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    request.setAttribute("page", "createUserAccount.jsp");
    request.getRequestDispatcher("admin/admin.jsp").forward(request, response);
}

}
