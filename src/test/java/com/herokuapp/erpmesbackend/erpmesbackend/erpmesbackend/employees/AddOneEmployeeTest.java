package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddOneEmployeeTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addOneEmployeeRequest(false);
    }

    @Test
    public void checkIfResponseContainsAddedEmployee() {
        ResponseEntity<EmployeeDTO> employeeResponseEntity = restTemplate
                .postForEntity("/employees", new HttpEntity<>(employeeRequest, requestHeaders),
                        EmployeeDTO.class);

        assertThat(employeeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        EmployeeDTO body = employeeResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(new EmployeeDTO(employeeRequest.extractUser())));
    }
}
