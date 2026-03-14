package dto;

public class OrderDetailDTO {

    private int orderID;
    private int productID;
    private String productName;
    private String sizeName;
    private String colorName;
    private int quantity;
    private int AttributeID;
    private double unitPrice;
    private boolean fromSaleOff;

    public OrderDetailDTO() {
    }

    public OrderDetailDTO(int orderID, int productID, String productName, String sizeName, String colorName, int quantity, int AttributeID, double unitPrice) {
        this.orderID = orderID;
        this.productID = productID;
        this.productName = productName;
        this.sizeName = sizeName;
        this.colorName = colorName;
        this.quantity = quantity;
        this.AttributeID = AttributeID;
        this.unitPrice = unitPrice;
    }

    public OrderDetailDTO(int orderID, int AttributeID, int quantity, double unitPrice) {
        this.orderID = orderID;
        this.AttributeID = AttributeID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAttributeID() {
        return AttributeID;
    }

    public void setAttributeID(int AttributeID) {
        this.AttributeID = AttributeID;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean isFromSaleOff() {
        return fromSaleOff;
    }

    public void setFromSaleOff(boolean fromSaleOff) {
        this.fromSaleOff = fromSaleOff;
    }
}
