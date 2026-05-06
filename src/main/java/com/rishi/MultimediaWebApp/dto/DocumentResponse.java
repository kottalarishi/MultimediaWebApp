package com.rishi.MultimediaWebApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentResponse {

    private Long id;

    private String originalFilename;

    private String fileType;

    private String transcriptionStatus;

    private String summary;

    private String extractedText;

    private String fileName;

    private LocalDateTime createdAt;
}