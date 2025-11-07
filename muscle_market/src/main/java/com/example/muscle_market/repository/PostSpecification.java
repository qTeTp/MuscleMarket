package com.example.muscle_market.repository;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import com.example.muscle_market.domain.Post;
import com.example.muscle_market.enums.PostStatus;

public class PostSpecification {
    public static Specification<Post> filterAndSearch(
        Long curUserId,
        Long sportId,
        Boolean isBungae,
        String keyword
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 기본적으로 ACTIVE고 DELETED가 아닌 글들 조회
            Predicate activePosts = cb.equal(root.get("status"), PostStatus.ACTIVE);
            predicates.add(cb.notEqual(root.get("status"), PostStatus.DELETED));

            // 로그인한 사용자는 active와 본인의 hidden글 접근 가능
            if (curUserId != null) {
                Predicate myHiddenPosts = cb.and(
                    cb.equal(root.get("status"), PostStatus.HIDDEN),
                    cb.equal(root.join("author").get("id"), curUserId)
                );

                predicates.add(cb.or(activePosts, myHiddenPosts));
            } else {
                predicates.add(activePosts);
            }

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
