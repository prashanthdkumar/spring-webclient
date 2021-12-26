package com.learnwebclient.service;

import com.learnwebclient.dto.Employee;
import com.learnwebclient.exception.ClientDataException;
import com.learnwebclient.exception.EmployeeServiceException;
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

    @Test
    void retrieveEmployeeById_custom_error_handling() {
        int id = 10;
        Assertions.assertThrows(ClientDataException.class,
                () -> employeeRestClient.retrieveEmployeeById_custom_error_handling(id));
    }

    @Test
    void testRetrieveEmployeeByName() {
        String name = "Chris";
        List<Employee> employeeList = employeeRestClient.retrieveEmployeeByName(name);
        Assertions.assertTrue(employeeList.size() > 0);
        Employee employee = employeeList.get(0);
        Assertions.assertEquals("Chris", employee.getFirstName());
    }

    @Test
    void testRetrieveEmployeeByName_notFound() {
        String name = "ABC";
        Assertions.assertThrows(WebClientResponseException.class,
                () -> employeeRestClient.retrieveEmployeeByName(name));
    }

    @Test
    void testAddNewEmployee() {
        Employee employee = new Employee(10, "Spider", "Man",
                "male", 0, "SSE" );
        Employee employee1 = employeeRestClient.addNewEmployee(employee);
        System.out.println(employee1);
        Assertions.assertTrue(employee1.getId()!=0);
    }

    @Test
    void testAddNewEmployee_badRequest() {
        Employee employee = new Employee(10, null, "Man",
                "male", 0, "SSE" );
        Assertions.assertThrows(WebClientResponseException.class,
                () -> employeeRestClient.addNewEmployee(employee));
    }

    @Test
    void testUpdateEmployee() {
        Employee employee = new Employee(10, "Spider1", "Man1",
                "male", 0, "SSE" );
        Employee employee1 = employeeRestClient.updateEmployee(3, employee);
        Assertions.assertEquals("Spider1", employee1.getFirstName());
        Assertions.assertEquals("Man1", employee1.getLastName());
    }

    @Test
    void testUpdateEmployee_notFound() {
        Employee employee = new Employee(10, "Spider1", "Man1",
                "male", 0, "SSE" );
        Assertions.assertThrows(WebClientResponseException.class,
                () -> employeeRestClient.updateEmployee(61, employee));
    }

    @Test
    void testDeleteEmployee() {
        Employee employee = new Employee(10, "Spider", "Man",
                "male", 0, "SSE" );
        Employee employee1 = employeeRestClient.addNewEmployee(employee);
        System.out.println(employee1);
        String response = employeeRestClient.deleteEmployee(employee1.getId());
        Assertions.assertEquals("Employee deleted successfully.", response);
    }

    @Test
    void testDeleteEmployee_notFound() {
        Assertions.assertThrows(WebClientResponseException.class,
                () -> employeeRestClient.deleteEmployee(61));
    }

    @Test
    void testErrorEndpoint() {
        Assertions.assertThrows(EmployeeServiceException.class,
                () -> employeeRestClient.errorEndpoint());
    }

}
