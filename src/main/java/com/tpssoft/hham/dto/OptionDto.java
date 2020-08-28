package com.tpssoft.hham.dto;

import com.tpssoft.hham.entity.Option;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class OptionDto {
    private Integer id;
    private Integer activityId;
    private String name;
    private ImageDto image;
    private BigDecimal price;
    private ZonedDateTime createdOn;
    private Set<VoteDto> votes;

    public static OptionDto from(Option option) {
        return new OptionDto(
                option.getId(),
                option.getActivity().getId(),
                option.getName(),
                null,
                option.getPrice(),
                option.getCreatedOn(),
                new HashSet<>()
        );
    }
}
