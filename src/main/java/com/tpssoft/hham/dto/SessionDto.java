package com.tpssoft.hham.dto;

import com.tpssoft.hham.entity.Session;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class SessionDto {
    private Integer userId;
    private String token;
    private String ipAddress = "";
    private String userAgent = "";
    private ZonedDateTime createdOn;
    private ZonedDateTime expiredOn;

    public static SessionDto from(Session session) {
        return new SessionDto(
                session.getUser().getId(),
                session.getToken(),
                session.getIpAddress(),
                session.getUserAgent(),
                session.getCreatedOn(),
                session.getExpiredOn()
        );
    }
}
