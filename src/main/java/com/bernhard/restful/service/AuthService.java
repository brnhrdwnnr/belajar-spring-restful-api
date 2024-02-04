package com.bernhard.restful.service;

import com.bernhard.restful.entity.User;
import com.bernhard.restful.model.LoginUserRequest;
import com.bernhard.restful.model.TokenResponse;
import com.bernhard.restful.repository.UserRepository;
import com.bernhard.restful.security.BCrypt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = { @Autowired})
public class AuthService {

    private final UserRepository userRepository;
    private final ValidationService validationService;

    @Transactional
    public TokenResponse login (LoginUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password wrong"));

        if(BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);
            return TokenResponse
                    .builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password wrong");
        }
    }

    private Long next30Days() {
        return System.currentTimeMillis() + (1000 * 60 * 24* 30);
    }

    @Transactional
    public void logout (User user) {
        user.setToken(null);
        user.setTokenExpiredAt(null);

        userRepository.save(user);
    }
}
