package com.tpssoft.hham.controller;

import com.tpssoft.hham.dto.FundDto;
import com.tpssoft.hham.dto.TransactionDto;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.service.EmailService;
import com.tpssoft.hham.service.SearchConstraints;
import com.tpssoft.hham.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/api/transactions")
@AllArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final EmailService emailService;

    @GetMapping
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse getMany(SearchConstraints constraints) {
        return new SuccessResponse().put("data",
                transactionService.findAll(constraints.getConstraints())
        );
    }

    @PostMapping
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse create(@RequestBody TransactionDto transactionDto) {
        transactionService.create(transactionDto);
        return new SuccessResponse();
    }

    @PutMapping("/calc/{id}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse calcFund(@PathVariable int id, @RequestBody FundDto dto) {
        return new SuccessResponse().put("data",
                transactionService.calcFund(id, dto.getAmount(), dto.getId())
        );
    }

    // TODO: Move this to more appropriate place
    @PostMapping("/remind")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse remind(@RequestBody TransactionDto dto) {
        emailService.sendFundingReminder(dto.getUserId());
        return new SuccessResponse();
    }

    @GetMapping("/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse get(int id) {
        return new SuccessResponse().put("data", transactionService.get(id));
    }

}
