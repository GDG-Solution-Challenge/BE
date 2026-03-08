package com.example.mamatolmi.domain.chat.service;

import com.example.mamatolmi.domain.chat.dto.request.ChatRequestDTO;
import com.example.mamatolmi.domain.chat.dto.response.ChatResponseDTO;
import com.example.mamatolmi.domain.chat.entity.ChatMessage;
import com.example.mamatolmi.domain.chat.entity.ChatRoom;
import com.example.mamatolmi.domain.chat.enums.SenderRole;
import com.example.mamatolmi.domain.chat.exception.ChatException;
import com.example.mamatolmi.domain.chat.exception.code.ChatErrorCode;
import com.example.mamatolmi.domain.chat.repository.ChatMessageRepository;
import com.example.mamatolmi.domain.chat.repository.ChatRoomRepository;
import com.example.mamatolmi.domain.kidsNote.entity.KidsNote;
import com.example.mamatolmi.domain.kidsNote.exception.KidsNoteException;
import com.example.mamatolmi.domain.kidsNote.exception.code.KidsNoteErrorCode;
import com.example.mamatolmi.domain.kidsNote.repository.KidsNoteRepository;
import com.example.mamatolmi.domain.user.entity.User;
import com.example.mamatolmi.domain.user.exception.UserException;
import com.example.mamatolmi.domain.user.exception.code.UserErrorCode;
import com.example.mamatolmi.domain.user.repository.UserRepository;
import com.example.mamatolmi.global.ai.gemini.dto.request.GeminiReqDTO;
import com.example.mamatolmi.global.ai.gemini.dto.response.GeminiResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final KidsNoteRepository kidsNoteRepository;
    private final RestTemplate restTemplate;

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    // 제미나이 API 엔드포인트
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    /*
     * 채팅방 생성
     * */
    @Transactional
    public ChatResponseDTO.ChatRoomCreateResult createChatRoom(ChatRequestDTO.ChatRoomCreate chatRoomCreate) {
        // 1. 유저와 키즈노트가 진짜로 존재하는지
        User user = userRepository.findById(chatRoomCreate.userId())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        KidsNote kidsNote = kidsNoteRepository.findById(chatRoomCreate.kidsNoteId())
                .orElseThrow(() -> new KidsNoteException(KidsNoteErrorCode.KIDSNOTE_NOT_FOUND));

        // 2. 방을 만들 때 주인(User)과 주제(KidsNote)를 연결해서 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .user(user)
                .kidsNote(kidsNote)
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        return new ChatResponseDTO.ChatRoomCreateResult(
                savedRoom.getId(),
                savedRoom.getCreatedAt()
        );
    }

    /*
     * ai와 대화하기
     * */
    @Transactional
    public ChatResponseDTO sendMessage(Long roomId, ChatRequestDTO.ChatMessage chatMessage) {
        // 1. 채팅방 찾기
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.ChAT_ROOM_NOT_FOUND));

        // 2. 사용자가 보낸 메시지를 DB에 저장
        ChatMessage userMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(SenderRole.USER)
                .content(chatMessage.message())
                .build();
        chatMessageRepository.save(userMessage);

        // 3. 키즈노트 원본 내용 및 과거 대화 내역 불러오기
        // AI가 문맥을 파악하려면 과거 대화 내역(history)을 같이 보내주는 것이 좋음
        List<ChatMessage> history = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoom.getId());

        // 4. 제미나이 API에 보낼 JSON 요청(Request) 만들기
        GeminiReqDTO.GeminiChatRequest.Part part = new GeminiReqDTO.GeminiChatRequest.Part(chatMessage.message());
        GeminiReqDTO.GeminiChatRequest.Content content = new GeminiReqDTO.GeminiChatRequest.Content("user", List.of(part));
        GeminiReqDTO.GeminiChatRequest geminiRequest = new GeminiReqDTO.GeminiChatRequest(List.of(content));

        // 5. 제미나이 API 호출
        String requestUrl = GEMINI_URL + geminiApiKey;
        GeminiResDTO.GeminiChatResponse geminiResponse = restTemplate.postForObject(requestUrl, geminiRequest, GeminiResDTO.GeminiChatResponse.class);

        // 6. 제미나이 답변 파싱(추출)
        String aiText = "";
        if (geminiResponse != null && !geminiResponse.candidates().isEmpty()) {
            aiText = geminiResponse.candidates().get(0).content().parts().get(0).text();
        } else {
            aiText = "AI가 응답을 생성하지 못했습니다.";
        }

        // 7. AI의 답변을 DB에 저장
        ChatMessage aiMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(SenderRole.ASSISTANT) // AI 보냄
                .content(aiText)
                .build();
        chatMessageRepository.save(aiMessage);

        // 8. 프론트엔드로 답변 리턴
        return new ChatResponseDTO(aiText);
    }


    /*
    * 채팅방 기록 보여주기
    * */
    @Transactional
    public ChatResponseDTO.ChatHistoryResult getChatHistory(Long roomId) {
        // 1. 채팅방 찾기
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.ChAT_ROOM_NOT_FOUND));
        // 2. 해당 방의 메시지 목록을 생성일자 순으로 조회
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(roomId);

        // 3. Entity -> DTO 변환
        List<ChatResponseDTO.ChatMessageDetail> messageDetails = messages.stream()
                .map(msg -> new ChatResponseDTO.ChatMessageDetail(
                        msg.getId(),
                        msg.getSender().name(),
                        msg.getContent(),
                        msg.getCreatedAt()
                ))
                .toList();

        return new ChatResponseDTO.ChatHistoryResult(messageDetails);

    }


}
