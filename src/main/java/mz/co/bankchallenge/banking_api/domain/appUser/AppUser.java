package mz.co.bankchallenge.banking_api.domain.appUser;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mz.co.bankchallenge.banking_api.domain.customer.Customer;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
/** Manual association between a Keycloak subject and a customer. */
public class AppUser {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "keycloak_user_id", nullable = false, unique = true, length = 100)
    private String keycloakUserId;

    @OneToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = OffsetDateTime.now();
    }
}
