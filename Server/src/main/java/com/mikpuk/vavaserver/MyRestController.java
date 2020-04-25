package com.mikpuk.vavaserver;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

//Tato classa sluzi na spristupnenie REST sluzieb
@RestController
public class MyRestController {
    Logger logger = LoggerFactory.getLogger(MyRestController.class);
    boolean isGettingLogs = false;

    @RequestMapping(value = "/register/{username}/{password}")
    @ResponseBody
    public ResponseEntity<Void> registerUser(@PathVariable String username, @PathVariable String password, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /register/{username}/{password} with variables: username {} and password {}",username,password);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        UserJdbcTemplate userJdbcTemplate = (UserJdbcTemplate) context.getBean("userJdbcTemplate");

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

        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        UserJdbcTemplate userJdbcTemplate = (UserJdbcTemplate) context.getBean("userJdbcTemplate");

        try {
            //Nacitanie XML a bean
            User user = userJdbcTemplate.getUserById(id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom ID
            logger.error("Caught expection",e);
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getuserbydata/{username}/{password}")
    @ResponseBody
    public ResponseEntity<User> getUserByData(@PathVariable String username, @PathVariable String password, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getuserbydata/{username}/{password} with variables: username {} password {}",username,password);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        UserJdbcTemplate userJdbcTemplate = (UserJdbcTemplate) context.getBean("userJdbcTemplate");

        try {
            User user = userJdbcTemplate.getUserByData(username,password);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<User>(user, HttpStatus.OK);
        }catch (Exception e)
        {
            //Toto nastava napr pri neexistujucom uzivatelovi
            logger.error("Caught expection",e);
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/setaccepteditem/{user_id}/{item_id}")
    @ResponseBody
    public ResponseEntity<Item> setAcceptedItem(@PathVariable Long user_id,@PathVariable Long item_id,@RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /setaccepteditem/{user_id}/{item_id} with variables: user_id {} item_id {}",user_id,item_id);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

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

    @RequestMapping(value = "/removeaccepteditem/{item_id}")
    @ResponseBody
    public ResponseEntity<Item> removeAcceptedItem(@PathVariable Long item_id,@RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /removeaccepteditem/{item_id} with variables: item_id {}",item_id);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

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

    @RequestMapping(value = "/removeitem/{item_id}")
    @ResponseBody
    public ResponseEntity<Item> removeItem(@PathVariable Long item_id,@RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /removeitem/{item_id} with variables: item_id {}",item_id);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

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

    @RequestMapping(value = "/getitem/{id}")
    @ResponseBody
    public ResponseEntity<Item> getItem(@PathVariable Long id,@RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getitem/{id} with variables: id {}",id);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

        try {
            Item item = itemJdbcTemplate.getItem(id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<Item>(item, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<Item>(HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/getitems/{id}")
    @ResponseBody
    public ResponseEntity<List<Item>> getItemsByUser(@PathVariable Long id, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getitems/{id} with variables: id {}",id);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

        try {
            List<Item> items = itemJdbcTemplate.getItemsByUser(id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<List<Item>>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getitems/limit/{id}/{limit_start}/{limit_end}")
    @ResponseBody
    public ResponseEntity<List<Item>> getItemsByUserLimit(@PathVariable Long id,@PathVariable Long limit_start,@PathVariable Long limit_end, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getitems/limit/{id}/{limit_start}/{limit_end} with variables: id {} limit_start {} limit_end {}",id,limit_start,limit_end);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

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

    @RequestMapping(value = "/getotheritems/limit/{id}/{limit_start}/{limit_end}")
    @ResponseBody
    public ResponseEntity<List<Item>> getOtherItemsByUserLimit(@PathVariable Long id,@PathVariable Long limit_start,@PathVariable Long limit_end, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getotheritems/limit/{id}/{limit_start}/{limit_end} with variables: id {} limit_start {} limit_end {}",id,limit_start,limit_end);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

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

    @RequestMapping(value = "/getapproveditems/{id}")
    @ResponseBody
    public ResponseEntity<List<Item>> getApprovedItems(@PathVariable Long id, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getapproveditems/{id} with variables: id {}",id);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

        try {
            List<Item> items = itemJdbcTemplate.getApprovedItems(id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<List<Item>>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/getapproveditems/limit/{id}/{limit_start}/{limit_end}")
    @ResponseBody
    public ResponseEntity<List<Item>> getApprovedItemsLimit(@PathVariable Long id,@PathVariable Long limit_start,@PathVariable Long limit_end, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getapproveditems/limit/{id}/{limit_start}/{limit_end} with variables: id {} limit_start {} limit_end{}",id,limit_start,limit_end);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

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

    @RequestMapping(value = "/getotheritems/{id}")
    @ResponseBody
    public ResponseEntity<List<Item>> getOtherItems(@PathVariable Long id, @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getotheritems/{id} with variables: id {}",id);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

        try {
            List<Item> items = itemJdbcTemplate.getOtherItems(id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<List<Item>>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<List<Item>>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/createitem/{name}/{description}/{longtitude}/{latitude}/{user_id}/{type_id}")
    @ResponseBody
    public ResponseEntity<Void> createItem(@PathVariable String name, @PathVariable String description,@PathVariable double longtitude,
                                           @PathVariable double latitude,@PathVariable long user_id, @PathVariable long type_id,
                                           @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /createitem/{name}/{description}/{longtitude}/{latitude}/{user_id}/{type_id} with variables: name {} | description {} | longtitude {} latitude {} user_id {} type_id {}",
                name,description,longtitude,latitude,user_id,type_id);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

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

    @RequestMapping(value = "/updateitem/{id}/{name}/{description}/{longtitude}/{latitude}/{accepted}")
    @ResponseBody
    public ResponseEntity<Void> updateItem(@PathVariable String name, @PathVariable String description,@PathVariable double longtitude,
                                           @PathVariable double latitude,@PathVariable long id, @PathVariable boolean accepted,
                                           @RequestHeader("auth") String authorization)
    {
        if(!isUserAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /updateitem/{id}/{name}/{description}/{longtitude}/{latitude}/{accepted} with variables: id {} name {} | description {} | longtitude {} latitude {}",
                id,name,description,longtitude,latitude);

        //Nacitanie XML a bean
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");

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

    @RequestMapping(value = "/show_logs")
    @ResponseBody
    public ResponseEntity<byte[]> getLog()
    {
        if(!isGettingLogs) {
            logger.info("Getting log file");
            isGettingLogs = true;
        }
        else {
            logger.error("Busy preparing log file");
            return new ResponseEntity<>(HttpStatus.SERVICE_UNAVAILABLE);
        }

        try {
            FileSystemResource file = new FileSystemResource("my_logs.log");

            byte [] content = new byte[(int)file.contentLength()];
            IOUtils.read(file.getInputStream(), content);

            isGettingLogs = false;

            logger.info("Returning result with {}",HttpStatus.OK);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .lastModified(file.lastModified())
                    .contentLength(file.contentLength())
                    .body(content);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/show_logs/string")
    @ResponseBody
    public ResponseEntity<String> getLogString()
    {
        try {
            logger.info("CALLED /show_logs/string");
            return new ResponseEntity<String>(readLineByLineJava8("my_logs.log"),HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String readLineByLineJava8(String filePath)
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
