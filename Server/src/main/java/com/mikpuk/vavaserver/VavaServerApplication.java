package com.mikpuk.vavaserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class VavaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VavaServerApplication.class, args);
		Logger logger = LoggerFactory.getLogger(VavaServerApplication.class);
		logger.info("Application logging starting");

		//testCase();
	}

	/*public static void testCase() {
		String AUTH_TOKEN = MD5Hashing.getSecurePassword(getAuthToken());

		try {

			String uri = "http://localhost:5000" +
					"//createitemxD/{longtitude}/{latitude}/{user_id}/{type_id}/{description}";
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			httpHeaders.add("auth", AUTH_TOKEN);

			Item testItem = new Item(999,"test name","XDDD",1.12f,1.22f,(long)1,false,2);

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(testItem);

			httpHeaders.add("name",testItem.getName());
			httpHeaders.add("description", Base64.encodeToString(descriptionText.getText().toString().getBytes(),Base64.URL_SAFE));

			restTemplate.exchange(uri, HttpMethod.POST,
					new HttpEntity<String>(httpHeaders), Item.class,
					longitude,latitude,user.getId(),selectedType);

			/*restTemplate.exchange(uri, HttpMethod.POST,
					new HttpEntity<>(testItem, httpHeaders), Item.class);


		} catch (HttpServerErrorException e) {
			//Error v pripade chyby servera
			System.out.println("SERVER EXCEPTION! " + e.getStatusCode());
		} catch (HttpClientErrorException e2) {
			//Error v pripade ziadosti klienka
			System.out.println("CLIENT EXCEPTION! " + e2.getStatusCode());
			e2.printStackTrace();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
	}

	private static String getAuthToken()
	{
		return "MyToken123Haha.!@";

	}*/


}
