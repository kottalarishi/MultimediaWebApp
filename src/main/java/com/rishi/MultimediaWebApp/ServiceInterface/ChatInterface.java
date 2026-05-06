package com.rishi.MultimediaWebApp.ServiceInterface;

import com.rishi.MultimediaWebApp.dto.ChatHistory;
import com.rishi.MultimediaWebApp.dto.ChatResponse;

import java.util.List;

public interface ChatInterface {

    ChatResponse chat(Long documentId, String question, String email);
    List<ChatHistory> getChatHistory(Long documentId, String email);
}
