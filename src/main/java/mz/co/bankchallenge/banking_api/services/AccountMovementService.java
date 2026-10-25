
package mz.co.bankchallenge.banking_api.services;

import lombok.RequiredArgsConstructor;
import mz.co.bankchallenge.banking_api.domain.appUser.AppUser;
import mz.co.bankchallenge.banking_api.domain.movement.AccountMovement;
import mz.co.bankchallenge.banking_api.dto.response.MovementResponse;
import mz.co.bankchallenge.banking_api.enums.*;
import mz.co.bankchallenge.banking_api.repositories.AccountMovementRepository;
import mz.co.bankchallenge.banking_api.repositories.AppUserRepository;
import mz.co.bankchallenge.banking_api.repositories.BankAccountRepository;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
/** Provides filtered, paginated account statements with ownership enforcement. */
public class AccountMovementService {

    private final AccountMovementRepository movements;
    private final BankAccountRepository accounts;
    private final AppUserRepository appUserRepository;


    @Transactional(readOnly = true)
    /**
     * Searches account movements using an optional time range, type and direction.
     * Non-administrative callers may access only an account linked to their customer.
     *
     * @param accountId account whose statement is requested
     * @param from inclusive lower time bound, or the beginning of the supported range
     * @param toExclusive exclusive upper time bound, or the end of the supported range
     * @param type optional transaction type filter
     * @param direction optional credit/debit filter
     * @param pageable pagination and sorting instructions
     * @param authentication authenticated caller
     * @return a filtered page of movements
     * @throws ResponseStatusException with {@code 400} for an invalid time range
     * @throws ResponseStatusException with {@code 403} when access is not allowed
     * @throws ResponseStatusException with {@code 404} when the account is absent
     */
    public Page<MovementResponse> search(
            UUID accountId,
            OffsetDateTime from,
            OffsetDateTime toExclusive,
            TransactionType type,
            MovementDirection direction,
            Pageable pageable,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_role_admin"));

        if (!isAdmin) {

            String keycloakUserId = authentication.getName();


            System.out.println("Authentication Name: " + keycloakUserId);
            System.out.println("Authorities: " + authentication.getAuthorities());
            AppUser appUser = appUserRepository
                    .findByKeycloakUserId(keycloakUserId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "User is not linked to a customer"
                    ));

            UUID customerId = appUser.getCustomer().getId();

            boolean isOwner = accounts
                    .existsByIdAndCustomer_Id(accountId, customerId);

            if (!isOwner) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "You do not have permission to access this account"
                );
            }
        }


        if (!accounts.existsById(accountId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Account not found");
        }

        OffsetDateTime start = from != null
                ? from
                : OffsetDateTime.parse("1970-01-01T00:00:00Z");

        OffsetDateTime end = toExclusive != null
                ? toExclusive
                : OffsetDateTime.parse("9999-12-31T00:00:00Z");

        if (!start.isBefore(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "from must be earlier than to");
        }

        Collection<TransactionType> types = type != null
                ? List.of(type)
                : Arrays.asList(TransactionType.values());

        Collection<MovementDirection> directions = direction != null
                ? List.of(direction)
                : Arrays.asList(MovementDirection.values());

        return movements.search(
                accountId,
                start,
                end,
                types,
                directions,
                pageable
        ).map(this::toResponse);
    }

    private MovementResponse toResponse(AccountMovement m) {
        return new MovementResponse(
                m.getId(),
                m.getTransaction().getReference(),
                m.getTransaction().getType(),
                m.getDirection(),
                m.getAmount(),
                m.getBalanceAfter(),
                m.getTransaction().getDescription(),
                m.getCreatedAt()
        );
    }
}
