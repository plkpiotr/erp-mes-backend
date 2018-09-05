package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.returns;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.returns.Return;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadAllReturnsTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addManyItemRequests(true);
        addManyReturnRequests(true);
    }

    @Test
    public void checkIfResponseContainsAllReturns() {
        ResponseEntity<Return[]> forEntity = restTemplate.exchange("/returns", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Return[].class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Return> returns = Arrays.asList(forEntity.getBody());
        assertThat(returns.size()).isEqualTo(3);
    }

}
