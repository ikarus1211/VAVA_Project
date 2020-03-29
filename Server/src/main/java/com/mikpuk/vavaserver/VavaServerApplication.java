package com.mikpuk.vavaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class VavaServerApplication {

	public static void main(String[] args) {

		SpringApplication.run(VavaServerApplication.class, args);
		//Testovanie klienta
		//testCase();
	}

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
			String uri2 = "http://vavaserver-env-2.eba-z8cwmvuf.eu-central-1.elasticbeanstalk.com/register/{name}/{pass}";
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
