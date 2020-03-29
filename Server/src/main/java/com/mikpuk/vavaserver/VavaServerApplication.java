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

@SpringBootApplication
public class VavaServerApplication {

	public static void main(String[] args) {

		SpringApplication.run(VavaServerApplication.class, args);
		//Testovanie klienta
		//testCase();
	}

	public static void testCase()
	{
		try {
			String uri = "http://localhost:5000/getuser/{id}";
			RestTemplate restTemplate = new RestTemplate();

			ResponseEntity<User> user5 = restTemplate.exchange(uri, HttpMethod.GET,
					new HttpEntity<String>(new HttpHeaders()), User.class, 2);
			System.out.println("User: " + user5.getBody().getUsername());
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


		try {
			String uri2 = "http://localhost:5000/register/{name}/{pass}";
			RestTemplate restTemplate2 = new RestTemplate();

			ResponseEntity<Void> user2 = restTemplate2.exchange(uri2, HttpMethod.POST,
					new HttpEntity<String>(new HttpHeaders()), Void.class, "intelij", "intelij");
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

}
