package com.Hanu.QrBasedAttendanceSystem.security;

import com.Hanu.QrBasedAttendanceSystem.entity.User;
import com.Hanu.QrBasedAttendanceSystem.repo.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");


        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return ;
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.extractClaims(token);

            String email = claims.getSubject();

            if(email != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {
                Optional<User> optionalUser = userRepository.findByEmail(email);
                if(optionalUser.isPresent()) {
                    User user = optionalUser.get();
                            SimpleGrantedAuthority authority =
                                    new SimpleGrantedAuthority(
                                            "ROLE_" + user.getRole().name()
                                    );
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            user,
                                            null,
                                            List.of(authority)
                                    );
                            SecurityContextHolder
                                    .getContext()
                                    .setAuthentication(authentication);
                        };
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
