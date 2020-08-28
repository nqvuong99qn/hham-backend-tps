package com.tpssoft.hham.controller;

import com.tpssoft.hham.dto.UserDto;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ActivationController {
    private final UserService userService;

    @GetMapping("/invitation/{token:[0-9a-fA-F]+}")
    public SuccessResponse getInvitation(@PathVariable String token) {
        return new SuccessResponse().put("data", userService.getInvitation(token));
    }

    @PostMapping("/activate")
    public SuccessResponse createAccount(@RequestBody UserDto dto) {
        return new SuccessResponse().put("data", userService.create(
                dto.getActivationToken(),
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail(),
                dto.getDisplayName(),
                dto.getJobTitle() != null ? dto.getJobTitle().getId() : null
        ));
    }
}
