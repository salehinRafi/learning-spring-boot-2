package com.salehinrafi.learningspringboot.testwithspringboot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.interactions.Actions;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.chrome.ChromeDriverService.*;

// - ensures the Spring Boot annotations integrate with JUnit.
@RunWith(SpringRunner.class)

/*
 * - @SpringBootTest is the test annotation where we can activate all of Spring
 * Boot in a controlled fashion.
 * 
 * - webEnvironment switched from the default setting of a mocked web
 * environment to SpringBootTest.
 * 
 * - WebEnvironment.RANDOM_PORT, a real embedded version of the app will launch
 * on a random available port.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndTests {

	// - handle on the bridge between Selenium and the Chrome handling library
	static ChromeDriverService service;

	// - implementation of the WebDriver interface, giving all the operations to
	// drive a test browser
	static ChromeDriver driver;

	// - Spring Boot annotation that instructs Boot to autowire the port number of
	// the web container into port
	@LocalServerPort
	int port;

	// - directs JUnit to run this method before any test method inside this class
	// runs and to only run this method once
	@BeforeClass
	public static void setUp() throws IOException {
		System.setProperty("webdriver.chrome.driver", "ext/chromedriver.exe");
		service = createDefaultService();
		driver = new ChromeDriver(service);
		// - test directory to capture screenshots
		Path testResults = Paths.get("build", "test-results");
		if (!Files.exists(testResults)) {
			Files.createDirectory(testResults);
		}
	}

	// - directs JUnit to run the tearDown method after ALL tests have run in this
	// class
	@AfterClass
	public static void tearDown() {
		// - commands ChromeDriverService to shut down. Otherwise, the server process
		// will stay up and running
		service.stop();
	}

	@Test
	public void homePageShouldWork() throws IOException {

		// - navigates to the home page using the injected port
		driver.get("http://localhost:" + port);
		takeScreenshot("homePageShouldWork-1");

		// - verify the title of the page is as expected
		assertThat(driver.getTitle()).isEqualTo("Learning Spring Boot: Spring-a-Gram");

		// - grab the entire page's HTML content and verify one of the links
		String pageContent = driver.getPageSource();
		assertThat(pageContent).contains("<a href=\"/images/bazinga.png/raw\">");

		// - hunt down that link using a W3C CSS selector (there are other options as
		// well), move to it, and click on it
		WebElement element = driver.findElement(By.cssSelector("a[href*=\"bazinga.png\"]"));

		// - grab another snapshot
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().perform();
		takeScreenshot("homePageShouldWork-2");

		// - click on the back button
		driver.navigate().back();
	}

	private void takeScreenshot(String name) throws IOException {
		// - driver.getScreenshotAs(OutputType.FILE) taps the TakesScreenshot
		// sub-interface to grab a snapshot of the screen and put it into a temp file
		
		// - Spring's FileCopyUtils utility method is used to copy that temp file into
		// the project's build/test-results folder using the input argument to give it a
		// custom name screen and put it into a temp file
		FileCopyUtils.copy(driver.getScreenshotAs(OutputType.FILE),
				new File("build/test-results/TEST-" + name + ".png"));
	}
}
