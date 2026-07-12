package com.recruitment.repository;

import com.recruitment.model.Message;
import com.recruitment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE (m.sender = :u1 AND m.receiver = :u2) OR (m.sender = :u2 AND m.receiver = :u1) ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("u1") User u1, @Param("u2") User u2);

    @Query("SELECT m FROM Message m WHERE m.receiver = :user AND m.isRead = false")
    List<Message> findUnreadForUser(@Param("user") User user);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :user AND m.isRead = false")
    long countUnreadForUser(@Param("user") User user);

    @Query("SELECT m FROM Message m WHERE m.id IN (" +
            "SELECT MAX(m2.id) FROM Message m2 WHERE m2.sender.id = :userId OR m2.receiver.id = :userId " +
            "GROUP BY CASE WHEN m2.sender.id = :userId THEN m2.receiver.id ELSE m2.sender.id END) " +
            "ORDER BY m.createdAt DESC")
    List<Message> findLatestMessagesPerConversation(@Param("userId") Long userId);
}
