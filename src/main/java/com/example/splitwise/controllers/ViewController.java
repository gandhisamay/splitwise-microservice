package com.example.splitwise.controllers;

import com.example.splitwise.models.moneyBalance.ViewMoneyBalanceResponse;
import com.example.splitwise.services.ViewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ViewController {
    private final ViewService viewService;

    ViewController(ViewService viewService) {
        this.viewService = viewService;
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public List<ViewMoneyBalanceResponse> getUserActivity(@PathVariable int userId) {
        return viewService.getUserActivity(userId);
    }

}
