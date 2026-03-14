package controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    // LÊ ĐẠT LEADER
    private static final String WELCOME = "login.jsp";
    private static final String LOGIN = "Login";
    private static final String LOGIN_CONTROLLER = "LoginController";
    private static final String LOGOUT = "Logout";
    private static final String LOGOUT_CONTROLLER = "LogoutController";
    private static final String EDIT_PROFILE = "EditProfile";
    private static final String EDIT_PROFILE_CONTROLLER = "EditProfileController";
    private static final String HOME = "Home";
    private static final String HOME_CONTROLLER = "HomeController";
    private static final String FORGOT_PASSWORD = "ForgotPassword";
    private static final String FORGOT_PASSWORD_CONTROLLER = "ForgotPasswordController";
    private static final String CREATE_USER = "CreateUser";
    private static final String CREATE_USER_CONTROLLER = "CreateUserController";
    private static final String VERIFY_ACCOUNT = "VerifyAccount";
    private static final String VERIFY_ACCOUNT_CONTROLLER = "VerifyAccountController";
    private static final String CHANGE_PASSWORD = "ChangePassword";
    private static final String CHANGE_PASSWORD_CONTROLLER = "ChangePasswordController";
    private static final String VERIFY_OTP = "VerifyOtp";
    private static final String VERIFY_OTP_CONTROLLER = "VerifyOtpController";
    private static final String RESET_PASSWORD = "ResetPassword";
    private static final String RESET_PASSWORD_CONTROLLER = "ResetPasswordController";
    private static final String MANAGE_USER_ACCOUNT = "ManageUserAccount";
    private static final String MANAGE_USER_ACCOUNT_CONTROLLER = "ManageUserAccountController";
    private static final String VIEW_SALE_OFF = "viewSaleOff";
    private static final String VIEW_SALE_OFF_CONTROLLER = "ViewSaleOffController";
    private static final String PRODUCT_LIST = "ProductList";
    private static final String PRODUCT_LIST_CONTROLLER = "ProductListController";
    private static final String ADD_TO_CART = "AddToCart";
    private static final String ADD_TO_CART_CONTROLLER = "AddToCartServlet";
    private static final String APPLY_DISCOUNT = "ApplyDiscount";
    private static final String APPLY_DISCOUNT_CONTROLLER = "ApplyDiscountServlet";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String url = WELCOME;
        try {
            String action = request.getParameter("action");
            if (action == null) {
                url = WELCOME;
            } else if (LOGIN.equals(action)) {
                url = LOGIN_CONTROLLER;
            } else if (LOGOUT.equals(action)) {
                url = LOGOUT_CONTROLLER;
            } else if (EDIT_PROFILE.equals(action)) {
                url = EDIT_PROFILE_CONTROLLER;
            } else if (HOME.equals(action)) {
                url = HOME_CONTROLLER;
            } else if (FORGOT_PASSWORD.equals(action)) {
                url = FORGOT_PASSWORD_CONTROLLER;
            } else if (CREATE_USER.equals(action)) {
                url = CREATE_USER_CONTROLLER;
            } else if (VERIFY_ACCOUNT.equals(action)) {
                url = VERIFY_ACCOUNT_CONTROLLER;
            } else if (CHANGE_PASSWORD.equals(action)) {
                url = CHANGE_PASSWORD_CONTROLLER;
            } else if (VERIFY_OTP.equals(action)) {
                url = VERIFY_OTP_CONTROLLER;
            } else if (RESET_PASSWORD.equals(action)) {
                url = RESET_PASSWORD_CONTROLLER;
            } else if (MANAGE_USER_ACCOUNT.equals(action)) {
                url = MANAGE_USER_ACCOUNT_CONTROLLER;
            } else if ("GoToShipping".equals(action)) {
                url = "GoToShippingServlet";
            } else if (VIEW_SALE_OFF.equals(action)) {
                url = VIEW_SALE_OFF_CONTROLLER;
            } else if (ADD_TO_CART.equals(action)) {
                url = ADD_TO_CART_CONTROLLER;
            } else if (APPLY_DISCOUNT.equals(action)) {
                url = APPLY_DISCOUNT_CONTROLLER;
            } else if (PRODUCT_LIST.equals(action)) {
                url = PRODUCT_LIST_CONTROLLER;
            }
        } catch (Exception e) {
            log("error at MainController: " + e.toString());
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
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

    @Override
    public String getServletInfo() {
        return "MainController for routing actions";
    }
}
