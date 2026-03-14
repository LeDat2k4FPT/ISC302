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
@WebServlet(name = "VerifyAccountController", urlPatterns = {"/VerifyAccountController"})
public class VerifyAccountController extends HttpServlet {

    private static final String ERROR = "user/verifyAccount.jsp";
    private static final String SUCCESS = "user/homepage.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = ERROR;
        try {
            String userOtp = request.getParameter("otp");
            HttpSession session = request.getSession();
            String sessionOtp = (String) session.getAttribute("OTP");
            UserDTO pendingUser = (UserDTO) session.getAttribute("PENDING_USER");
            if (userOtp != null && userOtp.equals(sessionOtp) && pendingUser != null) {
                UserDAO dao = new UserDAO();
                boolean checkCreate = dao.create(pendingUser);
                if (checkCreate) {
                    session.removeAttribute("OTP");
                    session.removeAttribute("PENDING_USER");
                    UserDTO loginUser = dao.checkLogin(pendingUser.getEmail(), pendingUser.getPassword());
                    session.setAttribute("LOGIN_USER", loginUser);
                    url = SUCCESS;
                }
            } else {
                request.setAttribute("MESSAGE", "Invalid OTP!");
                url = ERROR;
            }
        } catch (Exception e) {
            log("Error at VerifyAccountController: " + e.toString());
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
