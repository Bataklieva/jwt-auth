package com.auth.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
