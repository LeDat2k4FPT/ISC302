package dao;

import dto.OrderDTO;
import dto.OrderDetailDTO;
import dto.UserAddressDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;
import java.sql.Statement;

public class OrderDAO {

    private static final String GET_ALL_ORDERS = "SELECT OrderID, UserID, OrderDate, TotalAmount, Status FROM Orders";
    private static final String GET_ORDER_BY_ID = "SELECT * FROM Orders WHERE OrderID = ?";
    private static final String GET_ORDERS_BY_USER = "SELECT OrderID, OrderDate, Status, TotalAmount FROM Orders WHERE UserID = ? ORDER BY OrderDate DESC";
    private static final String CREATE_ORDER = "INSERT INTO Orders (userID, orderDate, total, status) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_ORDER_STATUS = "UPDATE Orders SET status = ? WHERE orderID = ?";
    private static final String INSERT_ORDER = "INSERT INTO Orders (UserID, TotalAmount) VALUES (?, ?)";
    private static final String UPDATE_STATUS = "UPDATE Orders SET Status = ? WHERE OrderID = ?";
    private static final String ADD_ORDER = "INSERT INTO Orders (UserID, OrderDate, Status, TotalAmount, ShipFee, VoucherID, Note) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_AFTER_PAYMENT = "UPDATE Orders SET Status = ?, Note = ? WHERE OrderID = ?";

    public List<OrderDTO> getAllOrders() throws SQLException, ClassNotFoundException {
        List<OrderDTO> list = new ArrayList<>();
        String sql = "SELECT o.OrderID, o.OrderDate, o.TotalAmount, o.Status, a.FullName "
                + "FROM Orders o JOIN Account a ON o.UserID = a.UserID "
                + "ORDER BY o.OrderDate DESC";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrderDTO order = new OrderDTO();
                order.setOrderID(rs.getInt("OrderID"));
                order.setOrderDate(rs.getDate("OrderDate"));
                order.setTotalAmount(rs.getDouble("TotalAmount"));
                order.setStatus(rs.getString("Status"));
                order.setFullName(rs.getString("FullName"));
                list.add(order);
            }
        }
        return list;
    }
    public List<OrderDTO> getOrdersByStatus(String status) throws SQLException, ClassNotFoundException {
    List<OrderDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM Orders WHERE Status = ?";
    try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, status);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            OrderDTO dto = new OrderDTO();
            dto.setOrderID(rs.getInt("OrderID"));
            dto.setUserID(rs.getInt("UserID"));
            dto.setOrderDate(rs.getDate("OrderDate"));
            dto.setStatus(rs.getString("Status"));
            dto.setTotalAmount(rs.getDouble("TotalAmount"));
            dto.setNote(rs.getString("Note"));
            list.add(dto);
        }
    }
    return list;
}

    public OrderDTO getOrderById(int orderID) throws SQLException, ClassNotFoundException {
        OrderDTO order = null;
        String sql = "SELECT o.OrderID, o.UserID, o.OrderDate, o.TotalAmount, o.Status, "
                + "a.FullName, a.Email, a.Phone "
                + "FROM Orders o "
                + "JOIN Account a ON o.UserID = a.UserID "
                + "WHERE o.OrderID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    order = new OrderDTO(
                            rs.getInt("OrderID"),
                            rs.getInt("UserID"),
                            rs.getDate("OrderDate"),
                            rs.getDouble("TotalAmount"),
                            rs.getString("Status")
                    );
                    // Kiểm tra null trước khi set
                    String fullName = rs.getString("FullName");
                    if (fullName != null) {
                        order.setFullName(fullName);
                    }
                    String email = rs.getString("Email");
                    if (email != null) {
                        order.setEmail(email);
                    }
                    String phone = rs.getString("Phone");
                    if (phone != null) {
                        order.setPhoneNumber(phone);
                    }

                    System.out.println("DEBUG: Found orderID = " + order.getOrderID());
                } else {
                    System.out.println("DEBUG: No order found for orderID = " + orderID);
                }
            }
        }
        return order;
    }

    public List<OrderDTO> getOrdersByUser(int userID) throws SQLException {
        List<OrderDTO> orders = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {

                ptm = conn.prepareStatement(GET_ORDERS_BY_USER);
                ptm.setInt(1, userID);
                rs = ptm.executeQuery();
                while (rs.next()) {
                    orders.add(new OrderDTO(
                            rs.getInt("OrderID"),
                            userID, // vì bạn không select UserID, gán thẳng
                            rs.getDate("OrderDate"),
                            rs.getDouble("TotalAmount"),
                            rs.getString("Status")
                    ));
                    System.out.println("DEBUG: Found orderID = " + rs.getInt("OrderID"));
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
        return orders;
    }

    public boolean updateOrderStatus(int orderID, String status) throws SQLException {

        if (orderID <= 0 || status == null || status.isEmpty()) {
            System.err.println("[updateOrderStatus] Lỗi: orderID hoặc status không hợp lệ. orderID=" + orderID + ", status=" + status);
            return false;
        }
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                System.out.println("[DAO] Preparing to update orderID=" + orderID + " to status=" + status);
                ptm = conn.prepareStatement("UPDATE Orders SET status = ? WHERE orderID = ?");
                ptm.setString(1, status);
                ptm.setInt(2, orderID);
                int count = ptm.executeUpdate();
                System.out.println("[DAO] Updated rows = " + count);
                check = count > 0;
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

    public List<OrderDetailDTO> getOrderDetails(int orderID) throws SQLException, ClassNotFoundException {
        List<OrderDetailDTO> list = new ArrayList<>();
        String sql = "SELECT od.Quantity, od.UnitPrice, "
                + "pv.ProductID, p.ProductName, s.SizeName, c.ColorName "
                + "FROM OrderDetail od "
                + "JOIN ProductVariant pv ON od.AttributeID = pv.AttributeID "
                + "JOIN Product p ON pv.ProductID = p.ProductID "
                + "LEFT JOIN Size s ON pv.SizeID = s.SizeID "
                + "LEFT JOIN Color c ON pv.ColorID = c.ColorID "
                + "WHERE od.OrderID = ?";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetailDTO od = new OrderDetailDTO();
                    od.setProductID(rs.getInt("ProductID"));
                    od.setProductName(rs.getString("ProductName"));
                    od.setSizeName(rs.getString("SizeName"));   // sẽ là null nếu không có
                    od.setColorName(rs.getString("ColorName")); // sẽ là null nếu không có
                    od.setQuantity(rs.getInt("Quantity"));
                    od.setUnitPrice(rs.getDouble("UnitPrice"));
                    list.add(od);
                }
            }
        }
        return list;
    }

    public int insertOrder(OrderDTO order) throws SQLException, ClassNotFoundException {
        int orderId = -1;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            // Kiểm tra dữ liệu đầu vào
            if (order == null || order.getUserID() <= 0 || order.getTotalAmount() <= 0 || order.getStatus() == null || order.getStatus().isEmpty()) {
                System.err.println("[insertOrder] Lỗi: Dữ liệu đầu vào không hợp lệ. userID=" + (order != null ? order.getUserID() : "null") + ", totalAmount=" + (order != null ? order.getTotalAmount() : "null") + ", status=" + (order != null ? order.getStatus() : "null"));
                return -1;
            }
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(INSERT_ORDER, PreparedStatement.RETURN_GENERATED_KEYS);
                ptm.setInt(1, order.getUserID());
                ptm.setDouble(2, order.getTotalAmount());
                int affectedRows = ptm.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating payment failed, no rows affected.");
                }
                try ( ResultSet generatedKeys = ptm.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating payment failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e);
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
        return orderId;
    }

    public boolean updateOrderStatus(OrderDTO order) throws SQLException, ClassNotFoundException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            ptm = conn.prepareStatement(UPDATE_STATUS);
            ptm.setString(1, order.getStatus());
            ptm.setInt(2, order.getOrderID());
            int rowsAffected = ptm.executeUpdate();
            check = rowsAffected > 0;
        } catch (SQLException e) {
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

    public List<OrderDTO> searchOrders(String keyword) throws SQLException, ClassNotFoundException {
        List<OrderDTO> list = new ArrayList<>();
        String sql = "SELECT o.OrderID, o.OrderDate, o.TotalAmount, o.Status, a.FullName "
                + "FROM Orders o "
                + "JOIN Account a ON o.UserID = a.UserID "
                + "WHERE o.OrderID LIKE ? OR o.Status LIKE ? OR a.FullName LIKE ? OR a.Email LIKE ? "
                + "ORDER BY o.OrderDate DESC";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, kw);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDTO order = new OrderDTO();
                    order.setOrderID(rs.getInt("OrderID"));
                    order.setOrderDate(rs.getDate("OrderDate"));
                    order.setTotalAmount(rs.getDouble("TotalAmount"));
                    order.setStatus(rs.getString("Status"));
                    order.setFullName(rs.getString("FullName"));
                    list.add(order);
                }
            }
        }
        return list;
    }
public List<OrderDTO> searchOrders(String keyword, String status) throws SQLException, ClassNotFoundException {
    List<OrderDTO> list = new ArrayList<>();
    String sql = "SELECT o.OrderID, o.OrderDate, o.TotalAmount, o.Status, a.FullName "
               + "FROM Orders o "
               + "JOIN Account a ON o.UserID = a.UserID "
               + "WHERE (o.OrderID LIKE ? OR a.FullName LIKE ? OR a.Email LIKE ?) "
               + "AND o.Status LIKE ? "
               + "ORDER BY o.OrderDate DESC";
    try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
        String kw = "%" + keyword + "%";
        String st = "%" + status + "%";
        ps.setString(1, kw);
        ps.setString(2, kw);
        ps.setString(3, kw);
        ps.setString(4, st);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                OrderDTO order = new OrderDTO();
                order.setOrderID(rs.getInt("OrderID"));
                order.setOrderDate(rs.getDate("OrderDate"));
                order.setTotalAmount(rs.getDouble("TotalAmount"));
                order.setStatus(rs.getString("Status"));
                order.setFullName(rs.getString("FullName"));
                list.add(order);
            }
        }
    }
    return list;
}

    public double getTotalSpentByUser(int userID) throws SQLException, ClassNotFoundException {
        String sql = "SELECT SUM(TotalAmount) FROM Orders WHERE UserID = ? AND Status = 'Delivered'";
        try ( Connection con = DBUtils.getConnection();  PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0;
    }

    public int getOrderCountByUser(int userID) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM Orders WHERE UserID = ? AND Status = 'Delivered'";
        try ( Connection con = DBUtils.getConnection();  PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public boolean hasUserPurchasedProduct(int userId, int productId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT COUNT(*) AS Total "
                + "FROM Orders o "
                + "JOIN OrderDetail od ON o.OrderID = od.OrderID "
                + "JOIN ProductVariant pv ON od.AttributeID = pv.AttributeID "
                + "WHERE o.UserID = ? AND o.Status = 'Delivered' AND pv.ProductID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Total") > 0;
                }
            }
        }
        return false;
    }

    public int addOrder(OrderDTO order) throws SQLException {
        int generatedOrderId = -1; // Giá trị mặc định nếu thất bại
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;

        try {
            conn = DBUtils.getConnection();
            // Dùng RETURN_GENERATED_KEYS để lấy khóa tự tăng
            ptm = conn.prepareStatement(ADD_ORDER, Statement.RETURN_GENERATED_KEYS);

            ptm.setInt(1, order.getUserID());
            ptm.setDate(2, order.getOrderDate());
            ptm.setString(3, order.getStatus());
            ptm.setDouble(4, order.getTotalAmount());
            ptm.setDouble(5, 30000);
            if (order.getVoucherID() != null) {
                ptm.setInt(6, order.getVoucherID());
            } else {
                ptm.setNull(6, Types.INTEGER);
            }
            ptm.setString(7, order.getNote());
            int affectedRows = ptm.executeUpdate();

            if (affectedRows > 0) {
                // Lấy orderID vừa được tạo
                rs = ptm.getGeneratedKeys();
                if (rs.next()) {
                    generatedOrderId = rs.getInt(1);
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

        return generatedOrderId;
    }

    public boolean updateOrderAfterPayment(OrderDTO order) throws SQLException, ClassNotFoundException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            ptm = conn.prepareStatement(UPDATE_AFTER_PAYMENT);
            ptm.setString(1, order.getStatus());
            ptm.setString(2, order.getNote());
            ptm.setInt(3, order.getOrderID());
            int rowsAffected = ptm.executeUpdate();
            check = rowsAffected > 0;
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

    public int getNextOrderID() throws Exception {
        int nextId = 1;
        String sql = "SELECT ISNULL(MAX(OrderID), 0) + 1 AS NextID FROM Orders";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                nextId = rs.getInt("NextID");
            }
        }
        return nextId;
    }

    public boolean updateOrderStatusAndNote(OrderDTO order) {
        String sql = "UPDATE Orders SET Status = ?, Note = ? WHERE OrderID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getStatus());
            ps.setString(2, order.getNote());
            ps.setInt(3, order.getOrderID());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void cancelOrder(int orderID, String reason) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtils.getConnection();
            String sql = "UPDATE Orders SET Status = 'Cancelled', Note = ? WHERE OrderID = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, reason);
            ps.setInt(2, orderID);
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public OrderDTO getOrderByID(int orderID) {
        OrderDTO order = null;
        String sql = "SELECT * FROM Orders WHERE OrderID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                order = new OrderDTO();
                order.setOrderID(rs.getInt("OrderID"));
                order.setUserID(rs.getInt("UserID"));
                order.setOrderDate(rs.getDate("OrderDate"));
                order.setStatus(rs.getString("Status"));
                order.setTotalAmount(rs.getDouble("TotalAmount"));
                order.setNote(rs.getString("Note"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    public List<OrderDTO> getPackedOrders() throws SQLException {
        List<OrderDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE Status = 'Packed'";

        try ( Connection con = DBUtils.getConnection();  PreparedStatement ps = con.prepareStatement(sql);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                OrderDTO order = new OrderDTO(
                        rs.getInt("OrderID"),
                        rs.getInt("UserID"),
                        rs.getDate("OrderDate"),
                        rs.getDouble("TotalAmount"),
                        rs.getString("Status")
                );
                list.add(order);
            }
        }
        return list;
    }

    public UserAddressDTO getUserAddressInfoByOrderId(int orderId) {
        UserAddressDTO info = null;
        String sql = "SELECT * FROM UserAddressInfo WHERE OrderID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                info = new UserAddressDTO(
                        rs.getInt("InfoID"),
                        rs.getInt("OrderID"),
                        rs.getString("Country"),
                        rs.getString("FullName"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        rs.getString("Address"),
                        rs.getString("District"),
                        rs.getString("City")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }
    private String getSafeString(ResultSet rs, String column) throws SQLException {
    String value = rs.getString(column);
    return value != null ? value : "";
}

    public boolean assignShipper(int orderID, int shipperID) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Shipping (OrderID, UserID) VALUES (?, ?); "
                + "UPDATE Orders SET Status = 'Shipped' WHERE OrderID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps1 = conn.prepareStatement("INSERT INTO Shipping (OrderID, UserID) VALUES (?, ?)");  PreparedStatement ps2 = conn.prepareStatement("UPDATE Orders SET Status = 'Shipped' WHERE OrderID = ?")) {

            // Insert into Shipping
            ps1.setInt(1, orderID);
            ps1.setInt(2, shipperID);
            ps1.executeUpdate();

            // Update status to 'Shipped'
            ps2.setInt(1, orderID);
            return ps2.executeUpdate() > 0;
        }
    }

}
