package com.example.mamatolmi.domain.chat.service;

import com.example.mamatolmi.domain.aiResponse.entity.AiResponse;
import com.example.mamatolmi.domain.aiResponse.repository.AiResponseRepository;
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
import com.example.mamatolmi.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final KidsNoteRepository kidsNoteRepository;
    private final AiResponseRepository aiResponseRepository;
    private final RestTemplate restTemplate;

    @Value("${GEMINI_API_KEY}")
    private String geminiApiKey;

    // 제미나이 API 엔드포인트
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

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
        // 1. 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new ChatException(ChatErrorCode.ChAT_ROOM_NOT_FOUND));

        KidsNote kidsNote = chatRoom.getKidsNote();

        // 2. 알림장 ID로 미리 분석해둔 AiResponse 데이터 가져오기
        // (분석 결과가 없을 수도 있으니 기본값을 빈 문자열로 세팅)
        AiResponse aiResponse = aiResponseRepository.findByKidsNote(kidsNote)
                .orElse(null);

        String summary = (aiResponse != null) ? aiResponse.getSummary() : "요약 정보 없음";
        String todoList = (aiResponse != null) ? aiResponse.getTodoList() : "할 일 정보 없음";
        String guide = (aiResponse != null) ? aiResponse.getGuide() : "가이드 정보 없음";

        // 3. 사용자가 보낸 메시지를 DB에 저장
        ChatMessage userMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(SenderRole.USER)
                .content(chatMessage.message())
                .build();
        chatMessageRepository.save(userMessage);

        // 4. 키즈노트 원본 내용 및 과거 대화 내역 불러오기
        // AI가 문맥을 파악하려면 과거 대화 내역(history)을 같이 보내주는 것이 좋음
        List<ChatMessage> history = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoom.getId());
        List<GeminiReqDTO.GeminiChatRequest.Content> contents = new ArrayList<>();

        // 과거 대화를 제미나이 규격(Content)으로 변환해서 리스트에 담기
        for (ChatMessage msg : history) {
            // 방금 저장한 '현재 질문'은 제외합니다. 알림장 정보랑 합쳐서 넣음
            if (msg.getId().equals(userMessage.getId())) continue;

            // DB의 SenderRole을 제미나이 역할 이름으로 변환 (User는 "user", AI는 "model"로 해야 제미나이가 알아듣습니다)
            String role = msg.getSender() == SenderRole.USER ? "user" : "model";
            GeminiReqDTO.GeminiChatRequest.Part part = new GeminiReqDTO.GeminiChatRequest.Part(msg.getContent());
            contents.add(new GeminiReqDTO.GeminiChatRequest.Content(role, List.of(part)));
        }


        // 5. AI에게 줄 프롬프트 조립 (수정 버전)
        String finalPrompt = String.format(
                "너는 어린이집 선생님이자 AI 육아 도우미야. 아래 [참고 데이터]를 바탕으로 부모님의 질문에 답변해줘.\n" +
                        "응답은 반드시 읽기 좋은 **마크업(Markdown) 형식**으로 작성하고, 불필요한 인사말이나 서론, 결론은 생략해.\n\n" +
                        "--- [참고 데이터] ---\n" +
                        "[알림장 원문]: %s\n" +
                        "[AI 요약]: %s\n" +
                        "[체크리스트(할 일)]: %s\n" +
                        "[맞춤 가이드]: %s\n" +
                        "-------------------\n\n" +
                        "**[출력 규칙]**\n" +
                        "1. 핵심 내용을 강조하기 위해 헤딩(###)과 볼드(**)를 적절히 사용해.\n" +
                        "2. 리스트 형태(1., -, *)를 사용하여 가독성을 높여.\n" +
                        "3. 친절하지만 간결한 문체를 유지해.\n\n" +
                        "부모님 질문: %s",
                kidsNote.getRawText(), summary, todoList, guide, chatMessage.message()
        );

        GeminiReqDTO.GeminiChatRequest.Part finalPart = new GeminiReqDTO.GeminiChatRequest.Part(finalPrompt);
        contents.add(new GeminiReqDTO.GeminiChatRequest.Content("user", List.of(finalPart)));

        // 6. 제미나이 API 실제 호출
        GeminiReqDTO.GeminiChatRequest geminiRequest = new GeminiReqDTO.GeminiChatRequest(contents);
        String requestUrl = GEMINI_URL + "?key=" + geminiApiKey;

        GeminiResDTO.GeminiChatResponse geminiResponse = restTemplate.postForObject(requestUrl, geminiRequest, GeminiResDTO.GeminiChatResponse.class);

        // 7. 응답 추출 및 DB 저장
        String aiText = "";
        if (geminiResponse != null && !geminiResponse.candidates().isEmpty()) {
            aiText = geminiResponse.candidates().get(0).content().parts().get(0).text();
        } else {
            aiText = "AI가 응답을 생성하지 못했습니다.";
        }

        ChatMessage aiMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(SenderRole.ASSISTANT) // AI 보냄
                .content(aiText)
                .build();
        chatMessageRepository.save(aiMessage);

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

    /*
    *  채팅방 사이드바 목록 보여주기
    자녀별 채팅방 목록 조회 API
     */
    @Transactional(readOnly = true)
    public ChatResponseDTO.ChatSidebarResult getChatSidebar(Long userId) {
        // 유저 검증
       if (!userRepository.existsById(userId)) {
           throw new UserException(UserErrorCode.USER_NOT_FOUND);
       }
        // 이번 주 월요일 00:00:00 계산
        LocalDateTime startOfWeek = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();

        // 이번 주 월요일 이후의 채팅방만 조회
        List<ChatRoom> chatRooms = chatRoomRepository.findAllByUserIdAndDateAfter(userId, startOfWeek);

       // 자녀 기준으로 채팅방 그룹화(키즈노트에서 자녀 정보 뺴오기)
        Map<Long, List<ChatRoom>> groupedByChild = chatRooms.stream()
                .collect(Collectors.groupingBy(room -> room.getKidsNote().getKid().getId()));

        //날짜 포맷터 생성 ("3/02")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd");

        // DTO로 변환
        List<ChatResponseDTO.ChildChatGroup> childChatGroups = groupedByChild.entrySet().stream()
                .map(entry -> {
                    Long childId = entry.getKey();
                    // 그룹의 첫 번째 방에서 자녀 이름을 가져옴
                    String childName = entry.getValue().get(0).getKidsNote().getKid().getName();

                    // 해당 자녀의 채팅방들을 Summary DTO로 변환
                    List<ChatResponseDTO.ChatRoomSummary> roomSummaries = entry.getValue().stream()
                            .map(room -> new ChatResponseDTO.ChatRoomSummary(
                                    room.getId(),
                                    room.getCreatedAt().format(formatter)
                            ))
                            .toList();

                    return new ChatResponseDTO.ChildChatGroup(childId, childName, roomSummaries);
                })
                .toList();

        return new ChatResponseDTO.ChatSidebarResult(childChatGroups);

    }


}
