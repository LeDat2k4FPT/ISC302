/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

/**
 *
 * @author gmt
 */
public class EmailUtils {

    public static void sendEmail(String to, String subject, String body) throws MessagingException {
        final String from = "summitspirit17718@gmail.com";
        final String appPassword = "fefy srod pssb fpfa";
        String host = "smtp.gmail.com";
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, appPassword);
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(to)
        );
        message.setSubject(subject);
        message.setContent(body, "text/html; charset=UTF-8");
        Transport.send(message);
    }
}
