package com.fullstack.customerservice.DomainLogic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fullstack.customerservice.DBAccessEntities.Customer;
import com.fullstack.customerservice.Utilities.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerLogicTest {

    @Autowired
    private CustomerLogic customerLogic;


    @Test
    void testBootByFirstName() throws EntityNotFoundException {
        customerLogic.bootByFirstName("ed");
        assert(!customerLogic.customerExists("ed"));
        assertThrows(EntityNotFoundException.class, () -> customerLogic.bootByFirstName("zach"));
    }

    @Test
    void testBootByFirstNameBadName() {
        assertThrows(EntityNotFoundException.class, () -> customerLogic.bootByFirstName("zach"));
        assertThrows(EntityNotFoundException.class, () -> customerLogic.bootByFirstName(""));
        assertThrows(EntityNotFoundException.class, () -> customerLogic.bootByFirstName(null));
    }

    @Test
    void testGetAllCustomers() throws EntityNotFoundException {
        List<Customer> returnedCustomers = customerLogic.getAllCustomers();
        assert (returnedCustomers.get(0).getFirstName().equals("alice"));
        assert (returnedCustomers.get(1).getFirstName().equals("bob"));
        assert (returnedCustomers.get(2).getFirstName().equals("chuck"));
        assert (returnedCustomers.get(3).getFirstName().equals("dave"));
        assert (returnedCustomers.get(4).getFirstName().equals("ed"));

        customerLogic.bootByFirstName("alice");
        customerLogic.bootByFirstName("bob");
        customerLogic.bootByFirstName("chuck");
        customerLogic.bootByFirstName("dave");
        customerLogic.bootByFirstName("ed");
        customerLogic.bootByFirstName("fred");
        customerLogic.bootByFirstName("george");

        assert (customerLogic.getAllCustomers().isEmpty());
    }

    @Test
    void testCustomerExists(){
        assert customerLogic.customerExists("chuck");
    }

    @Test
    void testCustomerExistsBadNames(){
        assert !customerLogic.customerExists("zach");
        assert !customerLogic.customerExists("");
        assert !customerLogic.customerExists(null);
    }

    @Test
    void testGetCustomerByFirstName() throws EntityNotFoundException {
        Customer expecterCustomer = Customer.builder().firstName("bob").address("124 main st").tableNumber(3).cash(2.34f).build();
        Customer returnedCustomer = customerLogic.getCustomerByFirstName("bob");
        assert expecterCustomer.equals(returnedCustomer);
    }

    @Test
    void testGetCustomerByFirstNameBadNames(){
        assertThrows(EntityNotFoundException.class, () -> customerLogic.getCustomerByFirstName(""));
        assertThrows(EntityNotFoundException.class, () -> customerLogic.getCustomerByFirstName(null));
        assertThrows(EntityNotFoundException.class, () -> customerLogic.getCustomerByFirstName("fakename"));
    }

    @Test
    void testInsertCustomer() {
        Customer customerToSave = Customer.builder().firstName("chad").address("test address1")
                .cash(12.34f).tableNumber(1).build();
        Customer returnedCustomer = customerLogic.insertCustomer(customerToSave);
        assert(customerToSave.equals(returnedCustomer));
        assertThrows(DataIntegrityViolationException.class, () -> customerLogic.insertCustomer(customerToSave));
    }

    @Test
    void testInsertCustomerBadInputs(){
        assertThrows(DataIntegrityViolationException.class, () -> customerLogic.insertCustomer(null));
    }

    @Test
    void testGetCustomersAtTableTest() throws EntityNotFoundException {
        List<Customer> returnedCustomers = customerLogic.getCustomersAtTable(1);
        assert(!returnedCustomers.isEmpty());
        assert(returnedCustomers.size() == 2);
        assert(returnedCustomers.get(0).getFirstName().equals("alice"));
        assert(returnedCustomers.get(1).getFirstName().equals("dave"));

    }
}