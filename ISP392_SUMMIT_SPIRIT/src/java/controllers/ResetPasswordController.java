package controllers;

import dao.UserDAO;
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
@WebServlet(name = "ResetPasswordController", urlPatterns = {"/ResetPasswordController"})
public class ResetPasswordController extends HttpServlet {

    private static final String ERROR = "resetPassword.jsp";
    private static final String SUCCESS = "login.jsp";
    private static final String NOT_MATCH = "Please make sure the password and confirm password match!";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = ERROR;
        try {
            String newPassword = request.getParameter("newPassword");
            String confirmNewPassword = request.getParameter("confirmNewPassword");
            if (!confirmNewPassword.equals(newPassword)) {
                request.setAttribute("MESSAGE", NOT_MATCH);
            } else if (!newPassword.matches("[A-Z](?=.*[a-z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{7,}$")) {
                request.setAttribute("MESSAGE", "Password must be at least 8 characters long and include one first uppercase, lowercase, number, and one special character.");
            } else {
                HttpSession session = request.getSession();
                String email = (String) session.getAttribute("email");
                if (email != null) {
                    UserDAO dao = new UserDAO();
                    boolean checkUpdate = dao.updatePassword(email, newPassword);
                    if (checkUpdate) {
                        session.invalidate();
                        url = SUCCESS;
                    } else {
                        url = ERROR;
                    }
                } else {
                    request.setAttribute("MESSAGE", "Session expired.");
                }
            }
        } catch (Exception e) {
            log("Error at ResetPasswordController: " + e.toString());
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
