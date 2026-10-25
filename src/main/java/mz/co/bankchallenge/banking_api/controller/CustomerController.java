package mz.co.bankchallenge.banking_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mz.co.bankchallenge.banking_api.dto.request.CreateCustomerRequest;
import mz.co.bankchallenge.banking_api.dto.response.CreateCustomerResponse;
import mz.co.bankchallenge.banking_api.dto.response.CustomerResponse;
import mz.co.bankchallenge.banking_api.services.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
/** Exposes customer creation and ownership-aware customer queries. */
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Criar cliente", description = "Cria um cliente e a sua conta bancária inicial.")
    @PostMapping
    @PreAuthorize("hasRole('role_admin')")
    /** Creates a customer and its initial bank account for administrators. */
    public ResponseEntity<CreateCustomerResponse> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request));
    }

    @Operation(summary = "Listar clientes", description = "Retorna clientes paginados.")
    @GetMapping
    @PreAuthorize("hasRole('role_admin')")
    /** Lists customers for administrators using pagination. */
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(customerService.getAllCustomers(pageable));
    }

    @Operation(summary = "Consultar cliente", description = "Consulta um cliente pelo seu identificador.")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('role_user')")
    /** Returns a customer after the service verifies ownership. */
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(customerService.getCustomerById(id, authentication));
    }
}
