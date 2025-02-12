package vn.minhhai.springb_fskill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Lớp Translator giúp lấy thông điệp theo ngôn ngữ hiện tại của người dùng.
 */
@Component
public class Translator {

    // Đối tượng ResourceBundleMessageSource để lấy thông điệp từ file
    // messages_xx.properties
    private static ResourceBundleMessageSource messageSource;

    private Translator(@Autowired ResourceBundleMessageSource messageSource) {
        Translator.messageSource = messageSource;
    }

    /**
     * Phương thức dịch một mã thông điệp (message code) theo ngôn ngữ hiện tại.
     * 
     * @param msgCode Mã thông điệp (key trong messages_xx.properties)
     * @return Chuỗi thông điệp theo ngôn ngữ hiện tại của người dùng
     */
    public static String toLocale(String msgCode) {
        // Lấy ngôn ngữ hiện tại từ LocaleContextHolder
        Locale locale = LocaleContextHolder.getLocale();

        return messageSource.getMessage(msgCode, null, locale);
    }
}
