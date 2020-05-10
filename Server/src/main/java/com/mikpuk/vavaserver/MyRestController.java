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

//This class is used to make REST services available
@RestController
public class MyRestController {
    Logger logger = LoggerFactory.getLogger(MyRestController.class);

    //Load XML and bean
    ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
    UserJdbcTemplate userJdbcTemplate = (UserJdbcTemplate) context.getBean("userJdbcTemplate");
    ItemJdbcTemplate itemJdbcTemplate = (ItemJdbcTemplate) context.getBean("itemJdbcTemplate");


    //This function insert User into our DB
    @RequestMapping(value = "/register/{username}/{password}")
    @ResponseBody
    public ResponseEntity<Void> registerUser(@PathVariable String username, @PathVariable String password, @RequestHeader("auth") String authorization)
    {
        if(isUserNotAuthorized(authorization)) {
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

    //This function returns User with specific ID
    @RequestMapping(value = "/getuserbyid/{id}")
    @ResponseBody
    public ResponseEntity<User> getUserById(@PathVariable long id, @RequestHeader("auth") String authorization)
    {
        if(isUserNotAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getuserbyid/{id} with variables: id {}",id);

        try {
            User user = userJdbcTemplate.getUserById(id);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //This function is called to verify User credentials and returns logged User
    @RequestMapping(value = "/getuserbydata/{username}/{password}")
    @ResponseBody
    public ResponseEntity<User> getUserByData(@PathVariable String username, @PathVariable String password, @RequestHeader("auth") String authorization)
    {
        if(isUserNotAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getuserbydata/{username}/{password} with variables: username {} password {}",username,"***");

        try {
            User user = userJdbcTemplate.getUserByData(username,password);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //This function set item as accepted in DB
    @RequestMapping(value = "/setaccepteditem/{user_id}/{item_id}")
    @ResponseBody
    public ResponseEntity<Void> setAcceptedItem(@PathVariable Long user_id,@PathVariable Long item_id,@RequestHeader("auth") String authorization)
    {
        if(isUserNotAuthorized(authorization)) {
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

    //This function set request as done so it removes item from DB and increase reputation for user.
    @RequestMapping(value = "/removeaccepteditem/{item_id}")
    @ResponseBody
    public ResponseEntity<Item> removeAcceptedItem(@PathVariable Long item_id,@RequestHeader("auth") String authorization)
    {
        if(isUserNotAuthorized(authorization)) {
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

    //This functions is called when user decides to delete request
    @RequestMapping(value = "/removeitem/{item_id}")
    @ResponseBody
    public ResponseEntity<Void> removeItem(@PathVariable Long item_id,@RequestHeader("auth") String authorization)
    {
        if(isUserNotAuthorized(authorization)) {
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

    //This function return list of all requests which user created
    @RequestMapping(value = "/getitems/limit/{id}/{limit_start}/{limit_end}")
    @ResponseBody
    public ResponseEntity<List<Item>> getItemsByUserLimit(@PathVariable Long id,@PathVariable Long limit_start,@PathVariable Long limit_end, @RequestHeader("auth") String authorization)
    {
        if(isUserNotAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getitems/limit/{id}/{limit_start}/{limit_end} with variables: id {} limit_start {} limit_end {}",id,limit_start,limit_end);

        try {
            List<Item> items = itemJdbcTemplate.getItemsByUserLimit(id,limit_start,limit_end);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //This function return all requests which user can confirm - this means that requests are not his and are not confirmed
    @RequestMapping(value = "/getotheritems/limit/{id}/{limit_start}/{limit_end}/{user_long}/{user_lat}")
    @ResponseBody
    public ResponseEntity<List<Item>> getOtherItemsByUserLimit(@PathVariable Long id,@PathVariable Long limit_start,@PathVariable Long limit_end,
                                                               @PathVariable double user_long, @PathVariable double user_lat, @RequestHeader("auth") String authorization)
    {
        if(isUserNotAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getotheritems/limit/{id}/{limit_start}/{limit_end}/{user_long}/{user_lat} with variables: id {} limit_start {} " +
                "limit_end {} user_long {} user_lat v{}",id,limit_start,limit_end,user_long,user_lat);

        try {
            List<Item> items = itemJdbcTemplate.getOtherItemsByUserLimit(id,limit_start,limit_end);
            logger.info("Returning result with {}",HttpStatus.OK);

            for(Item item:items){
                item.setDistance(getDistance(item.getLatitude(),item.getLongtitude(),user_lat,user_long));
                item.setUser(userJdbcTemplate.getUserById(item.getUser_id()));
            }

            return new ResponseEntity<>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //This function return list of requests which user accepted
    @RequestMapping(value = "/getapproveditems/limit/{id}/{limit_start}/{limit_end}/{user_long}/{user_lat}")
    @ResponseBody
    public ResponseEntity<List<Item>> getApprovedItemsLimit(@PathVariable Long id,@PathVariable Long limit_start,@PathVariable Long limit_end,
                                                            @PathVariable double user_long, @PathVariable double user_lat, @RequestHeader("auth") String authorization)
    {
        if(isUserNotAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /getapproveditems/limit/{id}/{limit_start}/{limit_end}/{user_lon}/{user_lat} with variables: id {} " +
                "limit_start {} limit_end {} user_long {} user_lat {}",id,limit_start,limit_end,user_long,user_lat);

        try {
            List<Item> items = itemJdbcTemplate.getApprovedItemsLimit(id,limit_start,limit_end);
            logger.info("Returning result with {}",HttpStatus.OK);

            for(Item item:items){
                item.setDistance(getDistance(item.getLatitude(),item.getLongtitude(),user_lat,user_long));
            }
            return new ResponseEntity<>(items, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //This function checks if username is free. Return 0 if username is free or 1 if username is taken;
    @RequestMapping(value = "/checkusername/{username}")
    @ResponseBody
    public ResponseEntity<Integer> checkUsername(@PathVariable String username, @RequestHeader("auth") String authorization)
    {
        if(isUserNotAuthorized(authorization)) {
            logger.info("Unauthorized access!");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("CALLED /checkusername/{username} with variables: username {}",username);

        try {
            Integer count = userJdbcTemplate.checkUsername(username);
            logger.info("Returning result with {}",HttpStatus.OK);
            return new ResponseEntity<>(count, HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //This function create new request and inserts it into DB
    @RequestMapping(value = "/createitem/{longtitude}/{latitude}/{user_id}/{type_id}")
    @ResponseBody
    public ResponseEntity<Void> createItem(@RequestHeader("name") String name, @RequestHeader("description") String description,@PathVariable double longtitude,
                                           @PathVariable double latitude,@PathVariable long user_id, @PathVariable long type_id,
                                           @RequestHeader("auth") String authorization)
    {
        String rawString = new String(description.getBytes());
        description = new String(Base64.decodeBase64(rawString.replaceAll("MySpaceLUL","\n")));
        if(isUserNotAuthorized(authorization)) {
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

    //TODO - we still don`t use it
    @RequestMapping(value = "/updateitem/{id}/{longtitude}/{latitude}/{accepted}")
    @ResponseBody
    public ResponseEntity<Void> updateItem(@RequestHeader("name") String name, @RequestHeader("description") String description,@PathVariable double longtitude,
                                           @PathVariable double latitude,@PathVariable long id, @PathVariable boolean accepted,
                                           @RequestHeader("auth") String authorization)
    {
        description = new String(Base64.decodeBase64(description.getBytes()));
        if(isUserNotAuthorized(authorization)) {
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

    //This function prints all logs from last delete
    @RequestMapping(value = "/show_logs/string")
    @ResponseBody
    public ResponseEntity<String> getLogString()
    {
        try {
            logger.info("CALLED /show_logs/string");
            return new ResponseEntity<>(readLineByLine("my_logs.log"), HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //This function deletes all logs
    @RequestMapping(value = "/delete_logs")
    @ResponseBody
    public ResponseEntity<Void> deleteLogs()
    {
        try {
            logger.info("CALLED /delete_logs");

            //Pokus o zmazanie logov
            new PrintWriter("my_logs.log").close();

            logger.info("SUCCESS deleting logs");
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (Exception e)
        {
            logger.error("Caught expection",e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //This function is used to read log file and return it as String
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

    //This function is used to calculate distance from item to user
    public double getDistance(double i_lat,double i_lon,double u_lat,double u_lon){
        int Radius = 6371;// radius of earth in Km

        //Ak nie je niekde nastavena vzdialenost
        if (i_lat == 0 && i_lon == 0 || u_lat == 0 && u_lon == 0)
        {
            return -1;
        }
        double dLat = Math.toRadians(u_lat - i_lat);
        double dLon = Math.toRadians(u_lon - i_lon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(i_lat))
                * Math.cos(Math.toRadians(u_lat)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (Radius * c);
    }

    //This function checks if header authorisation is the same as server authorization
    public boolean isUserNotAuthorized(String string)
    {
        return !string.equals(MD5Hashing.getSecurePassword(getAuthToken()));
    }

    //This function takes token from config file
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
