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
import org.testng.asserts.SoftAssert;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;

public class Day7 {
	
	WebDriver driverApi;
	WebDriver driverWeb;
	
	@Test
	public void test() {
		
		// Initialize Chrome Webdriver and set URL
		System.setProperty("webdriver.chrome.driver", "src/test/resources/WebDrivers/chromedriver.exe");
		driverApi = new ChromeDriver();
		driverApi.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);
		
		final int testPeriod = 7; // Forecast period of interest
		final String apiKey = "99a8db9a0f3c2e31";
		final String stateCode = "IL";
		final String city = "Chicago";
		
		String apiUrl = "http://api.wunderground.com/api/" + apiKey + "/forecast10day/q/" + stateCode + "/" + city + ".json";
		driverApi.get(apiUrl);
		
		driverWeb = new ChromeDriver();
		driverWeb.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);

		String webUrl = "https://weather.com/weather/tenday/l/USIL0225:1:US";
		driverWeb.get(webUrl);
		
		// JSON Response Body
		String jsonResponse = driverApi.findElement(By.tagName("pre")).getText();
		//System.out.println(jsonResponse);
		
		// Initialize JSON parser and parsed object handle
		JSONParser jsonParser = new JSONParser();
		Object parsedObject;
		//ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
		
		SoftAssert softAssert = new SoftAssert();
		
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
					System.out.println("Date: " + month + "-" + day + "-" + year);
					System.out.println("\n");
					
					// Gets Low Temperature
					JSONObject lowObject = (JSONObject) cursorObject.get("low");
					String lowApi = lowObject.get("fahrenheit").toString();
					
					String xpathLow = "//*[@id='twc-scrollabe']/table/tbody/tr[" + testPeriod + "]/td[4]/div/span[3]";
					String lowWeb = driverWeb.findElement(By.xpath(xpathLow)).getText();
					lowWeb = lowWeb.replace("°", "");
					
					// Gets High Temperature
					JSONObject highObject = (JSONObject) cursorObject.get("high");
					String highApi = highObject.get("fahrenheit").toString();
					
					String xpathHigh = "//*[@id='twc-scrollabe']/table/tbody/tr[" + testPeriod + "]/td[4]/div/span[1]";
					String highWeb = driverWeb.findElement(By.xpath(xpathHigh)).getText();
					highWeb = highWeb.replace("°", "");
					
					// Get Condition
					String conditionApi = cursorObject.get("conditions").toString();
					
					String xpathCondition = "//*[@id='twc-scrollabe']/table/tbody/tr[" + testPeriod + "]/td[3]/span";
					String conditionWeb = driverWeb.findElement(By.xpath(xpathCondition)).getText();
					
					// Get Humidity
					String humidityApi = cursorObject.get("avehumidity").toString();
					
					String xpathHumidity = "//*[@id='twc-scrollabe']/table/tbody/tr[" + testPeriod + "]/td[7]/span/span";
					String humidityWeb = driverWeb.findElement(By.xpath(xpathHumidity)).getText();
					humidityWeb = humidityWeb.replace("%", "");
					
					// Get Wind Speed
					JSONObject windObject = (JSONObject) cursorObject.get("avewind");
					String windSpeedApi = windObject.get("mph").toString();
					
					String xpathWind = "//*[@id='twc-scrollabe']/table/tbody/tr[" + testPeriod + "]/td[6]/span";
					String windSpeedWeb = driverWeb.findElement(By.xpath(xpathWind)).getText();
					windSpeedWeb = windSpeedWeb.replaceAll("[^0-9]", "");
					
					// Get Wind Direction
					String windDirApi = windObject.get("dir").toString();
					String windDirWeb = driverWeb.findElement(By.xpath(xpathWind)).getText();
					windDirWeb = windDirWeb.replaceAll("[^A-Z]", "");
					
					// Task 1 - Verification
					
					System.out.println("Task 1");
					System.out.println("\n");
					
					// Temperature Low
					System.out.println("Temperature Low Comparison");
					System.out.println("API: " + lowApi);
					System.out.println("Website: " + lowWeb);
					System.out.println("\n");
					softAssert.assertEquals(lowApi, lowWeb);
	
					// Temperature High
					System.out.println("Temperature High Comparison");
					System.out.println("API: " + highApi);
					System.out.println("Website: " + highWeb);
					System.out.println("\n");
					softAssert.assertEquals(highApi, highWeb);

					// Condition
					System.out.println("Condition Comparison");
					System.out.println("API: " + conditionApi);
					System.out.println("Website: " + conditionWeb);
					System.out.println("\n");
					softAssert.assertEquals(conditionApi, conditionWeb);
					
					// Humidity
					System.out.println("Humidity Comparison");
					System.out.println("API: " + humidityApi);
					System.out.println("Website: " + humidityWeb);
					System.out.println("\n");
					softAssert.assertEquals(humidityApi, humidityWeb);
					
					// Wind Speed
					System.out.println("Wind Speed Comparison");
					System.out.println("API: " + windSpeedApi);
					System.out.println("Website: " + windSpeedWeb);
					System.out.println("\n");
					softAssert.assertEquals(windSpeedApi, windSpeedWeb);
					
					// Wind Direction
					System.out.println("Wind Direction Comparison");
					System.out.println("API: " + windDirApi);
					System.out.println("Website: " + windDirWeb);
					System.out.println("\n");
					softAssert.assertEquals(windDirApi, windDirWeb);
					
					// Task 2 - Temperature Difference
					
					System.out.println("Task 2");
					System.out.println("\n");
					
					System.out.println("High: " + highApi + "°F");
					System.out.println("Low: " + lowApi + "°F");
					
					Long difference = Long.parseLong(highApi) - Long.parseLong(lowApi);
					
					if(difference >= 20) {
						System.out.println("The difference of " + difference + " is greater than or equal to 20. Test Failed.");
						softAssert.fail("Test Failed");
					}
					else
						System.out.println("The difference of " + difference + " is less than 20. Test Passed.");
					
					System.out.println("\n");
					softAssert.assertAll();
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
		driverApi.quit();
		driverWeb.quit();
	}
}
