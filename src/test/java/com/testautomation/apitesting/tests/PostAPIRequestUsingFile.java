package com.testautomation.apitesting.tests;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.utils.BaseTest;
import com.testautomation.apitesting.utils.FileNameConstant;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class PostAPIRequestUsingFile extends BaseTest {
	@Test
	public void PostAPIRequest() {

		try {
			String postApiRequestBody = FileUtils.readFileToString(new File(FileNameConstant.POST_API_REQUEST_BODY),
					"UTF-8");
//		System.out.println(postApiRequestBody);

			Response response = RestAssured.given().contentType(ContentType.JSON).body(postApiRequestBody)
					.baseUri("https://restful-booker.herokuapp.com/booking").when().post().then().assertThat()
					.statusCode(200).extract().response();

			JSONArray jsonArray = JsonPath.read(response.body().asString(), "$.booking..firstname");
			String firstName = (String) jsonArray.get(0);

			Assert.assertEquals(firstName, "Api Testing");
			JSONArray jsonArrayLastName = JsonPath.read(response.body().asString(), "$.booking..lastname");
			String lastName = (String) jsonArrayLastName.get(0);

			Assert.assertEquals(lastName, "Tutorials");
			
			JSONArray jsonArrayCheckin = JsonPath.read(response.body().asString(), "$.booking.bookingdates..checkin");
			String checkin = (String) jsonArrayCheckin.get(0);

			Assert.assertEquals(checkin, "2024-04-05");

			int bookingid = JsonPath.read(response.body().asString(), "$.bookingid");

			RestAssured
					.given()
						.contentType(ContentType.JSON)
						.baseUri("https://restful-booker.herokuapp.com/booking")
					.when()
						.get("/{bookingId}",bookingid)
					.then()
						.assertThat()
						.statusCode(210);
			
						
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
