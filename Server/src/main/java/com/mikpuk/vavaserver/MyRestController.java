package com.mikpuk.vavaserver;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Tato classa sluzi na spristupnenie REST sluzieb
@RestController
public class MyRestController {
    @RequestMapping(value = "/register/{username}/{password}")
    @ResponseBody
    public ResponseEntity<Void> registerUser(@PathVariable String username, @PathVariable String password, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        UserJdbcTemplate userJdbcTemplate = (UserJdbcTemplate) context.getBean("userJdbcTemplate");

        try {
            userJdbcTemplate.createUser(username, password);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e)
        {
            //Nastava pri duplikate mena a pod
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Zatial nepouzivame ale moze sa hodit neskor
    @RequestMapping(value = "/getuserbyid/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable int id, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);

        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        UserJdbcTemplate userJdbcTemplate = (UserJdbcTemplate) context.getBean("userJdbcTemplate");

        try {
            //Nacitanie XML a bean
            User user = userJdbcTemplate.getUserById(id);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom ID
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getuserbydata/{username}/{password}")
    @ResponseBody
    public ResponseEntity<User> getUserByData(@PathVariable String username, @PathVariable String password, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        UserJdbcTemplate userJdbcTemplate = (UserJdbcTemplate) context.getBean("userJdbcTemplate");

        try {
            User user = userJdbcTemplate.getUserByData(username,password);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom uzivatelovi
            e.printStackTrace();
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getitem/{id}")
    @ResponseBody
    public ResponseEntity<Item> getItem(@PathVariable Long id,@RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<Item>(HttpStatus.UNAUTHORIZED);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

        try {
            Item item = itemJdbcTemplate.getItem(id);
            return new ResponseEntity<Item>(item, HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom uzivatelovi
            e.printStackTrace();
            return new ResponseEntity<Item>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getitems/{id}")
    @ResponseBody
    public ResponseEntity<List<Item>> getItems(@PathVariable Long id, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<List<Item>>(HttpStatus.UNAUTHORIZED);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

        try {
            List<Item> items = itemJdbcTemplate.getItemsByUser(id);
            return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom uzivatelovi
            e.printStackTrace();
            return new ResponseEntity<List<Item>>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getapproveditems/{id}")
    @ResponseBody
    public ResponseEntity<List<Item>> getApprovedItems(@PathVariable Long id, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<List<Item>>(HttpStatus.UNAUTHORIZED);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

        try {
            List<Item> items = itemJdbcTemplate.getApprovedItems(id);
            return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom uzivatelovi
            e.printStackTrace();
            return new ResponseEntity<List<Item>>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getotheritems/{id}")
    @ResponseBody
    public ResponseEntity<List<Item>> getOtherItems(@PathVariable Long id, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<List<Item>>(HttpStatus.UNAUTHORIZED);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

        try {
            List<Item> items = itemJdbcTemplate.getOtherItems(id);
            return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom uzivatelovi
            e.printStackTrace();
            return new ResponseEntity<List<Item>>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/createitem/{name}/{description}/{longtitude}/{latitude}/{user_id}/{type_id}")
    @ResponseBody
    public ResponseEntity<Void> createItem(@PathVariable String name, @PathVariable String description,@PathVariable float longtitude,
                                           @PathVariable float latitude,@PathVariable long user_id, @PathVariable long type_id,
                                           @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

        try {
            itemJdbcTemplate.createItem(name,description,longtitude,latitude,user_id,type_id);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom uzivatelovi
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/updateitem/{id}/{name}/{description}/{longtitude}/{latitude}/{accepted}")
    @ResponseBody
    public ResponseEntity<Void> updateItem(@PathVariable String name, @PathVariable String description,@PathVariable float longtitude,
                                           @PathVariable float latitude,@PathVariable long id, @PathVariable boolean accepted,
                                           @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization))
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

        try {
            itemJdbcTemplate.updateItem(id,name,description,longtitude,latitude,accepted);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom uzivatelovi
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public boolean isUserAuthorized(String string)
    {
        return string.equals(MD5Hashing.getSecurePassword(getAuthToken()));
    }

    private static String getAuthToken()
    {
        return "MyToken123Haha.!@";
        //Zatial neviem, preco dolne sposoby nefunguju

        /*Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
            return (String)properties.get("token");
        } catch (IOException e) {
            System.out.println("NOT FOUND! :(");
        }
        try {
            properties.load(new FileInputStream("com.mikpuk.config.properties"));
            return (String)properties.get("token");
        } catch (IOException e) {
            System.out.println("NOT FOUND! :(");
        }
        try {
            properties.load(new FileInputStream("src/main/resources/config.properties"));
            return (String)properties.get("token");
        } catch (IOException e) {
            System.out.println("NOT FOUND! :(");
            return "";
        }*/
    }


}
