package com.ezc.request.auth;

import com.ezc.request.ExistsInDatabase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ezc.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

  @NotBlank
  @Size(max = 255)
  @ExistsInDatabase(entity=User.class, field="email", message="Email or password is wrong")
  private String email;

  @NotBlank
  @Size(max=100)
  private String password;
}
