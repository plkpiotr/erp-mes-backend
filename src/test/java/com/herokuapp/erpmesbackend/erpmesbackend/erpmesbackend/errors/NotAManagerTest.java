package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.errors;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotAManagerTest extends FillBaseTemplate {

    @Before
    public void init() {
        addOneNonAdminRequest(true);
    }

    @Test
    public void checkIfResponse400NotAManager() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity("/employees/{id}/subordinates",
                String.class, 1);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
