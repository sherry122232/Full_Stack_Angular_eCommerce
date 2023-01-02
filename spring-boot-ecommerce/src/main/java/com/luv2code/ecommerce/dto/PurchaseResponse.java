package com.luv2code.ecommerce.dto;

//use this class to send back a Java object as JSON

import lombok.Data;

@Data
public class PurchaseResponse {
    //lombok @data will generate constructor for final fields, must use final here
    // or use @NonNull annotation on the field instead of final
    private final String orderTrackingNumber;
}
