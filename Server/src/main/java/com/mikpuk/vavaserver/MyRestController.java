package com.mikpuk.vavaserver;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

//Tato classa sluzi na spristupnenie REST sluzieb
@RestController
public class MyRestController {
    Logger logger = LoggerFactory.getLogger(MyRestController.class);

    //Nacitanie XML a bean
    ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
    UserJdbcTemplate userJdbcTemplate = (UserJdbcTemplate) context.getBean("userJdbcTemplate");
    ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");


    //Tato funkcia registruje pouzivatela do nasho systemu
    @RequestMapping(value = "/register/{username}/{password}")
    @ResponseBody
    public ResponseEntity<Void> registerUser(@PathVariable String username, @PathVariable String password, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /register/{username}/{password} with variables: username {} and password {}",username,"***");

        try {
            userJdbcTemplate.createUser(username, password);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Zatial nepouzivame ale moze sa hodit neskor
    @RequestMapping(value = "/getuserbyid/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable long id, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getuserbyid/{id} with variables: id {}",id);

        try {
            User user = userJdbcTemplate.getUserById(id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }

    //Tato funkcia sa vola pri prihlasovani a vracia uzivatela, ktory sa prihlasuje
    @RequestMapping(value = "/getuserbydata/{username}/{password}")
    @ResponseBody
    public ResponseEntity<User> getUserByData(@PathVariable String username, @PathVariable String password, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getuserbydata/{username}/{password} with variables: username {} password {}",username,"***");

        try {
            User user = userJdbcTemplate.getUserByData(username,password);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }

    //Tato funkcia nastavi item ako prijaty
    @RequestMapping(value = "/setaccepteditem/{user_id}/{item_id}")
    @ResponseBody
    public ResponseEntity<Item> setAcceptedItem(@PathVariable Long user_id,@PathVariable Long item_id,@RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /setaccepteditem/{user_id}/{item_id} with variables: user_id {} item_id {}",user_id,item_id);

        try {
            itemJdbcTemplate.setAcceptedItem(item_id,user_id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Tato funkcia zmaze item, co znamena, ze ponuka bola vybavena
    @RequestMapping(value = "/removeaccepteditem/{item_id}")
    @ResponseBody
    public ResponseEntity<Item> removeAcceptedItem(@PathVariable Long item_id,@RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /removeaccepteditem/{item_id} with variables: item_id {}",item_id);

        try {
            itemJdbcTemplate.removeAcceptedItem(item_id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e)
        {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Tato funkcia sa vola ak sa pouzivatel rozhodne sitahnut ponuku
    @RequestMapping(value = "/removeitem/{item_id}")
    @ResponseBody
    public ResponseEntity<Item> removeItem(@PathVariable Long item_id,@RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /removeitem/{item_id} with variables: item_id {}",item_id);

        try {
            itemJdbcTemplate.removeItem(item_id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    //Tato funkcia vrati vsetky itemy, ktore pouzivatel vytvoril
    @RequestMapping(value = "/getitems/limit/{id}/{limit_start}/{limit_end}")
    @ResponseBody
    public ResponseEntity<List<Item>> getItemsByUserLimit(@PathVariable Long id,@PathVariable Long limit_start,@PathVariable Long limit_end, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getitems/limit/{id}/{limit_start}/{limit_end} with variables: id {} limit_start {} limit_end {}",id,limit_start,limit_end);

        try {
            List<Item> items = itemJdbcTemplate.getItemsByUserLimit(id,limit_start,limit_end);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<List<Item>>(HttpStatus.BAD_REQUEST);
        }
    }

    //Tato funkcia vracia vsetky itemy, ktore moze pouzivatel potvrdit, cize nie su jeho a nie su potvrdene.
    @RequestMapping(value = "/getotheritems/limit/{id}/{limit_start}/{limit_end}")
    @ResponseBody
    public ResponseEntity<List<Item>> getOtherItemsByUserLimit(@PathVariable Long id,@PathVariable Long limit_start,@PathVariable Long limit_end, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getotheritems/limit/{id}/{limit_start}/{limit_end} with variables: id {} limit_start {} limit_end {}",id,limit_start,limit_end);

        try {
            List<Item> items = itemJdbcTemplate.getOtherItemsByUserLimit(id,limit_start,limit_end);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<List<Item>>(HttpStatus.BAD_REQUEST);
        }
    }

    //Tato funkcia vrati zoznam vsetkych itemov, ktore pouzivatel volal
    @RequestMapping(value = "/getapproveditems/limit/{id}/{limit_start}/{limit_end}")
    @ResponseBody
    public ResponseEntity<List<Item>> getApprovedItemsLimit(@PathVariable Long id,@PathVariable Long limit_start,@PathVariable Long limit_end, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getapproveditems/limit/{id}/{limit_start}/{limit_end} with variables: id {} limit_start {} limit_end{}",id,limit_start,limit_end);

        try {
            List<Item> items = itemJdbcTemplate.getApprovedItemsLimit(id,limit_start,limit_end);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<List<Item>>(HttpStatus.BAD_REQUEST);
        }
    }

    //Tato funkcia overi, ci sa pouzivatelske meno nachadza v tabulke. Vrati 0 ak nie alebo >0 ak ano
    @RequestMapping(value = "/checkusername/{username}")
    @ResponseBody
    public ResponseEntity<Integer> checkUsername(@PathVariable String username, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /checkusername/{username} with variables: username {}",username);

        try {
            Integer count = userJdbcTemplate.checkUsername(username);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<Integer>(count, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<Integer>(HttpStatus.BAD_REQUEST);
        }
    }

    //Tato funkcia vlozi do databazy novy item
    @RequestMapping(value = "/createitem/{longtitude}/{latitude}/{user_id}/{type_id}")
    @ResponseBody
    public ResponseEntity<Void> createItem(@RequestHeader("name") String name, @RequestHeader("description") String description,@PathVariable double longtitude,
                                           @PathVariable double latitude,@PathVariable long user_id, @PathVariable long type_id,
                                           @RequestHeader("auth") String authorization)
    {
        description = new String(Base64.decodeBase64(description.getBytes()));
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /createitem/{name}/{description}/{longtitude}/{latitude}/{user_id}/{type_id} with variables: name {} | description {} | longtitude {} latitude {} user_id {} type_id {}",
                name,description,longtitude,latitude,user_id,type_id);

        try {
            itemJdbcTemplate.createItem(name,description,longtitude,latitude,user_id,type_id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Toto zatial nepouzivame ale planujeme implementovat
    @RequestMapping(value = "/updateitem/{id}/{longtitude}/{latitude}/{accepted}")
    @ResponseBody
    public ResponseEntity<Void> updateItem(@RequestHeader("name") String name, @RequestHeader("description") String description,@PathVariable double longtitude,
                                           @PathVariable double latitude,@PathVariable long id, @PathVariable boolean accepted,
                                           @RequestHeader("auth") String authorization)
    {
        description = new String(Base64.decodeBase64(description.getBytes()));
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /updateitem/{id}/{name}/{description}/{longtitude}/{latitude}/{accepted} with variables: id {} name {} | description {} | longtitude {} latitude {}",
                id,name,description,longtitude,latitude);

        try {
            itemJdbcTemplate.updateItem(id,name,description,longtitude,latitude,accepted);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Tato funkcia vypise vsetky logy na serveri
    @RequestMapping(value = "/show_logs/string")
    @ResponseBody
    public ResponseEntity<String> getLogString()
    {
        try {
            logger.info("CALLED /show_logs/string");
            return new ResponseEntity<String>(readLineByLine("my_logs.log"),HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Tato funkcia vymaze subor s logmi na serveri
    @RequestMapping(value = "/delete_logs")
    @ResponseBody
    public ResponseEntity<Void> deleteLogs()
    {
        try {
            logger.info("CALLED /delete_logs");

            //Pokus o zmazanie logov
            new PrintWriter("my_logs.log").close();

            logger.info("SUCCESS deleting logs");
            return new ResponseEntity<Void>(HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Pouziva sa pri citani logov. Vraci cely obsah logu
    private String readLineByLine(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s.replaceAll("[<>]","")).append(" </br> "));
        }
        catch (IOException e)
        {
            logger.error("Caught expection",e);
        }

        return contentBuilder.toString();
    }

    //Vrati true ak sa zhoduje prichadzajuci autorizacny token so serverovym
    public boolean isUserAuthorized(String string)
    {
        return string.equals(MD5Hashing.getSecurePassword(getAuthToken()));
    }

    //Vyberie token z configu
    private String getAuthToken()
    {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            return (String)properties.get("token");
        } catch (IOException e) {
            logger.error("getAuthTokenError!",e);
            return "";
        }
    }

}
