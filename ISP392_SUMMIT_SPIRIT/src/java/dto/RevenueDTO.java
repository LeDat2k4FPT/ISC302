/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author Hanne
 */


public class RevenueDTO {
    private String categoryName;
    private int month;
    private int year;
    private double totalRevenue;
    private int totalQuantity;
    private String productName;
    private String colorName;

    public RevenueDTO() {
    }

    // Constructor 1: theo category và thời gian
    public RevenueDTO(String categoryName, int month, int year, double totalRevenue, int totalQuantity) {
        this.categoryName = categoryName;
        this.month = month;
        this.year = year;
        this.totalRevenue = totalRevenue;
        this.totalQuantity = totalQuantity;
    }

    // Constructor 2: dùng cho pie chart theo sản phẩm-color
    public RevenueDTO(String productName, String colorName, int totalQuantity) {
        this.productName = productName;
        this.colorName = colorName;
        this.totalQuantity = totalQuantity;
    }
// Constructor bổ sung để tạo dữ liệu mặc định khi không có doanh thu
public RevenueDTO(int month, double totalRevenue) {
    this.month = month;
    this.totalRevenue = totalRevenue;
}

    // Getters and Setters

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }
}