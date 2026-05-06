package com.rishi.MultimediaWebApp.ServiceInterface;

import com.rishi.MultimediaWebApp.dto.ChatHistory;
import com.rishi.MultimediaWebApp.dto.ChatResponse;
import com.rishi.MultimediaWebApp.dto.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentInterface {

    DocumentResponse uploadDocument(MultipartFile file, String email);

    List<DocumentResponse> getUserDocuments(String email);

   String summarize(Long documentId, String email);






}
