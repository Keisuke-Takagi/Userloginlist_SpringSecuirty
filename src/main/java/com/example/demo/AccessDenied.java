package com.example.demo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class AccessDenied implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        try (PrintWriter writer = response.getWriter()) {
            writer.println("Access Denied!!");
        }
    }
}