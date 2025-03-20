package vn.minhhai.springb_fskill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import vn.minhhai.springb_fskill.service.UserService;

@Configuration
@RequiredArgsConstructor
@Profile("!prod") // Chạy nếu profile KHÔNG phải "prod"
public class AppConfig {
    private final UserService userService;// Chú ý nếu trong UserService lại khai báo BCryptPasswordEncoder để mã hóa
                                          // mật khẩu khi lưu user vào database sẽ tạo vòng lặp bean, tìm giải pháp
                                          // thích hợp để mã hóa mật khẩu hoạc khai báo userDetailsService
    private final PreFilter preFilter;

    private String[] WHITE_LIST = { "/auth/**" };

    @Bean // SỬ dụng cách 1(B_9): dùng WebMvcConfigurer
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("**") // Cho phép tất cả đường dẫn
                        .allowedOrigins("http://localhost:8500") // Chỉ cho phép frontend từ domain này gọi API
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // Các phương thức HTTP được phép
                        .allowedHeaders("*") // Allowed request headers
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

    /**
     * Tích hợp để mã hóa mật khẩu trước khi lưu vào database
     * 
     * @return
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain configure(@NonNull HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Tìm hiểu về csrf(AbstractHttpConfigurer::disable) ???
                // Dùng RestAPI jwt nên không cần
                // disable sẽ ẩn giao diện đòi login

                // requestMatchers cho phép các API được quyền truy cập khi chưa xác thực
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated())

                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Không sử dụng session. Mỗi request phải có xác thực riêng (ví dụ: JWT).
                // Khi dùng JWT hoặc API RESTful không cần session.
                // Tìm hiểu về SessionCreationPolicy, còn cái nào khác trong đó không ???

                .authenticationProvider(provider())

                // Lọc trước để validate token đúng mới trả về kết quả
                // Có nhiều các add Filter, có thể tìm hiểu thêm như lọc trước, sau,...
                // khi gọi các API, tham số của các hàm lọc cũng khác nhau
                .addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Cho phép thiết lập các quyền truy cập trên giao diện của trình duyệt
     * 
     * Với giao diện swagger phải chỉ định ở đây
     * 
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return webSecurity -> webSecurity.ignoring()
                .requestMatchers("/actuator/**", "/v3/**", "/webjars/**", "/swagger-ui*/*swagger-initializer.js",
                        "/swagger-ui*/**");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Cho phep truy vấn vào database thông qua userDetail (của spring security)
     * DaoAuthenticationProvider dùng để xác thực user từ database.
     * Liên kết với UserDetailsService để lấy thông tin user.
     * 
     * @return
     */
    @Bean
    public AuthenticationProvider provider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService.userDetailsService());
        provider.setPasswordEncoder(getPasswordEncoder());

        return provider;
    }

}
