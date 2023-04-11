package com.fullstack.customerservice.DomainLogic;

import com.fullstack.customerservice.DBAccessEntities.Customer;
import com.fullstack.customerservice.Repositories.CustomerRepository;
import com.fullstack.customerservice.Utilities.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CustomerLogic {

    public CustomerLogic(){}

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public Boolean customerExists(String firstName){
        return customerRepository.existsByFirstName(firstName);
    }

    public Customer getCustomerByFirstName(String firstName) throws EntityNotFoundException {
        Optional<Customer> ret = customerRepository.getCustomerByFirstName(firstName);
        if(ret.isEmpty()) throw new EntityNotFoundException("customer not found");
        else return ret.get();
    }

    public Customer insertCustomer(String firstName, String address, Float cash, Integer tableNumber){
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

    public Optional<List<Customer>> getCustomersAtTable(Integer tableNumber){
        return customerRepository.getCustomersByTableNumber(tableNumber);
    }
}
