package dao;

import dto.ChatProductResult;
import dto.ChatQuery;
import dto.VariantStockResult;
import java.util.List;

public interface ProductChatDAO {

    List<ChatProductResult> findProductsByName(String keyword) throws Exception;

    List<ChatProductResult> findProductsByFilters(ChatQuery query) throws Exception;

    List<ChatProductResult> findAllProductsByFilters(ChatQuery query) throws Exception;

    ChatProductResult findCheapestProduct() throws Exception;

    ChatProductResult findMostExpensiveProduct() throws Exception;

    ChatProductResult findBestSellingProduct() throws Exception;

    ChatProductResult findCheapestProductByCategory(String category) throws Exception;

    ChatProductResult findMostExpensiveProductByCategory(String category) throws Exception;

    ChatProductResult findCheapestProductByKeyword(String keyword) throws Exception;

    ChatProductResult findMostExpensiveProductByKeyword(String keyword) throws Exception;

    List<VariantStockResult> getVariantStock(int productId) throws Exception;

    Integer findProductIdByKeyword(String keyword) throws Exception;
}