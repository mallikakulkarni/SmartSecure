package edu.sjsu.smartsecure.service;

import edu.sjsu.smartsecure.context.AppContext;
import edu.sjsu.smartsecure.dataAccess.PasswordHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.security.util.Password;

import javax.websocket.Session;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Created by mallika on 5/3/16.
 */
public class PasswordService {
    static Logger decisionTreeLog = LoggerFactory.getLogger("decisionTree");
    private String from;
    private String subject;
    private String host;

    public String resetPassCode(String email) {
        //Generate new passcode
        //Generate an email
        //send passcode back
        String passcode = (int) Math.floor(Math.random() * 10000) + "";
        String hashText = "";
        decisionTreeLog.debug("Generated temporary passcode " + passcode);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(passcode.getBytes());
            byte[] digest = messageDigest.digest();
            BigInteger bigInt = new BigInteger(1,digest);
            hashText = bigInt.toString(16);
            decisionTreeLog.debug("Encrypted temporary passcode " + hashText);
        } catch (NoSuchAlgorithmException nsae) {
            decisionTreeLog.debug("No Such Algorithm Exception caught");
        }
        PasswordHandler passwordHandler = new PasswordHandler();
        String text = hashText.equals("") ? passcode : hashText;
        decisionTreeLog.debug("Saving new passcode " + text);
        passwordHandler.saveNewPassCode(text, email);
        decisionTreeLog.debug("Generating email ");
        generateEmail(email, passcode);
        return passcode;
    }

    public void generateEmail(String email, String passcode) {
        decisionTreeLog.debug("Getting context");
        ApplicationContext context = AppContext.getApplicationContextInstance();
        decisionTreeLog.debug("Getting Mailsender bean");
        MailSender mailSender = (MailSender) context.getBean("mailSender");
        decisionTreeLog.debug("Getting simple mail message bean");
        SimpleMailMessage templateMessage =  (SimpleMailMessage) context.getBean("templateMessage");
        SimpleMailMessage msg = new SimpleMailMessage(templateMessage);
        msg.setTo(email);
        msg.setText(
                "Dear User, " +
                        "Your new One time password is " +passcode + ". Please login and change it ASAP."
        );
        try{
            decisionTreeLog.debug("Sending email");
            mailSender.send(msg);
            decisionTreeLog.debug("Email sent");

        }
        catch (MailException ex) {
            decisionTreeLog.debug("Exception occured while sending email");
            decisionTreeLog.debug("ex.getMessage()");
            System.err.println(ex.getMessage());
        }
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

}
