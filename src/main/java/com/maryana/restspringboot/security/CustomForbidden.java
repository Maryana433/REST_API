package com.maryana.restspringboot.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomForbidden implements AccessDeniedHandler {

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exc)
                throws IOException {

            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Error: Forbidden. You are authorized, but you don't " +
                    "have role to access this page");
        }
}
