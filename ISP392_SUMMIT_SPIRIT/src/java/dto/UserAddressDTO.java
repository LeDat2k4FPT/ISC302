/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author gmt
 */
public class UserAddressDTO {

    private int infoID;
    private int orderID;
    private String country;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String district;
    private String city;

    public UserAddressDTO() {
    }

    public UserAddressDTO(int infoID, int orderID, String country, String fullName, String phone, String email, String address, String district, String city) {
        this.infoID = infoID;
        this.orderID = orderID;
        this.country = country;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.district = district;
        this.city = city;
    }

    public int getInfoID() {
        return infoID;
    }

    public void setInfoID(int infoID) {
        this.infoID = infoID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
