package com.example.splitwise.controllers;

import com.example.splitwise.models.ViewMoneyBalanceResponse;
import com.example.splitwise.services.ViewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/activity")
public class ViewController {
    private final ViewService viewService;

    ViewController(ViewService viewService){
        this.viewService = viewService;
    }

    @GetMapping("/{userId}")
    public ViewMoneyBalanceResponse getUserActivity(@PathVariable int userId){
        return viewService.getUserActivity(userId);
    }
}
