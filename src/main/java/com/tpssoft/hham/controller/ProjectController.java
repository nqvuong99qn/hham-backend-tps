package com.tpssoft.hham.controller;

import com.tpssoft.hham.dto.ProjectDto;
import com.tpssoft.hham.dto.UserDto;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.security.HhamUserDetails;
import com.tpssoft.hham.service.ProjectService;
import com.tpssoft.hham.service.SearchConstraint;
import com.tpssoft.hham.service.SearchConstraints;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
@RequestMapping("/api/projects")
@AllArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    /**
     * Get many projects via constraints
     * @param constraints
     * @param request
     * @return
     */
    @GetMapping
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse getMany(SearchConstraints constraints, HttpServletRequest request) {
        constraints.getConstraints().add(new SearchConstraint(
                "archivedOn",
                null,
                SearchConstraint.MatchMode.IDENTITY
        ));
        addConstraintFromParam(constraints, request, "userId", Integer::valueOf);
        addConstraintFromParam(constraints, request, "adminId", Integer::valueOf);
        return new SuccessResponse().put("data", projectService.findAll(constraints)
        );
    }

    /**
     * Get information of one project via projectID
     * @param id
     * @return
     */
    @GetMapping("/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse get(@PathVariable int id) {
        return new SuccessResponse().put("data", projectService.getOne(id));
    }

    /**
     * Create new project with input projectDto
     * @param dto
     * @param currentUser
     * @return
     */
    @PostMapping
    @RolesAllowed("SYSADMIN")
    public SuccessResponse create(@RequestBody ProjectDto dto,@AuthenticationPrincipal HhamUserDetails currentUser) {
        return new SuccessResponse().put("data", projectService.create(
                dto.getName(),
                dto.getDescription(),
                currentUser.getId()
        ));
    }

    /**
     * Update information of project via projectId
     * @param dto
     * @return
     */
    @PutMapping("/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse update(@RequestBody ProjectDto dto) {
        return new SuccessResponse().put("data", projectService.update(
                dto.getId(),
                dto.getName(),
                dto.getDescription()
        ));
    }

    /**
     * archive project via projectId
     * @param id
     * @return
     */
    @DeleteMapping("/{id:\\d+}")
    @RolesAllowed("SYSADMIN")
    public SuccessResponse archive(@PathVariable int id) {
        return new SuccessResponse().put("data", projectService.archive(id));
    }

    @GetMapping("/{id:\\d+}/admins")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse getAdmins(@PathVariable int id) {
        return new SuccessResponse().put("data", projectService.getAdmins(id));
    }

    /**
     * Add project admin into proejct
     * @param id
     * @param newAdmin
     * @return
     */
    @PostMapping("/{id:\\d+}/admins")
    @RolesAllowed("SYSADMIN")
    public SuccessResponse addAdmin(@PathVariable int id, @RequestBody UserDto newAdmin) {
        // TODO: Only user ID is needed, find a way to eliminate the DTO in parameter list
        return new SuccessResponse().put("data", projectService.promoteToAdmin(id, newAdmin.getId()));
    }

    /**
     * Delete project admin of project
     * @param id
     * @param adminId
     * @return
     */
    @DeleteMapping("/{id:\\d+}/admins/{adminId:\\d+}")
    @RolesAllowed("SYSADMIN")
    public SuccessResponse removeAdmin(@PathVariable int id, @PathVariable int adminId) {
        return new SuccessResponse().put("data", projectService.demoteToMember(id, adminId));
    }

    /**
     * Get all members of proejct via projectId
     * @param id
     * @return
     */
    @GetMapping("/{id:\\d+}/members")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse getMembers(@PathVariable int id) {
        return new SuccessResponse().put("data", projectService.getMembers(id));
    }

    /**
     * Add new member into pro
     * @param id
     * @param newMember
     * @return
     */
    @PostMapping("/{id:\\d+}/members")
    @RolesAllowed("SYSADMIN")
    public SuccessResponse addMember(@PathVariable int id, @RequestBody UserDto newMember) {
        // TODO: Only user ID is needed, find a way to eliminate the DTO in parameter list
        return new SuccessResponse().put("data", projectService.addMember(id, newMember.getId()));
    }

    /**
     * Delete member from project
     * @param id
     * @param memberId
     * @return
     */
    @DeleteMapping("/{id:\\d+}/members/{memberId:\\d+}")
    @RolesAllowed("SYSADMIN")
    public SuccessResponse removeMember(@PathVariable int id, @PathVariable int memberId) {
        return new SuccessResponse().put("data", projectService.removeMember(id, memberId));
    }
}
