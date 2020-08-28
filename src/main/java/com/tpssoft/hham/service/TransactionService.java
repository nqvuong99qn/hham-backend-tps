package com.tpssoft.hham.service;

import com.tpssoft.hham.dto.FundDto;
import com.tpssoft.hham.dto.JobTitleDto;
import com.tpssoft.hham.dto.TransactionDto;
import com.tpssoft.hham.entity.Fund;
import com.tpssoft.hham.entity.Transaction;
import com.tpssoft.hham.exception.ProjectNotFoundException;
import com.tpssoft.hham.exception.TransactionNotFoundException;
import com.tpssoft.hham.repository.FundRepository;
import com.tpssoft.hham.repository.JobTitleRepository;
import com.tpssoft.hham.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final FundRepository fundRepository;
    private final JobTitleRepository jobTitleRepository;

    public void create(TransactionDto transactionDto) {
        var transaction = new Transaction();
        transactionDto.copyTo(transaction);
        transactionRepository.save(transaction);
    }

    public FundDto calcFund(int id, BigDecimal amount, int projectId) {
        Fund fund = fundRepository
                .findById(projectId)
                .orElseThrow(ProjectNotFoundException::new);
        if (id == 1) {
            fund.setAmount(fund.getAmount().add(amount));
        } else {
            fund.setAmount(fund.getAmount().subtract(amount));
        }
        return FundDto.from(fundRepository.save(fund));
    }

    public List<TransactionDto> findAll(List<SearchConstraint> constraints) {
        return addConstraints(transactionRepository.findAll().stream(), constraints)
                .map(TransactionDto::from)
                .map(dto -> {
                    dto.getJobTitleDtos().addAll(jobTitleRepository
                            .findByUserId(dto.getUserId())
                            .stream()
                            .map(JobTitleDto::from)
                            .collect(Collectors.toList())
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private Stream<Transaction> addConstraints(Stream<Transaction> stream, List<SearchConstraint> constraints) {
        for (var constraint : constraints) {
            switch (constraint.getFieldName()) {
                case "amount":
                    stream = stream.filter(transaction -> constraint.matches(transaction.getAmount()));
                    break;
                case "userId":
                    stream = stream.filter(transaction -> constraint.matches(transaction.getUser().getId()));
                    break;
                case "fundId":
                    stream = stream.filter(transaction -> constraint.matches(transaction.getFund().getId()));
                    break;
                default:
                    break;
            }
        }
        return stream;
    }


    /**
     * Get information of a specific transaction
     *
     * @param id ID of the transaction
     *
     * @return Information of the specified transaction
     *
     * @throws TransactionNotFoundException if the ID provided does not belong to any transaction
     */
    public TransactionDto get(int id) {
        return TransactionDto.from(transactionRepository
                .findById(id)
                .orElseThrow(TransactionNotFoundException::new)
        );
    }
}
