package com.rishi.MultimediaWebApp.Mapper;

import com.rishi.MultimediaWebApp.dto.ChatHistory;
import com.rishi.MultimediaWebApp.entity.Chat;
import com.rishi.MultimediaWebApp.entity.Document;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public Chat toChat(Document document, String question, String answer, String timestampReference) {
        Chat chat = new Chat();
        chat.setDocument(document);
        chat.setQuestion(question);
        chat.setAnswer(answer);
        chat.setTimeStampReference(timestampReference);
        return chat;
    }

    public ChatHistory toChatHistory(Chat chat) {
        ChatHistory dto = new ChatHistory();
        dto.setQuestion(chat.getQuestion());
        dto.setAnswer(chat.getAnswer());
        dto.setTimestampReference(chat.getTimeStampReference());
        dto.setCreatedAt(chat.getCreatedAt());
        return dto;
    }
}
