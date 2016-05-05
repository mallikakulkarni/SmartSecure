package edu.sjsu.smartsecure.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by mallika on 5/4/16.
 */
public class AppContext {
    private static ApplicationContext context = null;
    private AppContext() {}

    public static ApplicationContext getApplicationContextInstance() {
        if (context == null) {
           context = context = new ClassPathXmlApplicationContext("spring-config.xml");
        }
        return context;
    }
}
