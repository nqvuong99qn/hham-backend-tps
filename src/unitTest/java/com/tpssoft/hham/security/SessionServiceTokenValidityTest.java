package com.tpssoft.hham.security;

import com.tpssoft.hham.entity.User;
import com.tpssoft.hham.repository.SessionRepository;
import com.tpssoft.hham.repository.UserRepository;
import com.tpssoft.hham.service.SessionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class SessionServiceTokenValidityTest {
    UserRepository userRepository = mock(UserRepository.class);
    SessionRepository sessionRepository = mock(SessionRepository.class);
    PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    SessionService service =
            new SessionService(sessionRepository, userRepository, passwordEncoder);
    String token = "DUMMY_TOKEN";

    Optional<User> createOptionalUserWithToken(String token, ZonedDateTime expireAt) {
        var user = new User();
//        user.setToken(token);
//        user.setTokenExpireAt(expireAt);
        return Optional.of(user);
    }

    @Test
    @DisplayName("Token not exist in database is invalid")
    void tokenNotExistIsInvalid() {
//        when(userRepository.findByToken(token)).thenReturn(Optional.empty());
        assertFalse(service.isValidToken(token));
    }

    @Test
    @DisplayName("Expired token is invalid")
    void expiredTokenIsInvalid() {
        var user = createOptionalUserWithToken(token, ZonedDateTime.now());
//        when(userRepository.findByToken(token)).thenReturn(user);
        assertFalse(service.isValidToken(token));
    }

    @Test
    @DisplayName("Empty token is invalid")
    void emptyTokenIsInvalid() {
        assertFalse(service.isValidToken(""));
    }

    @Test
    @DisplayName("null token is invalid")
    void nullTokenIsInvalid() {
        assertFalse(service.isValidToken(null));
    }

    @Test
    @DisplayName("Refreshed token is valid, even when it has expired")
    void refreshedTokenIsValid() {
        // This situation can happen when the token expires right after the validation
        var user = createOptionalUserWithToken(token, ZonedDateTime.now());
//        when(userRepository.findByToken(token)).thenReturn(user);
//        service.refreshToken(token);
        assertTrue(service.isValidToken(token));
    }
}
