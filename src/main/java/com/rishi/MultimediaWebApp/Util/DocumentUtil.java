package com.rishi.MultimediaWebApp.Util;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentUtil {


    public String extractPdfText(byte[] bytes) throws IOException {

        try (PDDocument pdf = Loader.loadPDF(bytes)) {
            return new PDFTextStripper().getText(pdf);
        }
    }









    public String determineFileType(String contentType, String filename) {
        if (contentType.contains("pdf")) return "PDF";
        if (contentType.contains("audio") || contentType.contains("mpeg") ||
                (filename != null && (filename.endsWith(".mp3") || filename.endsWith(".wav") || filename.endsWith(".m4a")))) return "AUDIO";
        if (contentType.contains("video") ||
                (filename != null && (filename.endsWith(".mp4") || filename.endsWith(".mov") || filename.endsWith(".avi")))) return "VIDEO";
        return "PDF";
    }

    public Double parseTimestampFromAnswer(String answer) {
        try {
            if (answer.contains("Timestamp:")) {
                String[] parts = answer.split("Timestamp:");
                String timePart = parts[1].trim().split("\\s+")[0].replaceAll("[^0-9.]", "");
                return Double.parseDouble(timePart);
            }
        } catch (Exception ignored) {}
        return null;
    }


    public List<String> splitIntoChunks(String text) {
        int chunkSize = 500;
        List<String> chunks = new ArrayList<>();

        for (int i = 0; i < text.length(); i += chunkSize) {
            chunks.add(text.substring(i, Math.min(text.length(), i + chunkSize)));
        }

        return chunks;
    }

    private int scoreChunk(String chunk, String[] keywords) {
        int score = 0;
        String lower = chunk.toLowerCase();

        for (String word : keywords) {
            if (lower.contains(word)) {
                score++;
            }
        }
        return score;
    }
    public List<String> getRelevantChunks(List<String> chunks, String question) {

        String[] keywords = question.toLowerCase().split(" ");

        return chunks.stream()
                .sorted((c1, c2) -> {
                    int score1 = scoreChunk(c1, keywords);
                    int score2 = scoreChunk(c2, keywords);
                    return Integer.compare(score2, score1); // descending
                })
                .limit(6)
                .toList();
    }

}
