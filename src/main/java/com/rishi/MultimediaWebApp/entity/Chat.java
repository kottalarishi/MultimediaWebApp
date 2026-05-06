package com.rishi.MultimediaWebApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="chat_messages")
@Builder
public class Chat {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "document_id", nullable = false)
      private  Document document;

      @ManyToOne(fetch = FetchType.LAZY)
      @JoinColumn(name = "user_id",nullable = false)
      private User user;

      @Column(columnDefinition = "TEXT", nullable = false)
      private String question;

      @Column(columnDefinition = "TEXT")
      private String answer;
      @Column(name = "timestamp_reference")
      private String timeStampReference;
      @Column(name = "created_at")
      private LocalDateTime createdAt;

      @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }




}
