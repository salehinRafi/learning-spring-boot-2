package com.salehinrafi.learningspringboot.reactivedataaccess;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

@Component
public class InitDatabase {
	@Bean
	CommandLineRunner init(MongoOperations operations) {
		return args -> {
			
			/* request that all entries be deleted */ 
			operations.dropCollection(Image.class);

			/* insert three new Image records */
			operations.insert(new Image("1", "learning-spring-boot-cover.jpg"));
			operations.insert(new Image("2", "learning-spring-boot-2nd-edition-cover.jpg"));
			operations.insert(new Image("3", "bazinga.png"));

			/* fetch with (findAll) and iterate over them, printing each out */
			operations.findAll(Image.class).forEach(image -> {
				System.out.println(image.toString());
			});
		};
	}

	/*
	 * @Component ensures that this class will be picked up automatically by Spring
	 * Boot, and scanned for bean definitions.
	 * 
	 * @Bean marks the init method as a bean definition requiring a MongoOperations.
	 * In turn, it returns a Spring Boot CommandLineRunner, of which all are run
	 * after the application context is fully formed (though in no particular
	 * order).
	 * 
	 * When invoked, the command-line runner will use MongoOperations, and request
	 * that all entries be deleted (dropCollection). Then it will insert three new
	 * Image records. Finally, it will fetch with (findAll) and iterate over them,
	 * printing each out.
	 */

}
