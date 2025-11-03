package com.example.muscle_market.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.muscle_market.domain.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

}
