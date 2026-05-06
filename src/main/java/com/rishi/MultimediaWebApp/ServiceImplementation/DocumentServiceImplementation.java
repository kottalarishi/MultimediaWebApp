package com.rishi.MultimediaWebApp.ServiceImplementation;

import com.rishi.MultimediaWebApp.Mapper.DocumentMapper;
import com.rishi.MultimediaWebApp.Repository.DocumentRepository;
import com.rishi.MultimediaWebApp.Repository.UserRepository;
import com.rishi.MultimediaWebApp.ServiceInterface.DocumentInterface;
import com.rishi.MultimediaWebApp.ServiceInterface.OpenAi;
import com.rishi.MultimediaWebApp.ServiceInterface.WhisperInterface;
import com.rishi.MultimediaWebApp.Util.DocumentUtil;
import com.rishi.MultimediaWebApp.dto.DocumentResponse;
import com.rishi.MultimediaWebApp.entity.Document;
import com.rishi.MultimediaWebApp.entity.FileType;
import com.rishi.MultimediaWebApp.entity.TranscriptionStatus;
import com.rishi.MultimediaWebApp.entity.User;
import com.rishi.MultimediaWebApp.exception.FileProcessingException;
import com.rishi.MultimediaWebApp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class DocumentServiceImplementation implements DocumentInterface {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final DocumentUtil documentUtil;
    private final OpenAi openAi;
    private final WhisperInterface whisperInterface;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public DocumentResponse uploadDocument(MultipartFile file, String email) {

        try {

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Path uploadPath = Paths.get(uploadDir);

            Files.createDirectories(uploadPath);

            String uniqueFilename =
                    UUID.randomUUID() + "_" + file.getOriginalFilename();

            Path filePath = uploadPath.resolve(uniqueFilename);

            Files.write(filePath, file.getBytes());

            FileType fileType = FileType.valueOf(
                    documentUtil.determineFileType(
                            file.getContentType(),
                            file.getOriginalFilename()
                    )
            );

            Document document = documentMapper.toDocument(
                    file,
                    filePath.toString().replace("\\", "/"),
                    uniqueFilename,
                    fileType
            );

            document.setUser(user);

            document.setTranscriptionStatus(
                    TranscriptionStatus.PROCESSING
            );


            documentRepository.save(document);

            try {

                String text;

                if (fileType == FileType.PDF) {

                    text = documentUtil.extractPdfText(file.getBytes());

                } else {

                    String whisperJson =
                            whisperInterface.transcribeMedia(
                                    file.getBytes(),
                                    file.getOriginalFilename()
                            );
                    document.setTranscriptionJson(whisperJson);

                    text = whisperInterface
                            .extractTextFromWhisperResponse(whisperJson);
                }

                text = sanitizeText(text);

                document.setExtractedText(text);

                String summary = openAi.summarize(text);

                summary = sanitizeText(summary);

                document.setSummary(summary);

                document.setTranscriptionStatus(
                        TranscriptionStatus.COMPLETED
                );

            } catch (Exception e) {

                log.error("AI processing failed", e);

                document.setSummary(
                        "Summary generation unavailable"
                );

                document.setTranscriptionStatus(
                        TranscriptionStatus.COMPLETED
                );
            }

            documentRepository.save(document);

            return documentMapper.toDocumentResponse(document);

        } catch (Exception e) {

            log.error("Document upload failed", e);

            throw new FileProcessingException(
                    "Failed to upload document: " + e.getMessage()
            );
        }
    }

    @Override
    public List<DocumentResponse> getUserDocuments(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Document> documents =
                documentRepository.findByUser(user);

        if (documents.isEmpty()) {

            throw new ResourceNotFoundException(
                    "No documents found"
            );
        }

        return documents.stream()
                .map(documentMapper::toDocumentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public String summarize(Long documentId, String email) {

        Document document = documentRepository
                .findByIdAndUserEmail(documentId, email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Document not found"
                        )
                );

        if (document.getSummary() != null) {

            return document.getSummary();
        }

        String summary =
                openAi.summarize(document.getExtractedText());

        summary = sanitizeText(summary);

        document.setSummary(summary);

        documentRepository.save(document);

        return summary;
    }

    private String sanitizeText(String text) {

        if (text == null) {

            return "";
        }

        return text
                .replace("\u0000", "")
                .trim();
    }
}