package com.salehinrafi.learningspringboot.reactivewebapp;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	/*
	 * Flux is Reactor's base type, a container holding 0..N items. none of which
	 * will be reached until the client calls the reactive stream's subscribe()
	 * method. In this case, the container holds a set of strings.
	 * 
	 * just() is a static helper method to construct a fixed collection. Other
	 * static helpers are also available, like fromArray(), fromIterable(), and
	 * fromStream(). This makes it easy to bridge existing Java collections.
	 */

	@Test
	public void data1() {
		Flux.just("alpha", "bravo", "charlie");
	}

	@Test
	public void data2() {
		String[] items = new String[] { "alpha", "bravo", "charlie" };
		Flux.fromArray(items);
	}

	@Test
	public void data3() {
		List<String> items = Arrays.asList("alpha", "bravo", "charlie");
		Flux.fromIterable(items);
	}

	@Test
	public void data4() {
		Stream<String> items = Arrays.asList("alpha", "bravo", "charlie").stream();
		Flux.fromStream(items);
	}

	@Test
	public void data5() {
		Flux.just("alpha", "bravo", "charlie").subscribe(System.out::println);
		// end::5[]
	}

	@Test
	public void data6() {
		Flux.just("alpha", "bravo", "charlie")
		.map(String::toUpperCase)
		.flatMap(s -> Flux.fromArray(s.split("")))
				.groupBy(String::toString).sort((o1, o2) -> o1.key().compareTo(o2.key()))
				.flatMap(group -> Mono.just(group.key()).zipWith(group.count()))
				.map(keyAndCount -> keyAndCount.getT1() + " => " + keyAndCount.getT2()).subscribe(System.out::println);

		/*
		 * Each entry is converted to uppercase using String::toUpperCase ensuring we'll
		   count lowers and uppers together.
		 * 
		 * The entries are then flatMapped into individual letters. To visualize
		   flatMapping, look at this example--["alpha", "bravo"] is mapped by
		   s.split("") into a collection of collections, [["a", "l", "p", "h", "a"],
		   ["b", "r", "a", "v", "o"]], and then flattened into a single collection,
		   ["a", "l", "p", "h", "a", "b", "r", "a", "v", "o"].
		 * 
		 * Then we group by the string value, which will combine all the "a" entries
		   into one subgroup, and so on and so forth. Next, we sort by the key value,
		   because the group type doesn't implement Comparable.
		   
		 * We flatMap the group's key and count value into a pair of Mono objects.
		 
		 * We unpack the tuple, and convert it into a string showing key and count.
		 
		 * We subscribe to the entire flow, printing out the results.
		 */
	}

	
	/*
	 * map() 	 | Converts one Flux into another Flux of identical size using a function
	 *		   	   applied to each element.
	 * flatMap() | Converts one Flux into another Flux of a different size by first mapping, 
	 *             and then removing any nesting.
	 * filter()	 | Converts one Flux into a smaller Flux with elements removed based on a
	 *			   filtering function.
	 * groupBy() | Converts the Flux into a bundled set of	subgroups based on the 
	 * 			   grouping function.
	 * sort() 	 | Converts one Flux into a sorted Flux based on the sorting function.
	 */
}
