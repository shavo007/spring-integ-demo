package com.example.repo;

import com.example.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
@Transactional
public interface CustomerRepository extends CrudRepository<Customer, Long>{
	public Customer findByFirstName(String name);
}
