package com.vnpay.common;

import dao.OrderDAO;
import dao.OrderDetailDAO;
import dao.PaymentDAO;
import dto.OrderDTO;
import dto.OrderDetailDTO;
import dto.PaymentDTO;
import dto.CartDTO;
import dto.CartItemDTO;
import dto.ProductDTO;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VnpayReturn extends HttpServlet {

    private final OrderDAO orderDao = new OrderDAO();
    private final OrderDetailDAO detailDao = new OrderDetailDAO();
    private final PaymentDAO paymentDao = new PaymentDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("==== VNPay Return ====");

        try {

            Map<String, String> fields = new HashMap<>();

            for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);

                if (fieldValue != null && !fieldValue.isEmpty()) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = request.getParameter("vnp_SecureHash");

            fields.remove("vnp_SecureHashType");
            fields.remove("vnp_SecureHash");

            String signValue = Config.hashAllFields(fields);

            if (!signValue.equals(vnp_SecureHash)) {

                request.setAttribute("paymentResult", "invalid");
                request.setAttribute("retryLink",
                        request.getContextPath() + "/user/checkout.jsp?retry=true");

                request.getRequestDispatcher("/user/paymentResult.jsp")
                        .forward(request, response);
                return;
            }

            String responseCode = request.getParameter("vnp_ResponseCode");
            String fullTxnRef = request.getParameter("vnp_TxnRef");
            String transactionNo = request.getParameter("vnp_TransactionNo");

            long amount = Long.parseLong(request.getParameter("vnp_Amount")) / 100;

            String cardType = request.getParameter("vnp_CardType");

            int orderId;

            try {

                String[] parts = fullTxnRef.split("-");
                orderId = Integer.parseInt(parts[0]);

                System.out.println("Parsed orderId: " + orderId);

            } catch (Exception ex) {

                request.setAttribute("paymentResult", "error");
                request.setAttribute("retryLink",
                        request.getContextPath() + "/user/checkout.jsp?retry=true");

                request.getRequestDispatcher("/user/paymentResult.jsp")
                        .forward(request, response);
                return;
            }

            HttpSession session = request.getSession();

            Object sessionObj = session.getAttribute("PENDING_ORDER");

            if (!(sessionObj instanceof Map)) {

                request.setAttribute("paymentResult", "error");
                request.setAttribute("retryLink",
                        request.getContextPath() + "/user/checkout.jsp?retry=true");

                request.getRequestDispatcher("/user/paymentResult.jsp")
                        .forward(request, response);
                return;
            }

            Map<String, Object> orderSessionData = (Map<String, Object>) sessionObj;

            String checkoutType = (String) orderSessionData.get("checkoutType");

            PaymentDTO paymentDTO = new PaymentDTO();

            paymentDTO.setOrderID(orderId);
            paymentDTO.setPaymentMethod(cardType);
            paymentDTO.setPaymentStatus("00".equals(responseCode) ? "Paid" : "Unpaid");
            paymentDTO.setPaymentDate(new java.sql.Date(System.currentTimeMillis()));
            paymentDTO.setTransactionCode(transactionNo);

            paymentDao.addPayment(paymentDTO);

            if ("00".equals(responseCode)) {

                OrderDTO order = new OrderDTO();

                order.setOrderID(orderId);
                order.setStatus("Processing");
                order.setNote("");

                boolean updated = orderDao.updateOrderAfterPayment(order);

                if (updated) {

                    if ("BUY_NOW".equals(checkoutType)) {

                        int productId = Integer.parseInt(
                                String.valueOf(orderSessionData.get("productId")));

                        int quantity = Integer.parseInt(
                                String.valueOf(orderSessionData.get("quantity")));

                        String size = (String) orderSessionData.get("size");
                        String color = (String) orderSessionData.get("color");

                        int attributeId;

                        if ((size == null || size.isEmpty())
                                && (color == null || color.isEmpty())) {

                            attributeId = detailDao.getAttributeIdByProduct(productId);

                        } else if (size == null || size.isEmpty()) {

                            attributeId = detailDao
                                    .getAttributeIdByProductColor(productId, color);

                        } else if (color == null || color.isEmpty()) {

                            attributeId = detailDao
                                    .getAttributeIdByProductSize(productId, size);

                        } else {

                            attributeId = detailDao
                                    .getAttributeIdByProductSizeColor(productId, color, size);
                        }

                        double unitPrice = detailDao
                                .getUnitPriceByAttributeId(attributeId);

                        OrderDetailDTO detail
                                = new OrderDetailDTO(orderId, attributeId, quantity, unitPrice);

                        detailDao.addOrderDetail(detail);

                        detailDao.updateProductVariantQuantity(attributeId, quantity);

                    } else if ("CART".equals(checkoutType)) {

                        Object cartObj = session.getAttribute("CART");

                        if (cartObj instanceof CartDTO) {

                            CartDTO cart = (CartDTO) cartObj;

                            for (CartItemDTO item : cart.getCartItems()) {

                                ProductDTO product = item.getProduct();

                                int attributeId
                                        = detailDao.getAttributeIdByProductSizeColor(
                                                product.getProductID(),
                                                product.getColor(),
                                                product.getSize());

                                double unitPrice
                                        = detailDao.getUnitPriceByAttributeId(attributeId);

                                OrderDetailDTO detail
                                        = new OrderDetailDTO(orderId, attributeId,
                                                item.getQuantity(), unitPrice);

                                detailDao.addOrderDetail(detail);

                                detailDao.updateProductVariantQuantity(
                                        attributeId, item.getQuantity());
                            }

                            session.removeAttribute("CART");
                        }
                    }

                    session.removeAttribute("PENDING_ORDER");
                    session.removeAttribute("TEMP_ORDER_ID");

                    request.setAttribute("paymentResult", "success");
                    request.setAttribute("transactionId", transactionNo);
                    request.setAttribute("amount",
                            String.format("%,.0f", (double) amount));
                    request.setAttribute("orderInfo",
                            request.getParameter("vnp_OrderInfo"));

                } else {

                    request.setAttribute("paymentResult", "error");
                    request.setAttribute("retryLink",
                            request.getContextPath()
                                    + "/user/checkout.jsp?retry=true");
                }

            } else {

                OrderDTO order = new OrderDTO();

                order.setOrderID(orderId);
                order.setStatus("Failed");
                order.setNote("Order payment failed");

                orderDao.updateOrderStatusAndNote(order);

                request.setAttribute("paymentResult", "failed");
                request.setAttribute("retryLink",
                        request.getContextPath()
                                + "/user/checkout.jsp?retry=true");
            }

        } catch (Exception e) {

            Logger.getLogger(VnpayReturn.class.getName())
                    .log(Level.SEVERE, null, e);

            request.setAttribute("paymentResult", "error");

            request.setAttribute("retryLink",
                    request.getContextPath()
                            + "/user/checkout.jsp?retry=true");
        }

        request.getRequestDispatcher("/user/paymentResult.jsp")
                .forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Handles VNPay return and creates order";
    }
}