package com.ezc.middleware;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ezc.entity.Permission;
import com.ezc.entity.Role;
import com.ezc.entity.UserDTO;
import com.ezc.helper.JwtHelper;
import com.ezc.helper.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import com.ezc.entity.User;
import com.ezc.repository.UserRepository;

@Slf4j
@Component("auth")
public class AuthMiddleware implements Middleware {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResponseHelper responseHelper;

    @Autowired
    private JwtHelper jwtHelper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, String param) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            responseHelper.error("Enter JWT token", null, false, HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String token = authHeader.substring(7);
        if (!jwtHelper.validateToken(token)) {
            responseHelper.error("Invalid token", null, false, HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        try{
            Map<String, Object> claims = jwtHelper.getClaimsFromToken(token);
            String email = (String) claims.get("sub");
            String role = claims.get("role").toString();
            List<String> permissions = (List<String>) claims.get("permissions");
            UserDTO user = new UserDTO();
            user.setEmail(email);
            user.setRole(role);
            user.setPermissions(permissions);
            request.setAttribute("currentUser", user);
        } catch (Exception e) {
            log.error("Error parsing JWT token: {}", e.getMessage());
            responseHelper.error("Invalid token", null, false, HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

    }
}