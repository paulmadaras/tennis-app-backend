package com.model;

/** lifecycle of a tournament‑registration */
public enum RegistrationStatus {
    NONE,       // not requested (convenience – never stored in DB)
    PENDING,
    APPROVED,
    REJECTED
}
