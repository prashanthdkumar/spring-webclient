package com.learnwebclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    private int age;
    private String firstName;
    private String lastName;
    private String gender;
    private int id;
    private String role;

}
