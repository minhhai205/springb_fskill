package vn.minhhai.springb_fskill.controller;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.minhhai.springb_fskill.dto.response.ResponseData;
import vn.minhhai.springb_fskill.dto.response.ResponseError;
import vn.minhhai.springb_fskill.service.MailService;

import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpStatus;

@Slf4j
@RestController
@RequestMapping("/common")
@SuppressWarnings("rawtypes")
public record CommonController(MailService mailService) {

    @PostMapping("/send-email")
    public ResponseData<?> sendEmail(@RequestParam String recipients, @RequestParam String subject,
            @RequestParam String content, @RequestParam(required = false) MultipartFile[] files) {
        log.info("Request GET /common/send-email");
        try {
            return new ResponseData(HttpStatus.ACCEPTED.value(),
                    mailService.sendEmail(recipients, subject, content, files));
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.error("Sending email was failure, message={}", e.getMessage(), e);
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Sending email was failure");
        }
    }
}
