package dao;

import dto.CategoryDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

public class CategoryDAO {

    private static final String GET_ALL_CATEGORIES = "SELECT * FROM Category";
    private static final String GET_CATEGORY_BY_ID = "SELECT * FROM Category WHERE CateID = ?";
    private static final String CREATE_CATEGORY = "INSERT INTO Category (CateID, CateName) VALUES (?, ?)";
    private static final String UPDATE_CATEGORY = "UPDATE Category SET CateName = ? WHERE CateID = ?";
    private static final String DELETE_CATEGORY = "DELETE FROM Category WHERE CateID = ?";

    public List<CategoryDTO> getAllCategories() throws SQLException {
        List<CategoryDTO> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(GET_ALL_CATEGORIES);
                rs = ptm.executeQuery();
                while (rs.next()) {
                    list.add(new CategoryDTO(
                            rs.getInt("CateID"),
                            rs.getString("CateName")
                    ));
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

    public CategoryDTO getCategoryByID(int cateID) throws SQLException {
        CategoryDTO category = null;
        Connection conn = null;
        PreparedStatement ptm = null;
        ResultSet rs = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(GET_CATEGORY_BY_ID);
                ptm.setInt(1, cateID);
                rs = ptm.executeQuery();
                if (rs.next()) {
                    category = new CategoryDTO(
                            rs.getInt("CateID"),
                            rs.getString("CateName")
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
        return category;
    }

    public boolean createCategory(CategoryDTO category) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(CREATE_CATEGORY);
                ptm.setInt(1, category.getCateID());
                ptm.setString(2, category.getCateName());
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

    public boolean updateCategory(CategoryDTO category) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(UPDATE_CATEGORY);
                ptm.setString(1, category.getCateName());
                ptm.setInt(2, category.getCateID());
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

    public boolean deleteCategory(int cateID) throws SQLException {
        boolean check = false;
        Connection conn = null;
        PreparedStatement ptm = null;
        try {
            conn = DBUtils.getConnection();
            if (conn != null) {
                ptm = conn.prepareStatement(DELETE_CATEGORY);
                ptm.setInt(1, cateID);
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

    public int getOrInsertCategoryByName(String cateName) throws SQLException, ClassNotFoundException {
        String checkSql = "SELECT CateID FROM Category WHERE CateName = ?";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, cateName);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CateID");
                }
            }
        }

        // Nếu chưa tồn tại, thì insert
        String insertSql = "INSERT INTO Category (CateName) VALUES (?)";
        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cateName);
            ps.executeUpdate();
            try ( ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert or retrieve new CategoryID.");
    }
}
