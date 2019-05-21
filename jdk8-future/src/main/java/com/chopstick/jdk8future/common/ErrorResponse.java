package com.chopstick.jdk8future.common;

import java.time.LocalDateTime;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
public class ErrorResponse {

    private String err;
    private String desc;
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(String err, String desc) {
        this.err = err;
        this.desc = desc;
    }
}
