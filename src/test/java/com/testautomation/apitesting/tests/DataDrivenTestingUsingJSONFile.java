package com.testautomation.apitesting.tests;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomation.apitesting.utils.FileNameConstant;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.minidev.json.JSONArray;

public class DataDrivenTestingUsingJSONFile {

	@Test(dataProvider = "getTestData")
	public void dataDrivenTestingUsingJSON(LinkedHashMap<String, String> testData) throws JsonProcessingException {

		
		
		BookingDates bookingDates = new BookingDates("2024-05-05", "2024-05-09");
		Booking booking = new Booking(testData.get("firstname"), testData.get("lastname"), "Breakfast", 1000, true, bookingDates);

		ObjectMapper objMapper = new ObjectMapper();
		String requestbody = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
		
					
		Response response=RestAssured
			.given().filter(new RestAssuredListener())
				.contentType(ContentType.JSON)
				.body(requestbody)
				.baseUri("https://restful-booker.herokuapp.com/booking")
			.when()
				.post()
			.then()
				.assertThat()
				.statusCode(200)
			.extract()
				.response();
		
	int	bookingId=response.path("bookingid");	
	System.out.println(bookingId);
	}

	@DataProvider(name = "getTestData")
	public Object[] getTestDataUsingJson() throws IOException {
		Object[] obj = null;

		String jsonTestDataString = FileUtils.readFileToString(new File(FileNameConstant.JSON_TEST_DATA), "UTF-8");

		JSONArray jsonArray = JsonPath.read(jsonTestDataString, "$");

		obj = new Object[jsonArray.size()];

		for (int i = 0; i < jsonArray.size(); i++) {
			obj[i] = jsonArray.get(i);

		}

		return obj;
	}
}
