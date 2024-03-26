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

/*
api returns response entities with a customer or list of customers and an HTTP status.
customer is null in the case of an error
*/
@Slf4j
@EnableTransactionManagement
@RestController
@SpringBootApplication(scanBasePackages = "com.fullstack.customerservice")
public class CustomerServiceApplication extends SpringBootServletInitializer {

	public static void main(String[] args) { SpringApplication.run(CustomerServiceApplication.class, args); }

	@Autowired
	private CustomerLogic customerLogic;

	@GetMapping("/getAllCustomers")
	public List<Customer> getAllCustomers(){
		log.debug("getAllCustomers requested");
		return customerLogic.getAllCustomers();
	}

	@GetMapping("/getCustomerByFirstName")
	public Customer getCustomersByFirstName(@RequestParam("firstName") String firstName) throws EntityNotFoundException {
		log.debug("getCustomerByFirstName requested");
		return customerLogic.getCustomerByFirstName(firstName);
	}

	@GetMapping("/customerExists")
	public Boolean customerExists(@RequestParam("firstName") String firstName){
		log.debug("customerExists requested");
		return customerLogic.customerExists(firstName);
	}

	@PostMapping("/insertCustomer")
	public Customer insertCustomer(@RequestParam("firstName")String firstName,
												   @RequestParam("address")String address,
												   @RequestParam("cash")Float cash,
												   @RequestParam("tableNumber")Integer tableNumber) {
		log.debug("insertCustomer requested");
		return customerLogic.insertCustomer(firstName, address, cash, tableNumber);
	}

	@PostMapping("/bootCustomer")
	public void bootCustomer(@RequestParam("firstName") String firstName) throws EntityNotFoundException {
		log.debug("bootCustomer requested");
		customerLogic.bootByFirstName(firstName);
	}

	@GetMapping("/getCustomerAtTable")
	public List<Customer> getCustomerAtTable(@RequestParam("tableNumber") Integer tableNumber) throws EntityNotFoundException {
		log.debug("getCustomerAtTable requested");
		return customerLogic.getCustomersAtTable(tableNumber);
	}

}
