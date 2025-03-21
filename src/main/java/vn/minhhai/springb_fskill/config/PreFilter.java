package vn.minhhai.springb_fskill.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.minhhai.springb_fskill.service.JwtService;
import vn.minhhai.springb_fskill.service.UserService;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static vn.minhhai.springb_fskill.util.TokenType.ACCESS_TOKEN;

/**
 * Hứng các request vào ứng dụng, sau đó xử lí mới chuyển đến các api
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {
    private final UserService userService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.info("---------- doFilterInternal ----------");

        final String authorization = request.getHeader(AUTHORIZATION);
        log.info("Authorization: {}", authorization);

        // Nếu rỗng hoặc không bắt đầu bằng "Bearer " thì trả về luôn
        if (StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorization.substring("Bearer ".length());
        // log.info("Token: {}", token);

        // Lấy ra username từ token
        final String userName = jwtService.extractUsername(token, ACCESS_TOKEN);

        if (StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Lấy userDetails tồn tại trong database
            UserDetails userDetails = userService.userDetailsService().loadUserByUsername(userName);

            // Kiểm tra nếu token hợp lệ
            if (jwtService.isValid(token, ACCESS_TOKEN, userDetails)) {

                // tạo SecurityContext, và lưu vào SecurityContextHolder, giúp ứng dụng biết
                // rằng người dùng đã đăng nhập, tìm hiểu thêm chi tiết các câu lệnh
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authentication);

                SecurityContextHolder.setContext(context);
            }
        }

        // Tiếp tục chuyển hướng sang các api
        filterChain.doFilter(request, response);
    }
}