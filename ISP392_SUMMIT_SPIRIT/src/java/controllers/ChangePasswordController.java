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

/**
 *
 * @author gmt
 */
@WebServlet(name = "ChangePasswordController", urlPatterns = {"/ChangePasswordController"})
public class ChangePasswordController extends HttpServlet {

    private static final String ERROR = "changePassword.jsp";
    private static final String SUCCESS = "login.jsp";
    private static final String NOT_MATCH = "Please make sure the password and confirm password match!";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = ERROR;
        boolean checkValidation = true;
        try {
            String password = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmNewPassword = request.getParameter("confirmNewPassword");
            if (checkValidation) {
                UserDAO dao = new UserDAO();
                HttpSession session = request.getSession();
                UserDTO loginUser = (UserDTO) session.getAttribute("LOGIN_USER");
                if (!password.equals(loginUser.getPassword())) {
                    request.setAttribute("MESSAGE", "Current password is incorrect!");
                    url = ERROR;
                } else if (!newPassword.matches("[A-Z](?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{7,}$")) {
                    request.setAttribute("MESSAGE", "Password must be at least 8 characters long and include one first uppercase, lowercase, number, and one special character.");
                    url = ERROR;
                } else if (!confirmNewPassword.equals(newPassword)) {
                    request.setAttribute("MESSAGE", NOT_MATCH);
                    url = ERROR;
                } else {
                    boolean checkUpdate = dao.updatePassword(loginUser.getEmail(), newPassword);
                    if (checkUpdate) {
                        session.invalidate();
                        url = SUCCESS;
                    } else {
                        url = ERROR;
                    }
                }
            }
        } catch (Exception e) {
            log("Error at ChangePasswordController: " + e.toString());
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
