package com.tpssoft.hham.dto;

import com.tpssoft.hham.entity.Activity;
import com.tpssoft.hham.entity.EndStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class ActivityDto {
    private Integer id;
    private String name;
    private String description;
    private ZonedDateTime createdOn;
    private ZonedDateTime lastModifiedOn;
    private ZonedDateTime lockedOn;
    private ZonedDateTime archivedOn;
    private EndStatus endStatus;
    private String endNote;

    public static ActivityDto from(Activity activity) {
        return new ActivityDto(
                activity.getId(),
                activity.getName(),
                activity.getDescription(),
                activity.getCreatedOn(),
                activity.getLastModifiedOn(),
                activity.getLockedOn(),
                activity.getArchivedOn(),
                activity.getEndStatus(),
                activity.getEndNote()
        );
    }
}
