package vn.minhhai.springb_fskill.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleResolver extends AcceptHeaderLocaleResolver implements WebMvcConfigurer {
    // Danh sách các ngôn ngữ được hỗ trợ (Tiếng Anh và Tiếng Pháp)
    @SuppressWarnings("deprecation")
    private static final List<Locale> LOCALES = List.of(new Locale("en"), new Locale("vi"));

    /**
     * Phương thức dùng để xác định Locale từ request
     * 
     * @param request HttpServletRequest chứa thông tin về yêu cầu của client
     * @return Locale phù hợp với ngôn ngữ từ header "Accept-Language" hoặc mặc định
     *         là Locale.US
     */
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        // Lấy giá trị của header "Accept-Language" từ request
        String languageHeader = request.getHeader("Accept-Language");

        // Kiểm tra xem header có giá trị không, nếu không thì mặc định là tiếng Anh
        return !StringUtils.hasLength(languageHeader)
                ? Locale.US
                // Tìm ngôn ngữ phù hợp nhất từ danh sách LOCALES
                : Locale.lookup(Locale.LanguageRange.parse(languageHeader), LOCALES);
    }

    /**
     * Cấu hình ResourceBundleMessageSource để hỗ trợ đa ngôn ngữ trong ứng dụng
     * 
     * @return ResourceBundleMessageSource để lấy dữ liệu dịch từ file
     *         messages_xx.properties
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource rs = new ResourceBundleMessageSource();

        // Đặt basename của file chứa thông điệp dịch (ví dụ: messages_en.properties,
        // messages_fr.properties)
        rs.setBasename("messages");

        // Đảm bảo encoding UTF-8 để hỗ trợ các ký tự đặc biệt trong nhiều ngôn ngữ
        rs.setDefaultEncoding("UTF-8");

        // Nếu không tìm thấy bản dịch, sử dụng key thay vì báo lỗi
        rs.setUseCodeAsDefaultMessage(true);

        // Cache bản dịch trong 3600 giây (1 giờ) để giảm tải hệ thống
        rs.setCacheSeconds(3600);

        return rs;
    }
}
