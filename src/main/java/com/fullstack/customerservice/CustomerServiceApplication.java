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


	private CustomerLogic customerLogic;

	@Autowired
	public void setCustomerLogic(CustomerLogic customerLogic) {
		this.customerLogic = customerLogic;
	}

	@GetMapping("/")
	public String index() {
		return "default customer service landing page";
	}

	@GetMapping("/customer/all")
	public List<Customer> getAllCustomers(){
		log.debug("GET /customer/all requested");
		return customerLogic.getAllCustomers();
	}

	@GetMapping("/customer/{firstName}")
	public Customer getCustomersByFirstName(@PathVariable String firstName) throws EntityNotFoundException {
		log.debug("GET /customer/{firstName} requested");
		return customerLogic.getCustomerByFirstName(firstName);
	}

	@GetMapping("/customer/exists/{firstName}")
	public Boolean customerExists(@PathVariable String firstName){
		log.debug("GET /customer/exists/{firstName} requested");
		return customerLogic.customerExists(firstName);
	}

	@PostMapping("/customer")
	public Customer insertCustomer(@RequestBody Customer customer) {
		log.debug("POST /customer requested");
		return customerLogic.insertCustomer(customer);
	}

	@PostMapping("/customer/group")
	public void insertGroup(@RequestBody List<Customer> customers) {
		log.debug("POST /customer/group requested");
		customerLogic.insertGroup(customers);
	}

	@DeleteMapping("/customer/{firstName}")
	public void bootCustomer(@PathVariable String firstName) throws EntityNotFoundException {
		log.debug("DELETE /customer/{firstName} requested");
		customerLogic.bootByFirstName(firstName);
	}

	@GetMapping("/customer/table/{tableNumber}")
	public List<Customer> getCustomersAtTable(@PathVariable Integer tableNumber) throws EntityNotFoundException {
		log.debug("GET /customer/table/{tableNumber} requested");
		return customerLogic.getCustomersAtTable(tableNumber);
	}
}