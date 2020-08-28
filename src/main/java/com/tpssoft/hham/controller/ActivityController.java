package com.tpssoft.hham.controller;

import com.tpssoft.hham.dto.ActivityDto;
import com.tpssoft.hham.entity.EndStatus;
import com.tpssoft.hham.exception.NegativeFundException;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.security.HhamUserDetails;
import com.tpssoft.hham.service.ActivityService;
import com.tpssoft.hham.service.SearchConstraint;
import com.tpssoft.hham.service.SearchConstraints;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.tpssoft.hham.Helper.addConstraintFromParam;

@RestController
@RequestMapping("/api/activities")
@AllArgsConstructor
public class ActivityController {
    private final ActivityService activityService;

    @GetMapping
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse getAll(HttpServletRequest request) {
        var constraints = new SearchConstraints();
        constraints.getConstraints().add(new SearchConstraint(
                "archivedOn",
                null,
                SearchConstraint.MatchMode.IDENTITY
        ));
        addConstraintFromParam(constraints, request, "userId", Integer::valueOf);
        addConstraintFromParam(constraints, request, "projectId", Integer::valueOf);
        return new SuccessResponse()
                .put("data", activityService.findAll(constraints.getConstraints()));
    }

    @PostMapping
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse create(@RequestBody ActivityDto dto, HttpServletRequest request) {
        List<Integer> projectsId = new ArrayList<>();
        if (request.getParameter("projectId") != null) {
            projectsId.add(Integer.valueOf(request.getParameter("projectId")));
        }
        return new SuccessResponse().put("data",
                activityService.create(dto.getName(), dto.getDescription(), projectsId)
        );

    }

    @GetMapping("/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse get(@PathVariable int id,
                               @AuthenticationPrincipal HhamUserDetails currentUser) {
        if (!activityService.userCanView(currentUser.getId(), id)) {
            throw new InsufficientAuthenticationException("No privilege to view this activity");
        }
        return new SuccessResponse().put("data", activityService.get(id));
    }

    @PutMapping("/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse update(@RequestBody ActivityDto dto,
                                  @AuthenticationPrincipal HhamUserDetails currentUser) {
        if (!activityService.userCanEdit(currentUser.getId(), dto.getId())) {
            throw new InsufficientAuthenticationException("No privilege to edit this activity");
        }
        return new SuccessResponse().put("data",
                activityService.update(dto.getId(), dto.getName(), dto.getDescription())
        );
    }

    @PutMapping("/{id:\\d+}/lock")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse lock(@PathVariable int id,
                                @AuthenticationPrincipal HhamUserDetails currentUser) {
        if (!activityService.userCanEdit(currentUser.getId(), id)) {
            throw new InsufficientAuthenticationException("No privilege to lock this activity");
        }
        return new SuccessResponse().put("data", activityService.lock(id));
    }

    @PutMapping("/{id:\\d+}/unlock")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse unlock(@PathVariable int id,
                                  @AuthenticationPrincipal HhamUserDetails currentUser) {
        if (!activityService.userCanEdit(currentUser.getId(), id)) {
            throw new InsufficientAuthenticationException("No privilege to unlock this activity");
        }
        return new SuccessResponse().put("data", activityService.unlock(id));
    }

    @PutMapping("/{id:\\d+}/finish")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse finish(@PathVariable int id,
                                  @AuthenticationPrincipal HhamUserDetails currentUser) {
        if (!activityService.userCanEdit(currentUser.getId(), id)) {
            throw new InsufficientAuthenticationException("No privilege to finish this activity");
        }
        try {
            return new SuccessResponse().put("data",
                    activityService.archive(id, EndStatus.FINISHED, currentUser.getId())
            );
        } catch (Exception exception) {
            throw new NegativeFundException();
        }
    }

    @PutMapping("/{id:\\d+}/cancel")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse cancel(@PathVariable int id,
                                  @AuthenticationPrincipal HhamUserDetails currentUser) {
        if (!activityService.userCanEdit(currentUser.getId(), id)) {
            throw new InsufficientAuthenticationException("No privilege to cancel this activity");
        }
        return new SuccessResponse().put("data",
                activityService.archive(id, EndStatus.CANCELED, currentUser.getId())
        );
    }

    @PostMapping("/{id:\\d+}/notify")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse notify(@PathVariable int id) {
        return new SuccessResponse().put("data", activityService.notify(id));
    }

    @GetMapping("/{id:\\d+}/admins")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse admins(@PathVariable int id) {
        return new SuccessResponse().put("data", activityService.getAdmins(id));
    }

    @GetMapping("/{id:\\d+}/members")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse members(@PathVariable int id) {
        return new SuccessResponse().put("data", activityService.getMembers(id));
    }
}
