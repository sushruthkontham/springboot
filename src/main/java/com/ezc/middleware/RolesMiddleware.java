package com.ezc.middleware;

import com.ezc.entity.UserDTO;
import com.ezc.helper.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import java.util.Set;

@Component("roles")
public class RolesMiddleware implements Middleware {

    @Autowired
    private ResponseHelper responseHelper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, String param) throws Exception {
        UserDTO user = (UserDTO) request.getAttribute("currentUser");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            return;
        }

        String[] allowedRoles = param.split(",");
        String userRole = user.getRole();
        boolean match = Set.of(allowedRoles).contains(userRole);

        if (!match) {
            responseHelper.error("Not your role", HttpServletResponse.SC_FORBIDDEN);
        }
    }
}