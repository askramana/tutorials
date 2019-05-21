package com.chopstick.jdk8future.service.impl;

import com.chopstick.jdk8future.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Async
public class AsyncCustomerService implements CustomerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    @Override
    public CompletableFuture<String> getCustomerPhoneNumber(long customerId) throws InterruptedException {
        LOGGER.debug("Received request to fetch phone number for customer: {}", customerId);
        assertCustomerId(customerId);

        // A time consuming process (say, a 3rd party call) can be substituted here.
        Thread.sleep(WAIT);
        String phoneNumber = "xxxxxxxxxxxxx";

        LOGGER.debug("Retrieved phone number for customer: {}", customerId);
        return CompletableFuture.completedFuture(phoneNumber);
    }

    @Override
    public CompletableFuture<String> getCustomerEmailId(long customerId) throws InterruptedException {
        LOGGER.debug("Received request to fetch email id for customer: {}", customerId);
        assertCustomerId(customerId);

        // A time consuming process (say, a 3rd party call) can be substituted here.
        Thread.sleep(WAIT);
        String emailId = "xxxxx.xxxxx@xxxxx.com";

        LOGGER.debug("Retrieved email id for customer: {}", customerId);
        return CompletableFuture.completedFuture(emailId);
    }

    private void assertCustomerId(long customerId) {
        if (customerId <= 0L) {
            LOGGER.error("Invalid customer id: {}.", customerId);
            throw new IllegalArgumentException("Invalid customer id");
        }
    }

}
