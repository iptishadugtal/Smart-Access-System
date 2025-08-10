package com.amdocs.sas.util;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import javax.activation.FileDataSource;


public class EmailUtil {

    public static void sendEmailWithQR(String toEmail, String subject, String body, String qrFilePath) {
        final String fromEmail = "shreyanshags@gmail.com"; // change to your email
        final String password = "cddl appb cxbz zfki"; // use App Password (not your Gmail password)

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // for Gmail
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // Email Body
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            // Attachment
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource source = new FileDataSource(qrFilePath);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName("VisitorQR.png");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("? QR Email Sent to " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
