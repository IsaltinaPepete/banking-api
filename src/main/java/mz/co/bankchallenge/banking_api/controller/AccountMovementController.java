package mz.co.bankchallenge.banking_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mz.co.bankchallenge.banking_api.dto.response.MovementResponse;
import mz.co.bankchallenge.banking_api.enums.MovementDirection;
import mz.co.bankchallenge.banking_api.enums.TransactionType;
import mz.co.bankchallenge.banking_api.services.AccountMovementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/movements")
@RequiredArgsConstructor
public class AccountMovementController {

    private final AccountMovementService service;

    @Operation(summary = "Consultar movimentos", description = "Lista os movimentos de uma conta com filtros de data, tipo, direção e paginação.")
    @GetMapping
    @PreAuthorize("hasAnyRole('role_admin', 'role_user')")
    public ResponseEntity<Page<MovementResponse>> search(
            @PathVariable UUID accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime to,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) MovementDirection direction,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication) {
        return ResponseEntity.ok(service.search(accountId, from, to, type, direction, pageable, authentication));
    }
}
