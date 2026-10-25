package mz.co.bankchallenge.banking_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mz.co.bankchallenge.banking_api.dto.request.DepositRequest;
import mz.co.bankchallenge.banking_api.dto.response.BalanceResponse;
import mz.co.bankchallenge.banking_api.services.DepositService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService service;

    @Operation(summary = "Depositar dinheiro", description = "Adiciona dinheiro ao saldo de uma conta.")
    @PostMapping("/{id}/deposits")
    @PreAuthorize("hasRole('role_admin')")
    public ResponseEntity<BalanceResponse> deposit(
            @PathVariable UUID id,
            @Valid @RequestBody DepositRequest request) {
        return ResponseEntity.ok(service.deposit(id, request));
    }
}
