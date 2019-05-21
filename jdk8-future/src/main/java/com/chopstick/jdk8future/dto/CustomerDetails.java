package com.chopstick.jdk8future.dto;

@lombok.Getter
@lombok.Setter
@lombok.NoArgsConstructor
public class CustomerDetails {

    private long id;
    private String phoneNumber;
    private String emailId;

    public CustomerDetails(long customerId) {
        this.id = customerId;
    }

    @Override
    public String toString() {
        return "Customer@" + id;
    }
}

