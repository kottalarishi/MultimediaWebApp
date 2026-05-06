package com.rishi.MultimediaWebApp.ServiceImplementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rishi.MultimediaWebApp.ServiceInterface.WhisperInterface;
import com.rishi.MultimediaWebApp.exception.FileProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RequiredArgsConstructor
@Service
public class WhisperServiceImplementation implements WhisperInterface {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String groqModel;

    private final ObjectMapper objectMapper;

    @Override
    public String transcribeMedia(byte[] fileBytes, String filename) {

        try{
            MultiValueMap<String ,Object>  formData= new LinkedMultiValueMap<>();

            ByteArrayResource arrayResource= new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
            formData.add("file", arrayResource);
            formData.add("model", groqModel);
            formData.add("response_format", "verbose_json");
            formData.add("timestamp_granularities[]", "segment");


            String response = WebClient.builder()
                    .baseUrl(groqApiUrl)
                    .defaultHeader("Authorization", "Bearer " + groqApiKey)
                    .build()
                    .post()
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return response;
            }catch (Exception e) {
            log.error("Groq Whisper transcription failed: {}", e.getMessage(), e);
            throw new FileProcessingException("Failed to transcribe audio");
        }


    }

    @Override
    public String extractTextFromWhisperResponse(String whisperJson) {
        try {
            JsonNode json = objectMapper.readTree(whisperJson);
            StringBuilder text = new StringBuilder();

            JsonNode segments = json.path("segments");
            if (segments.isArray()) {
                for (JsonNode segment : segments) {
                    double start = segment.path("start").asDouble();
                    String segText = segment.path("text").asText();
                    text.append(String.format("[%.1fs] %s\n", start, segText));
                }
            } else {
                text.append(json.path("text").asText());
            }

            return text.toString();

        } catch (Exception e) {
            log.error("Failed to parse Whisper response", e);
            throw new FileProcessingException("Failed to parse transcription response");
        }
    }


    @Override
    public Double extractTimestamp(String whisperJson, String topic) {
        try {
            JsonNode json = objectMapper.readTree(whisperJson);
            JsonNode segments = json.path("segments");

            if (segments.isArray()) {
                for (JsonNode segment : segments) {
                    String text = segment.path("text").asText().toLowerCase();
                    if (text.contains(topic.toLowerCase())) {
                        return segment.path("start").asDouble();
                    }
                }
            }
            return null;

        } catch (Exception e) {
            log.error("Failed to extract timestamp", e);
            return null;
        }
    }

    private String formatTimestamp(
            double seconds
    ) {

        int hrs = (int) seconds / 3600;

        int mins =
                ((int) seconds % 3600) / 60;

        int secs =
                (int) seconds % 60;

        return String.format(
                "%02d:%02d:%02d",
                hrs,
                mins,
                secs
        );
    }

    @Override
    public String findTimestampForQuestion(
            String whisperJson,
            String question
    ) {

        try {

            JsonNode root =
                    objectMapper.readTree(whisperJson);

            JsonNode segments =
                    root.path("segments");

            for (JsonNode segment : segments) {

                String text =
                        segment.path("text")
                                .asText()
                                .toLowerCase();

                if (text.contains(
                        question.toLowerCase()
                )) {

                    double start =
                            segment.path("start")
                                    .asDouble();

                    return formatTimestamp(start);
                }
            }

        } catch (Exception e) {

            log.error(
                    "Timestamp extraction failed",
                    e
            );
        }

        return null;
    }



}


