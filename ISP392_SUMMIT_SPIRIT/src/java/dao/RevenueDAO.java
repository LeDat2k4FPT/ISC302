/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import dto.ProductSoldDTO;
import dto.RevenueDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

/**
 *
 * @author Hanne
 */
public class RevenueDAO {
// 1. Doanh thu theo tháng, năm, danh mục (Line Chart + Table)
    public List<RevenueDTO> getRevenueByMonthYearCategory(int month, int year, String category)
            throws SQLException, ClassNotFoundException {
        List<RevenueDTO> list = new ArrayList<>();

        String sql = "SELECT c.CateName, MONTH(o.OrderDate) AS Month, YEAR(o.OrderDate) AS Year, " +
                "SUM(od.Quantity * od.UnitPrice) AS TotalRevenue, SUM(od.Quantity) AS TotalQuantity " +
                "FROM Orders o " +
                "JOIN OrderDetail od ON o.OrderID = od.OrderID " +
                "JOIN ProductVariant pv ON od.AttributeID = pv.AttributeID " +
                "JOIN Product p ON pv.ProductID = p.ProductID " +
                "JOIN Category c ON p.CateID = c.CateID " +
                "WHERE o.Status = 'Delivered' ";

        if (month > 0) {
            sql += "AND MONTH(o.OrderDate) = ? ";
        }
        if (year > 0) {
            sql += "AND YEAR(o.OrderDate) = ? ";
        }
        if (category != null && !category.isEmpty()) {
            sql += "AND c.CateName = ? ";
        }

        sql += "GROUP BY c.CateName, MONTH(o.OrderDate), YEAR(o.OrderDate) " +
               "ORDER BY Year ASC, Month ASC";

        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            if (month > 0) ps.setInt(index++, month);
            if (year > 0) ps.setInt(index++, year);
            if (category != null && !category.isEmpty()) ps.setString(index++, category);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RevenueDTO dto = new RevenueDTO();
                    dto.setCategoryName(rs.getString("CateName"));
                    dto.setMonth(rs.getInt("Month"));
                    dto.setYear(rs.getInt("Year"));
                    dto.setTotalRevenue(rs.getDouble("TotalRevenue"));
                    dto.setTotalQuantity(rs.getInt("TotalQuantity"));
                    list.add(dto);
                }
            }
        }

        return list;
    }

    // 2. Lấy top sản phẩm bán chạy nhất theo danh mục (Bar Chart)
    public List<RevenueDTO> getTopSellingProductsByCategory(int month, int year, String category)
            throws SQLException, ClassNotFoundException {
        List<RevenueDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP 5 p.ProductName, SUM(od.Quantity) AS TotalQuantity, SUM(od.Quantity * od.UnitPrice) AS TotalRevenue ");
        sql.append("FROM Orders o ");
        sql.append("JOIN OrderDetail od ON o.OrderID = od.OrderID ");
        sql.append("JOIN ProductVariant pv ON od.AttributeID = pv.AttributeID ");
        sql.append("JOIN Product p ON pv.ProductID = p.ProductID ");
        sql.append("JOIN Category c ON p.CateID = c.CateID ");
        sql.append("WHERE o.Status = 'Delivered' ");
        if (month > 0) {
            sql.append("AND MONTH(o.OrderDate) = ? ");
        }
        if (year > 0) {
            sql.append("AND YEAR(o.OrderDate) = ? ");
        }
        if (category != null && !category.isEmpty()) {
            sql.append("AND c.CateName = ? ");
        }
        sql.append("GROUP BY p.ProductName ");
        sql.append("ORDER BY TotalQuantity DESC");

        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (month > 0) ps.setInt(idx++, month);
            if (year > 0) ps.setInt(idx++, year);
            if (category != null && !category.isEmpty()) ps.setString(idx++, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RevenueDTO dto = new RevenueDTO();
                dto.setProductName(rs.getString("ProductName"));
                dto.setTotalQuantity(rs.getInt("TotalQuantity"));
                dto.setTotalRevenue(rs.getDouble("TotalRevenue"));
                list.add(dto);
            }
        }
        return list;
    }

    // 3. Lấy dữ liệu pie chart: số lượng sản phẩm bán ra theo màu trong một danh mục
    public List<RevenueDTO> getProductColorStatsByCategory(String category)
            throws SQLException, ClassNotFoundException {
        List<RevenueDTO> list = new ArrayList<>();
        String sql = "SELECT p.ProductName, c2.ColorName, SUM(od.Quantity) AS TotalQuantity " +
                "FROM Orders o " +
                "JOIN OrderDetail od ON o.OrderID = od.OrderID " +
                "JOIN ProductVariant pv ON od.AttributeID = pv.AttributeID " +
                "JOIN Product p ON pv.ProductID = p.ProductID " +
                "JOIN Category c ON p.CateID = c.CateID " +
                "LEFT JOIN Color c2 ON pv.ColorID = c2.ColorID " +
                "WHERE o.Status = 'Delivered' " +
                "AND c.CateName = ? " +
                "GROUP BY p.ProductName, c2.ColorName " +
                "ORDER BY p.ProductName";

        try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RevenueDTO dto = new RevenueDTO();
                dto.setProductName(rs.getString("ProductName"));
                dto.setColorName(rs.getString("ColorName"));
                dto.setTotalQuantity(rs.getInt("TotalQuantity"));
                list.add(dto);
            }
        }

        return list;
    }
}
