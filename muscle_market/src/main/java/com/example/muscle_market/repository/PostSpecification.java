package com.example.muscle_market.repository;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import com.example.muscle_market.domain.Post;

public class PostSpecification {
    public static Specification<Post> filterAndSearch(
        Long sportId,
        Boolean isBungae,
        String keyword
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (sportId != null) {
                predicates.add(cb.equal(root.get("sportId"), sportId));
            }

            if (isBungae != null) {
                predicates.add(cb.equal(root.get("isBungae"), isBungae));
            }

            if (keyword != null && !keyword.isEmpty()) {
                Predicate titleLike = cb.like(root.get("title"), "%" + keyword + "%");
                Predicate contentLike = cb.like(root.get("content"), "%" + keyword + "%");
                predicates.add(cb.or(titleLike, contentLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
