package dao;
import dto.UpdateHistoryDTO;
import java.sql.*;
import java.util.*;
import utils.DBUtils;

public class UpdateHistoryDAO {
    public List<UpdateHistoryDTO> getAllLogs() throws SQLException, ClassNotFoundException {
        List<UpdateHistoryDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM UpdateHistory ORDER BY PerformedAt DESC";
        try (Connection con = DBUtils.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new UpdateHistoryDTO(
                    rs.getInt("LogID"),
                    rs.getString("ActionType"),
                    rs.getString("TableName"),
                    rs.getInt("RecordID"),
                    rs.getString("OldValue"),
                    rs.getString("NewValue"),
                    rs.getInt("PerformedBy"),
                    rs.getTimestamp("PerformedAt")
                ));
            }
        }
        return list;
    }
} 