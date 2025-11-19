package com.example.muscle_market.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.muscle_market.domain.Comment;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class CommentSpecification {
    // 모든 댓글 가져오는 로직
    public static Specification<Comment> getAllComments(Long postId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 해당 게시글의 댓글
            predicates.add(cb.equal(root.get("post").get("postId"), postId));

            addSoftDeletePredicate(root, query, cb, predicates);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addSoftDeletePredicate(Root<Comment> root, CriteriaQuery<?> query, CriteriaBuilder cb, List<Predicate> predicates) {
        // 삭제 상태가 아닌 댓글
        Predicate notDeleted = cb.isNull(root.get("deletedAt"));
        // 삭제는 됐지만 자식이 있는 댓글
        Predicate isDeleted = cb.isNotNull(root.get("deletedAt"));
        
        // 삭제되지 않은 자식이 있는지 확인하고, 자식이 더 이상 없으면 삭제된 댓글은 포함 안시키기
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Comment> subRoot = subquery.from(Comment.class);
        subquery.select(cb.count(subRoot));
        subquery.where(
            cb.equal(subRoot.get("parent"), root),
            cb.isNull(subRoot.get("deletedAt"))
        );

        Predicate hasActiveChildren = cb.gt(subquery, 0L);
        Predicate deletedButHasChildren = cb.and(isDeleted, hasActiveChildren);

        predicates.add(cb.or(notDeleted, deletedButHasChildren));
    }
}
