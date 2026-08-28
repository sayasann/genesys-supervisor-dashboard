package com.genesys.service;

import com.genesys.entity.User;
import com.genesys.enums.audit.AuditAction;
import com.genesys.exception.BaseException;
import com.genesys.exception.ErrorMessage;
import com.genesys.exception.MessageType;
import com.genesys.hasher.PasswordHasher;
import com.genesys.jwt.JwtService;
import com.genesys.repo.UserRepository;
import com.genesys.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {


    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuditLogService auditLogService;

    public AuthService(
                        AuthenticationManager authenticationManager,
                       JwtService jwtService, AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.auditLogService=auditLogService;


        this.jwtService = jwtService;
    }



    public LoginResult login(String username, String rawPassword){

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username,rawPassword)
            );
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            auditLogService.record(AuditAction.LOGIN_SUCCESS,user.getUsername(),null);

            String token = jwtService.generateToken(user.getUsername());
            return new LoginResult(token,user.getRole().name());

        //ŞİFRE YANLIŞSA VEYA DEACTIVE ADAM GIRMEYE CALISIYORSA
        } catch (BadCredentialsException | DisabledException e) {

            auditLogService.record(AuditAction.LOGIN_FAILED, username, null);
            throw new BaseException(new ErrorMessage(MessageType.INVALID_CREDENTIALS, ""));
        }
    }

    public record LoginResult(String token, String role){}

}
