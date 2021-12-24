package com.learnwebclient.service;

import com.learnwebclient.dto.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

public class EmployeeRestClientTest {

    private final String baseUrl = "http://localhost:8081/employeeservice/";
    private final WebClient webClient = WebClient.create(baseUrl);

    EmployeeRestClient employeeRestClient = new EmployeeRestClient(webClient);

    @Test
    void testRetrieveAllEmployees() {
        List<Employee> employeesList = employeeRestClient.retrieveAllEmployees();
        Assertions.assertTrue(employeesList.size() > 0);
    }

    @Test
    void testRetrieveEmployeeById() {
        int id = 1;
        Employee employee = employeeRestClient.retrieveEmployeeById(id);
        Assertions.assertEquals("Chris", employee.getFirstName());
    }

    @Test
    void testRetrieveEmployeeById_notFound() {
        int id = 10;
        Assertions.assertThrows(WebClientResponseException.class,
                () -> employeeRestClient.retrieveEmployeeById(id));
    }

}
