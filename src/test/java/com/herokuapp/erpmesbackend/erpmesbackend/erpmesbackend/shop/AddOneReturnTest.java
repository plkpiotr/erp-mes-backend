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
public class AddOneReturnTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addOneReturnRequest(false);
    }

    @Test
    public void checkIfResponseContainsAddedReturn() {
        ResponseEntity<Return> returnResponseEntity = restTemplate.postForEntity("/returns",
                new HttpEntity<>(returnRequest, requestHeaders), Return.class);
        assertThat(returnResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Return r = returnResponseEntity.getBody();
        List<Return> returns = Arrays.asList(restTemplate.exchange("/returns", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Return[].class).getBody());
        assertTrue(returns.stream().anyMatch(ret -> ret.checkIfDataEquals(r)));
    }
}
