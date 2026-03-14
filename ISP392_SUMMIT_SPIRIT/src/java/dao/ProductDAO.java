package dao;

import dto.ProductDTO;
import utils.DBUtils;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductDAO {

    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#.000");

    private static final String BASE_SELECT
            = "SELECT p.ProductID, p.ProductName, p.Description, "
            + "MIN(pv.Price) AS Price, MIN(pv.Quantity) AS Stock, "
            + "MIN(s.SizeName) AS Size, MIN(i.ImageURL) AS ImageURL, p.CateID ";

    private static final String BASE_FROM
            = "FROM Product p "
            + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
            + "LEFT JOIN Size s ON pv.SizeID = s.SizeID "
            + "LEFT JOIN ProductImage i ON p.ProductID = i.ProductID ";

    public List<ProductDTO> getProductsByCategorySorted(int cateID, String sortOrder)
            throws SQLException, ClassNotFoundException {
        List<ProductDTO> list = new ArrayList<>();
        String query = BASE_SELECT + BASE_FROM
                + "WHERE p.CateID = ? AND p.Status = 'Active' "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, p.CateID "
                + "ORDER BY Price " + ("asc".equalsIgnoreCase(sortOrder) ? "ASC" : "DESC");

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ptm = conn.prepareStatement(query)) {
            ptm.setInt(1, cateID);
            try ( ResultSet rs = ptm.executeQuery()) {
                while (rs.next()) {
                    list.add(extractProduct(rs));
                }
            }
        }
        return list;
    }

    public ProductDTO getFullProductByID(int productID)
            throws SQLException, ClassNotFoundException {
        ProductDTO dto = null;
        String sql = BASE_SELECT + BASE_FROM
                + "WHERE p.ProductID = ? AND p.Status = 'Active' "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, p.CateID";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ptm = conn.prepareStatement(sql)) {
            ptm.setInt(1, productID);
            try ( ResultSet rs = ptm.executeQuery()) {
                if (rs.next()) {
                    dto = extractProduct(rs);
                }
            }
        }
        return dto;
    }

    public List<ProductDTO> getTopNProductsByIDs(int[] ids)
            throws SQLException, ClassNotFoundException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = BASE_SELECT + BASE_FROM
                + "WHERE p.ProductID = ? AND p.Status = 'Active' "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, p.CateID";

        try ( Connection conn = DBUtils.getConnection()) {
            for (int id : ids) {
                try ( PreparedStatement ptm = conn.prepareStatement(sql)) {
                    ptm.setInt(1, id);
                    try ( ResultSet rs = ptm.executeQuery()) {
                        if (rs.next()) {
                            list.add(extractProduct(rs));
                        }
                    }
                }
            }
        }
        return list;
    }

    public List<ProductDTO> getTop3Products() throws SQLException, ClassNotFoundException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = "SELECT TOP 3 " + BASE_SELECT.substring(7) + BASE_FROM
                + "WHERE p.Status = 'Active' "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, p.CateID";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ptm = conn.prepareStatement(sql);  ResultSet rs = ptm.executeQuery()) {
            while (rs.next()) {
                list.add(extractProduct(rs));
            }
        }
        return list;
    }

    public List<ProductDTO> getProductsByCategoryDistinct(int cateID)
            throws SQLException, ClassNotFoundException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = "SELECT DISTINCT " + BASE_SELECT.substring(7) + BASE_FROM
                + "WHERE p.CateID = ? AND p.Status = 'Active'";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ptm = conn.prepareStatement(sql)) {
            ptm.setInt(1, cateID);
            try ( ResultSet rs = ptm.executeQuery()) {
                while (rs.next()) {
                    list.add(extractProduct(rs));
                }
            }
        }
        return list;
    }

    public int insertAndReturnID(ProductDTO product) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Product (ProductName, Description, CateID, Status) VALUES (?, ?, ?, ?)";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getDescription());
            ps.setInt(3, product.getCateID());
            ps.setString(4, product.getStatus());
            ps.executeUpdate();

            try ( ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public List<ProductDTO> getAllProducts() throws SQLException, ClassNotFoundException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.ProductName, c.CateName, p.status, "
                + "MIN(pv.Price) AS Price, SUM(pv.Quantity) AS Stock "
                + "FROM Product p "
                + "JOIN Category c ON p.CateID = c.CateID "
                + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "GROUP BY p.ProductID, p.ProductName, c.CateName, p.status";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ProductDTO p = new ProductDTO();
                p.setProductID(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setCateName(rs.getString("CateName"));
                p.setStatus(rs.getString("status")); // ⚠ đây là dòng thêm
                p.setPrice(rs.getDouble("Price"));
                p.setStock(rs.getInt("Stock"));
                list.add(p);
            }
        }
        return list;
    }

    public List<ProductDTO> getTopSalesFromCategory(int cateID, int limit)
            throws SQLException, ClassNotFoundException {
        List<ProductDTO> result = new ArrayList<>();
        String sql = "SELECT TOP (?) " + BASE_SELECT.substring(7) + BASE_FROM
                + "WHERE p.CateID = ? AND p.Sold > 0 AND p.Status = 'Active' "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, p.CateID "
                + "ORDER BY p.Sold DESC";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, cateID);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(extractProduct(rs));
                }
            }
        }
        return result;
    }

    private ProductDTO extractProduct(ResultSet rs) throws SQLException {
        ProductDTO dto = new ProductDTO();
        dto.setProductID(rs.getInt("ProductID"));
        dto.setProductName(rs.getString("ProductName"));
        dto.setDescription(rs.getString("Description"));
        dto.setPrice(Double.parseDouble(
        PRICE_FORMAT.format(rs.getDouble("Price")).replace(",", ".")
));
        dto.setSize(rs.getString("Size"));
        dto.setStock(rs.getInt("Stock"));
        dto.setCateID(rs.getInt("CateID"));
        String image = rs.getString("ImageURL");
        dto.setProductImage(image != null ? image : "default.jpg");
        return dto;
    }

    public boolean deleteProductByID(int productID) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Product SET status = 'inactive' WHERE productID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    public ProductDTO getProductByID(int productID) throws SQLException, ClassNotFoundException {
        String sql = "SELECT p.ProductID, p.ProductName, p.Description, p.Status, p.CateID, "
                + "(SELECT TOP 1 Price FROM ProductVariant WHERE ProductID = p.ProductID) AS Price, "
                + "(SELECT TOP 1 ImageURL FROM ProductImage WHERE ProductID = p.ProductID) AS ImageURL "
                + "FROM Product p WHERE p.ProductID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductDTO dto = new ProductDTO();
                    dto.setProductID(rs.getInt("ProductID"));
                    dto.setProductName(rs.getString("ProductName"));
                    dto.setDescription(rs.getString("Description"));
                    dto.setStatus(rs.getString("Status"));
                    dto.setCateID(rs.getInt("CateID"));
                    dto.setPrice(rs.getDouble("Price"));
                    String img = rs.getString("ImageURL");
                    dto.setProductImage(img != null ? img : "default.jpg");
                    return dto;
                }
            }
        }
        return null;
    }

    public boolean updateProductByID(ProductDTO product) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Product SET ProductName = ?, Description = ?, Status = ?, CateID = ? WHERE ProductID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getProductName());
            ps.setString(2, product.getDescription());
            ps.setString(3, product.getStatus());
            ps.setInt(4, product.getCateID());
            ps.setInt(5, product.getProductID());
            return ps.executeUpdate() > 0;
        }
    }

    public int getStockByProductID(int productID) throws SQLException, ClassNotFoundException {
        String sql = "SELECT SUM(Quantity) AS Stock FROM ProductVariant WHERE ProductID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Stock");
                }
            }
        }
        return 0;
    }

    public List<ProductDTO> getProductsByName(String keyword) throws SQLException, ClassNotFoundException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.ProductName, c.CateName, "
                + "MIN(pv.Price) AS Price, SUM(pv.Quantity) AS Stock "
                + "FROM Product p "
                + "JOIN Category c ON p.CateID = c.CateID "
                + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "WHERE p.ProductName LIKE ? "
                + "GROUP BY p.ProductID, p.ProductName, c.CateName";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductDTO p = new ProductDTO();
                    p.setProductID(rs.getInt("ProductID"));
                    p.setProductName(rs.getString("ProductName"));
                    p.setCateName(rs.getString("CateName"));
                    p.setPrice(rs.getDouble("Price"));
                    p.setStock(rs.getInt("Stock"));
                    list.add(p);
                }
            }
        }
        return list;
    }

    // 🔻🔻🔻 HÀM MỚI ĐƯỢC THÊM 🔻🔻🔻
    public List<ProductDTO> getSaleOffProducts() throws SQLException, ClassNotFoundException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = BASE_SELECT + BASE_FROM
                + "JOIN ProductVariant pv2 ON p.ProductID = pv2.ProductID "
                + "WHERE pv2.Discount > 0 AND p.Status = 'Active' "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, p.CateID";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractProduct(rs));
            }
        }
        return list;
    }

    public List<ProductDTO> get3FixedShirtsForSaleOff() throws SQLException, ClassNotFoundException {
        List<ProductDTO> list = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.ProductName, p.Description, "
                + "MIN(pv.Price) AS Price, MIN(pv.Quantity) AS Stock, "
                + "MIN(s.SizeName) AS Size, MIN(i.ImageURL) AS ImageURL, p.CateID "
                + "FROM Product p "
                + "JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "LEFT JOIN Size s ON pv.SizeID = s.SizeID "
                + "LEFT JOIN ProductImage i ON p.ProductID = i.ProductID "
                + "WHERE p.ProductID IN (?, ?, ?) AND p.Status = 'Active' "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, p.CateID";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, 13);  // ✅ Đổi sang 3 ProductID hợp lệ có trong DB
            ps.setInt(2, 14);
            ps.setInt(3, 15);

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductDTO dto = extractProduct(rs);
                    dto.setDiscount(20.0); // 👈 chỉ để hiển thị
                    list.add(dto);
                }
            }
        }
        return list;
    }

    // Bổ sung method mới lấy tồn kho theo biến thể size-color
    public int getStockByVariant(int productID, String size, String color) throws SQLException, ClassNotFoundException {
        ProductVariantDAO variantDAO = new ProductVariantDAO();
        return variantDAO.getAvailableQuantity(productID, size, color);
    }
    public boolean updatePriceAndQuantity(int productID, double price, int quantity)
        throws SQLException, ClassNotFoundException {

    String sql = "UPDATE ProductVariant SET Price = ?, Quantity = ? WHERE ProductID = ?";

    try (Connection conn = DBUtils.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setDouble(1, price);
        ps.setInt(2, quantity);
        ps.setInt(3, productID);

        return ps.executeUpdate() > 0;
    }
}
}
