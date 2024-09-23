package com.example.myapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Verificar si el header contiene un token JWT
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);

            // Log para depurar el nombre de usuario y el token JWT
            System.out.println("Token JWT encontrado: " + jwt);
            System.out.println("Username extraído del token: " + username);
        }

        // Validar el token y autenticar al usuario
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Intentando cargar detalles del usuario para el username: " + username);

            // Aquí debería cargar el UserDetails, asegurando que sea el tipo correcto
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            System.out.println("Detalles del usuario cargados: " + userDetails);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Si el token es válido, autenticar al usuario en el contexto de seguridad
                var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Continuar con el siguiente filtro en la cadena
        chain.doFilter(request, response);
    }
}
