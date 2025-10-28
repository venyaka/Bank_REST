package com.example.bankcards.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.MailService;
import com.example.bankcards.util.MailUtils;
import com.example.bankcards.util.UrlPathUtility;

/**
 * Реализация сервиса для отправки электронных писем.
 */
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Value("${sender.mail}")
    private String fromMail;

    @Value("${sender.token-replace}")
    private String tokenReplace;

    @Value("${sender.email-replace}")
    private String emailReplaceString;

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendUserVerificationMail(User user, HttpServletRequest request) {
        String link = UrlPathUtility.getSiteUrl(request) + "/api/authorize/verification?email=" + user.getEmail() + "&token=" + user.getToken();
        String content = MailUtils.ACCOUNT_VERIFY_TEMPLATE.replace(MailUtils.LINK, link);
        sendEmail(user.getEmail(), MailUtils.ACCOUNT_VERIFY_HEADER, content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPasswordRestoreMail(User user, HttpServletRequest request) {
        String link = UrlPathUtility.getSiteUrl(request) + "/recovery?email=" + user.getEmail() + "&token=" + user.getToken();
        String content = MailUtils.CHANGE_PASSWORD_TEMPLATE.replace(MailUtils.LINK, link);
        sendEmail(user.getEmail(), MailUtils.ACCOUNT_CHANGE_PASSWORD_HEADER, content);
    }

    /**
     * Вспомогательный метод для отправки email.
     *
     * @param to      Email получателя.
     * @param subject Тема письма.
     * @param content Содержимое письма (HTML).
     */
    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(content, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(fromMail);
            mailSender.send(mimeMessage);
            logger.info("Email sent to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
            // В реальном приложении здесь может быть более сложная логика обработки,
            // например, повторная отправка или уведомление администратора.
            throw new RuntimeException("Failed to send email", e);
        }
    }

}