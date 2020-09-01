package com.tpssoft.hham.controller;

import com.tpssoft.hham.aspect.GooglePojo;
import com.tpssoft.hham.aspect.GoogleUtils;
import com.tpssoft.hham.dto.UserDto;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@AllArgsConstructor
public class ActivationController {
    private final UserService userService;
    @Autowired
    private GoogleUtils googleUtils;


    @GetMapping("/")
    public String a(){
        return "aaa";
    }


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
                dto.getJobTitle() != null ? dto.getJobTitle().getId() : null)
        );
    }
}
