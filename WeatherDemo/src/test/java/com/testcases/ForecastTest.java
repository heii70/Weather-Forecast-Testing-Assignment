package com.testcases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ForecastTest {
	
	WebDriver driver;
	WebDriverWait wait;

	private final String apiKey = "99a8db9a0f3c2e31";
	private final String state = "Illinois";
	private final String stateCode = "IL";
	private final String city = "Chicago";
	private final String apiUrl = "http://api.wunderground.com/api/" + apiKey + "/forecast10day/q/" + stateCode + "/" + city + ".json";
	private final String webUrl = "https://weather.com/";
	
	private String[] Date;
	
	private String[] lowApi;
	private String[] highApi;
	private String[] conditionApi;
	private String[] humidityApi;
	private String[] windSpeedApi;
	private String[] windDirApi;
	
	private String[] lowWeb;
	private String[] highWeb;
	private String[] conditionWeb;
	private String[] humidityWeb;
	private String[] windSpeedWeb;
	private String[] windDirWeb;
	
	@BeforeTest
    public static void setupTest() {
    	// setup driver executable file path
        WebDriverManager.chromedriver().setup();
    }
    
    //@Parameters({"testPeriod"})
    @BeforeClass
    public void setupClass(/*long testPeriod*/) {
    	// initialize driver, instance variables, & configurations
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 20);
        
        this.Date = new String[10];
        
        this.lowApi = new String[10];
        this.highApi = new String[10];
        this.conditionApi = new String[10];
        this.humidityApi = new String[10];
        this.windSpeedApi = new String[10];
        this.windDirApi = new String[10];
        
        this.lowWeb = new String[10];
        this.highWeb = new String[10];
        this.conditionWeb = new String[10];
        this.humidityWeb = new String[10];
        this.windSpeedWeb = new String[10];
        this.windDirWeb = new String[10];
    }
    
    @AfterClass 
    public void teardownClass() {
    	try {
    		Thread.sleep(10000);
    	}
    	catch (InterruptedException e) {
			e.printStackTrace();
    	}
    	driver.quit();
    }
    
	@DataProvider
	public static Object[] setTestPeriodIndex() {
		Object[] testPeriodIndexList = new Object[10];
		
		for(int i = 0; i < 10; i++) {
			testPeriodIndexList[i] = i;
		}
		return testPeriodIndexList;
	}
    
	@Test
	public void getAPIForecast() {		
		
		try {
			// set apiUrl connection and send GET request to API
			URL apiUrlObject = new URL(apiUrl);
			
			HttpURLConnection con = (HttpURLConnection) apiUrlObject.openConnection();
			con.setRequestMethod("GET");
			
			// read JSON response and store in a StringBuilder object
			BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream())
			); 
		
			String inputLine;
			StringBuffer response = new StringBuffer();
	    
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
	    
			in.close();
			 
		    // Initialize JSON parser and declare object handles
			String jsonResponse = response.toString();
		    JSONParser jsonParser = new JSONParser();
		    Object parsedResponseObject;
			ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
			
			// parse response and store return value in object handle
			parsedResponseObject = jsonParser.parse(jsonResponse);
			
//			String jsonResponsePretty = mapper.writeValueAsString(parsedJsonResponseObject);
//			System.out.println(jsonResponsePretty);
			
			// retrieve JSON nested object values from keys
			JSONObject jsonResponseObject = (JSONObject) parsedResponseObject;
			JSONObject forecastObject = (JSONObject) jsonResponseObject.get("forecast");
			JSONObject simpleForecastObject = (JSONObject) forecastObject.get("simpleforecast");
			JSONArray forecastDayObject = (JSONArray) simpleForecastObject.get("forecastday");
	
//			String forecastDay = mapper.writeValueAsString(forecastDayObject);
//			System.out.println(forecastDay);
			
			// Iterator to traverse through the "forecastDay" JSON array
			Iterator<?> itr = forecastDayObject.iterator();
			
			while(itr.hasNext()) {
			
				// Traverse to the next index in the array
				Object tempObject = itr.next();
				
				// JSON cursor object handles the current index
				JSONObject cursorObject = (JSONObject) tempObject;
				
				// Get the current period
				Long tempPeriod = (Long) cursorObject.get("period");
				int period = tempPeriod.intValue();
				
				// get field values for test period
				
				// Date
				JSONObject dataObject = (JSONObject) cursorObject.get("date");
				Long year = (Long) dataObject.get("year");
				Long month = (Long) dataObject.get("month");
				Long day = (Long) dataObject.get("day");
				String Date = "Date: " + month + "-" + day + "-" + year;
				
				// Low Temperature
				JSONObject lowObject = (JSONObject) cursorObject.get("low");
				String lowApi = lowObject.get("fahrenheit").toString();
					
				// High Temperature
				JSONObject highObject = (JSONObject) cursorObject.get("high");
				String highApi = highObject.get("fahrenheit").toString();
					
				// Condition
				String conditionApi = cursorObject.get("conditions").toString();
			
				// Humidity
				String humidityApi = cursorObject.get("avehumidity").toString();
					
				// Wind Speed & Wind Direction
				JSONObject windObject = (JSONObject) cursorObject.get("avewind");
				String windSpeedApi = windObject.get("mph").toString();
				String windDirApi = windObject.get("dir").toString();
					
				// assign values to instance variables
				this.Date[period-1] = Date;
				this.lowApi[period-1] = lowApi;
				this.highApi[period-1] = highApi;
				this.conditionApi[period-1] = conditionApi;
				this.humidityApi[period-1] = humidityApi;
				this.windSpeedApi[period-1] = windSpeedApi;
				this.windDirApi[period-1] = windDirApi;
			}
//			System.out.print(Arrays.toString(this.Date) + "\n");
//			System.out.print(Arrays.toString(this.lowApi) + "\n");
//			System.out.print(Arrays.toString(this.highApi) + "\n");
//			System.out.print(Arrays.toString(this.conditionApi) + "\n");
//			System.out.print(Arrays.toString(this.humidityApi) + "\n");
//			System.out.print(Arrays.toString(this.windSpeedApi) + "\n");
//			System.out.print(Arrays.toString(this.windDirApi) + "\n");
		}
		catch (IOException | ParseException e) {
			e.printStackTrace();
		} 
	}
	
	@Test
	public void getWebForecast() {
		try {
			// open URL
			driver.get(webUrl);
			
			// wait for search bar to load
			WebElement searchBar = wait.until(
				ExpectedConditions.presenceOfElementLocated(
					By.xpath("//*[contains(@id, 'header-TwcHeader')]/div/div/div/div[1]")
				)
			);
			
			// select search input field and wait for it to be clickable
			WebElement inputField = searchBar.findElement(
				By.xpath("//div/div[1]/div/input[@value='undefined']")
			);
					
			wait.until(
				ExpectedConditions.elementToBeClickable(inputField)
			);
				
			// enter city and state into search input field 
			inputField.sendKeys(this.city + ", " + this.state);
			
			// wait for search matches to appear
			WebElement searchMatches = wait.until(
				ExpectedConditions.visibilityOf(
					searchBar.findElement(
						By.xpath("//div/div[2]/div[1]")
					)
				)
			);
			
			// wait for top search match to be visible then click
//			WebElement topMatch = wait.until(
//				ExpectedConditions.visibilityOf(
//					searchMatches.findElement(
//						By.xpath("//*[contains(@id, 'header-TwcHeader')]/div/div/div/div[1]/div/div[2]/div[1]/ul/li[1]/a")
//					)
//				)
//			);
//			topMatch.click();
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			inputField.sendKeys(Keys.ENTER);	
			
			// wait for location page to load
			wait.until(
				ExpectedConditions.presenceOfElementLocated(
					By.xpath("//*[contains(@id, 'main-Nowcard')]")
				)
			);
			
			// click on 10 day forecast
			driver.findElement(
				By.xpath("//*[contains(@id, 'header-LocalsuiteNav')]/div/div/div/ul/li[4]/a/span[text()='10 Day']")
			).click();
			
			// wait for forecast page to load
			wait.until(
				ExpectedConditions.presenceOfElementLocated(
					By.xpath("//*[contains(@id, 'main-DailyForecast')]")
				)
			);
			
			// declare field value variables
			WebElement forecastTable;
			String lowWeb;
			String highWeb;
			String conditionWeb;
			String humidityWeb;
			String windWeb;
			
			// get field values
			for(int period = 1; period < 11; period++) {
				
				forecastTable = driver.findElement(
					By.xpath("//*[@id='twc-scrollabe']/table/tbody")
				);
				
				// Low Temperature
				lowWeb = forecastTable.findElement(
					By.xpath("//tr["+period+"]/td[4]/div/span[3]")
				).getText();
				
				// High Temperature
				highWeb = forecastTable.findElement(
					By.xpath("//tr["+period+"]/td[4]/div/span[1]")
				).getText();
				
				// Condition
				conditionWeb = forecastTable.findElement(
					By.xpath("//tr["+period+"]/td[3]/span")
				).getText();
		
				// Humidity
				humidityWeb = forecastTable.findElement(
					By.xpath("//tr["+period+"]/td[7]/span/span")
				).getText();
		
				// Wind Speed & Wind Direction
				windWeb = forecastTable.findElement(
					By.xpath("//tr["+period+"]/td[6]/span")
				).getText();
	
				// assign values to instance variables
				this.lowWeb[period-1] = lowWeb.replace("°", "");
				this.highWeb[period-1] = highWeb.replace("°", "");
				this.conditionWeb[period-1] = conditionWeb;
				this.humidityWeb[period-1] = humidityWeb.replace("%", "");
				this.windSpeedWeb[period-1] = windWeb.replaceAll("[^0-9]", "");
				this.windDirWeb[period-1] = windWeb.replaceAll("[^A-Z]", "");
			}
//			System.out.println(Arrays.toString(this.lowWeb) + "\n");
//			System.out.println(Arrays.toString(this.highWeb) + "\n");
//			System.out.println(Arrays.toString(this.conditionWeb) + "\n");
//			System.out.println(Arrays.toString(this.windSpeedWeb) + "\n");
//			System.out.println(Arrays.toString(this.windDirWeb) + "\n");
		}
		catch (NoSuchElementException | TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	//@Parameters({"testPeriodIndex"})
	@Test(dependsOnMethods={"getAPIForecast", "getWebForecast"}, dataProvider="setTestPeriodIndex")
	public void Verification(int testPeriodIndex) {
		
		SoftAssert softAssert = new SoftAssert();
		System.out.println("Task 1 - Verification\n");
		
		// date
		System.out.println(this.Date[testPeriodIndex]);

		// Low Temperature
		System.out.println("Low Temperature Comparison");
		System.out.println("API: " + this.lowApi[testPeriodIndex]);
		System.out.println("Website: " + this.lowWeb[testPeriodIndex]);
		System.out.println("\n");
		
		// Temperature High
		System.out.println("Temperature High Comparison");
		System.out.println("API: " + this.highApi[testPeriodIndex]);
		System.out.println("Website: " + this.highWeb[testPeriodIndex]);
		System.out.println("\n");
		
		// Condition
		System.out.println("Condition Comparison");
		System.out.println("API: " + this.conditionApi[testPeriodIndex]);
		System.out.println("Website: " + this.conditionWeb[testPeriodIndex]);
		System.out.println("\n");
		
		// Humidity
		System.out.println("Humidity Comparison");
		System.out.println("API: " + this.humidityApi[testPeriodIndex]);
		System.out.println("Website: " + this.humidityWeb[testPeriodIndex]);
		System.out.println("\n");
		
		// Wind Speed
		System.out.println("Wind Speed Comparison");
		System.out.println("API: " + this.windSpeedApi[testPeriodIndex]);
		System.out.println("Website: " + this.windSpeedWeb[testPeriodIndex]);
		System.out.println("\n");
		
		// Wind Direction
		System.out.println("Wind Direction Comparison");
		System.out.println("API: " + this.windDirApi[testPeriodIndex]);
		System.out.println("Website: " + this.windDirWeb[testPeriodIndex]);
		System.out.println("\n");
		
		try {
			softAssert.assertEquals(this.lowApi[testPeriodIndex], this.lowWeb[testPeriodIndex]);
			softAssert.assertEquals(this.highApi[testPeriodIndex], this.highWeb[testPeriodIndex]);
			softAssert.assertEquals(this.conditionApi[testPeriodIndex], this.conditionWeb[testPeriodIndex]);
			softAssert.assertEquals(this.humidityApi[testPeriodIndex], this.humidityWeb[testPeriodIndex]);
			softAssert.assertEquals(this.windSpeedApi[testPeriodIndex], this.windSpeedWeb[testPeriodIndex]);
			softAssert.assertEquals(this.windDirApi[testPeriodIndex], this.windDirWeb[testPeriodIndex]);
		}
		catch (AssertionError e) {
			e.printStackTrace();
		}

		softAssert.assertAll();
	}
	
	//@Parameters({"testPeriodIndex"})
	@Test(dependsOnMethods={"getAPIForecast", "getWebForecast"}, dataProvider="setTestPeriodIndex")
	public void VerifyTemperatureDifference(int testPeriodIndex) {
		
		System.out.println("Task 2 - Temperature Difference\n");
		
		System.out.println("High: " + this.highApi[testPeriodIndex] + "°F");
		System.out.println("Low: " + this.lowApi[testPeriodIndex] + "°F");
		Long difference = Long.parseLong(this.highApi[testPeriodIndex]) - Long.parseLong(this.lowApi[testPeriodIndex]);
		
		try {
			Assert.assertTrue(difference <= 20);
			System.out.println("\nThe difference of " + difference + " is less than 20. Test Passed.\n");
		}
		catch (AssertionError e) {
			System.out.println("The difference of " + difference + " is greater than or equal to 20. Test Failed.\n");
			e.printStackTrace();
		}
	}
}
	
