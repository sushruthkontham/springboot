package com.ezc.middleware;

import com.ezc.entity.Permission;
import com.ezc.entity.User;
import com.ezc.entity.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("authorization")
public class AuthorizationMiddleware implements Middleware {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, String param) throws Exception {
        UserDTO user = (UserDTO) request.getAttribute("currentUser");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        List<String> permissions = user.getPermissions();
        String[] requiredPermissions = param.split(",");
        boolean hasPermission = permissions.stream()
                .anyMatch(permission ->
                        Set.of(requiredPermissions).contains(permission.trim())
                );

        if (!hasPermission) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Permission Denied: Missing " + param);
        }
    }
}