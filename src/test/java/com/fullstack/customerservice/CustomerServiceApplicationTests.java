package com.fullstack.customerservice;

import com.fullstack.customerservice.DBAccessEntities.Customer;
import com.fullstack.customerservice.DomainLogic.CustomerLogic;
import com.fullstack.customerservice.Utilities.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CustomerServiceApplicationTests {

    @InjectMocks
    private CustomerServiceApplication application;

    @Mock
    private CustomerLogic customerLogic;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllCustomers() {
        when(customerLogic.getAllCustomers()).thenReturn(Collections.singletonList(new Customer()));
        List<Customer> response = application.getAllCustomers();
        assertEquals(1, response.size());

    }

    @Test
    public void testGetCustomerByFirstName() throws EntityNotFoundException {
        String firstName = "John";
        Customer customer = new Customer();
        when(customerLogic.getCustomerByFirstName(firstName)).thenReturn(customer);
        Customer response = application.getCustomersByFirstName(firstName);
        assertEquals(customer, response);
    }

    @Test
    public void testCustomerExists() {
        String firstName = "John";
        when(customerLogic.customerExists(firstName)).thenReturn(true);
        Boolean response = application.customerExists(firstName);
        assertEquals(true, response);
    }

    @Test
    public void testInsertCustomer() {
        Customer customer = new Customer();
        when(customerLogic.insertCustomer(customer)).thenReturn(customer);
        Customer response = application.insertCustomer(customer);
        assertEquals(customer, response);
    }

    @Test
    public void testBootCustomer() throws EntityNotFoundException {
        String firstName = "John";
        when(customerLogic.customerExists(firstName)).thenReturn(true);
        application.bootCustomer(firstName);
    }

    @Test
    public void testGetCustomerAtTable() throws EntityNotFoundException {
        Integer tableNumber = 1;
        when(customerLogic.getCustomersAtTable(tableNumber)).thenReturn(Collections.singletonList(new Customer()));
        List<Customer> response = application.getCustomerAtTable(tableNumber);
        assertEquals(1, response.size());
    }
}