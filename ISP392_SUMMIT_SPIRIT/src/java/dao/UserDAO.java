package dao;

import dto.UserDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

public class UserDAO {

    private static final String LOGIN = "SELECT UserID, FullName, Address, Phone, Role FROM Account WHERE Email = ? AND Password = ?";
    private static final String CREATE = "INSERT INTO Account (FullName, Address, Email, Phone, Role, Password) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String EDIT_PROFILE = "UPDATE Account set FullName=?, Address=?, Email=?, Phone=? WHERE UserID=?";
    private static final String GET_USER = "SELECT * FROM Account WHERE userID = ?";
    private static final String UPDATE_PASSWORD = "UPDATE Account SET Password=? WHERE Email=?";
    private static final String FIND_BY_EMAIL = "SELECT UserID, FullName, Address, Password, Phone, Role FROM Account WHERE Email = ?";
    private static final String CHECK_DUPLICATE = "SELECT FullName FROM Account WHERE Email = ?";
    private static final String CHECK_PHONE_DUPLICATE = "SELECT FullName FROM Account WHERE Phone = ?";

    public UserDTO checkLogin(String email, String password) throws SQLException {
        UserDTO user = null;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(LOGIN);
                ptm.setString(1, email);
                ptm.setString(2, password);
                rs = ptm.executeQuery();
                if (rs.next()) {
                    int userID = rs.getInt("userID");
                    String fullName = rs.getString("fullName");
                    String role = rs.getString("role");
                    String address = rs.getString("address");
                    String phone = rs.getString("phone");
                    user = new UserDTO(userID, fullName, address, "", phone, email, role);
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
        return user;
    }

    public boolean create(UserDTO user) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(CREATE);
                ptm.setString(1, user.getFullName());
                ptm.setString(2, user.getAddress());
                ptm.setString(3, user.getEmail());
                ptm.setString(4, user.getPhone());
                ptm.setString(5, user.getRole());
                ptm.setString(6, user.getPassword());
                check = ptm.executeUpdate() > 0 ? true : false;
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

    public boolean edit(UserDTO user) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(EDIT_PROFILE);
                ptm.setString(1, user.getFullName());
                ptm.setString(2, user.getAddress());
                ptm.setString(3, user.getEmail());
                ptm.setString(4, user.getPhone());
                ptm.setInt(5, user.getUserID());
                check = ptm.executeUpdate() > 0 ? true : false;
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

    public UserDTO getUser(String userID) throws SQLException {
        UserDTO user = null;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(GET_USER);
                ptm.setString(1, userID);
                rs = ptm.executeQuery();
                if (rs.next()) {
                    user = new UserDTO(
                            rs.getInt("userID"),
                            rs.getString("FullName"),
                            rs.getString("Address"),
                            rs.getString("Password"),
                            rs.getString("Phone"),
                            rs.getString("Email"),
                            rs.getString("RoleID")
                    );
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
        return user;
    }

    public boolean updatePassword(String email, String newPassword) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(UPDATE_PASSWORD);
                ptm.setString(1, newPassword);
                ptm.setString(2, email);
                check = ptm.executeUpdate() > 0 ? true : false;
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

    public UserDTO findByEmail(String email) throws SQLException {
        UserDTO user = null;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(FIND_BY_EMAIL);
                ptm.setString(1, email);
                rs = ptm.executeQuery();
                while (rs.next()) {
                    int userID = rs.getInt("UserID");
                    String fullName = rs.getString("FullName");
                    String address = rs.getString("Address");
                    String password = rs.getString("Password");
                    String phone = rs.getString("Phone");
                    String role = rs.getString("Role");

                    user = new UserDTO(userID, fullName, address, password, phone, email, role);
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
        return user;
    }

    public boolean checkEmailExists(String email) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(CHECK_DUPLICATE);
                ptm.setString(1, email);
                rs = ptm.executeQuery();
                if (rs.next()) {
                    check = true;
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
        return check;
    }

    public boolean checkPhoneExists(String phone) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(CHECK_PHONE_DUPLICATE);
                ptm.setString(1, phone);
                rs = ptm.executeQuery();
                if (rs.next()) {
                    check = true;
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
        return check;
    }

    public List<UserDTO> searchUsers(String keyword, String role) {
        List<UserDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM Account WHERE (FullName LIKE ? OR Email LIKE ? OR Phone LIKE ?) AND Role <> 'admin'"
        );
        if (role != null && !role.isEmpty() && !"all".equalsIgnoreCase(role)) {
            sql.append(" AND Role = ?");
        }

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);

            if (role != null && !role.isEmpty() && !"all".equalsIgnoreCase(role)) {
                ps.setString(4, role);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserDTO user = new UserDTO(
                        rs.getInt("UserID"),
                        rs.getString("FullName"),
                        rs.getString("Address"),
                        rs.getString("Password"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        rs.getString("Role")
                );
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<UserDTO> getAllUsers(String role) {
        List<UserDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Account WHERE Role <> 'admin'");
        if (role != null && !role.isEmpty() && !"all".equalsIgnoreCase(role)) {
            sql.append(" AND Role = ?");
        }
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (role != null && !role.isEmpty() && !"all".equalsIgnoreCase(role)) {
                ps.setString(1, role);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserDTO user = new UserDTO(
                        rs.getInt("UserID"),
                        rs.getString("FullName"),
                        rs.getString("Address"),
                        rs.getString("Password"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        rs.getString("Role")
                );
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<UserDTO> getAllUsers() {
        List<UserDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM Account WHERE Role <> 'admin'";

        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserDTO user = new UserDTO(
                        rs.getInt("UserID"),
                        rs.getString("FullName"),
                        rs.getString("Address"),
                        rs.getString("Password"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getString("Role")
                );
                list.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

//    public void toggleRole(int userID) {
//        String sql = "UPDATE Account SET Role = CASE WHEN Role = 'User' THEN 'Staff' ELSE 'User' END WHERE UserID = ?";
//        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setInt(1, userID);
//            ps.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public boolean deleteUser(int userID) {
        String sql = "DELETE FROM Account WHERE UserID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserRole(int userID, String newRole) {
        String sql = "UPDATE Account SET Role = ? WHERE UserID = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setInt(2, userID);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
