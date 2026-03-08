package com.example.mamatolmi.domain.aiResponse.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiResDTO {

    private String summary;
    private String todoList;
    private String guide;
}
