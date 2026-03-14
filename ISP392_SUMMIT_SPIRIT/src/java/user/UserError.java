/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

/**
 *
 * @author gmt
 */
public class UserError {

    private String fullName;
    private String address;
    private String phone;
    private String email;
    private String role;
    private String password;
    private String confirm;
    private String errorMessage;

    public UserError() {
        this.fullName = "";
        this.address = "";
        this.phone = "";
        this.email = "";
        this.role = "";
        this.password = "";
        this.confirm = "";
        this.errorMessage = "";
    }

    public UserError(String fullName, String address, String phone, String email, String role, String password, String confirm, String errorMessage) {
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.password = password;
        this.confirm = confirm;
        this.errorMessage = errorMessage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
