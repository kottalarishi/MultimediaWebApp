package com.rishi.MultimediaWebApp.ServiceInterface;

public interface OpenAi {
    String askQuestion(String context, String question);
    String summarize(String content);
}
