package dao;

import dto.VoucherDTO;
import java.sql.*;
import java.util.*;
import utils.DBUtils;

public class VoucherDAO {

    public List<VoucherDTO> getAllVouchers() throws SQLException, ClassNotFoundException {
        List<VoucherDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Voucher";
        try (Connection con = DBUtils.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new VoucherDTO(
                        rs.getInt("VoucherID"),
                        rs.getString("VoucherCode"),
                        rs.getDouble("DiscountValue"),
                        rs.getDate("ExpiryDate"),
                        rs.getString("Status")
                ));
            }
        }
        return list;
    }

    public boolean addVoucher(VoucherDTO v) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO Voucher(VoucherCode, DiscountValue, ExpiryDate, Status) VALUES ( ?, ?, ?, ?)";
        try (Connection con = DBUtils.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getVoucherCode());
            ps.setDouble(2, v.getDiscountValue());
            ps.setDate(3, v.getExpiryDate());
            ps.setString(4, v.getStatus());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean updateVoucher(VoucherDTO v) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Voucher SET VoucherCode=?, DiscountValue=?, ExpiryDate=?, Status=? WHERE VoucherID=?";
        try (Connection con = DBUtils.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getVoucherCode());
            ps.setDouble(2, v.getDiscountValue());
            ps.setDate(3, v.getExpiryDate());
            ps.setString(4, v.getStatus());
            ps.setInt(5, v.getVoucherID());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateVoucherStatus(int voucherID, String status) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE Voucher SET Status=? WHERE VoucherID=?";
        try (Connection con = DBUtils.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, voucherID);
            return ps.executeUpdate() > 0;
        }
    }

    public VoucherDTO getVoucherById(int voucherID) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM Voucher WHERE VoucherID=?";
        try (Connection con = DBUtils.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, voucherID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new VoucherDTO(
                            rs.getInt("VoucherID"),
                            rs.getString("VoucherCode"),
                            rs.getDouble("DiscountValue"),
                            rs.getDate("ExpiryDate"),
                            rs.getString("Status")
                    );
                }
            }
        }
        return null;
    }

    public VoucherDTO getVoucherByCode(String code) {
        VoucherDTO voucher = null;
        String sql = "SELECT VoucherID, VoucherCode, DiscountValue, ExpiryDate, Status FROM Voucher WHERE VoucherCode = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    voucher = new VoucherDTO();
                    voucher.setVoucherID(rs.getInt("VoucherID"));
                    voucher.setVoucherCode(rs.getString("VoucherCode"));
                    voucher.setDiscountValue(rs.getDouble("DiscountValue"));
                    voucher.setExpiryDate(rs.getDate("ExpiryDate"));
                    voucher.setStatus(rs.getString("Status"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voucher;
    }

    public List<VoucherDTO> getActiveVouchers() throws SQLException, ClassNotFoundException {
        List<VoucherDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Voucher WHERE Status = 'Active'";
        try (Connection con = DBUtils.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new VoucherDTO(
                        rs.getInt("VoucherID"),
                        rs.getString("VoucherCode"),
                        rs.getDouble("DiscountValue"),
                        rs.getDate("ExpiryDate"),
                        rs.getString("Status")
                ));
            }
        }
        return list;
    }
}
