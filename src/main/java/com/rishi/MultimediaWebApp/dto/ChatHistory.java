package com.rishi.MultimediaWebApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistory {

        private String question;
        private String answer;
        private String timestampReference;
        private LocalDateTime createdAt;
}
