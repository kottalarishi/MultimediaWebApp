package com.rishi.MultimediaWebApp.Repository;

import com.rishi.MultimediaWebApp.entity.Document;
import com.rishi.MultimediaWebApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository  extends JpaRepository<Document ,Long> {

    List<Document> findByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<Document> findByIdAndUserId(Long id, Long userId);
    Optional<Document> findByIdAndUserEmail(Long id, String email);

    List<Document> findByUser(User user);
}
