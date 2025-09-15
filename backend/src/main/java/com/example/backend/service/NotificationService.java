package com.example.backend.service;

import com.example.backend.model.LoanEntity;
import com.example.backend.repository.LoanRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  private final LoanRepository loanRepository;
  private final EmailService emailService;

  public NotificationService(
    LoanRepository loanRepository,
    EmailService emailService
  ) {
    this.loanRepository = loanRepository;
    this.emailService = emailService;
  }

  /**
   * Runs every day at 11:00 AM server time
   *
   * Update the cron as needed:
   *  - At 11:00 AM every day: 0 0 11 * * ?
   */
  @Scheduled(cron = "0 50 21 * * ?")
  public void sendDueDateReminders() {
    LocalDate today = LocalDate.now();

    // Find all loans that are due today or are overdue and not returned
    List<LoanEntity> dueToday = loanRepository.findAllDueToday(today);

    for (LoanEntity loan : dueToday) {
      String personEmail = loan.getPerson().getEmail();

      // Subject Line
      String subject = "Erinnerung";

      // Construct HTML body
      String htmlBody = buildHtmlEmailBody(
        loan.getPerson().getFirstName(),
        loan.getMedia().getTitle(),
        loan.getPerson().getUser().getUsername()
      );

      // Send the HTML email with logo
      emailService.sendHtmlEmailWithLogo(personEmail, subject, htmlBody);
    }
  }

  /**
   * Build the HTML email body, embedding the logo with cid:logoImage
   */
  private String buildHtmlEmailBody(
    String firstName,
    String mediaTitle,
    String username
  ) {
    return String.format(
      "<!DOCTYPE html>" +
      "<html>" +
      "  <head>" +
      "    <meta charset='UTF-8' />" +
      "    <meta name='viewport' content='width=device-width, initial-scale=1.0'/>" +
      "    <style>" +
      "      body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f2f2f2; }" +
      "      .email-container { max-width: 600px; margin: 20px auto; background-color: #fff; padding: 20px; border-radius: 5px; }" +
      "      .logo { display: block; margin: 0 auto 20px auto; max-width: 120px; }" +
      "      .content { line-height: 1.6; color: #333; }" +
      "      h1 { color: #333; text-align: center; }" +
      "      .footer { margin-top: 20px; font-size: 0.85rem; color: #999; text-align: center; }" +
      "      .btn { display: inline-block; padding: 10px 20px; margin-top: 20px; background-color: #007BFF; color: #fff; text-decoration: none; border-radius: 4px; }" +
      "    </style>" +
      "  </head>" +
      "  <body>" +
      "    <div class='email-container'>" +
      "      <img src='cid:logoImage' alt='Logo' class='logo' />" +
      "      <div class='content'>" +
      "        <h1>Freundliche Erinnerung</h1>" +
      "        <p>Hallo %s,</p>" +
      "        <p>Dies ist eine freundliche Erinnerung, dass du das Medium <strong>%s</strong> ausgeliehen hast.</p>" +
      "        <p>Falls du es bereits zurückgegeben hast, vielen Dank! Wenn du es jedoch noch hast, bringst du es bitte bald zurück, damit auch andere es nutzen können.</p>" +
      "        <p>Benötigst du mehr Zeit ? Schreib mir einfach.</p>" +
      "        <p>Vielen Dank und alles Gute!</p>" +
      "        <p>Beste Grüße,<br/>%s<br/>AdamPos</p>" +
      "      </div>" +
      "      <div class='footer'>" +
      "      </div>" +
      "    </div>" +
      "  </body>" +
      "</html>",
      firstName,
      mediaTitle,
      username
    );
  }
}
