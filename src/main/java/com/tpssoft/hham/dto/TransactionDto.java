package com.tpssoft.hham.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tpssoft.hham.entity.Activity;
import com.tpssoft.hham.entity.Fund;
import com.tpssoft.hham.entity.Transaction;
import com.tpssoft.hham.entity.TransactionType;
import com.tpssoft.hham.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TransactionDto {
    private Integer id;
    private Integer typeId;
    private String transactionType;
    private Integer userId;
    private String username;
    private List<JobTitleDto> jobTitleDtos;
    private Integer fundId;
    private Integer activityId;
    private String activityName;
    private ZonedDateTime activityArchivedOn;
    private BigDecimal amount;
    private String memo;
    private ZonedDateTime createdOn;

    public static TransactionDto from(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getType().getId(),
                transaction.getType().getName(),
                transaction.getUser().getId(),
                transaction.getUser().getUsername(),
                new ArrayList<>(),
                transaction.getFund().getId(),
                transaction.getActivity() != null ? transaction.getActivity().getId() : null,
                transaction.getActivity() != null ? transaction.getActivity().getName() : null,
                transaction.getActivity() != null ? transaction.getActivity().getArchivedOn() : null,
                transaction.getAmount(),
                transaction.getMemo(),
                transaction.getCreatedOn()
        );
    }

    public void copyTo(Transaction transaction) {
        transaction.setId(id);
        if (typeId != null) {
            if (transaction.getType() == null) {
                transaction.setType(new TransactionType());
            }
            transaction.getType().setId(typeId);
        }
        if (userId != null) {
            if (transaction.getUser() == null) {
                transaction.setUser(new User());
            }
            transaction.getUser().setId(userId);
        }
        if (fundId != null) {
            if (transaction.getFund() == null) {
                transaction.setFund(new Fund());
            }
            transaction.getFund().setId(fundId);
        }
        if (activityId != null) {
            if (transaction.getActivity() == null) {
                transaction.setActivity(new Activity());
            }
            transaction.getActivity().setId(activityId);
        }
        if (amount != null) {
            transaction.setAmount(amount);
        }
        if (memo != null) {
            transaction.setMemo(memo);
        }
    }
}
