package com.tpssoft.hham.service;

import com.tpssoft.hham.exception.UserNotFoundException;
import com.tpssoft.hham.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    @Value("${com.tpssoft.hham.base-url}")
    private String baseUrl;

    public void sendActivityReadyNotification(String[] recipients, int activityId) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("HHAM Team <hham.tps@gmail.com>");
        email.setTo(recipients);
        email.setSubject("[Happy Hour] New activity is ready for voting");
        email.setText("Hi guys,\r\nWe have created new happy hour activity in our company \r\n" +
                "Please go to this link for login and check this: http://localhost:4200/activities/" +
                activityId + "\r\nThank you so much! \n" +
                "HHAM TEAM!");
        mailSender.send(email);
    }

    public void sendInvitation(String inviterName, String inviterEmail, String recipient, String token) {
        var message = new SimpleMailMessage();
        message.setFrom("HHAM Team <hham.tps@gmail.com>");
        message.setTo(recipient);
        message.setSubject("[Happy Hour] Invitation to HHAM system");
        message.setText("Hello,\n" + inviterName + " <" + inviterEmail + "> " +
                "has invited you to join HHAM. " +
                "Click this link to create your account: " +
                baseUrl + "/activation?token=" + token + "\n" +
                "If for some reason the Activation Code on that page is empty, " +
                "you can paste this one into the input field:\n\n" + token);
        mailSender.send(message);
    }

    /**
     * Send funding reminder to the member with the given ID
     *
     * @param userId ID of the member to receive the reminder
     *
     * @throws UserNotFoundException if the ID provided does not belong to any user
     */
    public void sendFundingReminder(int userId) {
        sendFundingReminder(userRepository
                .findById(userId)
                .orElseThrow(UserNotFoundException::new)
                .getEmail()
        );
    }

    /**
     * Send funding reminder to a specific email
     *
     * @param email The email address to receive the reminder
     */
    public void sendFundingReminder(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("HHAM Team <hham.tps@gmail.com>");
        message.setTo(email);
        message.setSubject("REMINDER EMAIL");
        message.setText("Hi guys, \n" +
                "This is just a friendly reminder that today is the day" +
                " to start with new month for funding. \n" +
                "So Please check it on: http://localhost:4200/login \n" +
                "If you have any questions, don't hesitate to contact us \n" +
                "HHAM TEAM!\n");
        mailSender.send(message);
    }
}
