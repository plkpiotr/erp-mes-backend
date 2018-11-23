package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.production;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.dto.TaskDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Category;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateTaskCategoryTest extends FillBaseTemplate {
    @Before
    public void init() {
        super.init();
    }

    @Test
    public void checkIfTaskCategoryHasChanged() {
        ResponseEntity<TaskDTO> exchange = restTemplate.exchange("/tasks/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), TaskDTO.class, 1);
        assertThat(exchange.getBody().getCategory()).isEqualTo(Category.TO_DO);

        ResponseEntity<TaskDTO> taskResponseEntity = restTemplate.exchange("/tasks/{id}",
                HttpMethod.PUT, new HttpEntity<>(Category.DOING.name(), requestHeaders), TaskDTO.class, 1);
        assertThat(taskResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(taskResponseEntity.getBody().getCategory()).isEqualTo(Category.DOING);
    }
}