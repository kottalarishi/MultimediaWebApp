package com.rishi.MultimediaWebApp.ServiceInterface;

public interface WhisperInterface {

    String transcribeMedia(byte[] fileBytes, String filename);
    String extractTextFromWhisperResponse(String whisperJson);
    Double extractTimestamp(String whisperJson, String topic);
    String findTimestampForQuestion(
            String whisperJson,
            String question
    );
}
