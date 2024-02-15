package com.example.splitwise.controllers;

import com.example.splitwise.services.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/settle")
public class SettlementController {
    private final SettlementService settlementService;

    SettlementController(SettlementService settlementService){
        this.settlementService = settlementService;
    }


    @PostMapping("/{payerId}/{receiverId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<String> settleUsers(@PathVariable int payerId, @PathVariable int receiverId){

        return settlementService.settleUsers(payerId, receiverId);
    }
}
