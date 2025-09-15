package com.example.backend.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Send an HTML email with an embedded logo
     *
     * @param to      Recipient's email
     * @param subject Subject line of the email
     * @param htmlBody HTML content of the email
     */
    public void sendHtmlEmailWithLogo(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // true = multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("noreplyadampos@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);

            // Specify that this is HTML content
            helper.setText(htmlBody, true);

            // Embed the logo (assuming it's placed in 'src/main/resources/static/images/logo.png')
            // If your image is located elsewhere, adjust accordingly
            ClassPathResource logo = new ClassPathResource("static/AdamPos.png");

            // "logoImage" is the content-id we'll use in the HTML
            helper.addInline("logoImage", logo);

            mailSender.send(message);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
