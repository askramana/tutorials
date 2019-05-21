package com.chopstick.jdk8future.service;

import com.chopstick.jdk8future.dto.CustomerDetails;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public interface BusinessFlowService {

    CompletableFuture<CustomerDetails> getCustomerDetailsAsync(long customerId) throws InterruptedException, ExecutionException;

    CustomerDetails getCustomerDetails(long customerId) throws InterruptedException, ExecutionException;
}
