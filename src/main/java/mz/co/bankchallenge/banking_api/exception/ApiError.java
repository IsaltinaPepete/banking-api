package mz.co.bankchallenge.banking_api.exception;

import java.time.Instant;

/** Standard error payload returned by the HTTP API. */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String correlationId
) {
}
