/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

import java.sql.Date;

/**
 *
 * @author gmt
 */
public class PaymentDTO {

    private int paymentID;
    private int orderID;
    private String paymentMethod;
    private String paymentStatus;
    private Date paymentDate;
    private String transactionCode;

    public PaymentDTO() {
    }

    public PaymentDTO(int paymentID, int orderID, String paymentMethod, String paymentStatus, Date paymentDate, String transactionCode) {
        this.paymentID = paymentID;
        this.orderID = orderID;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.transactionCode = transactionCode;
    }

    public int getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(int paymentID) {
        this.paymentID = paymentID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }
}
