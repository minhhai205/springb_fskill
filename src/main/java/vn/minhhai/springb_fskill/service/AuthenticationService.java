package vn.minhhai.springb_fskill.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.minhhai.springb_fskill.dto.request.SignInRequest;
import vn.minhhai.springb_fskill.dto.response.TokenResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public TokenResponse authenticate(SignInRequest signInRequest) {
        log.info("---------- authenticate ----------");

        var user = userService.getByUsername(signInRequest.getUsername());

        // AuthenticationManager gọi đến AuthenticationProvider đã khai báo trong config
        // để tìm kiếm user theo username và check mật khẩu để trả về kết quả
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(),
                signInRequest.getPassword()));

        // create new access token
        String accessToken = "TOKEN";

        // create new refresh token
        String refreshToken = "TOKEN";

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }
}
