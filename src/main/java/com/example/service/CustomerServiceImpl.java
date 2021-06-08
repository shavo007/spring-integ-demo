package com.example.service;

import com.example.model.Customer;
import com.example.repo.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {


    private final CustomerRepository customerRepository;
    private final GreetingsService greetingsService;

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
        log.info("greetings! 😉 {}",greetingsService.sayHi());
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
