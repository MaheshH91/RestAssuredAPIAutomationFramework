package com.testautomation.apitesting.tests;

import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.testautomation.apitesting.utils.BaseTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


public class PostAPIRequest extends BaseTest{
  @Test
  public void createBooking() {
	  
	  
	  //Prepare request body
	  JSONObject booking = new JSONObject();
	  JSONObject bookingDates = new JSONObject();
	  
	  bookingDates.put("checkin", "2024-03-05");
      bookingDates.put("checkout", "2024-08-05");
      
      booking.put("firstname", "Api Testing");
      booking.put("lastname", "Tutorials");
      booking.put("totalprice", 1000);
      booking.put("depositpaid", true);
      booking.put("bookingdates", bookingDates);
      booking.put("additionalneeds", "Breakfast and Lunch");

     Response response= RestAssured
      			.given()
      				.contentType(ContentType.JSON)
      				.body(booking.toString())
      				.baseUri("https://restful-booker.herokuapp.com/booking")
//      				
      			.when()
      				.post()
      			.then()
      				.assertThat()

      				.statusCode(200)
     				.body("booking.firstname",Matchers.equalTo("Api Testing"))
     				.body("booking.totalprice", Matchers.equalTo(1000))
     				.body("booking.bookingdates.checkin", Matchers.equalTo("2024-03-05"))
     			.extract()
     				.response();
      
      			int bookingId = response.path("bookingid");
      			
      			RestAssured
      					.given()
      						.contentType(ContentType.JSON)
      						.pathParam("bookingID", bookingId)
      						.baseUri("https://restful-booker.herokuapp.com/booking")
      					.when()
      						.get("/{bookingID}")
      					.then()
      						.assertThat()
      						.statusCode(200)
      						.body("firstname", Matchers.equalTo("Api Testing"))
      			.body("lastname", Matchers.equalTo("Tutorials"));
  }		
  @Test(enabled = false)
  public void createBooking2() {
	  
	  //Prepare request body
	  JsonObject booking = new JsonObject();
	  JsonObject bookingDates = new JsonObject();
	  
	  bookingDates.addProperty("checkin", "2018-01-01");
      bookingDates.addProperty("checkout", "2019-01-01");
      
      booking.addProperty("firstname", "Api Testing");
      booking.addProperty("lastname", "Tutorials");
      booking.addProperty("totalprice", 1000);
      booking.addProperty("depositpaid", true);
      booking.add("bookingdates", bookingDates);
      booking.addProperty("additionalneeds", "Breakfast and Lunch");

      RestAssured
      			.given()
      				.contentType(ContentType.JSON)
      				.body(booking.toString())
      				.log().body()
//  				.log().headers()
//  				.log().all()
      				.baseUri("https://restful-booker.herokuapp.com/booking")
//      				.log().body()
      				.log().all()
      				.when()
      				.post()
      			.then()
      				.assertThat()
//  				.log().body()
//  				.log().all()
//  				.log().headers()
//  				.log().ifValidationFails()
      				.statusCode(200)
      				.body("booking.firstname",Matchers.equalTo("Api Testing"));
      				
      				
	  
  }
}
