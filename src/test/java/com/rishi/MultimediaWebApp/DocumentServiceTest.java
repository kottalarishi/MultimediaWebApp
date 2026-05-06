package com.rishi.MultimediaWebApp;

import com.rishi.MultimediaWebApp.Mapper.DocumentMapper;
import com.rishi.MultimediaWebApp.Repository.DocumentRepository;
import com.rishi.MultimediaWebApp.Repository.UserRepository;
import com.rishi.MultimediaWebApp.ServiceImplementation.DocumentServiceImplementation;
import com.rishi.MultimediaWebApp.ServiceInterface.OpenAi;
import com.rishi.MultimediaWebApp.ServiceInterface.WhisperInterface;
import com.rishi.MultimediaWebApp.Util.DocumentUtil;
import com.rishi.MultimediaWebApp.dto.DocumentResponse;
import com.rishi.MultimediaWebApp.entity.Document;
import com.rishi.MultimediaWebApp.entity.FileType;
import com.rishi.MultimediaWebApp.entity.TranscriptionStatus;
import com.rishi.MultimediaWebApp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private DocumentUtil documentUtil;

    @Mock
    private OpenAi openAi;

    @Mock
    private WhisperInterface whisperInterface;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DocumentServiceImplementation documentService;

    private User user;

    @BeforeEach
    void setup() {

        ReflectionTestUtils.setField(
                documentService,
                "uploadDir",
                "./uploads"
        );

        user = User.builder()
                .id(1L)
                .name("Rishi")
                .email("rishi@test.com")
                .build();
    }

    @Test
    void getUserDocuments_success() {

        Document document = Document.builder()
                .id(1L)
                .originalFileName("test.pdf")
                .fileType(FileType.PDF)
                .transcriptionStatus(TranscriptionStatus.COMPLETED)
                .user(user)
                .build();

        DocumentResponse response =
                DocumentResponse.builder()
                        .id(1L)
                        .originalFilename("test.pdf")
                        .build();

        when(userRepository.findByEmail("rishi@test.com"))
                .thenReturn(Optional.of(user));

        when(documentRepository.findByUser(user))
                .thenReturn(List.of(document));

        when(documentMapper.toDocumentResponse(document))
                .thenReturn(response);

        List<DocumentResponse> result =
                documentService.getUserDocuments("rishi@test.com");

        assertEquals(1, result.size());

        assertEquals("test.pdf",
                result.get(0).getOriginalFilename());
    }

    @Test
    void summarize_returnsExistingSummary() {

        Document document = Document.builder()
                .id(1L)
                .summary("Existing summary")
                .user(user)
                .build();

        when(documentRepository.findByIdAndUserEmail(
                1L,
                "rishi@test.com"
        )).thenReturn(Optional.of(document));

        String result =
                documentService.summarize(
                        1L,
                        "rishi@test.com"
                );

        assertEquals("Existing summary", result);

        verify(openAi, never()).summarize(any());
    }

    @Test
    void summarize_generatesNewSummary() {

        Document document = Document.builder()
                .id(1L)
                .extractedText("Sample text")
                .user(user)
                .build();

        when(documentRepository.findByIdAndUserEmail(
                1L,
                "rishi@test.com"
        )).thenReturn(Optional.of(document));

        when(openAi.summarize("Sample text"))
                .thenReturn("Generated summary");

        String result =
                documentService.summarize(
                        1L,
                        "rishi@test.com"
                );

        assertEquals("Generated summary", result);
    }

    @Test
    void sanitizeText_removesNullCharacters() {

        String dirty =
                "hello\u0000world";

        String cleaned =
                ReflectionTestUtils.invokeMethod(
                        documentService,
                        "sanitizeText",
                        dirty
                );

        assertEquals("helloworld", cleaned);
    }

    @Test
    void uploadDocument_pdf_success() throws Exception {

        MockMultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.pdf",
                        "application/pdf",
                        "dummy".getBytes()
                );

        Document document = Document.builder().build();

        DocumentResponse mappedResponse = DocumentResponse.builder()
                .id(1L)
                .originalFilename("test.pdf")
                .build();

        when(userRepository.findByEmail("rishi@test.com"))
                .thenReturn(Optional.of(user));

        when(documentUtil.determineFileType(any(), any()))
                .thenReturn("PDF");

        when(documentMapper.toDocument(any(), any(), any(), any()))
                .thenReturn(document);

        doReturn("PDF content")
                .when(documentUtil)
                .extractPdfText(any());

        when(openAi.summarize(any()))
                .thenReturn("Summary");

        when(documentRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        when(documentMapper.toDocumentResponse(any()))   // ← this was missing
                .thenReturn(mappedResponse);

        DocumentResponse response =
                documentService.uploadDocument(file, "rishi@test.com");

        assertNotNull(response);
        assertEquals("test.pdf", response.getOriginalFilename());
    }
}