package com.salehinrafi.learningspringboot.reactivedataaccess;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Employee {
	@Id
	private String id;
	private String firstName;
	private String lastName;
	private String role;
}
