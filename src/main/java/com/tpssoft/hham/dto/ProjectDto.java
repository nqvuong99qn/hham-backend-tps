package com.tpssoft.hham.dto;

import com.tpssoft.hham.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Integer id;
    private String name;
    private String description;
    private ImageDto image;
    private List<FundDto> funds = new ArrayList<>();
    private ZonedDateTime createdOn;
    private ZonedDateTime lastModifiedOn;
    private ZonedDateTime archivedOn;

    public static ProjectDto from(Project project) {
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                null,
                new ArrayList<>(),
                project.getCreatedOn(),
                project.getLastModifiedOn(),
                project.getArchivedOn()
        );
    }
}
