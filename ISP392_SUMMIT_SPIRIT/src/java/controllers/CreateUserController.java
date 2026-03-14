package controllers;

import dao.UserDAO;
import dto.UserDTO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Random;
import user.UserError;
import utils.EmailUtils;

/**
 *
 * @author gmt
 */
@WebServlet(name = "CreateUserController", urlPatterns = {"/CreateUserController"})
public class CreateUserController extends HttpServlet {

    private static final String ERROR = "user/createUser.jsp";
    private static final String SUCCESS = "user/verifyAccount.jsp";
    private static final String NOT_MATCH = "Please make sure the password and confirm password match!";
    private static final String DUPLICATE_MESSAGE = "Email already exists!";
    private static final String DUPLICATE_PHONE_NUMBER = "Phone number already exists!";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = ERROR;
        String fullName = "";
        String address = "";
        String email = "";
        String phone = "";
        String password = "";
        String confirm = "";
        boolean checkValidation = true;
        try {
            fullName = request.getParameter("fullName");
            address = request.getParameter("address");
            email = request.getParameter("email");
            phone = request.getParameter("phone");
            password = request.getParameter("password");
            confirm = request.getParameter("confirm");
            UserError userError = new UserError();
            UserDAO dao = new UserDAO();
            boolean emailExists = dao.checkEmailExists(email);
            if (emailExists) {
                userError.setEmail(DUPLICATE_MESSAGE);
                checkValidation = false;
            }
            if (!confirm.equals(password)) {
                userError.setConfirm(NOT_MATCH);
                checkValidation = false;
            }
            if (!password.matches("^[A-Z](?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{7,}$")) {
                userError.setPassword("Password must be at least 8 characters long and include one first uppercase, lowercase, number, and special character.");
                checkValidation = false;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                userError.setEmail("Invalid email format!");
                checkValidation = false;
            }
            if (!phone.matches("^0[0-9]{9}$")) {
                userError.setPhone("Invalid phone number format!");
                checkValidation = false;
            }
            boolean phoneExists = dao.checkPhoneExists(phone);
            if (phoneExists) {
                userError.setPhone(DUPLICATE_PHONE_NUMBER);
                checkValidation = false;
            }
            String otp = String.valueOf(new Random().nextInt(900000) + 100000);
            if (checkValidation) {
                HttpSession session = request.getSession();
                UserDTO user = new UserDTO(0, fullName, address, password, phone, email, "User");
                session.setAttribute("PENDING_USER", user);
                session.setAttribute("OTP", otp);
                session.setAttribute("email", email);
                String htmlBody = "<p>Hello <strong>" + fullName + "</strong>,</p>"
                        + "<p>You just registered the account on <strong>SUMMIT SPIRIT</strong> by this email.To complete the registration, please enter the following verification code:</p>"
                        + "<p><strong>Verification Code: <span style='font-size: 20px; color: #007bff;'>" + otp + "</span></strong></p>"
                        + "<p>If you did not initiate this action, please ignore this email.</p>"
                        + "<p>Best regards,<br><em>SUMMIT SPIRIT</em></p>";
                EmailUtils.sendEmail(email, "Verify Account", htmlBody);
                url = SUCCESS;
            } else {
                request.setAttribute("USER_ERROR", userError);
                url = ERROR;
            }
        } catch (Exception e) {
            log("Error at CreateUserController: " + e.toString());
        } finally {
            request.setAttribute("fullName", fullName);
            request.setAttribute("address", address);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("password", password);
            request.setAttribute("confirm", confirm);
            request.getRequestDispatcher(url).forward(request, response);
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
        processRequest(request, response);
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
