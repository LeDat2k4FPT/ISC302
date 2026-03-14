package dto;

public class ProductVariantDTO {

    private int attributeID;  // ID duy nhất cho mỗi biến thể
    private int productID;
    private Integer colorID;
    private Integer sizeID;
    private double price;
    private int quantity;

    private String colorName;
    private String sizeName;

    // ========== Constructors ==========
    public ProductVariantDTO() {
    }

    public ProductVariantDTO(int productID, Integer colorID, Integer sizeID, double price, int quantity) {
        this.productID = productID;
        this.colorID = colorID;
        this.sizeID = sizeID;
        this.price = price;
        this.quantity = quantity;
    }

    public ProductVariantDTO(int attributeID, int productID, Integer colorID, Integer sizeID, double price, int quantity) {
        this.attributeID = attributeID;
        this.productID = productID;
        this.colorID = colorID;
        this.sizeID = sizeID;
        this.price = price;
        this.quantity = quantity;
    }

    public int getAttributeID() {
        return attributeID;
    }

    public void setAttributeID(int attributeID) {
        this.attributeID = attributeID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public Integer getColorID() {
        return colorID;
    }

    public void setColorID(Integer colorID) {
        this.colorID = colorID;
    }

    public Integer getSizeID() {
        return sizeID;
    }

    public void setSizeID(Integer sizeID) {
        this.sizeID = sizeID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public String getSizeName() {
        return sizeName;
    }

    public void setSizeName(String sizeName) {
        this.sizeName = sizeName;
    }

}
