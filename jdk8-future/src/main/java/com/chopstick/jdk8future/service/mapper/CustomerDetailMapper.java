package com.chopstick.jdk8future.service.mapper;

import com.chopstick.jdk8future.dto.CustomerDetails;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class CustomerDetailMapper implements BiFunction<String, String, CustomerDetails> {

    @Override
    public CustomerDetails apply(String phoneNumber, String emailId) {
        CustomerDetails customerDetails = new CustomerDetails();
        customerDetails.setPhoneNumber(phoneNumber);
        customerDetails.setEmailId(emailId);
        return customerDetails;
    }
}