package com.salehinrafi.learningspringboot.testwithspringboot;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

import com.salehinrafi.learningspringboot.testwithspringboot.Image;
import com.salehinrafi.learningspringboot.testwithspringboot.ImageRepository;

@RunWith(SpringRunner.class) // ensure that Spring Boot test annotations run properly within JUnit
@DataMongoTest
public class EmbeddedImageRepositoryTests {

	@Autowired
	ImageRepository repository;

	@Autowired
	MongoOperations operations;

	/**
	 * To avoid {@code block()} calls, use blocking {@link MongoOperations} during
	 * setup.
	 */
	@Before
	/*
	 * @Before, flags this method to be run before every single @Test method in this
	 * class
	 */
	public void setUp() {
		operations.dropCollection(Image.class);
		operations.insert(new Image("1", "learning-spring-boot-cover.jpg"));
		operations.insert(new Image("2", "learning-spring-boot-2nd-edition-cover.jpg"));
		operations.insert(new Image("3", "bazinga.png"));
		operations.findAll(Image.class).forEach(image -> {
			System.out.println(image.toString());
		});
	}

	// 1st test case
	@Test
	public void findAllShouldWork() {
		Flux<Image> images = repository.findAll();
		StepVerifier.create(images).recordWith(ArrayList::new).expectNextCount(3).consumeRecordedWith(results -> {
			assertThat(results).hasSize(3);
			assertThat(results).extracting(Image::getName).contains("learning-spring-boot-cover.jpg",
					"learning-spring-boot-2nd-edition-cover.jpg", "bazinga.png");
		}).expectComplete().verify();
	}

	// 2nd test case
	@Test
	public void findByNameShouldWork() {
		Mono<Image> image = repository.findByName("bazinga.png");
		StepVerifier.create(image).expectNextMatches(results -> {
			assertThat(results.getName()).isEqualTo("bazinga.png");
			assertThat(results.getId()).isEqualTo("3");
			return true;
		});
	}
}
