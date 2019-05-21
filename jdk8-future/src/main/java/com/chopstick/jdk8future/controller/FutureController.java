package com.chopstick.jdk8future.controller;

import com.chopstick.jdk8future.dto.CustomerDetails;
import com.chopstick.jdk8future.service.BusinessFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class FutureController {

    private final BusinessFlowService businessFlowService;

    @Autowired
    public FutureController(BusinessFlowService businessFlowService) {
        this.businessFlowService = businessFlowService;
    }

    /**
     * @param customerId
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/customer/{id}")
    public CustomerDetails getCustomer(@PathVariable("id") long customerId) throws ExecutionException, InterruptedException {
        return businessFlowService.getCustomerDetails(customerId);
    }
}
