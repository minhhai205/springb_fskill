package vn.minhhai.springb_fskill.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.minhhai.springb_fskill.dto.request.SignInRequest;
import vn.minhhai.springb_fskill.dto.response.TokenResponse;
import vn.minhhai.springb_fskill.exception.InvalidDataException;

import static org.springframework.http.HttpHeaders.REFERER;
import static vn.minhhai.springb_fskill.util.TokenType.ACCESS_TOKEN;
import static vn.minhhai.springb_fskill.util.TokenType.REFRESH_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public TokenResponse authenticate(SignInRequest signInRequest) {
        log.info("---------- authenticate ----------");

        var user = userService.getByUsername(signInRequest.getUsername());

        // AuthenticationManager gọi đến AuthenticationProvider đã khai báo trong config
        // để tìm kiếm user theo username và check mật khẩu để trả về kết quả
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(),
                signInRequest.getPassword()));

        // create new access token
        String accessToken = jwtService.generateToken(user);

        // create new refresh token
        String refreshToken = jwtService.generateRefreshToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }

    public TokenResponse refreshToken(HttpServletRequest request) {
        log.info("---------- refreshToken ----------");

        // Lấy token từ header
        final String refreshToken = request.getHeader(REFERER);

        // Kiểm tra xem có truyền token không
        if (StringUtils.isBlank(refreshToken)) {
            throw new InvalidDataException("Token must be not blank");
        }

        // Lấy username từ refresh token
        final String userName = jwtService.extractUsername(refreshToken, REFRESH_TOKEN);

        // Lấy user ứng với userName trong database
        var user = userService.getByUsername(userName);

        // Kiểm tra xem token có hợp lệ vói user lấy ra không
        if (!jwtService.isValid(refreshToken, REFRESH_TOKEN, user)) {
            throw new InvalidDataException("Not allow access with this token");
        }

        // tạo access token mới để trả về
        String accessToken = jwtService.generateToken(user);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
    }
}
