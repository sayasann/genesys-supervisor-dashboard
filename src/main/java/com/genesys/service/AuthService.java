package com.genesys.service;

import com.genesys.entity.User;
import com.genesys.exception.BaseException;
import com.genesys.exception.ErrorMessage;
import com.genesys.exception.MessageType;
import com.genesys.hasher.PasswordHasher;
import com.genesys.jwt.JwtService;
import com.genesys.repo.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordHasher passwordHasher,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.jwtService = jwtService;
    }

    public String login(String username, String rawPassword){
        User user = userRepository.findByUsername(username).orElseThrow(()
                -> new BaseException(new ErrorMessage(MessageType.INVALID_CREDENTIALS,"")));

        if(!passwordHasher.matches(rawPassword,user.getPassword())){
            throw new BaseException(new ErrorMessage(MessageType.INVALID_CREDENTIALS,""));
        }

        return jwtService.generateToken(user.getUsername());
    }

}
