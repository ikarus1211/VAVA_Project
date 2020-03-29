package com.mikpuk.vavaserver;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyRestController {

    @RequestMapping(value = "/register/{username}/{password}")
    @ResponseBody
    public ResponseEntity<Void> registerUser(@PathVariable String username, @PathVariable String password)
    {
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
    public ResponseEntity<User> getUser(@PathVariable int id)
    {
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


}
