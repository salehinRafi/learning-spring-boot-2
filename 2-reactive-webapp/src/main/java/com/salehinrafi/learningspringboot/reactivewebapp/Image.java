package com.salehinrafi.learningspringboot.reactivewebapp;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
/*
 * Lombok annotation that generates getters, toString, hashCode, equals as well
 * as setters for all non-final fields
 */
@NoArgsConstructor
/* Lombok annotation to generate a no-argument constructor */
public class Image {

	private int id;
	private String name;

	public Image(int i, String name) {
		this.id = i;
		this.name = name;
	}

}
