package com.example.muscle_market.repository;

import com.example.muscle_market.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 페이징 지원
    @Query("SELECT p FROM Product p JOIN FETCH p.sport")
    Page<Product> findAllWithSport(Pageable pageable);

    // 운동 종목별 페이징 지원
    @Query("SELECT p FROM Product p JOIN FETCH p.sport s WHERE s.id = :sportId")
    Page<Product> findAllBySportIdWithSport(@Param("sportId") Long sportId, Pageable pageable);
}
