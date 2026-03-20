package dao;

import dto.ChatProductResult;
import dto.ChatQuery;
import dto.VariantStockResult;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import utils.DBUtils;

public class ProductChatDAOImpl implements ProductChatDAO {

    @Override
    public List<ChatProductResult> findProductsByName(String keyword) throws Exception {
        List<ChatProductResult> list = new ArrayList<ChatProductResult>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return list;
        }

        String sql = "SELECT TOP 3 "
                + "p.ProductID, "
                + "p.ProductName, "
                + "p.Description, "
                + "c.CateName, "
                + "img.ImageURL, "
                + "MIN(pv.Price) AS MinPrice, "
                + "MAX(pv.Price) AS MaxPrice, "
                + "SUM(ISNULL(pv.Quantity, 0)) AS TotalQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.CateID = c.CateID "
                + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "LEFT JOIN ( "
                + "    SELECT ProductID, MIN(ImageURL) AS ImageURL "
                + "    FROM ProductImage "
                + "    GROUP BY ProductID "
                + ") img ON p.ProductID = img.ProductID "
                + "WHERE p.Status = ? "
                + "AND LOWER(REPLACE(REPLACE(p.ProductName, '-', ' '), '  ', ' ')) LIKE LOWER(?) "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, c.CateName, img.ImageURL "
                + "ORDER BY p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");
            String normalizedKeyword = keyword.trim().replace("-", " ").replaceAll("\\s+", " ");
            ps.setString(2, "%" + normalizedKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapChatProductResult(rs));
                }
            }
        }

        return list;
    }

    @Override
    public Integer findProductIdByKeyword(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT TOP 1 p.ProductID "
                + "FROM Product p "
                + "WHERE p.Status = ? "
                + "AND LOWER(REPLACE(REPLACE(p.ProductName, '-', ' '), '  ', ' ')) LIKE LOWER(?) "
                + "ORDER BY p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");
            String normalizedKeyword = keyword.trim().replace("-", " ").replaceAll("\\s+", " ");
            ps.setString(2, "%" + normalizedKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ProductID");
                }
            }
        }

        return null;
    }

    @Override
    public List<VariantStockResult> getVariantStock(int productId) throws Exception {
        List<VariantStockResult> list = new ArrayList<VariantStockResult>();

        String sql = "SELECT "
                + "cl.ColorName, "
                + "s.SizeName, "
                + "pv.Price, "
                + "pv.Quantity "
                + "FROM ProductVariant pv "
                + "LEFT JOIN Color cl ON pv.ColorID = cl.ColorID "
                + "LEFT JOIN Size s ON pv.SizeID = s.SizeID "
                + "WHERE pv.ProductID = ? "
                + "ORDER BY cl.ColorName, s.SizeName";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VariantStockResult item = new VariantStockResult();
                    item.setColor(rs.getString("ColorName"));
                    item.setSize(rs.getString("SizeName"));
                    item.setPrice(rs.getDouble("Price"));
                    item.setQuantity(rs.getInt("Quantity"));
                    list.add(item);
                }
            }
        }

        return list;
    }

    @Override
    public List<ChatProductResult> findProductsByFilters(ChatQuery query) throws Exception {
        return findProductsByFilters(query, true);
    }

    @Override
    public List<ChatProductResult> findAllProductsByFilters(ChatQuery query) throws Exception {
        return findProductsByFilters(query, false);
    }

    @Override
    public ChatProductResult findCheapestProduct() throws Exception {
        String sql = "SELECT TOP 1 "
                + "p.ProductID, "
                + "p.ProductName, "
                + "p.Description, "
                + "c.CateName, "
                + "img.ImageURL, "
                + "MIN(pv.Price) AS MinPrice, "
                + "MAX(pv.Price) AS MaxPrice, "
                + "SUM(ISNULL(pv.Quantity, 0)) AS TotalQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.CateID = c.CateID "
                + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "LEFT JOIN ( "
                + "    SELECT ProductID, MIN(ImageURL) AS ImageURL "
                + "    FROM ProductImage "
                + "    GROUP BY ProductID "
                + ") img ON p.ProductID = img.ProductID "
                + "WHERE p.Status = ? "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, c.CateName, img.ImageURL "
                + "ORDER BY MIN(pv.Price) ASC, p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapChatProductResult(rs);
                }
            }
        }

        return null;
    }

    @Override
    public ChatProductResult findMostExpensiveProduct() throws Exception {
        String sql = "SELECT TOP 1 "
                + "p.ProductID, "
                + "p.ProductName, "
                + "p.Description, "
                + "c.CateName, "
                + "img.ImageURL, "
                + "MIN(pv.Price) AS MinPrice, "
                + "MAX(pv.Price) AS MaxPrice, "
                + "SUM(ISNULL(pv.Quantity, 0)) AS TotalQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.CateID = c.CateID "
                + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "LEFT JOIN ( "
                + "    SELECT ProductID, MIN(ImageURL) AS ImageURL "
                + "    FROM ProductImage "
                + "    GROUP BY ProductID "
                + ") img ON p.ProductID = img.ProductID "
                + "WHERE p.Status = ? "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, c.CateName, img.ImageURL "
                + "ORDER BY MAX(pv.Price) DESC, p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapChatProductResult(rs);
                }
            }
        }

        return null;
    }

    @Override
    public ChatProductResult findBestSellingProduct() throws Exception {
        String sql = "SELECT TOP 1 "
                + "p.ProductID, "
                + "p.ProductName, "
                + "p.Description, "
                + "c.CateName, "
                + "img.ImageURL, "
                + "MIN(pv.Price) AS MinPrice, "
                + "MAX(pv.Price) AS MaxPrice, "
                + "SUM(ISNULL(od.Quantity, 0)) AS TotalQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.CateID = c.CateID "
                + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "LEFT JOIN OrderDetail od ON pv.AttributeID = od.AttributeID "
                + "LEFT JOIN ( "
                + "    SELECT ProductID, MIN(ImageURL) AS ImageURL "
                + "    FROM ProductImage "
                + "    GROUP BY ProductID "
                + ") img ON p.ProductID = img.ProductID "
                + "WHERE p.Status = ? "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, c.CateName, img.ImageURL "
                + "ORDER BY SUM(ISNULL(od.Quantity, 0)) DESC, p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapChatProductResult(rs);
                }
            }
        }

        return null;
    }

    @Override
    public ChatProductResult findCheapestProductByCategory(String category) throws Exception {
        if (category == null || category.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT TOP 1 "
                + "p.ProductID, "
                + "p.ProductName, "
                + "p.Description, "
                + "c.CateName, "
                + "img.ImageURL, "
                + "MIN(pv.Price) AS MinPrice, "
                + "MAX(pv.Price) AS MaxPrice, "
                + "SUM(ISNULL(pv.Quantity, 0)) AS TotalQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.CateID = c.CateID "
                + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "LEFT JOIN ( "
                + "    SELECT ProductID, MIN(ImageURL) AS ImageURL "
                + "    FROM ProductImage "
                + "    GROUP BY ProductID "
                + ") img ON p.ProductID = img.ProductID "
                + "WHERE p.Status = ? AND LOWER(c.CateName) = LOWER(?) "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, c.CateName, img.ImageURL "
                + "ORDER BY MIN(pv.Price) ASC, p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");
            ps.setString(2, category.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapChatProductResult(rs);
                }
            }
        }

        return null;
    }

    @Override
    public ChatProductResult findMostExpensiveProductByCategory(String category) throws Exception {
        if (category == null || category.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT TOP 1 "
                + "p.ProductID, "
                + "p.ProductName, "
                + "p.Description, "
                + "c.CateName, "
                + "img.ImageURL, "
                + "MIN(pv.Price) AS MinPrice, "
                + "MAX(pv.Price) AS MaxPrice, "
                + "SUM(ISNULL(pv.Quantity, 0)) AS TotalQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.CateID = c.CateID "
                + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "LEFT JOIN ( "
                + "    SELECT ProductID, MIN(ImageURL) AS ImageURL "
                + "    FROM ProductImage "
                + "    GROUP BY ProductID "
                + ") img ON p.ProductID = img.ProductID "
                + "WHERE p.Status = ? AND LOWER(c.CateName) = LOWER(?) "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, c.CateName, img.ImageURL "
                + "ORDER BY MAX(pv.Price) DESC, p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");
            ps.setString(2, category.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapChatProductResult(rs);
                }
            }
        }

        return null;
    }

    @Override
    public ChatProductResult findCheapestProductByKeyword(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        String normalizedKeyword = keyword.trim().replace("-", " ").replaceAll("\\s+", " ");

        String sql = "SELECT TOP 1 "
                + "p.ProductID, "
                + "p.ProductName, "
                + "p.Description, "
                + "c.CateName, "
                + "img.ImageURL, "
                + "MIN(pv.Price) AS MinPrice, "
                + "MAX(pv.Price) AS MaxPrice, "
                + "SUM(ISNULL(pv.Quantity, 0)) AS TotalQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.CateID = c.CateID "
                + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "LEFT JOIN ( "
                + "    SELECT ProductID, MIN(ImageURL) AS ImageURL "
                + "    FROM ProductImage "
                + "    GROUP BY ProductID "
                + ") img ON p.ProductID = img.ProductID "
                + "WHERE p.Status = ? "
                + "AND (LOWER(REPLACE(REPLACE(p.ProductName, '-', ' '), '  ', ' ')) LIKE LOWER(?) "
                + "OR LOWER(p.Description) LIKE LOWER(?)) "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, c.CateName, img.ImageURL "
                + "ORDER BY MIN(pv.Price) ASC, p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");
            ps.setString(2, "%" + normalizedKeyword + "%");
            ps.setString(3, "%" + normalizedKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapChatProductResult(rs);
                }
            }
        }

        return null;
    }

    @Override
    public ChatProductResult findMostExpensiveProductByKeyword(String keyword) throws Exception {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }

        String normalizedKeyword = keyword.trim().replace("-", " ").replaceAll("\\s+", " ");

        String sql = "SELECT TOP 1 "
                + "p.ProductID, "
                + "p.ProductName, "
                + "p.Description, "
                + "c.CateName, "
                + "img.ImageURL, "
                + "MIN(pv.Price) AS MinPrice, "
                + "MAX(pv.Price) AS MaxPrice, "
                + "SUM(ISNULL(pv.Quantity, 0)) AS TotalQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.CateID = c.CateID "
                + "LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID "
                + "LEFT JOIN ( "
                + "    SELECT ProductID, MIN(ImageURL) AS ImageURL "
                + "    FROM ProductImage "
                + "    GROUP BY ProductID "
                + ") img ON p.ProductID = img.ProductID "
                + "WHERE p.Status = ? "
                + "AND (LOWER(REPLACE(REPLACE(p.ProductName, '-', ' '), '  ', ' ')) LIKE LOWER(?) "
                + "OR LOWER(p.Description) LIKE LOWER(?)) "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, c.CateName, img.ImageURL "
                + "ORDER BY MAX(pv.Price) DESC, p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");
            ps.setString(2, "%" + normalizedKeyword + "%");
            ps.setString(3, "%" + normalizedKeyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapChatProductResult(rs);
                }
            }
        }

        return null;
    }

    private List<ChatProductResult> findProductsByFilters(ChatQuery query, boolean top3Only) throws Exception {
        List<ChatProductResult> list = new ArrayList<ChatProductResult>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        if (top3Only) {
            sql.append("TOP 3 ");
        }

        sql.append("p.ProductID, ")
           .append("p.ProductName, ")
           .append("p.Description, ")
           .append("c.CateName, ")
           .append("img.ImageURL, ")
           .append("MIN(pv.Price) AS MinPrice, ")
           .append("MAX(pv.Price) AS MaxPrice, ")
           .append("SUM(ISNULL(pv.Quantity, 0)) AS TotalQuantity ")
           .append("FROM Product p ")
           .append("JOIN Category c ON p.CateID = c.CateID ")
           .append("LEFT JOIN ProductVariant pv ON p.ProductID = pv.ProductID ")
           .append("LEFT JOIN Color cl ON pv.ColorID = cl.ColorID ")
           .append("LEFT JOIN Size s ON pv.SizeID = s.SizeID ")
           .append("LEFT JOIN ( ")
           .append("    SELECT ProductID, MIN(ImageURL) AS ImageURL ")
           .append("    FROM ProductImage ")
           .append("    GROUP BY ProductID ")
           .append(") img ON p.ProductID = img.ProductID ")
           .append("WHERE p.Status = ? ");

        List<Object> params = new ArrayList<Object>();
        params.add("Active");

        if (query.getCategory() != null && !query.getCategory().trim().isEmpty()) {
            sql.append("AND LOWER(c.CateName) = LOWER(?) ");
            params.add(query.getCategory().trim());
        }

        if (query.getColor() != null && !query.getColor().trim().isEmpty()) {
            sql.append("AND LOWER(cl.ColorName) = LOWER(?) ");
            params.add(query.getColor().trim());
        }

        if (query.getSize() != null && !query.getSize().trim().isEmpty()) {
            sql.append("AND LOWER(s.SizeName) = LOWER(?) ");
            params.add(query.getSize().trim());
        }

        if (query.getMinPrice() != null) {
            sql.append("AND pv.Price >= ? ");
            params.add(query.getMinPrice());
        }

        if (query.getMaxPrice() != null) {
            sql.append("AND pv.Price <= ? ");
            params.add(query.getMaxPrice());
        }

        if (query.isInStockOnly()) {
            sql.append("AND pv.Quantity > 0 ");
        }

        appendDescriptionKeywordCondition(sql, params, query);

        sql.append("GROUP BY p.ProductID, p.ProductName, p.Description, c.CateName, img.ImageURL ")
           .append("ORDER BY p.ProductID ASC");

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            bindParams(ps, params);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapChatProductResult(rs));
                }
            }
        }

        return list;
    }

    private void bindParams(PreparedStatement ps, List<Object> params) throws Exception {
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);

            if (param instanceof String) {
                ps.setString(i + 1, (String) param);
            } else if (param instanceof Double) {
                ps.setDouble(i + 1, (Double) param);
            } else if (param instanceof Integer) {
                ps.setInt(i + 1, (Integer) param);
            } else {
                ps.setObject(i + 1, param);
            }
        }
    }

    private void appendDescriptionKeywordCondition(StringBuilder sql, List<Object> params, ChatQuery query) {
        String keyword = query.getDescriptionKeyword();

        if (keyword == null || keyword.trim().isEmpty()) {
            return;
        }

        String normalized = keyword.trim().toLowerCase();

        if ("uv".equals(normalized)) {
            sql.append("AND (")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ?")
               .append(") ");
            params.add("%uv%");
            params.add("%sun protection%");
            params.add("%uv protection%");
            params.add("%uv resistant%");
            params.add("%omni-shade%");
        } else if ("quick-dry".equals(normalized)) {
            sql.append("AND (")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ?")
               .append(") ");
            params.add("%quick-dry%");
            params.add("%quick dry%");
            params.add("%dry quickly%");
            params.add("%omni-wick%");
        } else if ("waterproof".equals(normalized)) {
            sql.append("AND (")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ?")
               .append(") ");
            params.add("%waterproof%");
            params.add("%water resistant%");
            params.add("%rain proof%");
        } else if ("windproof".equals(normalized)) {
            sql.append("AND (")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.ProductName) LIKE ? OR ")
               .append("LOWER(p.ProductName) LIKE ?")
               .append(") ");
            params.add("%windproof%");
            params.add("%wind resistant%");
            params.add("%ao gio%");
            params.add("%jacket%");
        } else if ("trekking".equals(normalized)) {
            sql.append("AND (")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ?")
               .append(") ");
            params.add("%trekking%");
            params.add("%hiking%");
            params.add("%mountaineering%");
            params.add("%climbing%");
        } else if ("lightweight".equals(normalized)) {
            sql.append("AND (")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ?")
               .append(") ");
            params.add("%lightweight%");
            params.add("%ultralight%");
            params.add("%ultra-light%");
            params.add("%minimalist%");
        } else {
            sql.append("AND LOWER(p.Description) LIKE LOWER(?) ");
            params.add("%" + normalized + "%");
        }
    }

    private ChatProductResult mapChatProductResult(ResultSet rs) throws Exception {
        ChatProductResult item = new ChatProductResult();
        item.setProductId(rs.getInt("ProductID"));
        item.setProductName(rs.getString("ProductName"));
        item.setDescription(rs.getString("Description"));
        item.setCategoryName(rs.getString("CateName"));
        item.setImageUrl(rs.getString("ImageURL"));
        item.setMinPrice(rs.getDouble("MinPrice"));
        item.setMaxPrice(rs.getDouble("MaxPrice"));
        item.setTotalQuantity(rs.getInt("TotalQuantity"));
        return item;
    }
}