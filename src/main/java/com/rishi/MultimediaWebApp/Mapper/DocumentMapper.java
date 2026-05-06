package com.rishi.MultimediaWebApp.Mapper;

import com.rishi.MultimediaWebApp.dto.DocumentResponse;
import com.rishi.MultimediaWebApp.entity.Document;
import com.rishi.MultimediaWebApp.entity.FileType;
import com.rishi.MultimediaWebApp.entity.TranscriptionStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class DocumentMapper {


    public Document toDocument(MultipartFile file, String filePath, String uniqueFilename, FileType fileType) {
        Document document = new Document();
        document.setFileName(uniqueFilename);
        document.setOriginalFileName(file.getOriginalFilename());
        document.setFileType(fileType);
        document.setFilePath(filePath);
        document.setTranscriptionStatus(TranscriptionStatus.PENDING);
        return document;
    }
    public DocumentResponse toDocumentResponse(Document document) {

        DocumentResponse dto = new DocumentResponse();
        dto.setId(document.getId());
        dto.setOriginalFilename(document.getOriginalFileName());
        dto.setFileType(document.getFileType().name());
        dto.setTranscriptionStatus(
                document.getTranscriptionStatus().name()
        );
        dto.setSummary(document.getSummary());
        dto.setExtractedText(document.getExtractedText());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setFileName(document.getFileName());
        return dto;
    }


}
