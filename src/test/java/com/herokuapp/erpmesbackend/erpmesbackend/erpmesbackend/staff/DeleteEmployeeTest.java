package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.staff;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteEmployeeTest extends FillBaseTemplate {

    private Employee deletedEmployee;

    @Before
    public void init() {
        super.init();
        deletedEmployee = restTemplate.getForEntity("/employees/{id}", Employee.class, 1)
                .getBody();
    }

    @Test
    public void checkIfResponseDoesNotContainDeletedEmployee() {
        restTemplate.delete("/employees/{id}", 1);

        List<Employee> employees = Arrays.asList(restTemplate.exchange("/employees", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee[].class).getBody());
        assertFalse(employees.stream().anyMatch(employee -> employee.checkIfDataEquals(deletedEmployee)));
    }
}
