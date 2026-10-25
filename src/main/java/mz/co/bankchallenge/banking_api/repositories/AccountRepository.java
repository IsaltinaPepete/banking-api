package mz.co.bankchallenge.banking_api.repositories;

import mz.co.bankchallenge.banking_api.domain.bankAccount.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Basic Spring Data repository for bank accounts. */
public interface AccountRepository extends JpaRepository<BankAccount, UUID> {
}
