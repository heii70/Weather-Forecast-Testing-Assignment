package com.testcases;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;

public class Day2 {
	
	WebDriver driver;
	
	@Test
	public void test() {
		
		// Initialize Chrome Webdriver and set URL
		System.setProperty("webdriver.chrome.driver", "src/test/resources/WebDrivers/chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
		
		final int testPeriod = 2; // Forecast period of interest
		final String apiKey = "99a8db9a0f3c2e31";
		final String stateCode = "IL";
		final String city = "Chicago";
		
		String baseUrl = "http://api.wunderground.com/api/" + apiKey + "/forecast10day/q/" + stateCode + "/" + city + ".json";
		driver.get(baseUrl);
		
		// JSON Response Body
		String jsonResponse = driver.findElement(By.tagName("pre")).getText();
		//System.out.println(jsonResponse);
		
		// Initialize JSON parser and parsed object handle
		JSONParser jsonParser = new JSONParser();
		Object parsedObject;
		//ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		
		try {
			
			// Parse JSON Response and store return value in object handle
			parsedObject = jsonParser.parse(jsonResponse);
			
			// Retrieve JSON Object values from keys
			JSONObject responseObject = (JSONObject) parsedObject;
			JSONObject forecastObject = (JSONObject) responseObject.get("forecast");
			JSONObject simpleForecastObject = (JSONObject) forecastObject.get("simpleforecast");
			JSONArray forecastDayObject = (JSONArray) simpleForecastObject.get("forecastday");
			
			// Iterator to traverse through the "forecastDay" JSON array
			Iterator itr = forecastDayObject.iterator();
			
			while(itr.hasNext()) {
				
				// Traverse to the next index in the array
				Object tempObject = itr.next();
				
				// JSON cursor object handles the current index
				JSONObject cursorObject = (JSONObject) tempObject;
				
				// Get the current period
				Long Period = (Long) cursorObject.get("period");
				
				// Only process the test Period
				if(Period == testPeriod)
				{
					// Gets Date
					JSONObject dataObject = (JSONObject) cursorObject.get("date");
					Long year = (Long) dataObject.get("year");
					Long month = (Long) dataObject.get("month");
					Long day = (Long) dataObject.get("day");
					
					// Gets Low Temperature
					JSONObject lowObject = (JSONObject) cursorObject.get("low");
					String low = lowObject.get("fahrenheit").toString();
					
					// Gets High Temperature
					JSONObject highObject = (JSONObject) cursorObject.get("high");
					String high = highObject.get("fahrenheit").toString();
			
					System.out.println("Date: " + month + "-" + day + "-" + year);
					System.out.println("High: " + high + "°F");
					System.out.println("Low: " + low + "°F");
					
					Long difference = Long.parseLong(high) - Long.parseLong(low);
					
					if(difference >= 20) {
						System.out.println("The difference of " + difference + " is greater than or equal to 20. Test Failed.");
						Assert.fail("Test Failed");
					}
					else
						System.out.println("The difference of " + difference + " is less than 20. Test Passed.");
					break;
				}
			}
			//String ForecastDay = mapper.writeValueAsString(ForecastDayObject);
			//System.out.println(ForecastDay);
		}
		catch (ParseException e){
			e.printStackTrace();
		} 
//		catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}
	}
	
	@AfterTest
	public void closeBrowser() {
		driver.quit();
	}
}
