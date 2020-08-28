package com.tpssoft.hham.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpssoft.hham.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoteDto {
    private Integer optionId;
    private Integer userId;
    private String note = "";
    private ZonedDateTime createdOn;

    public static VoteDto from(Vote vote) {
        return new VoteDto(
                vote.getOptionId(),
                vote.getUserId(),
                vote.getNote(),
                vote.getCreatedOn()
        );
    }
}
