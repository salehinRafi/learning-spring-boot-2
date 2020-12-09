package com.salehinrafi.learningspringboot.reactivedataaccess;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Greg Turnquist
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ImageServiceTests.TestConfig.class)
@DataMongoTest
public class ImageServiceTests {

	@Autowired
	ImageRepository repository;

	@Autowired
	ImageService imageService;

	@Autowired
	MongoOperations operations;

	@Before
	public void setUp() {
		operations.dropCollection(Image.class);

		operations.insert(new Image("1", "learning-spring-boot-cover.jpg"));
		operations.insert(new Image("2", "learning-spring-boot-2nd-edition-cover.jpg"));
		operations.insert(new Image("3", "bazinga.png"));
	}

	@Test
	public void findAllImages() {
		imageService.findAllImages().collectList().block(Duration.ofSeconds(30));
	}

	@Test
	public void findOneImage() {
		imageService.findOneImage("learning-spring-boot.png").block(Duration.ofSeconds(30));
	}

	@Test
	public void deleteImage() {
		imageService.deleteImage("learning-spring-boot.png").block(Duration.ofSeconds(30));
	}

	@Configuration
	@EnableReactiveMongoRepositories(basePackageClasses = ImageRepository.class)
	static class TestConfig {

		@Bean
		public ImageService imageService(ResourceLoader resourceLoader, ImageRepository imageRepository) {
			return new ImageService(resourceLoader, imageRepository);
		}

	}

}