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

    public Customer insertCustomer(String firstName, String address, Float cash, Integer tableNumber) {
        if(customerExists(firstName)) throw new DataIntegrityViolationException("customer already in restaurant");
        Customer newCustomer = Customer.builder()
                .firstName(firstName).address(address).cash(cash)
                .tableNumber(tableNumber).build();
        return customerRepository.save(newCustomer);
    }

    public boolean bootByFirstName(String firstName) throws EntityNotFoundException {
        if(!customerRepository.existsByFirstName(firstName)) throw new EntityNotFoundException("customer not found");
        customerRepository.deleteByFirstName(firstName);
        return !customerRepository.existsByFirstName(firstName);
    }

    public List<Customer> getCustomersAtTable(Integer tableNumber) throws EntityNotFoundException {
        Optional<List<Customer>> customers = customerRepository.getCustomersByTableNumber(tableNumber);
        if(customers.isEmpty() || customers.get().isEmpty()) throw new EntityNotFoundException("no customers at table " + tableNumber);
        return customers.get();
    }
}
