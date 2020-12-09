package com.salehinrafi.learningspringboot.testwithspringboot;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.salehinrafi.learningspringboot.testwithspringboot.Image;

public class ImageTests {
	@Test
	public void imagesManagedByLombokShouldWork() {
		Image image = new Image("id", "file-name.jpg");
		assertThat(image.getId()).isEqualTo("id");
		assertThat(image.getName()).isEqualTo("file-name.jpg");
	}

}
