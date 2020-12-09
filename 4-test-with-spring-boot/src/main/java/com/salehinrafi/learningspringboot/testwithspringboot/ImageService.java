package com.salehinrafi.learningspringboot.testwithspringboot;

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
import java.util.UUID;

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

	private final ImageRepository imageRepository;

	public ImageService(ResourceLoader resourceLoader, ImageRepository imageRepository) {
		this.resourceLoader = resourceLoader;
		this.imageRepository = imageRepository;
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
		return imageRepository.findAll();
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

		/* flatMap each one into two independent actions */
		return files.flatMap(file -> {

			/* 1. saving the image */
			Mono<Image> saveDatabaseImage = imageRepository
					.save(new Image(UUID.randomUUID().toString(), file.filename()));

			/* 2. copying the file to the server */
			Mono<Void> copyFile = Mono.just(Paths.get(UPLOAD_ROOT, file.filename()).toFile())
					.log("createImage-picktarget").map(destFile -> {
						try {
							destFile.createNewFile();
							return destFile;
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}).log("createImage-newfile").flatMap(file::transferTo).log("createImage-copy");

			/* ensure both of these operations are completed */
			return Mono.when(saveDatabaseImage, copyFile);
		}).then();
	}
	/*
	 * With a Flux of multipart files, flatMap each one into two independent
	 * actions: saving the image and copying the file to the server.
	 * 
	 * Using imageRepository, put together a Mono that stores the image in MongoDB,
	 * using UUID to create a unique key and the filename. Using FilePart, WebFlux's
	 * reactive multipart API, build another Mono that copies the file to the
	 * server.
	 * 
	 * To ensure both of these operations are completed, join them together using
	 * Mono.when(). This means that each file won't be completed until the record is
	 * written to MongoDB and the file is copied to the server. The entire flow is
	 * terminated with then() so we can signal when all the files have been
	 * processed.
	 */

	public Mono<Void> deleteImage(String filename) {
		/* delete the MongoDB image record */
		Mono<Void> deleteDatabaseImage = imageRepository.findByName(filename).flatMap(imageRepository::delete);

		Mono<Void> deleteFile = Mono.fromRunnable(() -> {
			try {
				Files.deleteIfExists(Paths.get(UPLOAD_ROOT, filename));

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		return Mono.when(deleteDatabaseImage, deleteFile).then();
	}
	/*
	 * First we create a Mono to delete the MongoDB image record. It uses
	 * imageRepository to first findByName, and then it uses a Java 8 method handle
	 * to invoke imageRepository.delete.
	 * 
	 * Next, we create a Mono using Mono.fromRunnable to delete the file using
	 * Files.deleteIfExists. 
	 * 
	 * This delays deletion until Mono is invoked. To have
	 * both of these operations completed together, we join them with Mono.when().
	 * 
	 * Since we're not interested in the results, we append a then(), which will be
	 * completed when the combined Mono is done.
	 */
}
