package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import utils.DBUtils;
import dto.ProductVariantDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ProductVariantDAO {

    public void insertVariant(ProductVariantDTO variant) throws SQLException, ClassNotFoundException {
        System.out.println("[DEBUG] Insert variant: " + variant);
        String sql = "INSERT INTO ProductVariant (ProductID, ColorID, SizeID, Price, Quantity) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, variant.getProductID());

            // colorID
            if (variant.getColorID() != null) {
                ps.setInt(2, variant.getColorID());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            // sizeID
            if (variant.getSizeID() != null) {
                ps.setInt(3, variant.getSizeID());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }

            ps.setDouble(4, variant.getPrice());
            ps.setInt(5, variant.getQuantity());

            ps.executeUpdate();
        }
    }

    public int getNextAttributeID() throws SQLException, ClassNotFoundException {
        String sql = "SELECT ISNULL(MAX(AttributeID), 0) + 1 FROM ProductVariant";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 1;
    }

    public void deleteByProductID(int productID) throws Exception {
        String sql = "DELETE FROM ProductVariant WHERE ProductID = ?";
        try (Connection con = DBUtils.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productID);
            ps.executeUpdate();
        }
    }

    public List<String> getAvailableSizesByProductId(int productId) throws SQLException, ClassNotFoundException {
        List<String> sizes = new ArrayList<>();
        String sql = "SELECT DISTINCT s.SizeName "
                + "FROM ProductVariant pv "
                + "JOIN Size s ON pv.SizeID = s.SizeID "
                + "WHERE pv.ProductID = ?";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sizes.add(rs.getString("SizeName"));
                }
            }
        }
        return sizes;
    }

    public List<String> getAvailableColorsByProductId(int productId) throws SQLException, ClassNotFoundException {
        List<String> colors = new ArrayList<>();
        String sql = "SELECT DISTINCT c.ColorName "
                + "FROM ProductVariant pv "
                + "JOIN Color c ON pv.ColorID = c.ColorID "
                + "WHERE pv.ProductID = ?";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    colors.add(rs.getString("ColorName"));
                }
            }
        }
        return colors;
    }

    public Map<Integer, List<ProductVariantDTO>> getAllVariantsGroupedByProduct() throws SQLException, ClassNotFoundException {
        Map<Integer, List<ProductVariantDTO>> map = new HashMap<>();
        String sql = "SELECT pv.AttributeID, pv.ProductID, pv.ColorID, pv.SizeID, pv.Price, pv.Quantity, c.ColorName, s.SizeName "
                + "FROM ProductVariant pv "
                + "LEFT JOIN Color c ON pv.ColorID = c.ColorID "
                + "LEFT JOIN Size s ON pv.SizeID = s.SizeID";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ProductVariantDTO variant = new ProductVariantDTO();
                variant.setAttributeID(rs.getInt("AttributeID"));
                variant.setProductID(rs.getInt("ProductID"));
                variant.setColorID(rs.getInt("ColorID"));
                variant.setSizeID(rs.getInt("SizeID"));
                variant.setPrice(rs.getDouble("Price"));
                variant.setQuantity(rs.getInt("Quantity"));
                variant.setColorName(rs.getString("ColorName"));
                variant.setSizeName(rs.getString("SizeName"));
                int productId = variant.getProductID();
                map.computeIfAbsent(productId, k -> new ArrayList<>()).add(variant);
            }
        }
        return map;
    }

    public List<ProductVariantDTO> getVariantsByProductId(int productId) throws SQLException, ClassNotFoundException {
        List<ProductVariantDTO> list = new ArrayList<>();
        String sql = "SELECT pv.AttributeID, pv.ProductID, pv.ColorID, pv.SizeID, pv.Price, pv.Quantity, c.ColorName, s.SizeName "
                + "FROM ProductVariant pv "
                + "LEFT JOIN Color c ON pv.ColorID = c.ColorID "
                + "LEFT JOIN Size s ON pv.SizeID = s.SizeID "
                + "WHERE pv.ProductID = ?";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductVariantDTO variant = new ProductVariantDTO();
                    variant.setAttributeID(rs.getInt("AttributeID"));
                    variant.setProductID(rs.getInt("ProductID"));
                    variant.setColorID(rs.getInt("ColorID"));
                    variant.setSizeID(rs.getInt("SizeID"));
                    variant.setPrice(rs.getDouble("Price"));
                    variant.setQuantity(rs.getInt("Quantity"));
                    variant.setColorName(rs.getString("ColorName"));
                    variant.setSizeName(rs.getString("SizeName"));
                    list.add(variant);
                }
            }
        }
        return list;
    }

    public void deleteVariantByID(int attributeID) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM ProductVariant WHERE AttributeID=?";
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attributeID);
            ps.executeUpdate();
        }
    }

//    public void updateVariant(ProductVariantDTO variant) throws SQLException, ClassNotFoundException {
//        System.out.println("[DEBUG] Update variant: " + variant);
//        String sql;
//        if (variant.getSizeID() > 0 && variant.getColorID() > 0) {
//            sql = "UPDATE ProductVariant SET ColorID = ?, SizeID = ?, Price = ?, Quantity = ? WHERE AttributeID = ?";
//        } else {
//            sql = "UPDATE ProductVariant SET Price = ?, Quantity = ? WHERE AttributeID = ?";
//        }
//
//        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
//            if (variant.getSizeID() > 0 && variant.getColorID() > 0) {
//                ps.setInt(1, variant.getColorID());
//                ps.setInt(2, variant.getSizeID());
//                ps.setDouble(3, variant.getPrice());
//                ps.setInt(4, variant.getQuantity());
//                ps.setInt(5, variant.getAttributeID());
//            } else {
//                ps.setDouble(1, variant.getPrice());
//                ps.setInt(2, variant.getQuantity());
//                ps.setInt(3, variant.getAttributeID());
//            }
//            int affected = ps.executeUpdate();
//            if (affected == 0) {
//                insertVariant(variant);
//            }
//        }
//    }
    public boolean updateVariant(ProductVariantDTO v) throws Exception {

        String sql = "UPDATE ProductVariant SET Quantity=?, Price=? WHERE AttributeID=?";

        try (Connection con = DBUtils.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, v.getQuantity());
            ps.setDouble(2, v.getPrice());
            ps.setInt(3, v.getAttributeID());

            int rows = ps.executeUpdate();

            System.out.println("Variant updated rows = " + rows);

            return rows > 0;
        }
    }

    // ==== Sửa hàm lấy tồn kho cho trường hợp size hoặc color có thể NULL ====
    public int getAvailableQuantity(int productId, String sizeName, String colorName) throws SQLException, ClassNotFoundException {
        int quantity = 0;
        String sql = null;
        if ((sizeName == null || sizeName.isEmpty()) && (colorName == null || colorName.isEmpty())) {
            sql = "SELECT Quantity FROM ProductVariant WHERE ProductID = ? AND SizeID IS NULL AND ColorID IS NULL";
        } else if (sizeName == null || sizeName.isEmpty()) {
            sql = "SELECT pv.Quantity FROM ProductVariant pv JOIN Color c ON pv.ColorID = c.ColorID WHERE pv.ProductID = ? AND c.ColorName = ? AND pv.SizeID IS NULL";
        } else if (colorName == null || colorName.isEmpty()) {
            sql = "SELECT pv.Quantity FROM ProductVariant pv JOIN Size s ON pv.SizeID = s.SizeID WHERE pv.ProductID = ? AND s.SizeName = ? AND pv.ColorID IS NULL";
        } else {
            sql = "SELECT pv.Quantity FROM ProductVariant pv JOIN Size s ON pv.SizeID = s.SizeID JOIN Color c ON pv.ColorID = c.ColorID WHERE pv.ProductID = ? AND s.SizeName = ? AND c.ColorName = ?";
        }
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            if ((sizeName == null || sizeName.isEmpty()) && (colorName == null || colorName.isEmpty())) {
                // only productId param
            } else if (sizeName == null || sizeName.isEmpty()) {
                ps.setString(2, colorName);
            } else if (colorName == null || colorName.isEmpty()) {
                ps.setString(2, sizeName);
            } else {
                ps.setString(2, sizeName);
                ps.setString(3, colorName);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    quantity = rs.getInt("Quantity");
                }
            }
        }
        return quantity;
    }

    // ==== Sửa hàm lấy giá cho trường hợp size hoặc color có thể NULL ====
    public double getPriceByVariant(int productId, String sizeName, String colorName) throws SQLException, ClassNotFoundException {
        double price = 0;
        String sql = null;
        if ((sizeName == null || sizeName.isEmpty()) && (colorName == null || colorName.isEmpty())) {
            sql = "SELECT Price FROM ProductVariant WHERE ProductID = ? AND SizeID IS NULL AND ColorID IS NULL";
        } else if (sizeName == null || sizeName.isEmpty()) {
            sql = "SELECT pv.Price FROM ProductVariant pv JOIN Color c ON pv.ColorID = c.ColorID WHERE pv.ProductID = ? AND c.ColorName = ? AND pv.SizeID IS NULL";
        } else if (colorName == null || colorName.isEmpty()) {
            sql = "SELECT pv.Price FROM ProductVariant pv JOIN Size s ON pv.SizeID = s.SizeID WHERE pv.ProductID = ? AND s.SizeName = ? AND pv.ColorID IS NULL";
        } else {
            sql = "SELECT pv.Price FROM ProductVariant pv JOIN Size s ON pv.SizeID = s.SizeID JOIN Color c ON pv.ColorID = c.ColorID WHERE pv.ProductID = ? AND s.SizeName = ? AND c.ColorName = ?";
        }
        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            if ((sizeName == null || sizeName.isEmpty()) && (colorName == null || colorName.isEmpty())) {
                // only productId param
            } else if (sizeName == null || sizeName.isEmpty()) {
                ps.setString(2, colorName);
            } else if (colorName == null || colorName.isEmpty()) {
                ps.setString(2, sizeName);
            } else {
                ps.setString(2, sizeName);
                ps.setString(3, colorName);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    price = rs.getDouble("Price");
                }
            }
        }
        return price;
    }
}
