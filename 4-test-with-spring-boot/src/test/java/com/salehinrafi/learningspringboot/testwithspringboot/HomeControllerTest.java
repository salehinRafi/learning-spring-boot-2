package com.salehinrafi.learningspringboot.testwithspringboot;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.salehinrafi.learningspringboot.testwithspringboot.HomeController;
import com.salehinrafi.learningspringboot.testwithspringboot.Image;
import com.salehinrafi.learningspringboot.testwithspringboot.ImageService;

/**
 * Testing WebFlux controllers - @WebFluxTest
 * 
 * Default configuration => enable all @controller beans & @RestController beans
 * as well as mock web env. => at the same time disable other autoconfiguration.
 * 
 * using `controller` arguments => specifies ONLY what controller we want.
 * 
 * @Import(... ) => specifies what additional bits we want configured outside of
 * any Spring WebFlux controllers.
 */

@RunWith(SpringRunner.class)
@WebFluxTest(controllers = HomeController.class)
@Import({ ThymeleafAutoConfiguration.class })
public class HomeControllerTest {

	@Autowired
	WebTestClient webClient; // auto-wired into this test case, giving the means to make mock web calls.

	@MockBean
	ImageService imageService; // signals that the ImageService collaborator bean needed by our HomeController
								// will be replaced by a mock.

	@Test
	public void baseRouteShouldListAllImages() {
		// GIVEN
		// mock up the ImageService bean to return a Flux of two images when findAllImages gets called.
		Image alphaImage = new Image("1", "alpha.png");
		Image bravoImage = new Image("2", "bravo.png");
		given(imageService.findAllImages()).willReturn(Flux.just(alphaImage, bravoImage));

		// WHEN
		/*
		* - webClient is then used to perform a GET / using its fluent API.
		* - verify the HTTP status to be a 200 OK.
		* - extract the body of the result into a string.
		*/
		EntityExchangeResult<String> result = webClient.get().uri("/").exchange().expectStatus().isOk()
				.expectBody(String.class).returnResult();

		// THEN
		// to prove that ImageService bean's findAllImages was indeed called.
		verify(imageService).findAllImages();
		// to prove that no other calls are made to imageService mock.
		verifyNoMoreInteractions(imageService); 
		assertThat(result.getResponseBody()).contains("<title>Learning Spring Boot: Spring-a-Gram</title>")
				.contains("<a href=\"/images/alpha.png/raw\">").contains("<a href=\"/images/bravo.png/raw\">");
	}

	/* this tests successful file fetching. */
	@Test
	public void fetchingImageShouldWork() {
		given(imageService.findOneImage(any())).willReturn(Mono.just(new ByteArrayResource("data".getBytes())));

		webClient.get().uri("/images/alpha.png/raw").exchange().expectStatus().isOk().expectBody(String.class)
				.isEqualTo("data");

		verify(imageService).findOneImage("alpha.png");
		verifyNoMoreInteractions(imageService);
	}

	/* this test is aimed at a failure scenario. */
	@Test
	public void fetchingNullImageShouldFail() throws IOException {
		
		// mock out the file on the server
		Resource resource = mock(Resource.class); 
		// force it to throw an IOException when getInputStream is invoked
		given(resource.getInputStream()).willThrow(new IOException("Bad file"));
		// mock is returned when imageService.findOneImage is called
		given(imageService.findOneImage(any())).willReturn(Mono.just(resource));
		/*
		* - webClient is again used to make the call.
		* - After the exchange() method is made, we verify that the HTTP status is a 400 Bad Request.
		* - check the response body and ensure it matches the expected body from our controller's
		*   exception handler.
		*/
		webClient.get().uri("/images/alpha.png/raw").exchange().expectStatus().isBadRequest().expectBody(String.class)
				.isEqualTo("Couldn't find alpha.png => Bad file");
		verify(imageService).findOneImage("alpha.png");
		verifyNoMoreInteractions(imageService);
	}

	@Test
	public void deleteImageShouldWork() {
		Image alphaImage = new Image("1", "alpha.png");
		given(imageService.deleteImage(any())).willReturn(Mono.empty());

		webClient.delete().uri("/images/alpha.png").exchange().expectStatus().isSeeOther().expectHeader()
				.valueEquals(HttpHeaders.LOCATION, "/");

		verify(imageService).deleteImage("alpha.png");
		verifyNoMoreInteractions(imageService);
	}
}