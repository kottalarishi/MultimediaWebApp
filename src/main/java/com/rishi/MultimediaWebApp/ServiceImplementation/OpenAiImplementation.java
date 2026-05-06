package com.rishi.MultimediaWebApp.ServiceImplementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rishi.MultimediaWebApp.ServiceInterface.OpenAi;
import com.rishi.MultimediaWebApp.exception.FileProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAiImplementation implements OpenAi {

    @Value("${groqa.api.key}")
    private String groqApiKey;

    @Value("${groqa.api.url}")
    private String groqApiUrl;

    private final ObjectMapper objectMapper;

    private final WebClient webClient = WebClient.builder().build();

    @Override
    public String askQuestion(String context, String question) {

        if (context.length() > 4000) {
            context = context.substring(0, 4000);
        }
        try {
            String prompt = """
           You are an AI assistant answering questions ONLY from the given document.

     Rules:
- Do NOT make up information
- If answer is not present, say: "Not found in document"
- Be concise and structured
- Use bullet points when listing
- Prefer extracting exact info from context

Context:
%s

Question:
%s
""".formatted(context, question);
            return callGroq(prompt);

        } catch (Exception e) {
            log.error("Gemini askQuestion failed", e);
            throw new FileProcessingException("Failed to get answer from AI: " + e.getMessage());
        }
    }

    @Override
    public String summarize(String content) {

        if (content == null || content.trim().length() < 30) {
            return "Insufficient content to summarize";
        }

        if (content.length() > 5000) {
            content = content.substring(0, 5000);
        }


        try {
            String prompt = """
        Summarize the following content clearly and concisely.
        
        Rules:
        - Keep it to 3-5 sentences
        - Cover only the main points
        - Do not assume or add information not present in the content
        - Use plain readable language
        
        Content:
        %s
        """.formatted(content);

            return callGroq(prompt);

        } catch (Exception e) {

            log.error("Gemini summarize failed", e);

            e.printStackTrace();


            return "Summary unavailable due to AI service issue";
        }
    }

    private String callGroq(String prompt) {

        try {


            ObjectNode body =
                    objectMapper.createObjectNode();

            body.put(
                    "model",
                    "llama-3.3-70b-versatile"
            );

            ArrayNode messages =
                    body.putArray("messages");

            ObjectNode message =
                    messages.addObject();

            message.put("role", "user");

            message.put("content", prompt);

            System.out.println(body.toPrettyString());

            String response =
                    webClient.post()
                            .uri(groqApiUrl)
                            .header(
                                    "Authorization",
                                    "Bearer " + groqApiKey
                            )
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
                            .accept(
                                    MediaType.APPLICATION_JSON
                            )
                            .bodyValue(body)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

            System.out.println(response);

            JsonNode json =
                    objectMapper.readTree(response);

            JsonNode choices =
                    json.path("choices");

            if (
                    !choices.isArray()
                            || choices.isEmpty()
            ) {

                return "No AI response generated";
            }

            return choices.get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (
                org.springframework.web.reactive
                        .function.client
                        .WebClientResponseException
                        .TooManyRequests e
        ) {

            log.error("Groq rate limit exceeded");

            return "AI service busy. Try again shortly.";

        } catch (Exception e) {

            log.error("REAL GROQ ERROR", e);

            e.printStackTrace();

            return "AI response temporarily unavailable";
        }
    }
}