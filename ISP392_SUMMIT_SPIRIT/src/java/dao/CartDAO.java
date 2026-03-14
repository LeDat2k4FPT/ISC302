package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import utils.DBUtils;

/**
 * CartDAO chứa các hàm xử lý logic liên quan đến giỏ hàng như kiểm tra mã giảm
 * giá.
 */
public class CartDAO {

    /**
     * Kiểm tra mã giảm giá có hợp lệ hay không dựa trên điều kiện: - Mã tồn tại
     * và đang hoạt động - Chưa hết hạn
     *
     * @param code Mã giảm giá
     * @return Optional chứa DiscountValue nếu hợp lệ, hoặc Optional.empty()
     */
    public Optional<Double> validateDiscountCode(String code, double cartTotal) {
        String sql = "SELECT DiscountValue FROM Voucher "
                + "WHERE VoucherCode = ? AND Status = 'Active' "
                + "AND ExpiryDate >= GETDATE()";

        try (
                 Connection conn = DBUtils.getConnection();  PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try ( ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getDouble("DiscountValue"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kiểm tra mã giảm giá: " + e.getMessage());
        }

        return Optional.empty();
    }
}
