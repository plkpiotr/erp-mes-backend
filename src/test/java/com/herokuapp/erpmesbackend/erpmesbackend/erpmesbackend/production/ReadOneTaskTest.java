package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.production;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadOneTaskTest extends FillBaseTemplate {

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void checkIfResponseContainsTaskWithGivenId() {
        for (int i = 0; i < 3; i++) {
            ResponseEntity<Task> forEntity = restTemplate.exchange("/tasks/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Task.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }
}
