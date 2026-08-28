package com.genesys.AuthTest;

import com.genesys.entity.User;
import com.genesys.enums.user_enums.UserRole;
import com.genesys.exception.BaseException;
import com.genesys.jwt.JwtService;
import com.genesys.security.CustomUserDetails;
import com.genesys.service.AuditLogService;
import com.genesys.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;


    @Test
    void check_base_exception_at_login() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login("ahmet", "yanlisSifre"))
                .isInstanceOf(BaseException.class);

        // jwtService'e HİÇ dokunulmamalı — şifre yanlışsa token üretilmemeli
        verifyNoInteractions(jwtService);
    }


    @Test
    void login_result_dto_check() {
        User user = new User("serkan", "hash", UserRole.SUPERVISOR);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken("serkan")).thenReturn("fake-token-123");

        AuthService.LoginResult result = authService.login("serkan", "hash");

        assertThat(result.token()).isEqualTo("fake-token-123");
        assertThat(result.role()).isEqualTo("SUPERVISOR");
    }

    @Test //important test, found a bug
    void login_deactive_baseExceptionThrow() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new DisabledException("Account disabled"));

        assertThatThrownBy(() -> authService.login("pasif_kullanici", "herhangiBirSifre"))
                .isInstanceOf(BaseException.class);

        verifyNoInteractions(jwtService);
    }


    @Test
    void login_true_auditLogSaving() {
        User user = new User("ahmet", "hash-degeri", UserRole.SUPERVISOR);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken("ahmet")).thenReturn("sahte-token");

        authService.login("ahmet", "hash-degeri");

        verify(auditLogService).record(any(), eq("ahmet"), isNull());
    }




}
