package com.tpssoft.hham.controller;

import com.sun.istack.NotNull;
import com.tpssoft.hham.dto.UserDto;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.security.HhamUserDetails;
import com.tpssoft.hham.service.FindConstraint;
import com.tpssoft.hham.service.SearchConstraint;
import com.tpssoft.hham.service.SearchConstraints;
import com.tpssoft.hham.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * This API is using to get User with constraits.
     *
     * @param constraints
     * @return
     */
    @GetMapping
    @RolesAllowed("SYSADMIN")
    public SuccessResponse getMany(@NotNull SearchConstraints constraints) {
        return new SuccessResponse().put("data", userService.findAll(constraints));
    }

    /**
     * This API is using for get User by Id
     * @param id
     * @return
     */
    @GetMapping({"/{id:\\d+}"})
    @RolesAllowed({"SYSADMIN"})
    public SuccessResponse getOne(@PathVariable int id){
        return new SuccessResponse().put("data", userService.findOne(id));
    }

    /**
     * This API is using to invite new user.
     *
      * @param dto
     * @param currentUser
     * @return
     * @throws NoSuchAlgorithmException
     */
    @PostMapping("/invite")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse invite(@RequestBody UserDto dto, @AuthenticationPrincipal HhamUserDetails currentUser)
            throws NoSuchAlgorithmException {
        userService.invite(currentUser.getDisplayName(), currentUser.getEmail(), dto.getEmail());
        return new SuccessResponse();
    }

    /**
     * This API is using to invite new User into project (only Project Admin can do it)
     * @param dto
     * @param projectId
     * @param currentUser
     * @return
     * @throws NoSuchAlgorithmException
     */
    @PostMapping("/invite/{projectId}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse inviteToProject(@RequestBody UserDto dto,
                                           @PathVariable int projectId,
                                           @AuthenticationPrincipal HhamUserDetails currentUser)
            throws NoSuchAlgorithmException {
        userService.invite(currentUser.getDisplayName(), currentUser.getEmail(),
                dto.getEmail(), projectId);
        return new SuccessResponse();
    }

    /**
     * update information of User
     * @param id
     * @param dto
     * @return
     */
    @PutMapping("/{id:\\d+}")
    @RolesAllowed({ "SYSADMIN", "USER" })
    public SuccessResponse update(@PathVariable int id, @RequestBody UserDto dto) {
        dto.setId(id);
        if (dto.getPassword() != null) {
            userService.changePassword(id, dto.getPassword());
        }
        return new SuccessResponse().put("data", userService.update(
                dto.getId(),
                dto.getUsername(),
                dto.getDisplayName(),
                dto.getEmail(),
                dto.getJobTitle() != null ? dto.getJobTitle().getId() : null
        ));
    }

    /**
     * Deactive user then can active if by admin if necessary
     * @param id
     * @return
     */
    @DeleteMapping("/{id:\\d+}")
    @RolesAllowed("SYSADMIN")
    public SuccessResponse deactivate(@PathVariable int id) {
        return new SuccessResponse().put("data", userService.deactivate(id));
    }
}
