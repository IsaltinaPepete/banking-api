package mz.co.bankchallenge.banking_api.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
/** Generates random, fixed-length account numbers for new accounts. */
public class AccountNumberGenerator {

    /** @return a 20-character account number candidate */
    public String generate() {
        return UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20);
    }
}
