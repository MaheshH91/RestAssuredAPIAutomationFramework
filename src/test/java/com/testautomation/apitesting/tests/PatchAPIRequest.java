package com.testautomation.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.utils.FileNameConstant;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class PatchAPIRequest {
	@Test
	public void putAPIRequest() {

		try {
			String postApiRequestBody = FileUtils.readFileToString(new File(FileNameConstant.POST_API_REQUEST_BODY),
					"UTF-8");

			String tokenApiRequestBody = FileUtils.readFileToString(new File(FileNameConstant.TOKEN_REQUEST_BODY),
					"UTF-8");

			String putApiRequestBody = FileUtils.readFileToString(new File(FileNameConstant.PUT_API_REQUEST_BODY),
					"UTF-8");

			String patchApiRequestBody = FileUtils.readFileToString(new File(FileNameConstant.PATCH_API_REQUEST_BODY),
					"UTF-8");
			Response response = RestAssured
										.given()
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
			System.out.println(bookingId);
			//Token Generation 
			Response tokenResponse=RestAssured
					.given()
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

			//Put Api Request
			RestAssured
					.given()
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
					.given()
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
						
					
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
