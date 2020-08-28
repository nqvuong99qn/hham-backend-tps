package com.tpssoft.hham.controller;

import com.tpssoft.hham.dto.VoteDto;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.security.HhamUserDetails;
import com.tpssoft.hham.service.SearchConstraints;
import com.tpssoft.hham.service.VoteService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import static com.tpssoft.hham.Helper.addConstraintFromParam;

@RestController
@RequestMapping("/api/votes")
@AllArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @GetMapping
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse getMany(SearchConstraints constraints, HttpServletRequest request) {
        addConstraintFromParam(constraints, request, "userId", Integer::valueOf);
        addConstraintFromParam(constraints, request, "activityId", Integer::valueOf);
        return new SuccessResponse().put("data", voteService.findAll(constraints.getConstraints()));
    }

    @PostMapping
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse create(@AuthenticationPrincipal HhamUserDetails currentUser,
                                  @RequestBody VoteDto dto) {
        return new SuccessResponse().put("data",
                voteService.create(currentUser.getId(), dto.getOptionId(), dto.getNote())
        );
    }

    @DeleteMapping("/{userId:\\d+}/{optionId:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse remove(@PathVariable int userId, @PathVariable int optionId) {
        return new SuccessResponse().put("data", voteService.delete(userId, optionId));
    }
}
