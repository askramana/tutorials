package com.chopstick.jdk8future.service;

import java.util.concurrent.CompletableFuture;

public interface CustomerService {

    long WAIT = 3600L;

    CompletableFuture<String> getCustomerPhoneNumber(long customerId) throws InterruptedException;

    CompletableFuture<String> getCustomerEmailId(long customerId) throws InterruptedException;

}
