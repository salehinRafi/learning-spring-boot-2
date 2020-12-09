package com.salehinrafi.learningspringboot.testwithspringboot;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
/*
 * Lombok annotation that generates getters, toString, hashCode, equals as well
 * as setters for all non-final fields
 */
@Document
/*
 * to identify this is a MongoDB domain object, but we accept Spring Data
 * MongoDB's decision about what to name the collection (it's the short name of
 * the class, lowercase, that is, image)
 */
public class Image {

	@Id
	final private String id;
	final private String name;

}
