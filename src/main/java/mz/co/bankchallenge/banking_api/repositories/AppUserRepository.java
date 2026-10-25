package mz.co.bankchallenge.banking_api.repositories;

import mz.co.bankchallenge.banking_api.domain.appUser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;


public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByKeycloakUserId(String keycloakUserId);

}