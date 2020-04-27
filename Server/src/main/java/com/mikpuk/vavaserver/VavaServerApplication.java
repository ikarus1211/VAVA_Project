package com.mikpuk.vavaserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.MalformedURLException;


@SpringBootApplication
public class VavaServerApplication {

	public static void main(String[] args) {
		try {
			new File("my_logs.log").delete();
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		SpringApplication.run(VavaServerApplication.class, args);
		Logger logger = LoggerFactory.getLogger(VavaServerApplication.class);
		logger.info("Application logging starting");
		//testCase();
		//testCase2();
		//testCase3();
		//testCase4();
	}

	public static void testCase4()
	{
		String AUTH_TOKEN = MD5Hashing.getSecurePassword(getAuthToken());

		try {

			String uri = "http://localhost:5000"+
					"/checkusername/{username}";
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("auth",AUTH_TOKEN);

			ResponseEntity<Integer> count = restTemplate.exchange(uri, HttpMethod.POST,
					new HttpEntity<String>(httpHeaders), Integer.class,"test");

			System.out.println("Count = "+count.getBody());

		} catch (HttpServerErrorException e)
		{
			//Error v pripade chyby servera
			System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
		} catch (HttpClientErrorException e2)
		{
			//Error v pripade ziadosti klienka
			System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
			e2.printStackTrace();
		} catch (Exception e3)
		{
			e3.printStackTrace();
		}

		/*try {
			String uri = "http://localhost:5000/getapproveditems/limit/{id}/{limit_start}/{limit_end}";
			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("auth",AUTH_TOKEN);

			ResponseEntity<Item[]> items = restTemplate.exchange(uri, HttpMethod.GET,
					new HttpEntity<String>(httpHeaders), Item[].class,1,0,3);
			for (Item item:items.getBody()) {
				System.out.println(item.getName()+ " | "+item.getDescription());
			}

		} catch (HttpServerErrorException e)
		{
			System.out.println("SERVER EXCEPTION! "+e.getRawStatusCode());
		} catch (HttpClientErrorException e2)
		{
			System.out.println("CLIENT EXCEPTION! "+e2.getRawStatusCode());
			e2.printStackTrace();
		} catch (Exception e3)
		{
			System.out.println("caught other exception");
			e3.printStackTrace();
		}

		/*try {

			String uri = "http://localhost:5000" +
					"/removeaccepteditem/{user_id}/{item_id}";
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("auth", AUTH_TOKEN);

			restTemplate.exchange(uri, HttpMethod.POST,
					new HttpEntity<String>(httpHeaders), Item.class, 1, 1);

		} catch (HttpServerErrorException e)
		{
			//Error v pripade chyby servera
			System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
		} catch (HttpClientErrorException e2)
		{
			//Error v pripade ziadosti klienka
			System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
			e2.printStackTrace();
		} catch (Exception e3)
		{
			e3.printStackTrace();
		}*/
	}

	public static void testCase3()
	{
		String AUTH_TOKEN = MD5Hashing.getSecurePassword(getAuthToken());

		try {

			String uri = "http://localhost:5000"+
					"/createitem/{name}/{description}/{longtitude}/{latitude}/{user_id}/{type_id}";
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("auth",AUTH_TOKEN);

			restTemplate.exchange(uri, HttpMethod.POST,
					new HttpEntity<String>(httpHeaders), Item.class,"testik","desc test",
					0.0,0.0,1,7);

		} catch (HttpServerErrorException e)
		{
			//Error v pripade chyby servera
			System.out.println("SERVER EXCEPTION! "+e.getStatusCode());
		} catch (HttpClientErrorException e2)
		{
			//Error v pripade ziadosti klienka
			System.out.println("CLIENT EXCEPTION! "+e2.getStatusCode());
			e2.printStackTrace();
		} catch (Exception e3)
		{
			e3.printStackTrace();
		}
	}


	public static void testCase2()
	{
		String AUTH_TOKEN = getAuthToken();
		try {
			String uri = "http://localhost:5000/getotheritems/{id}";
			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("auth",MD5Hashing.getSecurePassword(AUTH_TOKEN));

			ResponseEntity<Item[]> items = restTemplate.exchange(uri, HttpMethod.GET,
					new HttpEntity<String>(httpHeaders), Item[].class,"1");
			for (Item item:items.getBody()) {
				System.out.println(item.getName()+ " | "+item.getDescription());
			}

		} catch (HttpServerErrorException e)
		{
			System.out.println("SERVER EXCEPTION! "+e.getRawStatusCode());
		} catch (HttpClientErrorException e2)
		{
			System.out.println("CLIENT EXCEPTION! "+e2.getRawStatusCode());
			e2.printStackTrace();
		} catch (Exception e3)
		{
			System.out.println("caught other exception");
			e3.printStackTrace();
		}

	// WORKING FOR GETTING APPROVED ITEMS
		try {
			String uri = "http://localhost:5000/getapproveditems/{id}";
			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("auth",MD5Hashing.getSecurePassword(AUTH_TOKEN));

			ResponseEntity<Item[]> items = restTemplate.exchange(uri, HttpMethod.GET,
					new HttpEntity<String>(httpHeaders), Item[].class,"1");
			for (Item item:items.getBody()) {
				System.out.println(item.getName()+ " | "+item.getDescription());
			}

		} catch (HttpServerErrorException e)
		{
			System.out.println("SERVER EXCEPTION! "+e.getRawStatusCode());
		} catch (HttpClientErrorException e2)
		{
			System.out.println("CLIENT EXCEPTION! "+e2.getRawStatusCode());
			e2.printStackTrace();
		} catch (Exception e3)
		{
			System.out.println("caught other exception");
			e3.printStackTrace();
		}

	}

	//Testovanie klienta - DEBUG na localhoste
	public static void testCase()
	{
		String AUTH_TOKEN = getAuthToken();

		try {
			String uri = "http://localhost:5000/getuserbydata/{username}/{password}";
			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.add("auth",MD5Hashing.getSecurePassword(AUTH_TOKEN));

			ResponseEntity<User> user5 = restTemplate.exchange(uri, HttpMethod.GET,
					new HttpEntity<String>(httpHeaders), User.class, "yt",MD5Hashing.getSecurePassword("yt"));
			System.out.println("User: " + user5.getBody().getUsername());
			System.out.println("STATUS CODE " + user5.getStatusCode());
		} catch (HttpServerErrorException e)
		{
			System.out.println("SERVER EXCEPTION! "+e.getRawStatusCode());
		} catch (HttpClientErrorException e2)
		{
			System.out.println("CLIENT EXCEPTION! "+e2.getRawStatusCode());
			e2.printStackTrace();
		} catch (Exception e3)
		{
			System.out.println("caught other exception");
			e3.printStackTrace();
		}


		try {
			String uri2 = "http://localhost:5000/register/{name}/{pass}";
			RestTemplate restTemplate2 = new RestTemplate();

			HttpHeaders httpHeaders2 = new HttpHeaders();
			httpHeaders2.add("auth",MD5Hashing.getSecurePassword(AUTH_TOKEN));

			ResponseEntity<Void> user2 = restTemplate2.exchange(uri2, HttpMethod.POST,
					new HttpEntity<String>(httpHeaders2), Void.class, "intelij333", "intelij");
			System.out.println("STATUS CODE " + user2.getStatusCode());
		} catch (HttpServerErrorException e)
		{
			System.out.println("SERVER EXCEPTION! "+e.getRawStatusCode());
		} catch (HttpClientErrorException e2)
		{
			System.out.println("CLIENT EXCEPTION! "+e2.getRawStatusCode());
		} catch (Exception e3)
		{
			System.out.println("caught other exception");
		}


		//ResponseEntity<User> user = userJdbcTemplate.getUser(3);
		//userJdbcTemplate.createUser("intelij","intelij");
	}

	private static String getAuthToken()
	{
		return "MyToken123Haha.!@";

		/*Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("src/main/resources/config.properties"));
			return (String)properties.get("token");
		} catch (IOException e) {
			System.out.println("NOT FOUND! :(");
			return "";
		}*/

	}

}
