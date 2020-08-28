package com.tpssoft.hham.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteId implements Serializable {
    private Integer userId;
    private Integer optionId;
}
