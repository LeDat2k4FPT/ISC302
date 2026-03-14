package dto;

public class ProductImageDTO {
    private int imageID;          // Optional nếu auto-increment
    private String imageURL;
    private int productID;

    public ProductImageDTO() {
    }

    // Constructor dùng khi thêm ảnh mới (không cần imageID nếu để DB tự tăng)
    public ProductImageDTO(String imageURL, int productID) {
        this.imageURL = imageURL;
        this.productID = productID;
    }

    // Constructor đầy đủ
    public ProductImageDTO(int imageID, String imageURL, int productID) {
        this.imageID = imageID;
        this.imageURL = imageURL;
        this.productID = productID;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }
}
