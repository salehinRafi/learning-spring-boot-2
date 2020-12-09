package com.salehinrafi.learningspringboot.testwithspringboot.webdriver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
/*
 * indicates that this class is a source of beans' definitions. After all,
 * that's what auto-configuration classes do--create beans.
 */
@ConditionalOnClass(WebDriver.class)
/*
 * this configuration class will only be evaluated by Spring Boot if it detects
 * WebDriver on the classpath, a telltale sign of Selenium WebDriver being part
 * of the project.
 */
@EnableConfigurationProperties(WebDriverConfigurationProperties.class)
/*
 * activates a set of properties to support what we put into our test case.
 * We'll soon see how to easily define a set of properties that get the rich
 * support Spring Boot provides of overriding through multiple means.
 */
@Import({ ChromeDriverFactory.class, FirefoxDriverFactory.class, SafariDriverFactory.class })
/* to pull in extra bean definition classes. */
public class WebDriverAutoConfiguration {

	@Autowired
	WebDriverConfigurationProperties properties;

	@Primary
	/*
	 * this method should be given priority when someone is trying to autowire a
	 * WebDriver bean over any other method
	 */
	@Bean(destroyMethod = "quit")
	/*
	 * lags the method as a Spring bean definition, but with the extra feature of
	 * invoking WebDriver.quit() when the application context shuts down.
	 */
	@ConditionalOnMissingBean(WebDriver.class)
	/*
	 * classic Spring Boot technique. It says to skip this method if there is already
	 * a defined WebDriver bean. HINT: There should be a test case to verify that
	 * Boot backs off properly!
	 */
	public WebDriver weDriver(FirefoxDriverFactory firefoxDriverFactory, SafariDriverFactory safariDriverFactory,
			ChromeDriverFactory chromeDriverFactory) {

		WebDriver driver = new FirefoxDriver();
		if (driver == null) {
			driver = safariDriverFactory.getObject();
		}
		if (driver == null) {
			driver = chromeDriverFactory.getObject();
		}
		if (driver == null) {
			driver = new HtmlUnitDriver();
		}
		return driver;
	}

}
