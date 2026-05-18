package com.cinemamonarca.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(1)
public class CacheControlFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();

        if (path.startsWith("/api/")          || path.startsWith("/auth/")     ||
            path.startsWith("/funciones")     || path.startsWith("/reservas")  ||
            path.startsWith("/sillas")        || path.startsWith("/peliculas") ||
            path.startsWith("/clientes")      || path.startsWith("/salas")     ||
            path.startsWith("/transacciones") || path.startsWith("/pagos")) {

            resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            resp.setHeader("Pragma",        "no-cache");
            resp.setHeader("Expires",       "0");
        }

        chain.doFilter(request, response);
    }
}