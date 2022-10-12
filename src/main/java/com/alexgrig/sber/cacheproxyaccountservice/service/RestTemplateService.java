package com.alexgrig.sber.cacheproxyaccountservice.service;

import com.alexgrig.sber.cacheproxyaccountservice.dto.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


//https://howtodoinjava.com/spring-boot2/resttemplate/spring-restful-client-resttemplate-example/
https://www.javaguides.net/2019/06/spring-resttemplate-get-post-put-and-delete-example.html
    //    https://zetcode.com/springboot/resttemplate/
@Service
public class RestTemplateService {

    private RestTemplate restClient;
    private final String URI_ACCOUNTS_ID = "/accounts/{id}";

    @Autowired
    public RestTemplateService(RestTemplate restClient) {
        this.restClient = restClient;
    }

    public AccountDto getAccountById(Long id) {
        ResponseEntity<AccountDto> entity= restClient.getForEntity(URI_ACCOUNTS_ID, AccountDto.class);
        return entity.getBody();
    }
}
