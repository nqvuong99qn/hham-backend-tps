package com.tpssoft.hham.dto;

import com.tpssoft.hham.entity.JobTitle;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class JobTitleDto {
    private Integer id;
    private String name;
    private BigDecimal monthlyAmount;
    private ZonedDateTime createdOn;
    private ZonedDateTime archivedOn;

    public static JobTitleDto from(JobTitle job) {
        return new JobTitleDto(
                job.getId(),
                job.getName(),
                job.getMonthlyAmount(),
                job.getCreatedOn(),
                job.getArchivedOn()
        );
    }
}
