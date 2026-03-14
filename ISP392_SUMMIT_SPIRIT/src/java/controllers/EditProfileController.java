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
import user.UserError;

/**
 *
 * @author gmt
 */
@WebServlet(name = "EditProfileController", urlPatterns = {"/EditProfileController"})
public class EditProfileController extends HttpServlet {

    private static final String ERROR = "user/profile.jsp";
    private static final String UNKNOW_MESSAGE = "Unknow error!";
    private static final String STAFF_PAGE = "staff/staffProfile.jsp";
    private static final String ADMIN_PAGE = "admin/admin.jsp?page=adminProfile.jsp";
    private static final String USER_PAGE = "user/profile.jsp";
    private static final String SHIPPER_PAGE = "ship/shipProfile.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = ERROR;
        UserError userError = new UserError();
        boolean checkValidation = true;
        try {
            int userID = Integer.parseInt(request.getParameter("userID"));
            String fullName = request.getParameter("fullName");
            String address = request.getParameter("address");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phone");
            UserDAO dao = new UserDAO();
            HttpSession session = request.getSession();
            UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
            boolean emailExists = dao.checkEmailExists(email);
            if (emailExists && !email.equals(loginUser.getEmail())) {
                userError.setEmail("Email already exists!");
                checkValidation = false;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                userError.setEmail("Invalid email format!");
                checkValidation = false;
            }
            if (!phoneNumber.matches("^0[0-9]{9}$")) {
                userError.setPhone("Invalid phone number format!");
                checkValidation = false;
            }
            boolean phoneExists = dao.checkPhoneExists(phoneNumber);
            if (phoneExists && !phoneNumber.equals(loginUser.getPhone())) {
                userError.setPhone("Phone number already exists!");
                checkValidation = false;
            }
            if (checkValidation) {
                UserDTO user = new UserDTO(userID, fullName, address, "", phoneNumber, email, "");
                boolean checkEdit = dao.edit(user);
                if (checkEdit) {
                    if (loginUser != null && loginUser.getUserID() == userID) {
                        loginUser.setFullName(fullName);
                        loginUser.setAddress(address);
                        loginUser.setEmail(email);
                        loginUser.setPhone(phoneNumber);
                        session.setAttribute("LOGIN_USER", loginUser);
                    }
                    if ("Staff".equals(loginUser.getRole())) {
                        url = STAFF_PAGE;
                    } else if ("Admin".equals(loginUser.getRole())) {
                        url = ADMIN_PAGE;
                    } else if ("Shipper".equals(loginUser.getRole())) {
                        url = SHIPPER_PAGE;
                    } else {
                        url = USER_PAGE;
                    }

                } else {
                    userError.setErrorMessage(UNKNOW_MESSAGE);
                }
            } else {
                request.setAttribute("USER_ERROR", userError);
            }
        } catch (Exception e) {
            log("Error at EditProfileController: " + e.toString());
        } finally {
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
