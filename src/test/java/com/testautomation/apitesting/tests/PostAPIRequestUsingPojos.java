package com.testautomation.apitesting.tests;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomation.apitesting.utils.BaseTest;
import com.testautomation.apitesting.utils.FileNameConstant;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
 
public class PostAPIRequestUsingPojos extends BaseTest {
	@Test
	public void PostAPIRequest() {
		try {
			
			String jsonschema = FileUtils.readFileToString(new File(FileNameConstant.JSONSCHEMA),"UTF-8");
			
			
			BookingDates bookingDates = new BookingDates("2024-05-05", "2024-05-09");
			Booking booking = new Booking("Api Testing", "Tutorials", "Breakfast", 1000, true, bookingDates);

			ObjectMapper objMapper = new ObjectMapper();
			String requestbody = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);
			
			System.out.println(requestbody);
						
			//De-serialization
			Booking bookingDetails = objMapper.readValue(requestbody, Booking.class);
			
			System.out.println(bookingDetails.getFirstname());
			System.out.println(bookingDetails.getTotalprice());
			
			System.out.println(bookingDetails.getBookingdates().getCheckin());
			System.out.println(bookingDetails.getBookingdates().getCheckout());
			
			Response response=RestAssured
				.given()
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
		System.out.println("*** ");
		System.out.println(jsonschema);
		
		RestAssured
			.given()
				.contentType(ContentType.JSON)
				.baseUri("https://restful-booker.herokuapp.com/booking")
			.when()
				.get("/{bookingId}",bookingId)
			.then()
				.assertThat()
				.statusCode(200)
				.body(JsonSchemaValidator.matchesJsonSchema(jsonschema));
		
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
