package vn.minhhai.springb_fskill.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;

    /**
     * Send email by Google SMTP
     *
     * @param recipients Người nhận (nhiều người nhận)
     * @param subject    Chủ đề
     * @param content    Nội dung
     * @param files      File (nhiều file)
     * @return
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public String sendEmail(String recipients, String subject, String content, MultipartFile[] files)
            throws UnsupportedEncodingException, MessagingException {
        log.info("Email is sending ...");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(emailFrom, "Minh Hai Nguyen"); // Gán mail gửi đi, thay "Minh Hai Nguyen" cho tên mail gửi

        if (recipients.contains(",")) { // send to multiple users
            helper.setTo(InternetAddress.parse(recipients));
        } else { // send to single user
            helper.setTo(recipients);
        }

        // Send attach files
        if (files != null) {
            for (MultipartFile file : files) {
                helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            }
        }

        helper.setSubject(subject);
        helper.setText(content, true); // Có thể dùng hàm setText khác để gửi định dạng template
        mailSender.send(message);

        log.info("Email has sent to successfully, recipients: {}", recipients);

        return "Sent";
    }

    /**
     * 
     * Gửi mail xác thực cho người dùng trong đó có gán đường link ấn vào để
     * xác nhận, tạo 1 api để xác nhận khi người dùng ấn vào link đó.
     *
     * @param emailTo
     * @param resetToken
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public void sendConfirmLink(String emailTo, Long userId, String secretCode)
            throws MessagingException, UnsupportedEncodingException {
        log.info("Sending confirming link to user, email={}", emailTo);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        Context context = new Context();

        /*
         * linkConfirm để gán vào trong "confirm-email.html", chưa làm tính năng xác
         * thực qua link này, đây chỉ là ví dụ về gửi mail với Thymeleaf Template Engine
         * có gán link
         */
        String linkConfirm = String.format("http://localhost:8080/user/confirm/%s?secretCode=%s", userId, secretCode);

        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Minh Hai Nguyen");
        helper.setTo(emailTo);
        helper.setSubject("Please confirm your account");
        String html = templateEngine.process("confirm-email.html", context);
        helper.setText(html, true);

        mailSender.send(message);
        log.info("Confirming link has sent to user, email={}, linkConfirm={}", emailTo, linkConfirm);
    }

}
