package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.shop;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Return;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadOneReturnTest extends FillBaseTemplate {

    private List<Return> returns;

    @Before
    public void init() {
        super.init();
        returns = Arrays.asList(restTemplate.exchange("/returns", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Return[].class).getBody());
    }

    @Test
    public void checkIfResponseContainsRequestedReturn() {
        for (int i = 0; i < returnRequests.size(); i++) {
            ResponseEntity<Return> forEntity = restTemplate.exchange("/returns/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Return.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertTrue(returns.stream().anyMatch(r -> r.checkIfDataEquals(forEntity.getBody())));
        }
    }
}
