package com.tpssoft.hham.dto;

import com.tpssoft.hham.entity.Membership;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class MembershipDto {
    private Integer projectId;
    private Integer userId;
    private ZonedDateTime joinedOn;
    private ZonedDateTime leftOn;

    public static MembershipDto from(Membership membership) {
        return new MembershipDto(
                membership.getProjectId(),
                membership.getUserId(),
                membership.getJoinedOn(),
                membership.getLeftOn()
        );
    }
}
