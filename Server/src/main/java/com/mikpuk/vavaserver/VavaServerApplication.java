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
		SpringApplication.run(VavaServerApplication.class, args);
		Logger logger = LoggerFactory.getLogger(VavaServerApplication.class);
		logger.info("Application logging starting");
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
