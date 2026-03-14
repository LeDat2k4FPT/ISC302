package dto;

public class VoucherDTO {

    private int voucherID;
    private String voucherCode;
    private double discountValue;
    private java.sql.Date expiryDate;
    private String status;

    public VoucherDTO() {
    }

    public VoucherDTO(int voucherID, String voucherCode, double discountValue, java.sql.Date expiryDate, String status) {
        this.voucherID = voucherID;
        this.voucherCode = voucherCode;
        this.discountValue = discountValue;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public int getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(int voucherID) {
        this.voucherID = voucherID;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public java.sql.Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(java.sql.Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
