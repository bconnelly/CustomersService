package com.fullstack.customerservice.DomainLogic;

import com.fullstack.customerservice.DBAccessEntities.Customer;
import com.fullstack.customerservice.Repositories.CustomerRepository;
import com.fullstack.customerservice.Utilities.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CustomerLogic {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerLogic(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public Boolean customerExists(String firstName){
        return customerRepository.existsByFirstName(firstName);
    }

    public Customer getCustomerByFirstName(String firstName) throws EntityNotFoundException {
        Optional<Customer> customer = customerRepository.getCustomerByFirstName(firstName);
        if(customer.isEmpty()) throw new EntityNotFoundException("customer not found");
        else return customer.get();
    }

    public Customer insertCustomer(Customer customer){
        if(customer == null) throw new DataIntegrityViolationException("customer cannot be null");
        if(customerExists(customer.getFirstName())) throw new DataIntegrityViolationException("customer already in restaurant");
        return customerRepository.save(customer);
    }

    public void insertGroup(List<Customer> customers) {
        if(customers == null || customers.isEmpty()) throw new DataIntegrityViolationException("customer group cannot be null or empty");
        for(Customer customer : customers) {
            if(customerExists(customer.getFirstName())) throw new DataIntegrityViolationException("customer already in restaurant");
        }

        customerRepository.saveAll(customers);
    }

    public void bootByFirstName(String firstName) throws EntityNotFoundException {
        if(!customerRepository.existsByFirstName(firstName)) throw new EntityNotFoundException("customer not found");
        customerRepository.deleteByFirstName(firstName);
    }

    public List<Customer> getCustomersAtTable(Integer tableNumber) throws EntityNotFoundException {
        Optional<List<Customer>> customers = customerRepository.getCustomersByTableNumber(tableNumber);
        if(customers.isEmpty() || customers.get().isEmpty()) throw new EntityNotFoundException("no customers at table " + tableNumber);
        return customers.get();
    }
}