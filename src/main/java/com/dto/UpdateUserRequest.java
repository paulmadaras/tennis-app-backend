// src/main/java/com/dto/UpdateUserRequest.java
package com.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class UpdateUserRequest {
    @NotBlank
    private String fullName;

    @NotBlank @Email
    private String email;

    // blank or null => don’t change password
    private String password;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
