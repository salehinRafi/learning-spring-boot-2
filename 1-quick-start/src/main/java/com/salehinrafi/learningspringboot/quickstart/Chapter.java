package com.salehinrafi.learningspringboot.quickstart;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Data
/* annotation from Lombok generates getters, setters, a toString() method, an equals() method,
 * a hashCode() method, and a constructor for all required (that is, final) fields
 */
@Document
/* annotation flags this class as suitable for storing in a MongoDB data store */
public class Chapter {
	
	@Id
	private String id;
	private String name;
	
	public Chapter(String name) {
		this.name = name;
	}

}
