package com.rishi.MultimediaWebApp.Controller;

import com.rishi.MultimediaWebApp.ServiceInterface.ChatInterface;
import com.rishi.MultimediaWebApp.ServiceInterface.DocumentInterface;
import com.rishi.MultimediaWebApp.Util.ApiResponse;
import com.rishi.MultimediaWebApp.dto.*;
import com.rishi.MultimediaWebApp.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentInterface documentService;
    private final ChatInterface chatService;


    private User getUser(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        if (user == null) {
            throw new RuntimeException("Unauthorized");
        }
        return user;
    }

    @PostMapping("/documents/upload")
    public ResponseEntity<ApiResponse<DocumentResponse>> upload(
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        User user = getUser(request);

        DocumentResponse doc = documentService.uploadDocument(file, user.getEmail());

        ApiResponse<DocumentResponse> response =
                new ApiResponse<>(201, "Document uploaded successfully", doc);

        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @GetMapping("/documents")
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getAllDocuments(
            HttpServletRequest request) {

        User user = getUser(request);

        List<DocumentResponse> docs =
                documentService.getUserDocuments(user.getEmail());

        ApiResponse<List<DocumentResponse>> response =
                new ApiResponse<>(200, "Documents fetched successfully", docs);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/documents/{id}/summarize")
    public ResponseEntity<ApiResponse<String>> summarize(
            @PathVariable Long id,
            HttpServletRequest request) {

        User user = getUser(request);

        String summary = documentService.summarize(id, user.getEmail());

        ApiResponse<String> response =
                new ApiResponse<>(200, "Summary generated successfully", summary);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/documents/{id}/chat")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @PathVariable Long id,
            @RequestBody ChatRequest requestBody,
            HttpServletRequest request) {

        User user = getUser(request);

        ChatResponse chat =
                chatService.chat(id, requestBody.getQuestion(), user.getEmail());

        ApiResponse<ChatResponse> response =
                new ApiResponse<>(200, "Chat response generated", chat);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/documents/{id}/history")
    public ResponseEntity<ApiResponse<List<ChatHistory>>> history(
            @PathVariable Long id,
            HttpServletRequest request) {

        User user = getUser(request);

        List<ChatHistory> history =
                chatService.getChatHistory(id, user.getEmail());

        ApiResponse<List<ChatHistory>> response =
                new ApiResponse<>(200, "Chat history fetched", history);

        return ResponseEntity.ok(response);
    }
}