package com.ezc.request.auth;

import com.ezc.entity.Permission;
import com.ezc.entity.Role;
import com.ezc.request.UniqueValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.ezc.entity.User;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

  @NotBlank
  @Size(max = 255)
  @UniqueValue(entity=User.class, field="email", message="Email already taken")
  private String email;

  @NotBlank
  @Size(max=255)
  private String name;

  @NotBlank
  @Size(max=100)
  private String password;

  @NotNull
  private Integer roleId;

  private List<Integer> permissionIds;
}
