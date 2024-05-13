package com.testautomation.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.utils.BaseTest;
import com.testautomation.apitesting.utils.FileNameConstant;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class EndToEndAPITest extends BaseTest{
	
	private static final Logger logger= LogManager.getLogger(EndToEndAPITest.class.getName());
	@Test
	public void e2eAPIRequest() {
		
		logger.info("e2eAPIRequest test execution started...");

		try {
			String postApiRequestBody = FileUtils.readFileToString(new File(FileNameConstant.POST_API_REQUEST_BODY),
					"UTF-8");

			String tokenApiRequestBody = FileUtils.readFileToString(new File(FileNameConstant.TOKEN_REQUEST_BODY),
					"UTF-8");

			String putApiRequestBody = FileUtils.readFileToString(new File(FileNameConstant.PUT_API_REQUEST_BODY),
					"UTF-8");

			String patchApiRequestBody = FileUtils.readFileToString(new File(FileNameConstant.PATCH_API_REQUEST_BODY),
					"UTF-8");
			
			//POST API Call
			Response response = RestAssured
										.given().filter(new RestAssuredListener())
											.contentType(ContentType.JSON)
											.body(postApiRequestBody)
											.baseUri("https://restful-booker.herokuapp.com/booking")
										.when()
											.post()
										.then()
											.assertThat()
											.statusCode(200)
										.extract().response();

			JSONArray jsonArray = JsonPath.read(response.body().asString(), "$.booking..firstname");
			String firstName = (String) jsonArray.get(0);

			Assert.assertEquals(firstName, "Api Testing");
			int bookingId = JsonPath.read(response.body().asString(), "$.bookingid");
			System.out.println("BookingId : "+bookingId);
			//Get API Call
			RestAssured
					.given().filter(new RestAssuredListener())
						.contentType(ContentType.JSON)
						.baseUri("https://restful-booker.herokuapp.com/booking")
					.when()
					.get("/{bookingId}",  bookingId )
					.then()
						.assertThat()
						.statusCode(200);
			
			//Token Generation 
			Response tokenResponse=RestAssured
					.given().filter(new RestAssuredListener())
						.contentType(ContentType.JSON)
						.body(tokenApiRequestBody)
						.baseUri("https://restful-booker.herokuapp.com/auth")
					.when()
						.post()
					.then()
						.assertThat()
						.statusCode(200)
						.extract().response();
						
						
			String token = JsonPath.read(tokenResponse.body().asString(), "$.token");
			System.out.println("Token Id : "+token);
			//Put Api Request
			RestAssured
					.given().filter(new RestAssuredListener())
						.contentType(ContentType.JSON)
						.body(putApiRequestBody)
						.header("Cookie","token="+token)
						.baseUri("https://restful-booker.herokuapp.com/booking")
					.when()
						.put("/{bookingId}",bookingId)
					.then()
						.assertThat()
						.statusCode(200)
						.body("firstname", Matchers.equalTo("Specflow"))
						.body("lastname", Matchers.equalTo("Selenium C#"));
					
		//Patch Api Request Body
			RestAssured
					.given().filter(new RestAssuredListener())
						.header("cookie","token="+token)
						.contentType(ContentType.JSON)
						.body(patchApiRequestBody)
						.baseUri("https://restful-booker.herokuapp.com/booking")
					.when()
						.patch("/{bookingId}",bookingId)
					.then()
						.assertThat()
						.statusCode(200)
						.body("firstname", Matchers.equalTo("Reshma Mahesh Holkar"));
						
		//Delete API Call
			RestAssured
					.given().filter(new RestAssuredListener())
						.contentType(ContentType.JSON)
						.header("cookie","token="+token)
						.baseUri("https://restful-booker.herokuapp.com/booking")
					.when()
						.delete("/{bookingId}",bookingId)
					.then()
						.assertThat()
						.statusCode(201)
						.statusLine("HTTP/1.1 201 Created");
					
	
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("e2eAPIRequest test execution ended...");
	}
}
