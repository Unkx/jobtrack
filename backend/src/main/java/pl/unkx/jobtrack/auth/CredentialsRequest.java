package pl.unkx.jobtrack.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record CredentialsRequest(
        @NotBlank @Email @Size(max = 255) String email,
        // BCrypt ignores everything past 72 bytes
        @NotBlank @Size(min = 8, max = 72) String password) {
}
