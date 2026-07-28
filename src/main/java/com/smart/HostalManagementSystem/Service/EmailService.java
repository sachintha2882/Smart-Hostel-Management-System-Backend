package com.smart.HostalManagementSystem.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.login-url}")
    private String loginUrl;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Student ekata login credentials email karanawa
    public void sendCredentialsEmail(
            String toEmail,
            String studentName,
            String username,
            String tempPassword,
            String roomNumber
    ) {


        System.out.println("========== EMAIL PROCESS START ==========");
        System.out.println("Sending email to : " + toEmail);
        System.out.println("Student name     : " + studentName);
        System.out.println("Username         : " + username);



        SimpleMailMessage message = new SimpleMailMessage();


        message.setFrom(mailUsername);

        message.setTo(toEmail);

        message.setSubject(
                "Your Hostel Room Allocation - Login Details"
        );


        String body =
                "Dear " + studentName + ",\n\n" +
                        "You have been successfully allocated a hostel room.\n\n" +
                        "Room Number   : " + roomNumber + "\n" +
                        "Username      : " + username + "\n" +
                        "Temp Password : " + tempPassword + "\n\n" +
                        "Please login using the link below and change your password on first login:\n" +
                        loginUrl + "\n\n" +
                        "Regards,\n" +
                        "Smart Hostel Management System";


        message.setText(body);



        try {

            mailSender.send(message);


            System.out.println(
                    "========== EMAIL SENT SUCCESS =========="
            );


        }
        catch(Exception e){


            System.out.println(
                    "========== EMAIL FAILED =========="
            );


            e.printStackTrace();

        }


    }
}