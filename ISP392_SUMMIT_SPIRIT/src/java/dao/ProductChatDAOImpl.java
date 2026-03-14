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
        List<ChatProductResult> list = new ArrayList<>();

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
                + "WHERE p.Status = ? AND LOWER(p.ProductName) LIKE LOWER(?) "
                + "GROUP BY p.ProductID, p.ProductName, p.Description, c.CateName, img.ImageURL "
                + "ORDER BY p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");
            ps.setString(2, "%" + keyword.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChatProductResult item = mapChatProductResult(rs);
                    list.add(item);
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
                + "WHERE p.Status = ? AND LOWER(p.ProductName) LIKE LOWER(?) "
                + "ORDER BY p.ProductID ASC";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "Active");
            ps.setString(2, "%" + keyword.trim() + "%");

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
        List<VariantStockResult> list = new ArrayList<>();

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
        List<ChatProductResult> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TOP 3 ")
           .append("p.ProductID, ")
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

        List<Object> params = new ArrayList<>();
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

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChatProductResult item = mapChatProductResult(rs);
                    list.add(item);
                }
            }
        }

        return list;
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
        } else if ("trekking".equals(normalized)) {
            sql.append("AND (")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ? OR ")
               .append("LOWER(p.Description) LIKE ?")
               .append(") ");
            params.add("%trekking%");
            params.add("%hiking%");
            params.add("%mountaineering%");
            params.add("%climbing%");
            params.add("%outdoor%");
            params.add("%camping%");
            params.add("%picnic%");
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