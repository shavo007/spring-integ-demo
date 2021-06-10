package com.example.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import com.example.model.Customer;
import com.example.repo.CustomerRepository;
import com.example.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

		private final CustomerService customerService;

		@GetMapping
		public ResponseEntity<List<Customer>> getAll() {
				try {
						List<Customer> items = new ArrayList<Customer>();

						customerService.getAllCustomers().forEach(items::add);

						if (items.isEmpty())
								return new ResponseEntity<>(HttpStatus.NO_CONTENT);

						return new ResponseEntity<>(items, HttpStatus.OK);
				} catch (Exception e) {
						return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
				}
		}

		@GetMapping("{id}")
		public ResponseEntity<Customer> getById(@PathVariable("id") Long id) {
				Customer existingItem = customerService.getCustomerById(id);

				if (existingItem != null) {
						return new ResponseEntity<>(existingItem, HttpStatus.OK);
				} else {
						return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
		}

		@PostMapping
		public ResponseEntity<Customer> create(@Valid @RequestBody Customer item) {
				try {
						Customer savedItem = customerService.save(item);
						return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
				} catch (Exception e) {
						return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
				}
		}

		// @PutMapping("{id}")
		// public ResponseEntity<Customer> update(@PathVariable("id") Long id, @RequestBody Customer item) {
		// 		Optional<Customer> existingItemOptional = customerService.getCustomerById(id);
		// 		if (existingItemOptional.isPresent()) {
		// 				Customer existingItem = existingItemOptional.get();
		// 				System.out.println("TODO for developer - update logic is unique to entity and must be implemented manually.");
		// 				//existingItem.setSomeField(item.getSomeField());
		// 				return new ResponseEntity<>(customerService.save(existingItem), HttpStatus.OK);
		// 		} else {
		// 				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		// 		}
		// }

		@DeleteMapping("{id}")
		public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
				try {
						customerService.delete(id);
						return new ResponseEntity<>(HttpStatus.NO_CONTENT);
				} catch (Exception e) {
						return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
				}
		}
}
