package com.example.muscle_market.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.example.muscle_market.domain.Comment;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CommentSpecification {
    // 모든 댓글 가져오는 로직
    public static Specification<Comment> getAllComments(Long postId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. 해당 게시글의 댓글
            predicates.add(cb.equal(root.get("post").get("postId"), postId));

            addSoftDeletePredicate(root, cb, predicates);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addSoftDeletePredicate(Root<Comment> root, CriteriaBuilder cb, List<Predicate> predicates) {
        // 삭제 상태가 아닌 댓글
        Predicate notDeleted = cb.isNull(root.get("deletedAt"));
        // 삭제는 됐지만 자식이 있는 댓글
        Predicate isDeleted = cb.isNotNull(root.get("deletedAt"));
        Predicate hasChildren = cb.isNotEmpty(root.get("children"));
        Predicate deletedButHasChildren = cb.and(isDeleted, hasChildren);

        predicates.add(cb.or(notDeleted, deletedButHasChildren));
    }
}
