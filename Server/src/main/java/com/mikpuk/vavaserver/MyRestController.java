package com.mikpuk.vavaserver;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@RestController
public class MyRestController {
    @RequestMapping(value = "/register/{username}/{password}")
    @ResponseBody
    public ResponseEntity<Void> registerUser(@PathVariable String username, @PathVariable String password, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);

        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        UserJdbcTemplate userJdbcTemplate = (UserJdbcTemplate) context.getBean("userJdbcTemplate");

        try {
            userJdbcTemplate.createUser(username, password);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }catch (Exception e)
        {
            //Nastava pri duplikate mena a pod
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getuser/{id}")
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable int id, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);

        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        UserJdbcTemplate userJdbcTemplate = (UserJdbcTemplate) context.getBean("userJdbcTemplate");

        try {
            User user = userJdbcTemplate.getUser(id);
            return new ResponseEntity<User>(userJdbcTemplate.getUser(id), HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom ID
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }

    public boolean isUserAuthorized(String string)
    {
        return string.equals(MD5Hashing.getSecurePassword(getAuthToken()));
    }

    private static String getAuthToken()
    {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
            return (String)properties.get("token");
        } catch (IOException e) {
            System.out.println("NOT FOUND! :(");
            return "";
        }
    }


}
