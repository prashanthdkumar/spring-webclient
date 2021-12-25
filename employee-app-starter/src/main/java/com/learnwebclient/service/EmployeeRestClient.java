package com.learnwebclient.service;

import com.learnwebclient.constants.EmployeeConstants;
import com.learnwebclient.dto.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
public class EmployeeRestClient {

    private WebClient webClient;

    public EmployeeRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    //http://localhost:8081/employeeservice/v1/allEmployees
    public List<Employee> retrieveAllEmployees() {
        return webClient.get()
                .uri(EmployeeConstants.GET_ALL_EMPLOYEES_V1)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .retrieve()
                .bodyToFlux(Employee.class)
                .collectList()
                .block();
    }

    //http://localhost:8081/employeeservice/v1/employee/1
    public Employee retrieveEmployeeById(int id) {
        try {
            return webClient.get()
                    .uri(EmployeeConstants.EMPLOYEE_BY_ID_V1, id)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response Code is {} and the body is {}",
                    ex.getRawStatusCode(),
                    ex.getResponseBodyAsString());
            log.error("WebClientResponseException in retrieveEmployeeById ", ex);
            throw ex;
        } catch (Exception e) {
            log.error("Exception in retrieveEmployeeById ", e);
            throw e;
        }
    }

    //http://localhost:8081/employeeservice/v1/employeeName?employee_name=Chris
    public List<Employee> retrieveEmployeeByName(String employeeName) {
        String uri = UriComponentsBuilder.fromUriString(EmployeeConstants.EMPLOYEE_BY_NAME_V1)
                .queryParam("employee_name", employeeName)
                .build().toUriString();
        try {
            return webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .retrieve()
                    .bodyToFlux(Employee.class)
                    .collectList()
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response Code is {} and the body is {}",
                    ex.getRawStatusCode(),
                    ex.getResponseBodyAsString());
            log.error("WebClientResponseException in retrieveEmployeeByName ", ex);
            throw ex;
        } catch (Exception e) {
            log.error("Exception in retrieveEmployeeByName ", e);
            throw e;
        }
    }

    //http://localhost:8081/employeeservice/v1/employee
    public Employee addNewEmployee(Employee employee) {
        try {
            return webClient.post()
                    .uri(EmployeeConstants.ADD_NEW_EMPLOYEE_V1)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .syncBody(employee)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response Code is {} and the body is {}",
                    ex.getRawStatusCode(),
                    ex.getResponseBodyAsString());
            log.error("WebClientResponseException in addNewEmployee ", ex);
            throw ex;
        } catch (Exception e) {
            log.error("Exception in addNewEmployee ", e);
            throw e;
        }
    }

}
