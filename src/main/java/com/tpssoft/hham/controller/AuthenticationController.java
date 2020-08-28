package com.tpssoft.hham.controller;

import com.tpssoft.hham.dto.LoginRequest;
import com.tpssoft.hham.exception.AuthenticationFailureException;
import com.tpssoft.hham.response.SuccessResponse;
import com.tpssoft.hham.security.HhamUserDetails;
import com.tpssoft.hham.service.SessionService;
import com.tpssoft.hham.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Transactional
public class AuthenticationController {
    private final UserService userService;
    private final SessionService sessionService;

    @PostMapping("/login")
    public SuccessResponse login(HttpServletRequest request,
                                 @RequestBody LoginRequest loginRequest) {
        if (!sessionService.authenticate(loginRequest.getUsername(), loginRequest.getPassword())) {
            throw new AuthenticationFailureException();
        }
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        var session = sessionService.createFor(
                loginRequest.getUsername(),
                ipAddress,
                userAgent
        );
        var user = userService.getByUsername(loginRequest.getUsername());
        return new SuccessResponse().put("session", session).put("user", user);
    }

    @PostMapping("/logout")
    public SuccessResponse logout(@AuthenticationPrincipal HhamUserDetails currentUser) {
        sessionService.destroyToken(currentUser.getToken());
        return new SuccessResponse();
    }
}
