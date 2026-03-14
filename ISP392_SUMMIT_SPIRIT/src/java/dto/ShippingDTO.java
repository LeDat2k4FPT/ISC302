/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author Hanne
 */

import java.sql.Timestamp;

public class ShippingDTO {
    private int shippingID;
    private int orderID;
    private int userID;
    private Timestamp deliveryTime;
    private String deliveryImageURL;
    private String note;

    // Optional: thêm orderStatus để hiển thị nếu dùng JOIN Orders
    private String orderStatus;
    private String shipperName;

    public ShippingDTO() {
    }

    public ShippingDTO(int shippingID, int orderID, int userID, Timestamp deliveryTime, String deliveryImageURL, String note) {
        this.shippingID = shippingID;
        this.orderID = orderID;
        this.userID = userID;
        this.deliveryTime = deliveryTime;
        this.deliveryImageURL = deliveryImageURL;
        this.note = note;
    }

    // Getters and Setters
    public int getShippingID() {
        return shippingID;
    }

    public void setShippingID(int shippingID) {
        this.shippingID = shippingID;
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

    public Timestamp getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Timestamp deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryImageURL() {
        return deliveryImageURL;
    }

    public void setDeliveryImageURL(String deliveryImageURL) {
        this.deliveryImageURL = deliveryImageURL;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }
}

