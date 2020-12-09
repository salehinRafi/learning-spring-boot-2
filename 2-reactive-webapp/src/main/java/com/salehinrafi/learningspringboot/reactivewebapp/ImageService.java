package com.salehinrafi.learningspringboot.reactivewebapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
/*
 * Java NIO APIs are used to create a Path from UPLOAD_ROOT, which is used to open a lazy 
 * DirectoryStream courtesy of Files.newDirectoryStream(). 
 * DirectoryStream is a lazy iterable, which means that nothing is fetched 
 * until next() is called, making it a perfect fit for our Reactor Flux.
*/

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
/*
 * This indicates this is a Spring bean used as a service. Spring Boot will
 * automatically scan this class and create an instance.
 */
public class ImageService {

	private static String UPLOAD_ROOT = "upload-dir";
	private final ResourceLoader resourceLoader;
	/*
	 * ResourceLoader is a Spring utility class used to manage files. It is created
	 * automatically by Spring Boot and injected to the service via constructor
	 * injection. This ensures our service starts off with a consistent state.
	 */

	public ImageService(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Pre-load some test images
	 *
	 * @return Spring Boot {@link CommandLineRunner} automatically run after app
	 *         context is loaded.
	 */
	@Bean
	CommandLineRunner setUp() throws IOException {
		return (args) -> {
			FileSystemUtils.deleteRecursively(new File(UPLOAD_ROOT));
			Files.createDirectories(Paths.get(UPLOAD_ROOT));

			FileCopyUtils.copy("Test file", new FileWriter(UPLOAD_ROOT + "/learning-spring-boot-cover.jpg"));

			FileCopyUtils.copy("Test file2",
					new FileWriter(UPLOAD_ROOT + "/learning-spring-boot-2nd-edition-cover.jpg"));

			FileCopyUtils.copy("Test file3", new FileWriter(UPLOAD_ROOT + "/bazinga.png"));
		};
	}

	public Flux<Image> findAllImages() {
		try {
			return Flux.fromIterable(Files.newDirectoryStream(Paths.get(UPLOAD_ROOT)))
					.map(path -> new Image(path.hashCode(), path.getFileName().toString()));
			/*
			 * Flux.fromIterable is used to wrap this lazy iterable, allowing us to only
			 * pull each item as demanded by a reactive streams client. The Flux maps over
			 * the paths, converting each one to an Image.
			 */
		} catch (IOException e) {
			return Flux.empty();
		}
	}

	/* Mono is a container of one. Resource is Spring's abstract type for files. */
	public Mono<Resource> findOneImage(String filename) {

		/*
		 * To delay fetching the file until the client subscribes, we wrap it with
		 * Mono.fromSupplier(), and put getResource() inside a lambda. if use
		 * Mono.just(resourceLoader.getResource(... )), the resource fetching would
		 * happen immediately when the method is called.
		 */
		return Mono.fromSupplier(() -> resourceLoader.getResource("file:" + UPLOAD_ROOT + "/" + filename));
	}

	public Mono<Void> createImage(Flux<FilePart> files) {
		return files.flatMap(file -> file.transferTo(Paths.get(UPLOAD_ROOT, file.filename()).toFile())).then();
	}

	public Mono<Void> deleteImage(String filename) {
		return Mono.fromRunnable(() -> {
			try {
				Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
