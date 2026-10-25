package mz.co.bankchallenge.banking_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mz.co.bankchallenge.banking_api.dto.response.AccountResponse;
import mz.co.bankchallenge.banking_api.dto.response.BalanceResponse;
import mz.co.bankchallenge.banking_api.services.BankAccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
/** Exposes account, balance and administrative account-listing endpoints. */
public class BankAccountController {

    private final BankAccountService service;

    @Operation(summary = "Listar contas", description = "Retorna todas as contas com paginação.")
    @GetMapping
    @PreAuthorize("hasRole('role_admin')")
    /** Lists all accounts for administrators. */
    public ResponseEntity<Page<AccountResponse>> all(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Consultar conta", description = "Consulta uma conta pelo identificador.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('role_user')")
    /** Returns an account after ownership is verified by the service. */
    public ResponseEntity<AccountResponse> one(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(service.get(id, authentication));
    }

    @Operation(summary = "Consultar saldo", description = "Retorna o saldo atual da conta.")
    @GetMapping("/{id}/balance")
    @PreAuthorize("hasRole('role_user')")
    /** Returns the current balance after ownership is verified by the service. */
    public ResponseEntity<BalanceResponse> balance(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(service.balance(id, authentication));
    }

    @Operation(summary = "Listar contas do cliente", description = "Retorna as contas de um cliente com paginação.")
    @GetMapping("/by-customer/{customerId}")
    @PreAuthorize("hasRole('role_admin')")
    /** Lists accounts belonging to a selected customer for administrators. */
    public ResponseEntity<Page<AccountResponse>> byCustomer(
            @PathVariable UUID customerId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(service.findByCustomer(customerId, pageable));
    }
}
