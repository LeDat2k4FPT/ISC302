package admin;

import dao.VoucherDAO;
import dto.VoucherDTO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ManageVoucherController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            VoucherDAO dao = new VoucherDAO();
            dao.deactivateExpiredVouchers();
            List<VoucherDTO> vouchers = dao.getActiveVouchers();
            request.setAttribute("vouchers", vouchers);
            String msg = request.getParameter("msg");
            String type = request.getParameter("type") != null ? request.getParameter("type") : "success";
            if (msg != null) {
                request.setAttribute("msg", msg);
                request.setAttribute("type", type);
            }
            request.setAttribute("page", "manageVoucher.jsp");
            request.getRequestDispatcher("admin/admin.jsp").forward(request, response);
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException(e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        VoucherDAO dao = new VoucherDAO();
        try {
            if ("add".equals(action)) {
                try {
                    VoucherDTO v = new VoucherDTO(
                        0,
                        request.getParameter("voucherCode"),
                        Double.parseDouble(request.getParameter("discountValue")),
                        java.sql.Date.valueOf(request.getParameter("expiryDate")),
                        request.getParameter("status")
                    );
                    boolean ok = dao.addVoucher(v);
                    if (ok) {
                        response.sendRedirect("ManageVoucherController?msg=Voucher+added+successfully&type=success");
                    } else {
                        response.sendRedirect("ManageVoucherController?msg=Failed+to+add+voucher&type=danger");
                    }
                } catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg != null && (msg.toLowerCase().contains("duplicate") || msg.toLowerCase().contains("unique"))) {
                        response.sendRedirect("ManageVoucherController?msg=Voucher+code+already+exists!+Please+choose+another+code.&type=danger");
                    } else {
                        response.sendRedirect("ManageVoucherController?msg=Error:+" + (msg != null ? msg.replaceAll("[\\r\\n]+", " ") : "Unknown error") + "&type=danger");
                    }
                }
                return;
            } else if ("update".equals(action)) {
                int voucherID = Integer.parseInt(request.getParameter("voucherID"));
                String status = request.getParameter("status");
                dao.updateVoucherStatus(voucherID, status);
            } else if ("delete".equals(action)) {
                int voucherID = Integer.parseInt(request.getParameter("voucherID"));
                dao.updateVoucherStatus(voucherID, "Inactive");
                response.sendRedirect("ManageVoucherController?msg=Voucher+deleted+successfully&type=success");
                return;
            }
            response.sendRedirect("ManageVoucherController");
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException(e);
        }
    }
} 