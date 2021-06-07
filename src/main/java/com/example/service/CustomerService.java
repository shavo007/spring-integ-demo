package com.example.service;

import com.example.model.Customer;

public interface CustomerService {

    public Customer getCustomerById(Long id);

    public Customer getCustomerByName(String name);

    public Iterable<Customer> getAllCustomers();

    public boolean exists(String email);

    public Customer save(Customer customer);
		public void delete(Long id);
}
