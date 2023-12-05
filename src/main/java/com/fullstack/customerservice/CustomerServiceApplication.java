package com.fullstack.customerservice;

import com.fullstack.customerservice.DBAccessEntities.Customer;
import com.fullstack.customerservice.DomainLogic.CustomerLogic;
import com.fullstack.customerservice.Utilities.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

	@GetMapping(path = "/getAllCustomers")
	public ResponseEntity<List<Customer>> getAllCustomers(){
		log.debug("getAllCustomers requested");
		return ResponseEntity.status(HttpStatus.OK).body(customerLogic.getAllCustomers());
	}

	@GetMapping(path = "/getCustomerByFirstName")
	public ResponseEntity<Customer> getCustomerByFirstName(@RequestParam("firstName") String firstName) throws EntityNotFoundException {
		log.debug("getCustomerByFirstName requested");
		return ResponseEntity.status(HttpStatus.OK).body(customerLogic.getCustomerByFirstName(firstName));
	}

	@GetMapping(path = "/customerExists")
	public ResponseEntity<Boolean> customerExists(@RequestParam("firstName") String firstName){
		return ResponseEntity.status(HttpStatus.OK).body(customerLogic.customerExists(firstName));
	}

	@PostMapping(path = "/insertCustomer")
	public ResponseEntity<Customer> insertCustomer(@RequestParam(value = "firstName")String firstName,
												   @RequestParam(value = "address")String address,
												   @RequestParam(value = "cash")Float cash,
												   @RequestParam(value = "tableNumber")Integer tableNumber){

		return ResponseEntity.status(HttpStatus.OK).body(customerLogic.insertCustomer(firstName, address, cash, tableNumber));
	}

	@PostMapping(path = "/bootCustomer")
	public ResponseEntity<Customer> bootCustomer(@RequestParam(value = "firstName") String firstName) throws EntityNotFoundException {
			if(customerLogic.bootByFirstName(firstName)) return ResponseEntity.status(HttpStatus.OK).body(null);
			else throw new RuntimeException("Entity still exists after deletion attempt");
	}

	@GetMapping(path = "/getCustomerAtTable")
	public ResponseEntity<List<Customer>> getCustomerAtTable(@RequestParam(value = "tableNumber") Integer tableNumber){
		Optional<List<Customer>> customersReturned;
		customersReturned = customerLogic.getCustomersAtTable(tableNumber);
		return customersReturned.map(customers -> ResponseEntity.status(HttpStatus.OK).body(customers)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
	}

}
//