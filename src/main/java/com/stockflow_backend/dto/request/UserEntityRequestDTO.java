package com.stockflow_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class UserEntityRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3,message = "The username must be at least 3 characters long.")
    private String username;
    @NotNull
    @Size(min = 8, message = "The Password must be at least 8 characters long.")
    private String password;
}
