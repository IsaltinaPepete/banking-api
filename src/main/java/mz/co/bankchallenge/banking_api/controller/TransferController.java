package mz.co.bankchallenge.banking_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mz.co.bankchallenge.banking_api.dto.request.CreateTransferRequest;
import mz.co.bankchallenge.banking_api.dto.response.TransferResponse;
import mz.co.bankchallenge.banking_api.services.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
/** Exposes authenticated account-to-account transfers. */
public class TransferController {

    private final TransferService service;

    @Operation(summary = "Transferir dinheiro", description = "Transfere dinheiro entre duas contas.")
    @PostMapping
    @PreAuthorize("hasRole('role_user')")
    /** Initiates a transfer subject to validation and source-account ownership. */
    public ResponseEntity<TransferResponse> create(
            @Valid @RequestBody CreateTransferRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.transfer(request, authentication));
    }
}
