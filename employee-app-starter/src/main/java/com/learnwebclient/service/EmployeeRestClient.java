package com.learnwebclient.service;

import com.learnwebclient.constants.EmployeeConstants;
import com.learnwebclient.dto.Employee;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

public class EmployeeRestClient {

    private WebClient webClient;

    public EmployeeRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    //http://localhost:8081/employeeservice/v1/allEmployees
    public List<Employee> retrieveAllEmployees() {
        return webClient.get()
                .uri(EmployeeConstants.GET_ALL_EMPLOYEES_V1)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(Employee.class)
                .collectList()
                .block();
   }

}
