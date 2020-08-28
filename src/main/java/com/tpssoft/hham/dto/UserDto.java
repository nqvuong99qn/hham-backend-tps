package com.tpssoft.hham.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tpssoft.hham.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Integer id;
    private String username;
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; // Plain password
    private String hashedPassword;
    private String email;
    private String displayName;
    private ImageDto image;
    private boolean admin;
    private JobTitleDto jobTitle;
    private ZonedDateTime createdOn;
    private ZonedDateTime deactivatedOn;
    private String activationToken = "";
    private List<TransactionDto> transDtos = new ArrayList();

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                "",
                user.getPassword(),
                user.getEmail(),
                user.getDisplayName(),
                null,
                user.isAdmin(),
                null,
                user.getCreatedOn(),
                user.getDeactivatedOn(),
                "",
                new ArrayList<>()
        );
    }
}
