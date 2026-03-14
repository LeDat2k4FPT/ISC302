package dto;

public class UserDTO {

    private int userID;
    private String fullName;
    private String address;
    private String password;
    private String phone;
    private String email;
    private String role;

    public UserDTO() {
        this.userID = 0;
        this.fullName = "";
        this.address = "";
        this.password = "";
        this.phone = "";
        this.email = "";
        this.role = "";
    }

    public UserDTO(int userID, String fullName, String address, String password, String phone, String email, String role) {
        this.userID = userID;
        this.fullName = fullName;
        this.address = address;
        this.password = password;
        this.phone = phone;
        this.email = email;
        this.role = role;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
