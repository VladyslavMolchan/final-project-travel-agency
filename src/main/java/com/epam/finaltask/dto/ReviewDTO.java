package com.epam.finaltask.dto;



import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private UUID id;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
}
