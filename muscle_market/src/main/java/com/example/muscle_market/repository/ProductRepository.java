package com.example.muscle_market.repository;

import com.example.muscle_market.domain.Product;
import com.example.muscle_market.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 페이징 지원
    @Query("SELECT p FROM Product p JOIN FETCH p.sport WHERE p.status != 'DELETE'")
    Page<Product> findAllWithSport(Pageable pageable);

    // 운동 종목별 페이징 지원
    @Query("SELECT p FROM Product p JOIN FETCH p.sport s WHERE s.id = :sportId AND p.status != 'DELETE'")
    Page<Product> findAllBySportIdWithSport(@Param("sportId") Long sportId, Pageable pageable);

    // 통합 검색 쿼리
    // 게시물 키워드 검색
    // sportId null일 시 전체 카테고리 검색, !null일 시 특정 카테고리 검색
    @Query("SELECT p FROM Product p JOIN FETCH p.sport s WHERE p.status != 'DELETE' AND (:sportId IS NULL OR s.id = :sportId) AND (p.title LIKE %:keyword% OR p.description LIKE %:keyword%) ORDER BY p.createdAt DESC")
    Page<Product> searchByKeywordAndSport(@Param("sportId") Long sportId, @Param("keyword") String keyword, Pageable pageable);

    // 작성자, 상태, 삭제가 안된 상태
    @Query("SELECT p FROM Product p JOIN FETCH p.sport s " +
            "WHERE p.user.id = :authorId " +
            "AND p.status = :status " +
            "AND p.status != 'DELETE' " +
            "ORDER BY p.createdAt DESC")
    Page<Product> findAllByAuthorIdAndStatus(
            @Param("authorId") Long authorId,
            @Param("status") TransactionStatus status, // ENUM은 JPQL에서 String으로 처리
            Pageable pageable);

    // 제목에 키워드 포함된 상품 검색
    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findByTitleContainingIgnoreCaseWithImages(@Param("keyword")String title);
}
