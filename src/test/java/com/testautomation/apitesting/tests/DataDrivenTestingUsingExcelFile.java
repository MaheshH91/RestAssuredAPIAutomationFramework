package com.testautomation.apitesting.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testautomation.apitesting.listener.RestAssuredListener;
import com.testautomation.apitesting.pojos.Booking;
import com.testautomation.apitesting.pojos.BookingDates;
import com.testautomation.apitesting.utils.FileNameConstant;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class DataDrivenTestingUsingExcelFile {
	@Test(dataProvider = "ExcelTestData")
	public void dataDrivenTesting(Map<String, String> testData) {

		int totalPrice = Integer.parseInt(testData.get("TotalPrice"));

		try {
			BookingDates bookingDates = new BookingDates("2024-05-05", "2024-05-09");
			Booking booking = new Booking(testData.get("FirstName"), testData.get("LastName"), "Breakfast", totalPrice,
					true, bookingDates);

			ObjectMapper objMapper = new ObjectMapper();
			String requestbody = objMapper.writerWithDefaultPrettyPrinter().writeValueAsString(booking);

			Response response = 
								RestAssured
										.given()
											.filter(new RestAssuredListener()).contentType(ContentType.JSON)
											.body(requestbody)
											.baseUri("https://restful-booker.herokuapp.com/booking")
										.when()
											.post()
										.then()
											.assertThat()
											.statusCode(200)
											.extract()
											.response();

			int bookingId = response.path("bookingid");
			System.out.println("Booking Id : "+bookingId);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@DataProvider(name = "ExcelTestData")
	public Object[][] getTestData() {

		String query = "select * from Sheet1 where Run='Yes'";

		Object[][] objArray = null;
		Map<String, String> testData = null;
		List<Map<String, String>> testDataList = null;

		Fillo fillo = new Fillo();
		Connection connection = null;
		Recordset recordset = null;

		try {
			connection = fillo.getConnection(FileNameConstant.EXCEL_TEST_DATA);
			recordset = connection.executeQuery(query);

			testDataList = new ArrayList<Map<String, String>>();

			while (recordset.next()) {

				testData = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

				for (String field : recordset.getFieldNames()) {
					testData.put(field, recordset.getField(field));

				}
				testDataList.add(testData);
			}
			objArray = new Object[testDataList.size()][1];
			for (int i = 0; i < testDataList.size(); i++) {
				objArray[i][0] = testDataList.get(i);
			}

		} catch (FilloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return objArray;

	}
}
