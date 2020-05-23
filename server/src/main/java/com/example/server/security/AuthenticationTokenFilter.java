package com.example.server.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

            Set<SimpleGrantedAuthority> authorities = new HashSet<>();

            authorities.add(new SimpleGrantedAuthority("PKI_ADMINISTRATION"));

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("User", null, authorities);
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails((HttpServletRequest)request));
            SecurityContextHolder.getContext().setAuthentication(auth);


        chain.doFilter(request, response);
    }
}
