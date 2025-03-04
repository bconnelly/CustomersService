package com.fullstack.customerservice;

import com.fullstack.customerservice.DBAccessEntities.Customer;
import com.fullstack.customerservice.DomainLogic.CustomerLogic;
import com.fullstack.customerservice.Utilities.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@EnableTransactionManagement
@RestController
@SpringBootApplication(scanBasePackages = "com.fullstack.customerservice")
public class CustomerServiceApplication extends SpringBootServletInitializer {

	public static void main(String[] args) { SpringApplication.run(CustomerServiceApplication.class, args); }

	@Autowired
	private CustomerLogic customerLogic;

	@GetMapping("/customer/all")
	public List<Customer> getAllCustomers(){
		log.debug("getAllCustomers requested");
		return customerLogic.getAllCustomers();
	}

	@GetMapping("/customer/{firstName}")
	public Customer getCustomersByFirstName(@PathVariable String firstName) throws EntityNotFoundException {
		log.debug("getCustomerByFirstName requested");
		return customerLogic.getCustomerByFirstName(firstName);
	}

	@GetMapping("/customer/exists/{firstName}")
	public Boolean customerExists(@PathVariable String firstName){
		log.debug("customerExists requested");
		return customerLogic.customerExists(firstName);
	}

	@PostMapping("/customer")
	public Customer insertCustomer(@RequestBody Customer customer) {
		log.debug("insertCustomer requested");
		return customerLogic.insertCustomer(customer);
	}

	@PostMapping("/customer/group")
	public void insertGroup(@RequestBody List<Customer> customers) {
		log.debug("insertGroup requested");
		customerLogic.insertGroup(customers);
	}

	@DeleteMapping("/customer")
	public void bootCustomer(String firstName) throws EntityNotFoundException {
		log.debug("bootCustomer requested");
		customerLogic.bootByFirstName(firstName);
	}

	@GetMapping("/customers/table/{tableNumber}")
	public List<Customer> getCustomersAtTable(@PathVariable Integer tableNumber) throws EntityNotFoundException {
		log.debug("getCustomerAtTable requested");
		return customerLogic.getCustomersAtTable(tableNumber);
	}
}