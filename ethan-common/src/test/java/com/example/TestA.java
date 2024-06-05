package com.example;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@ToString
@Data
public class TestA {

    private int a;
    private double b;
    private String c;
    
}
