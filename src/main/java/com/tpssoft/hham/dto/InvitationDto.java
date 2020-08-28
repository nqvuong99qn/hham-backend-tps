package com.tpssoft.hham.dto;

import com.tpssoft.hham.entity.Invitation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDto {
    private String token;
    private String email;
    private ZonedDateTime expiredOn;

    public static InvitationDto from(Invitation invitation) {
        var dto = new InvitationDto();
        dto.setToken(invitation.getToken());
        dto.setEmail(invitation.getEmail());
        dto.setExpiredOn(invitation.getExpireOn());
        return dto;
    }
}
