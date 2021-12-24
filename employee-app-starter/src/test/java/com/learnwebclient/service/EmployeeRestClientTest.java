package com.learnwebclient.service;

import com.learnwebclient.dto.Employee;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class EmployeeRestClientTest {

    private String baseUrl = "http://localhost:8081/employeeservice/";
    private WebClient webClient = WebClient.create(baseUrl);

    EmployeeRestClient employeeRestClient = new EmployeeRestClient(webClient);

    @Test
    void testRetrieveAllEmployees() {
        List<Employee> employeesList = employeeRestClient.retrieveAllEmployees();
        System.out.println(employeesList);
        Assertions.assertTrue(employeesList.size() > 0);
    }

}
