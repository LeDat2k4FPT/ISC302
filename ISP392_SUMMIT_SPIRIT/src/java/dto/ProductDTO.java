package dto;

public class ProductDTO {

    private int productID;
    private String productName;
    private String productImage;
    private String description;
    private String size;
    private String color;
    private double price;
    private double discount; // ✅ Thêm thuộc tính discount
    private String status;
    private int stock;
    private int cateID;
    private String cateName;
    private int quantity = 1;
    private boolean fromSaleOff = false; // ✅ Thêm thuộc tính này
    private int attributeID;

    public ProductDTO() {
    }

    public ProductDTO(int productID, String productName, String productImage, String description,
            String size, double price, String status, int stock, int cateID) {
        this.productID = productID;
        this.productName = productName;
        this.productImage = productImage;
        this.description = description;
        this.size = size;
        this.price = price;
        this.status = status;
        this.stock = stock;
        this.cateID = cateID;
    }

    public ProductDTO(int productID, String productName, String description, int cateID, String status) {
        this.productID = productID;
        this.productName = productName;
        this.description = description;
        this.cateID = cateID;
        this.status = status;
    }

    public ProductDTO(int productID, String productName, String cateName, double price, int stock, String color) {
        this.productID = productID;
        this.productName = productName;
        this.cateName = cateName;
        this.price = price;
        this.stock = stock;
        this.color = color;
    }

    public ProductDTO(int aInt, String string, double aDouble, String string0) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
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

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCateID() {
        return cateID;
    }

    public void setCateID(int cateID) {
        this.cateID = cateID;
    }

    // ✅ Getter/Setter for fromSaleOff
    public boolean isFromSaleOff() {
        return fromSaleOff;
    }

    public void setFromSaleOff(boolean fromSaleOff) {
        this.fromSaleOff = fromSaleOff;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getAttributeID() {
        return attributeID;
    }

    public void setAttributeID(int attributeID) {
        this.attributeID = attributeID;
    }
    
    

    // === COPY CONSTRUCTOR: Tạo đối tượng ProductDTO mới dựa trên 1 ProductDTO khác ===
    public ProductDTO(ProductDTO other) {
        this.productID = other.productID;
        this.productName = other.productName;
        this.productImage = other.productImage;
        this.description = other.description;
        this.size = other.size;
        this.color = other.color;
        this.price = other.price;
        this.discount = other.discount;
        this.status = other.status;
        this.stock = other.stock;
        this.cateID = other.cateID;
        this.cateName = other.cateName;
        this.quantity = other.quantity;
        this.fromSaleOff = other.fromSaleOff;
        this.attributeID = other.attributeID;
    }
}
