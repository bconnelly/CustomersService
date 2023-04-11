package com.fullstack.customerservice.DomainLogic;

import static org.junit.jupiter.api.Assertions.assertThrows;
import com.fullstack.customerservice.DBAccessEntities.Customer;
import com.fullstack.customerservice.Utilities.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Optional;


@SpringBootTest
public class CustomerLogicTest {

    @Autowired
    private CustomerLogic customerLogic;

    @Test
    void getAllCustomersTest(){
        List<Customer> returnedCustomers = customerLogic.getAllCustomers();
        assert(returnedCustomers.get(0).getFirstName().equals("alice"));
        assert(returnedCustomers.get(1).getFirstName().equals("bob"));
        assert(returnedCustomers.get(2).getFirstName().equals("chuck"));
        assert(returnedCustomers.get(3).getFirstName().equals("dave"));
        assert(returnedCustomers.get(4).getFirstName().equals("ed"));
    }

    @Test
    void customerExistsTest(){
        assert customerLogic.customerExists("chuck");
        assert !customerLogic.customerExists("zach");
    }

    @Test
    void getCustomerByFirstNameTest() throws EntityNotFoundException {
        Customer expecterCustomer = Customer.builder().firstName("bob").address("124 main st").tableNumber(3).cash(2.34f).build();
        Customer returnedCustomer = customerLogic.getCustomerByFirstName("bob");
        assert expecterCustomer.equals(returnedCustomer);

        assertThrows(EntityNotFoundException.class, () -> customerLogic.getCustomerByFirstName("zach"));
    }

    @Test
    void insertCustomerTest() {
        Customer customerToSave = Customer.builder().firstName("alice").address("test address1")
                .cash(12.34f).tableNumber(1).build();

        Customer returnedCustomer = customerLogic.insertCustomer(
                customerToSave.getFirstName(), customerToSave.getAddress(), 12.34f, 1);

        assert(customerToSave.equals(returnedCustomer));
    }

    @Test
    void bootByFirstNameTest() throws EntityNotFoundException {
        assert(customerLogic.bootByFirstName("ed"));
        assertThrows(EntityNotFoundException.class, () -> customerLogic.bootByFirstName("zach"));
    }

    @Test
    void getCustomersAtTableTest(){
        Optional<List<Customer>> expected =
                customerLogic.getCustomersAtTable(1);


    }
}