package com.rishi.MultimediaWebApp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {


   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false)
    private String fileName;


    @Column(name = "original_filename", nullable = false)
    private String originalFileName;

  @Column(columnDefinition = "TEXT")
  private String transcriptionJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String extractedText;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Enumerated(EnumType.STRING)
    private  TranscriptionStatus transcriptionStatus;


    @Column(name = "created_at")
    private LocalDateTime createdAt;




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        if (transcriptionStatus == null){
            transcriptionStatus=TranscriptionStatus.PENDING;}
    }





}
