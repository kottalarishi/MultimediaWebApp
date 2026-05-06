package com.rishi.MultimediaWebApp.ServiceImplementation;

import com.rishi.MultimediaWebApp.Mapper.ChatMapper;
import com.rishi.MultimediaWebApp.Mapper.DocumentMapper;
import com.rishi.MultimediaWebApp.Repository.ChatRepository;
import com.rishi.MultimediaWebApp.Repository.DocumentRepository;
import com.rishi.MultimediaWebApp.Repository.UserRepository;
import com.rishi.MultimediaWebApp.ServiceInterface.ChatInterface;
import com.rishi.MultimediaWebApp.ServiceInterface.OpenAi;
import com.rishi.MultimediaWebApp.ServiceInterface.WhisperInterface;
import com.rishi.MultimediaWebApp.Util.DocumentUtil;
import com.rishi.MultimediaWebApp.dto.ChatHistory;
import com.rishi.MultimediaWebApp.dto.ChatResponse;
import com.rishi.MultimediaWebApp.entity.Chat;
import com.rishi.MultimediaWebApp.entity.Document;
import com.rishi.MultimediaWebApp.entity.FileType;
import com.rishi.MultimediaWebApp.entity.User;
import com.rishi.MultimediaWebApp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatInterface {

    private final ChatRepository chatRepository;
    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final ChatMapper chatMapper;
   private final DocumentUtil documentutil;
    private final OpenAi openAiService;

    private final WhisperInterface whisperInterface;
    private final UserRepository userRepository;

    @Override
    public ChatResponse chat(Long documentId, String question, String email) {

        Document document = documentRepository
                .findByIdAndUserEmail(documentId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        List<String> chunks = documentutil.splitIntoChunks(document.getExtractedText());


        List<String> relevantChunks = documentutil.getRelevantChunks(chunks, question);


        String context;
        if (relevantChunks.isEmpty()) {
            context = document.getExtractedText()
                    .substring(0, Math.min(1000, document.getExtractedText().length()));
        } else {
            context = String.join("\n", relevantChunks);
        }


        String answer = openAiService.askQuestion(context, question);
        String timestamp = null;

        if (
                document.getFileType() == FileType.AUDIO
                        ||
                        document.getFileType() == FileType.VIDEO
        ) {

            timestamp =
                    whisperInterface
                            .findTimestampForQuestion(
                                    document.getTranscriptionJson(),
                                    question
                            );
        }

        Chat chat = chatMapper.toChat(document, question, answer, timestamp);


        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        chat.setUser(user);

        chatRepository.save(chat);

        ChatResponse response = new ChatResponse();
        response.setAnswer(answer);
        response.setTimestampReference(timestamp);
        response.setFileType(document.getFileType().name());

        return response;
    }

    @Override
    public List<ChatHistory> getChatHistory(Long documentId, String email) {
        Document document = documentRepository
                .findByIdAndUserEmail(documentId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));
        List<Chat> chats = chatRepository.findByDocumentIdOrderByCreatedAtAsc(documentId);

        if (chats.isEmpty()) {
            throw new ResourceNotFoundException("No chat history found for document: " + documentId);
        }

        return chats.stream()
                .map(chatMapper::toChatHistory)
                .collect(Collectors.toList());
    }
}

