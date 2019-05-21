package com.chopstick.jdk8future.service.impl;

import com.chopstick.jdk8future.dto.CustomerDetails;
import com.chopstick.jdk8future.service.BusinessFlowService;
import com.chopstick.jdk8future.service.CustomerService;
import com.chopstick.jdk8future.service.mapper.CustomerDetailMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class BusinessFlowServiceImpl implements BusinessFlowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessFlowService.class);

    @Autowired
    private CustomerDetailMapper customerDetailMapper;

    @Autowired
    private CustomerService customerService;

    @Override
    public CustomerDetails getCustomerDetails(long customerId) throws InterruptedException, ExecutionException {
        LOGGER.debug("Received request to fetch details for customer: {}", customerId);

        CustomerDetails customerDetails = customerService.getCustomerPhoneNumber(customerId)
                .thenCombine(customerService.getCustomerEmailId(customerId), customerDetailMapper)
                .get();
        customerDetails.setId(customerId);

        LOGGER.debug("Retrieved details for customer: {}", customerId);
        return customerDetails;
    }

    @Override
    @Async
    public CompletableFuture<CustomerDetails> getCustomerDetailsAsync(long customerId)
            throws InterruptedException, ExecutionException {
        return CompletableFuture.completedFuture(getCustomerDetails(customerId));
    }

}
