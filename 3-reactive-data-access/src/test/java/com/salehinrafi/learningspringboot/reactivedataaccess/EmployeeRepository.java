package com.salehinrafi.learningspringboot.reactivedataaccess;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface EmployeeRepository
		extends ReactiveCrudRepository<Employee, String>, ReactiveQueryByExampleExecutor<Employee> {

}

/*
 * ReactiveCrudRepository provides the standard CRUD operations with reactive
 * options (Mono and Flux return types, and more)
 * 
 * ReactiveQueryByExampleExecutor is a mix-in interface that introduces the
 * Query by Example operations
 */
