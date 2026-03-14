/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.PaymentDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import utils.DBUtils;

/**
 *
 * @author gmt
 */
public class PaymentDAO {

    private static final String ADD_PAYMENT = "INSERT INTO Payment (OrderID, PaymentMethod, PaymentStatus, PaymentDate, TransactionCode) "
            + "VALUES (?, ?, ?, ?, ?)";

    public boolean addPayment(PaymentDTO paymentDTO) throws SQLException, ClassNotFoundException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(ADD_PAYMENT);
                ptm.setInt(1, paymentDTO.getOrderID());
                ptm.setString(2, paymentDTO.getPaymentMethod());
                ptm.setString(3, paymentDTO.getPaymentStatus());
                ptm.setDate(4, new java.sql.Date(paymentDTO.getPaymentDate().getTime()));
                ptm.setString(5, paymentDTO.getTransactionCode());
                check = ptm.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }
}
