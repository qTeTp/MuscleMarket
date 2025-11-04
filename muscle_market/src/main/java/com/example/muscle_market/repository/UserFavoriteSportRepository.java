package com.example.muscle_market.repository;

import com.example.muscle_market.domain.User;
import com.example.muscle_market.domain.UserFavoriteSport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFavoriteSportRepository extends JpaRepository<UserFavoriteSport,Long> {
    Optional<UserFavoriteSport> findByUser(User user);
}
