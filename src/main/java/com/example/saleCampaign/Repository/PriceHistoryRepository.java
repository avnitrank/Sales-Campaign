package com.example.saleCampaign.Repository;
 
import com.example.saleCampaign.Model.PriceHistory;
import com.example.saleCampaign.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Integer> {
    Optional<PriceHistory> findPriceHistoryByLocalDate(LocalDate localDate);

    @Query(value = "SELECT * FROM price_history WHERE product_id = :productId AND date = :date ORDER BY id DESC LIMIT 1", nativeQuery = true)
    PriceHistory findTopByProductIdAndDate(@Param("productId") int productId, @Param("date") LocalDate date);

    @Query(value = "select * from price_history where product_id = :productId",nativeQuery = true)
    List<PriceHistory> findByProductId(@Param("productId") int productId);

    @Query(value = "select * from price_history where product_id = :productId order by date desc limit 1",nativeQuery = true)
    PriceHistory findByProductIdOrderByDate(@Param("productId")int productId);

    @Query(value = "select * from price_history where product_id = :productId and date< :date order by date desc limit 1",nativeQuery = true)
    PriceHistory findByProductIdAndBeforeDate(@Param("productId")int productId,@Param("date") LocalDate date);
}
