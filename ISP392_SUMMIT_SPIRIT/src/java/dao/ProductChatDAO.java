package dao;

import dto.ChatProductResult;
import dto.ChatQuery;
import dto.VariantStockResult;
import java.util.List;

public interface ProductChatDAO {

    List<ChatProductResult> findProductsByName(String keyword) throws Exception;

    List<ChatProductResult> findProductsByFilters(ChatQuery query) throws Exception;

    List<VariantStockResult> getVariantStock(int productId) throws Exception;

    Integer findProductIdByKeyword(String keyword) throws Exception;
}