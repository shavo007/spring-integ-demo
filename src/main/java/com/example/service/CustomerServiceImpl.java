package com.example.service;

import com.example.model.Customer;
import com.example.repo.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {


    private final CustomerRepository customerRepository;

    @Override
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }

    @Override
    public Customer getCustomerByName(String name) {
        return customerRepository.findByFirstName(name);
    }

    @Override
    public boolean exists(String name) {
        if (customerRepository.findByFirstName(name) != null) {
            return true;
        }
        return false;
    }

    @Override
    public Customer save(Customer employee) {
        return customerRepository.save(employee);
    }

    @Override
    public Iterable<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

		@Override
		public void delete(Long id) {
			customerRepository.deleteById(id);

		}

}
