package dto;

import java.sql.Date;
import java.sql.Timestamp;

public class OrderDTO {

    private int orderID;
    private int userID;
    private Date orderDate;
    private double totalAmount;
    private String status;
    private double shipFee;
    private Integer voucherID;
    private String note;
    private String paymentStatus;
    private Timestamp paymentDate;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String shippingAddress;
    private String recipientName;
    private String recipientPhone;
    private String recipientEmail;
    private String discountCode;
    private double discountPercent;

    public OrderDTO() {
    }

    public OrderDTO(int orderID, int userID, Date orderDate, double totalAmount, String status) {
        this.orderID = orderID;
        this.userID = userID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public OrderDTO(int orderID, int userID, Date orderDate, double totalAmount, String status, String paymentStatus, Timestamp paymentDate) {
        this.orderID = orderID;
        this.userID = userID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    public OrderDTO(int orderID, Date orderDate, double totalAmount, String status, String fullName) {
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.fullName = fullName;
    }

    public OrderDTO(int orderID, int userID, Date orderDate, double totalAmount, String status, double shipFee, Integer voucherID, String paymentStatus, Timestamp paymentDate, String fullName, String email, String phoneNumber, String shippingAddress, String recipientName, String recipientPhone, String recipientEmail, String discountCode, double discountPercent) {
        this.orderID = orderID;
        this.userID = userID;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shipFee = shipFee;
        this.voucherID = voucherID;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.shippingAddress = shippingAddress;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.recipientEmail = recipientEmail;
        this.discountCode = discountCode;
        this.discountPercent = discountPercent;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getShipFee() {
        return shipFee;
    }

    public void setShipFee(double shipFee) {
        this.shipFee = shipFee;
    }

    public Integer getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(Integer voucherID) {
        this.voucherID = voucherID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }
}
