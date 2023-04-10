package com.fullstack.customerservice.DomainLogic;

import com.fullstack.customerservice.DBAccessEntities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class CustomerLogicTest {

    @Autowired
    private CustomerLogic customerLogic;

    @Test
    void insertCustomerTest() {
        Customer newCustomer = Customer.builder().tableNumber(1).firstName("test customer").cash(50.50f).address("123 main st").build();

        Customer insertedCustomer = customerLogic.insertCustomer(newCustomer.getFirstName(), newCustomer.getAddress(), newCustomer.getCash(), newCustomer.getTableNumber());

        assert(newCustomer.equals(insertedCustomer));
    }

}