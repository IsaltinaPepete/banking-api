package mz.co.bankchallenge.banking_api.repositories;

import mz.co.bankchallenge.banking_api.domain.appUser.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;


/** Repository for the manual Keycloak-user to customer association. */
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    /** @param keycloakUserId JWT subject associated with the user
     *  @return the linked application user, when present
     */
    Optional<AppUser> findByKeycloakUserId(String keycloakUserId);

}
