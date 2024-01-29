package com.example.release_tracking_bot.repository;

import com.example.release_tracking_bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("select u from User u left join fetch u.artists where u.chatId = :chatId")
    User findByChatId(@Param(value = "chatId") String chatId);

    @Query("select user from User user left join fetch user.artists")
    List<User> findAll();
}
