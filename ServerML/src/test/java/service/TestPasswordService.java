package service;

import edu.sjsu.smartsecure.service.PasswordService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Bean;

/**
 * Created by mallika on 5/3/16.
 */
public class TestPasswordService {
    PasswordService passwordService;
    @Before
    public void setup() {
        passwordService = new PasswordService();
    }
    @Test
    public void testPasswordService() {
        String email = "dinesh.a.joshi@gmail.com";
        String passcode = "123";
        passwordService.generateEmail(email, passcode);
    }

    @Test
    public void testResetPassword() {
        String email = "mallikak2709@gmail.com";
        passwordService.resetPassCode("mallikaadinath.kulkarni@sjsu.edu");
    }
}
