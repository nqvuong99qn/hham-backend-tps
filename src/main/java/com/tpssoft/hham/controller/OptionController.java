package com.tpssoft.hham.controller;

import com.tpssoft.hham.dto.OptionDto;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.service.OptionService;
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
import javax.servlet.http.HttpServletRequest;

import static com.tpssoft.hham.Helper.addConstraintFromParam;

@RestController
@RequestMapping("/api/options")
@AllArgsConstructor
public class OptionController {
    private final OptionService optionService;

    @GetMapping
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse getMany(SearchConstraints constraints, HttpServletRequest request) {
        addConstraintFromParam(constraints, request, "activityId", Integer::valueOf);
        return new SuccessResponse().put("data", optionService.findAll(constraints.getConstraints()));
    }

    @PostMapping
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse create(@RequestBody OptionDto dto) {
        return new SuccessResponse().put("data", optionService.create(
                dto.getName(),
                dto.getPrice(),
                dto.getActivityId(),
                dto.getImage() != null ? dto.getImage().getDigest() : null
        ));
    }

    @GetMapping("/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public OptionDto get(@PathVariable int id) {
        return optionService.get(id);
    }

    @PutMapping("/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse update(@RequestBody OptionDto dto) {
        return new SuccessResponse().put("data",
                optionService.update(dto.getId(), dto.getName(), dto.getPrice())
        );
    }

    @DeleteMapping("/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse delete(@PathVariable int id) {
        return new SuccessResponse().put("data", optionService.delete(id));
    }
}
