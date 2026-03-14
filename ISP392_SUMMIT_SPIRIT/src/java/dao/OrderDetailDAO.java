package dao;

import dto.OrderDetailDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

public class OrderDetailDAO {

    private static final String GET_ORDER_DETAILS = "SELECT * FROM OrderDetail WHERE OrderID = ?";
    private static final String CREATE_ORDER_DETAIL = "INSERT INTO OrderDetail (OrderID, ProductID, Quantity) VALUES (?, ?, ?)";
    private static final String DELETE_ORDER_DETAIL = "DELETE FROM OrderDetail WHERE OrderID = ? AND ProductID = ?";
    private static final String UPDATE_ORDER_DETAIL = "UPDATE OrderDetail SET Quantity = ? WHERE OrderID = ? AND ProductID = ?";
    private static final String ADD_ORDER_DETAIL = "INSERT INTO OrderDetail (OrderID, AttributeID, Quantity, UnitPrice)"
            + "VALUES (?, ?, ?, ?)";

    public List<OrderDetailDTO> getOrderDetails(int orderID) throws SQLException {
        List<OrderDetailDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(GET_ORDER_DETAILS);
                ptm.setInt(1, orderID);
                rs = ptm.executeQuery();
                while (rs.next()) {
                    OrderDetailDTO od = new OrderDetailDTO();
                    od.setOrderID(orderID); // từ tham số method
                    od.setProductID(rs.getInt("ProductID"));
                    od.setProductName(rs.getString("ProductName"));
                    od.setSizeName(rs.getString("SizeName"));
                    od.setColorName(rs.getString("ColorName"));
                    od.setQuantity(rs.getInt("Quantity"));
                    od.setUnitPrice(rs.getDouble("UnitPrice"));
                    list.add(od);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return list;
    }

    public boolean createOrderDetail(OrderDetailDTO orderDetail) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(CREATE_ORDER_DETAIL);
                ptm.setInt(1, orderDetail.getOrderID());
                ptm.setInt(2, orderDetail.getProductID());
                ptm.setInt(3, orderDetail.getQuantity());
                check = ptm.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }

    public boolean deleteOrderDetail(int orderID, int productID) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(DELETE_ORDER_DETAIL);
                ptm.setInt(1, orderID);
                ptm.setInt(2, productID);
                check = ptm.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }

    public boolean updateOrderDetail(OrderDetailDTO orderDetail) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(UPDATE_ORDER_DETAIL);
                ptm.setInt(1, orderDetail.getQuantity());
                ptm.setInt(2, orderDetail.getOrderID());
                ptm.setInt(3, orderDetail.getProductID());
                check = ptm.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }

    public boolean addOrderDetail(OrderDetailDTO detail) throws SQLException {
        boolean result = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            ptm = conn.prepareStatement(ADD_ORDER_DETAIL);
            ptm.setInt(1, detail.getOrderID());
            ptm.setInt(2, detail.getAttributeID());
            ptm.setInt(3, detail.getQuantity());
            ptm.setDouble(4, detail.getUnitPrice());
            result = ptm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return result;
    }

    public int getAttributeIdByProductSizeColor(int productId, String color, String size) throws SQLException, ClassNotFoundException {
        int attributeId = -1;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        StringBuilder sql = new StringBuilder("SELECT pv.AttributeID "
                + "FROM ProductVariant pv "
                + "LEFT JOIN Color c ON pv.ColorID = c.ColorID "
                + "LEFT JOIN Size s ON pv.SizeID = s.SizeID "
                + "WHERE pv.ProductID = ?");

        List<Object> parameters = new ArrayList<>();
        parameters.add(productId);
        if (color != null && !color.trim().isEmpty()) {
            sql.append(" AND c.ColorName = ?");
            parameters.add(color);
        } else {
            sql.append(" AND pv.ColorID IS NULL");
        }
        if (size != null && !size.trim().isEmpty()) {
            sql.append(" AND s.SizeName = ?");
            parameters.add(size);
        } else {
            sql.append(" AND pv.SizeID IS NULL");
        }
        try {
            conn = DBUtils.getConnection();
            ptm = conn.prepareStatement(sql.toString());
            for (int i = 0; i < parameters.size(); i++) {
                ptm.setObject(i + 1, parameters.get(i));
            }
            rs = ptm.executeQuery();
            if (rs.next()) {
                attributeId = rs.getInt("AttributeID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return attributeId;
    }

    public double getUnitPriceByAttributeId(int attributeId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT Price FROM ProductVariant WHERE AttributeID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, attributeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Price");
            }
        }
        throw new SQLException("Không tìm thấy Price cho AttributeID=" + attributeId);
    }

    public int getAttributeIdByProduct(int productId) throws SQLException {
        int attributeId = -1;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        String sql = "SELECT AttributeID "
                + "FROM ProductVariant "
                + "WHERE ProductID = ? ";
        try {
            conn = DBUtils.getConnection();
            ptm = conn.prepareStatement(sql);
            ptm.setInt(1, productId);
            rs = ptm.executeQuery();
            if (rs.next()) {
                attributeId = rs.getInt("AttributeID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return attributeId;
    }

    public int getAttributeIdByProductColor(int productId, String color) throws SQLException {
        int attributeId = -1;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        String sql = "SELECT pv.AttributeID "
                + "FROM ProductVariant pv "
                + "JOIN Color c ON pv.ColorID = c.ColorID "
                + "WHERE pv.ProductID = ? AND "
                + (color != null ? "c.ColorName = ?" : "c.ColorName IS NULL");
        try {
            conn = DBUtils.getConnection();
            ptm = conn.prepareStatement(sql);
            ptm.setInt(1, productId);
            if (color != null) {
                ptm.setString(2, color);
            }
            rs = ptm.executeQuery();
            if (rs.next()) {
                attributeId = rs.getInt("AttributeID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return attributeId;
    }

    public int getAttributeIdByProductSize(int productId, String size) throws SQLException {
        int attributeId = -1;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        String sql = "SELECT pv.AttributeID "
                + "FROM ProductVariant pv "
                + "JOIN Size s ON pv.SizeID = s.SizeID "
                + "WHERE pv.ProductID = ? AND "
                + (size != null ? "s.SizeName = ?" : "s.SizeName IS NULL");
        try {
            conn = DBUtils.getConnection();
            ptm = conn.prepareStatement(sql);
            ptm.setInt(1, productId);
            if (size != null) {
                ptm.setString(2, size);
                rs = ptm.executeQuery();
                if (rs.next()) {
                    return rs.getInt("AttributeID");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return attributeId;
    }

    public boolean updateProductVariantQuantity(int attributeId, int quantityPurchased) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        String sql = "UPDATE ProductVariant SET Quantity = Quantity - ? WHERE AttributeID = ?";
        try {
            conn = DBUtils.getConnection();
            ptm = conn.prepareStatement(sql);
            ptm.setInt(1, quantityPurchased);
            ptm.setInt(2, attributeId);
            return ptm.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ptm != null) {
                ptm.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return check;
    }
}
