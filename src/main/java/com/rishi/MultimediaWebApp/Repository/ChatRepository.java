package com.rishi.MultimediaWebApp.Repository;


import com.rishi.MultimediaWebApp.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository  extends JpaRepository<Chat ,Long> {

    List<Chat> findByDocumentIdAndUserIdOrderByCreatedAtAsc(Long documentId, Long userId);
    List<Chat> findByDocumentIdOrderByCreatedAtAsc(Long documentId);
}
