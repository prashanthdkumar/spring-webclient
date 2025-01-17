package com.learnwebclient.service;

import com.learnwebclient.constants.EmployeeConstants;
import com.learnwebclient.dto.Employee;
import com.learnwebclient.exception.ClientDataException;
import com.learnwebclient.exception.EmployeeServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

import java.time.Duration;
import java.util.List;

@Slf4j
public class EmployeeRestClient {

    private WebClient webClient;

    public Retry<?> fixedRetry = Retry.anyOf(WebClientResponseException.class)
            .fixedBackoff(Duration.ofSeconds(2))
            .retryMax(3)
            .doOnRetry(message -> {
                log.error("Exception is {}", message);
            });

    public Retry<?> fixedRetry5xx = Retry.anyOf(EmployeeServiceException.class)
            .fixedBackoff(Duration.ofSeconds(2))
            .retryMax(3)
            .doOnRetry(message -> {
                log.error("Exception is {}", message);
            });

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

    //http://localhost:8081/employeeservice/v1/employee/1
    public Employee retrieveEmployeeById_withRetry(int id) {
        try {
            return webClient.get()
                    .uri(EmployeeConstants.EMPLOYEE_BY_ID_V1, id)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .retryWhen(fixedRetry)
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

    //http://localhost:8081/employeeservice/v1/employee/1
    public Employee retrieveEmployeeById_custom_error_handling(int id) {
        return webClient.get()
                .uri(EmployeeConstants.EMPLOYEE_BY_ID_V1, id)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxError(clientResponse))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxError(clientResponse))
                .bodyToMono(Employee.class)
                .block();
    }

    private Mono<? extends Throwable> handle4xxError(ClientResponse clientResponse) {
        Mono<String> errorMessage = clientResponse.bodyToMono(String.class);
        return errorMessage.flatMap((message) -> {
            log.error("Error Response Code is " + clientResponse.rawStatusCode() + " and the message is " + message);
            throw new ClientDataException(message);
        });
    }

    private Mono<? extends Throwable> handle5xxError(ClientResponse clientResponse) {
        Mono<String> errorMessage = clientResponse.bodyToMono(String.class);
        return errorMessage.flatMap((message) -> {
            log.error("Error Response Code is " + clientResponse.rawStatusCode() + " and the message is " + message);
            throw new EmployeeServiceException(message);
        });
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

    //http://localhost:8081/employeeservice/v1/employee/5
    public Employee updateEmployee(int id, Employee employee) {
        try {
            return webClient.put()
                    .uri(EmployeeConstants.EMPLOYEE_BY_ID_V1, id)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .syncBody(employee)
                    .retrieve()
                    .bodyToMono(Employee.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response Code is {}-{} and the body is {} and Status Text {}",
                    ex.getRawStatusCode(),
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString(),
                    ex.getStatusText());
            log.error("WebClientResponseException in updateEmployee ", ex);
            throw ex;
        } catch (Exception e) {
            log.error("Exception in updateEmployee ", e);
            throw e;
        }
    }

    //http://localhost:8081/employeeservice/v1/employee/5
    public String deleteEmployee(int employeeId) {
        try {
            return webClient.delete()
                    .uri(EmployeeConstants.EMPLOYEE_BY_ID_V1, employeeId)
                    .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException ex) {
            log.error("Error Response Code is {}-{} and the body is {} and Status Text {}",
                    ex.getRawStatusCode(),
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString(),
                    ex.getStatusText());
            log.error("WebClientResponseException in deleteEmployee ", ex);
            throw ex;
        } catch (Exception e) {
            log.error("Exception in deleteEmployee ", e);
            throw e;
        }
    }

    //http://localhost:8081/employeeservice/v1/employee/error
    public String errorEndpoint() {
        return webClient.get()
                .uri(EmployeeConstants.ERROR_EMPLOYEE_V1)
                .accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> handle4xxError(clientResponse))
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> handle5xxError(clientResponse))
                .bodyToMono(String.class)
                .retryWhen(fixedRetry5xx)
                .block();
    }

}
