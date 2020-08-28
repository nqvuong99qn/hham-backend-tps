package com.tpssoft.hham.service;

import com.tpssoft.hham.dto.SessionDto;
import com.tpssoft.hham.dto.UserDto;
import com.tpssoft.hham.entity.Session;
import com.tpssoft.hham.exception.SessionNotFoundException;
import com.tpssoft.hham.exception.UserNotFoundException;
import com.tpssoft.hham.repository.SessionRepository;
import com.tpssoft.hham.repository.UserRepository;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Random;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SessionService {
    @NonNull
    private final SessionRepository sessionRepository;
    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final PasswordEncoder passwordEncoder;
    @Getter
    @Setter
    private Random randomNumberGenerator = new Random();
    private static final String TOKEN_CHARSET = buildTokenCharset();
    @Value("${com.tpssoft.hham.token-length}")
    private int tokenLength;
    @Value("${com.tpssoft.hham.token-lifetime}")
    private int tokenLifetimeInSecond;

    // Don't know why using attribute doesn't work, need to make it a method
    private Duration getDefaultTokenLifetime() {
        return Duration.ofSeconds(tokenLifetimeInSecond);
    }

    private static String buildTokenCharset() {
        var builder = new StringBuilder();
        IntStream.rangeClosed(32, 126)
                .mapToObj(ch -> (char) ch)
                .filter(ch -> ch != '\\' && ch != '"')
                .forEachOrdered(builder::append);
        return builder.toString();
    }

    public boolean isValidToken(String token) {
        if (token == null || token.isEmpty())
            return false;
        var sessionOptional = sessionRepository.findByToken(token);
        if (sessionOptional.isEmpty()) {
            return false;
        }
        var session = sessionOptional.get();
        return session.getExpiredOn().compareTo(ZonedDateTime.now()) > 0;
    }

    public UserDto findByToken(String token) {
        var session = sessionRepository
                .findByToken(token)
                .orElseThrow(SessionNotFoundException::new);
        return UserDto.from(session.getUser());
    }

    public String generateToken() {
        return generateToken(tokenLength);
    }

    public String generateToken(int length) {
        StringBuilder builder = new StringBuilder();
        randomNumberGenerator
                .ints(length, 0, TOKEN_CHARSET.length())
                .forEachOrdered(index -> builder.append(TOKEN_CHARSET.charAt(index)));
        return builder.toString();
    }

    public SessionDto createFor(String username, String ipAddress, String userAgent) {
        return createFor(username, ipAddress, userAgent, getDefaultTokenLifetime());
    }

    public SessionDto createFor(String username,
                                String ipAddress,
                                String userAgent,
                                Duration lifetime) {
        var user = userRepository
                .findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
        String token = generateToken(tokenLength);
        var session = new Session();
        session.setUser(user);
        session.setToken(token);
        session.setExpiredOn(ZonedDateTime.now().plus(lifetime));
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        return SessionDto.from(sessionRepository.save(session));
    }

    public void refreshToken(String token) {
        refreshToken(token, getDefaultTokenLifetime());
    }

    public void refreshToken(String token, Duration lifetime) {
        var session = sessionRepository
                .findByToken(token)
                .orElseThrow(SessionNotFoundException::new);
        session.setExpiredOn(ZonedDateTime.now().plus(lifetime));
        sessionRepository.save(session);
    }

    public void destroyToken(String token) {
        var session = sessionRepository
                .findByToken(token)
                .orElseThrow(SessionNotFoundException::new);
        session.setExpiredOn(ZonedDateTime.now());
        sessionRepository.save(session);
    }

    public boolean authenticate(String username, String plainPassword) {
        var user = userRepository.findByUsername(username).orElse(null);
        return user != null && passwordEncoder.matches(plainPassword, user.getPassword());
    }
}
