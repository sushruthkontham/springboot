package com.ezc.controller.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ezc.entity.Permission;
import com.ezc.repository.PermissionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import com.ezc.entity.Role;
import com.ezc.entity.User;
import com.ezc.helper.JwtHelper;
import com.ezc.helper.ResponseHelper;
import com.ezc.middleware.UseMiddleware;
import com.ezc.repository.RoleRepository;
import com.ezc.repository.UserRepository;
import com.ezc.request.auth.LoginRequest;
import com.ezc.request.auth.RegisterRequest;
import com.ezc.service.user.AddUserService;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  private Validator validator;

  @Autowired
  private ResponseHelper responseHelper;

  @Autowired
  private JwtHelper jwtHelper;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private AddUserService addUserService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder bcrCryptPasswordEncoder;

  @Autowired
  private PermissionRepository permissionRepository;

  @GetMapping("/test")
  @Operation(summary = "Test connection for JWT", description = "Return success for user with valid JWT")
  @UseMiddleware(names = { "auth", "roles:user" })
  @SecurityRequirement(name = "bearerAuth")
  public ResponseEntity<Map<String, Object>> test() {
    log.info("in controlelr");
    return responseHelper.success("success try feature");
  }


  @PostMapping(path = "/user/register")
  @Operation(summary = "User Registration", description = "Creation of users with role and permissions")
  @UseMiddleware(names = { "auth"})
  public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) throws Exception {
    String name = request.getName();
    String email = request.getEmail();
    String password = request.getPassword();
    Integer roleId = request.getRoleId();
    List<Integer> permissionIds = request.getPermissionIds();

    if (email == null || password == null || roleId == null) {
      return responseHelper.error("Missing required fields", HttpStatus.BAD_REQUEST);
    }

    Role role = roleRepository.findById(Long.valueOf(roleId)).orElse(null);
    if (role == null) return responseHelper.error("Invalid roleId", HttpStatus.BAD_REQUEST);

    Set<Permission> permissions = new HashSet<>();
    for (Integer pid : permissionIds) {
      permissionRepository.findById(Long.valueOf(pid)).ifPresent(permissions::add);
    }

    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword(bcrCryptPasswordEncoder.encode(password));
    user.setRole(role);
    user.setPermissions(permissions);

    userRepository.save(user);
    return responseHelper.success("User created with permissions");
  }

  @PostMapping("/permission/create")
  @UseMiddleware(names = { "auth", "roles:ADMIN" })
  @Operation(summary = "Create New Permission", description = "Creating new permissions ")
  public ResponseEntity<Map<String, Object>> createPermission(@RequestBody Map<String, String> request) {
    String name = request.get("name");
    if (name == null || name.isBlank()) {
      return responseHelper.error("Permission name is required", HttpStatus.BAD_REQUEST);
    }

    Permission permission = new Permission();
    permission.setName(name);
    permissionRepository.save(permission);

    return responseHelper.success("Permission created");
  }

  @PostMapping("/role/create")
  @UseMiddleware(names = { "auth", "roles:ADMIN" })
  @Operation(summary = "Create New Role", description = "Creating new roles ")
  public ResponseEntity<Map<String, Object>> createRole(@RequestBody Map<String, String> request) {
      String name = request.get("name");
      if (name == null || name.isBlank()) {
      return responseHelper.error("Role name is required", HttpStatus.BAD_REQUEST);
      }

      Role role = new Role();
      role.setName(name);
      roleRepository.save(role);

      return responseHelper.success("Role created");
  }

  @GetMapping("/permissions")
  @Operation(summary = "All permissions", description = "Returns all the permissions which are created")
  public ResponseEntity<List<Permission>> getPermissions() {
    return ResponseEntity.ok(permissionRepository.findAll());
  }

  @GetMapping("/dashboard")
  @Operation(summary = "Dashboard", description = "Dashboard data")
  @UseMiddleware(names = { "auth", "authorization:EDIT_DATA, CAN_DELETE_USER" })
  public ResponseEntity<Map<String, Object>> dashboard() {
    return responseHelper.success("Welcome to the dashboard!");
  }

  @PostMapping("/reset-password")
  @Operation(summary = "Reset Password", description = "Reset user password")
  @UseMiddleware(names = { "auth", "roles:USER" })
  public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> request) {
      String email = request.get("email");
      String newPassword = request.get("newPassword");

      if (email == null || newPassword == null) {
          return responseHelper.error("Email and new password are required", HttpStatus.BAD_REQUEST);
      }

      User user = userRepository.findByEmail(email).orElse(null);
      if (user == null) {
          return responseHelper.error("User not found", HttpStatus.NOT_FOUND);
      }

      user.setPassword(bcrCryptPasswordEncoder.encode(newPassword));
      userRepository.save(user);

      return responseHelper.success("Password reset successfully");
  }

  @PostMapping("/user/edit")
  @Operation(summary = "Edit User", description = "Edit user roles and permissions")
  @UseMiddleware(names = { "auth", "roles:ADMIN" })
  public ResponseEntity<Map<String, Object>> editUser( @RequestBody Map<String, Object> request) {
      Long userId = ((Number) request.get("userId")).longValue();
      Integer roleId = ((Number) request.get("roleId")).intValue();
      List<Integer> permissionIds = (List<Integer>) request.get("permissionIds");

      User user = userRepository.findById(userId).orElse(null);
      if (user == null) {
          return responseHelper.error("User not found", HttpStatus.NOT_FOUND);
      }

      Role role = roleRepository.findById(Long.valueOf(roleId)).orElse(null);
      if (role == null) {
          return responseHelper.error("Invalid roleId", HttpStatus.BAD_REQUEST);
      }

      Set<Permission> permissions = new HashSet<>();
      for (Integer pid : permissionIds) {
          permissionRepository.findById(Long.valueOf(pid)).ifPresent(permissions::add);
      }

      user.setRole(role);
      user.setPermissions(permissions);

      userRepository.save(user);

      return responseHelper.success("User updated with new role and permissions");
  }

    @PostMapping("/login")
    @Operation(summary = "User login and get JWT", description = "Return success if login is successful")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null || !bcrCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())) {
            return responseHelper.error("Email or password is incorrect", HttpStatus.UNAUTHORIZED);
        }

        String role = (user.getRole() != null) ? user.getRole().getName() : "";
        List<String> permissions = user.getPermissions() != null
                ? user.getPermissions().stream().map(Permission::getName).toList()
                : new ArrayList<>();

        String accessToken = jwtHelper.generateAccessToken(user.getEmail(), role, permissions);
        String refreshToken = jwtHelper.generateRefreshToken(user.getEmail());

        user.setToken(accessToken);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);

        return responseHelper.success("Login successful", response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, Object>> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null || refreshToken.isBlank()) {
            return responseHelper.error("Refresh token is required", HttpStatus.BAD_REQUEST);
        }

        String email = jwtHelper.getSubjectFromToken(refreshToken); // implement this
        if (email == null) {
            return responseHelper.error("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !refreshToken.equals(user.getRefreshToken())) {
            return responseHelper.error("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        String role = user.getRole() != null ? user.getRole().getName() : "";
        List<String> permissions = user.getPermissions() != null
                ? user.getPermissions().stream().map(Permission::getName).toList()
                : new ArrayList<>();

        String newAccessToken = jwtHelper.generateAccessToken(email, role, permissions);

        user.setToken(newAccessToken);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return responseHelper.success("New access token generated", response);
    }


}
