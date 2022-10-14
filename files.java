package com.alexgrig.sber.cacheproxyaccount.service;

import com.alexgrig.sber.cacheproxyaccount.exception.RecordNotFoundException;
import com.alexgrig.sber.cacheproxyaccount.model.AccountDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class RestClientService {

    private RestTemplate restClient;
    private final String URL_ACCOUNTS_CACHE1 = "http://localhost:8082/cache/";
    private final String URL_ACCOUNTS_CACHE2 = "http://localhost:8083/cache/";
    private Map<Long, Integer> actualData = new HashMap<>();
    //private static int versionGenerator = 0;

    @Autowired
    public RestClientService(RestTemplate restClient) {
        this.restClient = restClient;

    }


    public AccountDto getAccountById(long id) throws RecordNotFoundException {
        try {
            ResponseEntity<AccountDto> entity = restClient.getForEntity(URL_ACCOUNTS_CACHE1 + id, AccountDto.class);
            return entity.getBody();
        }
        catch (HttpStatusCodeException ex) {
            System.out.println(ex.getStatusCode());
            System.out.println("+++++++++++++++++++++++++++++++++++++");
            throw new RecordNotFoundException("id = " + id + " not found");
        }

    }

    public AccountDto add(AccountDto accountDto) {
//        long id = accountDto.getId();
//        int version = accountDto.getVersion();
//        actualData.compute(id, (keyId, valVer) -> valVer == null ? 1 : valVer + 1);
//        accountDto.setVersion(actualData.get(id));
        AccountDto entity1 = restClient.postForEntity(URL_ACCOUNTS_CACHE1, accountDto, AccountDto.class).getBody();
        AccountDto entity2 = restClient.postForEntity(URL_ACCOUNTS_CACHE2, accountDto, AccountDto.class).getBody();
        return entity1;
    }

}

======================================================
======================================================
======================================================
package com.alexgrig.sber.cacheproxyaccount.controller;

import com.alexgrig.sber.cacheproxyaccount.exception.RecordNotFoundException;
import com.alexgrig.sber.cacheproxyaccount.model.AccountDto;
import com.alexgrig.sber.cacheproxyaccount.service.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class ProxyAccountController {

    private RestClientService restClientService;

    @Autowired
    public ProxyAccountController(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    @GetMapping("cache/{cacheId}")
    public ResponseEntity<AccountDto> getById(@PathVariable("cacheId") long id) {
        AccountDto accountDto = null;
        try {
            accountDto = restClientService.getAccountById(id);
        } catch (RecordNotFoundException ex) {
            ex.printStackTrace();
            return new ResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(accountDto);
    }

    @PostMapping("cache")
    public ResponseEntity<AccountDto> add(@RequestBody AccountDto account) {
        AccountDto account1 = restClientService.add(account);
        return ResponseEntity.ok(account1);
    }
}
======================================================
======================================================
======================================================
package com.alexgrig.sber.cacheproxyaccount.config;

import com.alexgrig.sber.cacheproxyaccount.exception.RestTemplateResponseErrorHandler;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                //.errorHandler(new RestTemplateResponseErrorHandler())
                .setConnectTimeout(Duration.ofMillis(60000))
                .setReadTimeout(Duration.ofMillis(60000))
                .build();
    }
}
======================================================
======================================================
======================================================
package com.alexgrig.sber.cacheproxyaccount.model;

import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountDto implements Serializable {
    private Long id;
    private String owner;
    private double amount;
    private int version;
}
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
package com.alexgrig.sber.cacheaccount.controller;

import com.alexgrig.sber.cacheaccount.model.Account;
import com.alexgrig.sber.cacheaccount.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accountService.getAll();
    }

    @GetMapping("cache/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable("id") long id)  {
        Optional<Account> account = accountService.getById(id);
        if (account.isPresent()) {
            return new ResponseEntity<>(account.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity("id = " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("cache")
    public ResponseEntity<Account> add(@RequestBody Account account) {
         Account account1 = accountService.add(account);
         return ResponseEntity.ok(account1);
    }

}

======================================================
======================================================
DROP TABLE IF EXISTS account;

CREATE TABLE account
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    owner      VARCHAR(250) NOT NULL,
    amount     DECIMAL(9, 2) NOT NULL,
    version    INT NOT NULL
);

INSERT INTO account (owner, amount, version)
VALUES
('Alex', 12000, 1),
('Ivan', 880000, 1),
('Svetlana', 34000, 1)

======================================================
======================================================
package com.alexgrig.sber.cacheaccount.service;


import com.alexgrig.sber.cacheaccount.model.Account;
import com.alexgrig.sber.cacheaccount.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> getAll() {
        return accountRepository.findAll();
    }

    public Optional<Account> getById(Long id) {
        return accountRepository.findById(id);
    }

    public Account add(Account account) {
        return accountRepository.save(account);
    }
}
======================================================
======================================================
server:
  port: 8082

spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb
    username: admin
    password: admin

  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    database-platform: org.hibernate.dialect.H2Dialect

  h2:
    console:
      enabled: true
      path: /h2-console


  flyway:
    enabled: true
    locations: classpath:db/migration
