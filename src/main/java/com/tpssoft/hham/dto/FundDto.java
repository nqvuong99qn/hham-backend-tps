package com.tpssoft.hham.dto;

import com.tpssoft.hham.entity.Fund;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class FundDto {
    private Integer id;
    private String name;
    private BigDecimal amount;
    private ZonedDateTime createdOn;
    private ZonedDateTime archivedOn;

    public static FundDto from(Fund fund) {
        var dto = new FundDto();
        dto.setId(fund.getId());
        dto.setName(fund.getName());
        dto.setAmount(fund.getAmount());
        // TODO: Add dates when needed
        return dto;
    }
}
