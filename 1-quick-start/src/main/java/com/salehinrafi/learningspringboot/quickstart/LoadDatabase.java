package com.salehinrafi.learningspringboot.quickstart;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Flux;

@Configuration /*marks this class as a source of beans*/
public class LoadDatabase {

	@Bean /*indicates that the return value of is a Spring Bean*/
	CommandLineRunner init(ChapterRepository repository) {
		return args -> {
			Flux.just(
					new Chapter("Quick Start with Java"),
					new Chapter("Reactive Web with Spring Boot"),
					new Chapter("... and more!"))
			.flatMap(repository::save).subscribe(System.out::println);
		};
	}

}
