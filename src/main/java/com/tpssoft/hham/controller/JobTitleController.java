package com.tpssoft.hham.controller;


import com.tpssoft.hham.dto.JobTitleDto;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.service.JobTitleService;
import com.tpssoft.hham.service.SearchConstraint;
import com.tpssoft.hham.service.SearchConstraints;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/api/jobs")
@AllArgsConstructor
public class JobTitleController {
    private final JobTitleService jobTitleService;

    @GetMapping
    @RolesAllowed("SYSADMIN")
    public SuccessResponse getMany(SearchConstraints constraints) {
        constraints.getConstraints().add(
                new SearchConstraint("archivedOn", null, SearchConstraint.MatchMode.IDENTITY)
        );
        return new SuccessResponse().put("data", jobTitleService.findAll(constraints.getConstraints()));
    }

    @PostMapping
    @RolesAllowed("SYSADMIN")
    public SuccessResponse create(@RequestBody JobTitleDto dto) {
        return new SuccessResponse().put("data",
                jobTitleService.create(dto.getName(), dto.getMonthlyAmount()));
    }

    @GetMapping("{id:\\d+}")
    @RolesAllowed({ "SYSADMIN" })
    public SuccessResponse get(@PathVariable int id) {
        return new SuccessResponse().put("data", jobTitleService.get(id));
    }

    @PutMapping("/{id:\\d+}")
    @RolesAllowed("SYSADMIN")
    public SuccessResponse update(@PathVariable int id, @RequestBody JobTitleDto dto) {
        return new SuccessResponse().put("data",
                jobTitleService.update(id, dto.getName(), dto.getMonthlyAmount()));
    }

    @DeleteMapping("/{id:\\d+}")
    @RolesAllowed("SYSADMIN")
    public SuccessResponse archive(@PathVariable int id) {
        return new SuccessResponse().put("data", jobTitleService.archive(id));
    }
}
