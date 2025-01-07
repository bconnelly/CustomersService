package com.fullstack.customerservice;

import com.fullstack.customerservice.DBAccessEntities.Customer;
import com.fullstack.customerservice.DomainLogic.CustomerLogic;
import com.fullstack.customerservice.Utilities.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    private CustomerLogic customerLogicMock;

    @Test
    public void testGetAllCustomers() {
        when(customerLogicMock.getAllCustomers()).thenReturn(Collections.singletonList(new Customer()));

        List<Customer> response = application.getAllCustomers();

        assertEquals(1, response.size());
        verify(customerLogicMock, times(1)).getAllCustomers();
    }

    @Test
    public void testGetCustomerByFirstName() throws EntityNotFoundException {
        String firstName = "John";
        Customer customer = new Customer();
        when(customerLogicMock.getCustomerByFirstName(firstName)).thenReturn(customer);

        Customer response = application.getCustomersByFirstName(firstName);

        assertEquals(customer, response);
        verify(customerLogicMock, times(1)).getCustomerByFirstName(firstName);
    }

    @Test
    public void testCustomerExists() {
        String firstName = "John";
        when(customerLogicMock.customerExists(firstName)).thenReturn(true);

        Boolean response = application.customerExists(firstName);

        assertEquals(true, response);
        verify(customerLogicMock, times(1)).customerExists(firstName);
    }

    @Test
    public void testInsertCustomer() {
        Customer customer = new Customer();
        when(customerLogicMock.insertCustomer(customer)).thenReturn(customer);

        Customer response = application.insertCustomer(customer);

        assertEquals(customer, response);
        verify(customerLogicMock, times(1)).insertCustomer(customer);
    }

    @Test
    public void testBootCustomer() throws EntityNotFoundException {
        String firstName = "John";
        doNothing().when(customerLogicMock).bootByFirstName(firstName);

        application.bootCustomer(firstName);

        verify(customerLogicMock, times(1)).bootByFirstName(firstName);
    }

    @Test
    public void testGetCustomersAtTable() throws EntityNotFoundException {
        Integer tableNumber = 1;
        when(customerLogicMock.getCustomersAtTable(tableNumber)).thenReturn(Collections.singletonList(new Customer()));

        List<Customer> response = application.getCustomersAtTable(tableNumber);

        assertEquals(1, response.size());
        verify(customerLogicMock, times(1)).getCustomersAtTable(tableNumber);
    }
}