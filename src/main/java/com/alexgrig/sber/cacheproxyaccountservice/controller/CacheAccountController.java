package com.alexgrig.sber.cacheproxyaccountservice.controller;
import com.alexgrig.sber.cacheproxyaccountservice.dto.AccountDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CacheAccountController {

    @GetMapping("/cache/{cacheId}")
    public AccountDto getCacheAccount(@PathVariable("cacheId") Long id) {
        return new AccountDto();
    }
}
